package io.amps.decaf.pass;

import io.amps.decaf.ByteUtils;
import io.amps.decaf.Context;
import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.ConstantPool.Constant;
import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.cfg.Block.BlockVisitor;
import io.amps.decaf.structure.cfg.BlockPath;
import io.amps.decaf.structure.ins.FieldInsn;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class DeMultiplier extends Pass {

	private final Map<FieldNode, Map<Integer, Integer>> tmpMultipliers = new HashMap<>();
	public final Map<FieldNode, Integer> multipliers = new HashMap<>();

	public DeMultiplier(final Context context) {
		super(context);
	}

	private void clean() {
		multipliers.clear();
		for (final FieldNode node : tmpMultipliers.keySet()) {
			int winner = 0;
			int winnerVal = Integer.MIN_VALUE;
			for (final int key : tmpMultipliers.get(node).keySet()) {

				final int count = tmpMultipliers.get(node).get(key);

				if (count > winnerVal) {
					winnerVal = count;
					winner = key;
				}
			}
			multipliers.put(node, winner);
		}
	}

	@Override
	public void execute() {
		tmpMultipliers.clear();

		for (final ClassNode cnode : context.getClasses()) {
			for (final MethodNode mnode : cnode.methods) {
				mnode.traverse(new BlockVisitor() {
					@SuppressWarnings("unchecked")
					@Override
					public Object visit(final BlockPath path) {
						final Stack<Object> iStack = new Stack<>();
						for (final InsnNode iNode : path.allInstructions()) {
							final Instruction opcode = iNode.opcode;
							switch (opcode) {
							case ldc: {
								final int index = ByteUtils
										.toByte(iNode.args[0]);
								final Constant<?> constantWrap = cnode.constantPool
										.get(index);
								if (constantWrap.type == Integer.class) {
									iStack.push(((Constant<Integer>) constantWrap)
											.get());
								}
							}
								break;
							case ldc_w: {
								final int index = ByteUtils
										.toNumber(iNode.args).intValue();
								final Constant<?> constantWrap = cnode.constantPool
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
								final FieldInsn fin = (FieldInsn) iNode;
								iStack.push(fin);

							}
								break;
							case imul: {
								if (iStack.size() < 2) {
									break;
								}
								final Object a = iStack.pop();
								final Object b = iStack.pop();
								if (a.getClass() == Integer.class
										|| a.getClass() == FieldInsn.class) {
									if (b.getClass() == Integer.class
											&& a.getClass() == FieldInsn.class
											|| b.getClass() == FieldInsn.class
											&& a.getClass() == Integer.class) {
										final FieldInsn fin = (FieldInsn) (a
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
											} catch (final ArithmeticException e) {
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

}
