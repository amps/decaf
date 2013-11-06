package deobber.pass;

import static deobber.rebuild.nodes.Instruction.new_;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deobber.Context;
import deobber.rebuild.nodes.ClassNode;
import deobber.rebuild.nodes.ConstantPool.Constant;
import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.attributes.InsnList;
import deobber.rebuild.nodes.FieldNode;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.MethodNode;

public class DeMultiplier extends Pass {

	private final Map<FieldNode, Map<Integer, Integer>> tmpMultipliers = new HashMap<>();
	public final Map<FieldNode, Integer> multipliers = new HashMap<>();

	public DeMultiplier(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		tmpMultipliers.clear();

		int constant = 0;
		FieldNode field = null;
		int fieldIndex = -1, ldcIndex = -1;
		boolean isGet = false;
		for (ClassNode cnode : context.getClasses()) {
			for (MethodNode mnode : cnode.methods) {
				InsnList code = mnode.code.instructions;
				for (int j = 0; j < code.size(); j++) {
					InsnNode i = code.get(j);
					switch (i.opcode) {
					case ldc: {
						try {
							Constant<?> cpi = cnode.constantPool
									.get(i.args[0] & 0x000000FF);
							if (cpi.type == Integer.class) {
								constant = (Integer) cpi.get();
							}
							ldcIndex = j;
						} catch (ArrayIndexOutOfBoundsException e) {
							e.printStackTrace();
						}
					}
						break;
					case ldc_w: {
						short index = (short) ((((short) i.args[0]) << 8) + (((short) i.args[1]) & 0x000000FF));
						try {
							Constant<?> cpi = cnode.constantPool.get(index);
							if (cpi.type == Integer.class) {
								constant = (Integer) cpi.get();
							}
							ldcIndex = j;
						} catch (ArrayIndexOutOfBoundsException e) {

							e.printStackTrace();
						}
					}
						break;
					case getfield:
					case getstatic:
						try {
							isGet = true;
							short index = (short) ((((short) i.args[0]) << 8) + (((short) i.args[1]) & 0x000000FF));
							MemberRef ref = cnode.constantPool
									.getMemberRef(index);
							String className = ref.getClassOwner()
									.getSimpleName();
							if (!context.getMappedClasses().containsKey(
									className)) {
								break;
							}
							String fieldName = ref.getNameType().getName();
							ClassNode node = context.getMappedClasses().get(
									className);
							field = node.field(fieldName);

							fieldIndex = j;
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;

					case putfield:
					case putstatic:
						try {
							isGet = false;
							short index = (short) ((((short) i.args[0]) << 8) + (((short) i.args[1]) & 0x000000FF));
							MemberRef ref = cnode.constantPool
									.getMemberRef(index);
							String className = ref.getClassOwner()
									.getSimpleName();
							if (!context.getMappedClasses().containsKey(
									className)) {
								break;
							}
							String fieldName = ref.getNameType().getName();
							ClassNode node = context.getMappedClasses().get(
									className);
							field = node.field(fieldName);

							fieldIndex = j;
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case imul:
						if (field == null) {
							break;
						}
						if (!isGet) {
							try {
								constant = BigInteger
										.valueOf(constant)
										.modInverse(
												BigInteger.valueOf((long) Math
														.pow(2, 32)))
										.intValue();
							} catch (ArithmeticException e) {
								constant = 0;
								fieldIndex = -1;
								field = null;
								break;
							}
						}
						Map<Integer, Integer> counts = null;
						int value = 1;
						if (field.name.equals("m")
								&& field.parent.name.equals("dv")) {
							System.out.println(constant);
							System.out.println(BigInteger
									.valueOf(constant)
									.modInverse(
											BigInteger.valueOf((long) Math.pow(
													2, 32))).intValue());
							System.out.println(isGet);
						}
						if (tmpMultipliers.containsKey(field)) {
							counts = tmpMultipliers.get(field);
							if (counts.containsKey(constant)) {
								value = counts.get(constant);
							}
						} else {
							counts = new HashMap<>();
							tmpMultipliers.put(field, counts);
						}
						counts.put(constant, value);

						constant = 0;
						fieldIndex = -1;
						field = null;
						break;
					default:
						break;
					}
				}
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
