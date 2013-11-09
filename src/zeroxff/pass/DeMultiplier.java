package zeroxff.pass;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import zeroxff.ByteUtils;
import zeroxff.Context;
import zeroxff.rebuild.nodes.ClassNode;
import zeroxff.rebuild.nodes.FieldNode;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.MethodNode;
import zeroxff.rebuild.nodes.ConstantPool.Constant;
import zeroxff.rebuild.nodes.cfg.Block;
import zeroxff.rebuild.nodes.cfg.BlockPath;
import zeroxff.rebuild.nodes.cfg.Block.BlockVisitor;
import zeroxff.rebuild.nodes.ins.FieldInsn;

public class DeMultiplier extends Pass {

	private final Map<FieldNode, Map<Integer, Integer>> tmpMultipliers = new HashMap<>();
	public final Map<FieldNode, Integer> multipliers = new HashMap<>();

	public DeMultiplier(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		tmpMultipliers.clear();

		for (final ClassNode cnode : context.getClasses()) {
			for (final MethodNode mnode : cnode.methods) {
				mnode.traverse(new BlockVisitor() {
					@SuppressWarnings("unchecked")
					@Override
					public Object visit(Block block, Block parent,
							BlockPath path) {
						Stack<Object> iStack = new Stack<>();
						for (InsnNode iNode : path.allInstructions()) {
							Instruction opcode = iNode.opcode;
							switch (opcode) {
							case ldc: {
								int index = ByteUtils.toByte(iNode.args[0]);
								Constant<?> constantWrap = cnode.constantPool
										.get(index);
								if (constantWrap.type == Integer.class) {
									iStack.push(((Constant<Integer>) constantWrap)
											.get());
								}
							}
								break;
							case ldc_w: {
								int index = ByteUtils.toNumber(iNode.args).intValue();
								Constant<?> constantWrap = cnode.constantPool
										.get(index);
								if (constantWrap.type == Integer.class) {
									iStack.push(((Constant<Integer>) constantWrap)
											.get());
								}
							}
								break;

							case getfield:
							case getstatic:
							case putfield:
							case putstatic: {
								FieldInsn fin = (FieldInsn) iNode;
								iStack.push(fin);

							}
								break;
							case imul: {
								if (iStack.size() < 2) {
									break;
								}
								Object a = iStack.pop();
								Object b = iStack.pop();
								if (a.getClass() == Integer.class
										|| a.getClass() == FieldInsn.class) {
									if ((b.getClass() == Integer.class && a
											.getClass() == FieldInsn.class)
											|| (b.getClass() == FieldInsn.class && a
													.getClass() == Integer.class)) {
										FieldInsn fin = (FieldInsn) (a
												.getClass() == FieldInsn.class ? a
												: b);
										int multi = (Integer) (a.getClass() == Integer.class ? a
												: b);

										if (fin.isPut()) {
											try {
												multi = BigInteger
														.valueOf(multi)
														.modInverse(
																BigInteger
																		.valueOf((long) Math
																				.pow(2,
																						32)))
														.intValue();
											} catch (ArithmeticException e) {
												break;
											}
										}
										int value = 1;
										Map<Integer, Integer> counts = null;
										if (tmpMultipliers.containsKey(fin
												.getField())) {
											counts = tmpMultipliers.get(fin
													.getField());

											if (counts.containsKey(multi)) {
												value = counts.get(multi) + 1;
											}
										} else {
											counts = new HashMap<>();
											tmpMultipliers.put(fin.getField(),
													counts);
										}
										counts.put(multi, value);

									}
								}
							}
								break;
							default:
								break;
							}

						}
						return null;
					}
				});
			}

		}
		clean();
	}

	private void clean() {
		multipliers.clear();
		for (FieldNode node : tmpMultipliers.keySet()) {
			int winner = 0;
			int winnerVal = Integer.MIN_VALUE;
			for (int key : tmpMultipliers.get(node).keySet()) {

				int count = tmpMultipliers.get(node).get(key);

				if (count > winnerVal) {
					winnerVal = count;
					winner = key;
				}
			}
			multipliers.put(node, winner);
		}
	}

}
