package deobber.rebuild.nodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import deobber.rebuild.nodes.attributes.CodeAttribute;
import deobber.rebuild.nodes.attributes.InsnList;
import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.InstructionVisitor;

public abstract class InsnNode extends Node {
	private static Map<Byte, Integer> specialCaseStack = new HashMap<>();

	// TODO Move into enum
	static {
		for (Instruction i : Instruction.values()) {
			switch (i) {
			case aload:
			case astore:
			case bipush:
			case dload:
			case dstore:
			case fload:
			case fstore:
			case iload:
			case istore:
			case lload:
			case lstore:
			case ret:
			case ldc:
			case newarray:
				specialCaseStack.put(i.code, 1);
				break;
			case instanceof_:
			case putfield:
			case putstatic:
			case sipush:
			case ldc_w:
			case new_:
			case ldc2_w:
			case anewarray:
			case checkcast:
			case getfield:
			case getstatic:
			case goto_:
			case if_acmpeq:
			case if_acmpne:
			case if_icmpeq:
			case if_icmpge:
			case if_icmpgt:
			case if_icmple:
			case if_icmplt:
			case if_icmpne:
			case ifeq:
			case ifge:
			case ifgt:
			case ifle:
			case iflt:
			case ifne:
			case ifnonnull:
			case ifnull:
			case iinc:
			case invokespecial:
			case invokestatic:
			case invokevirtual:
			case jsr:
				specialCaseStack.put(i.code, 2);
				break;
			case multianewarray:

				specialCaseStack.put(i.code, 3);
				break;
			case goto_w:
			case jsr_w:
			case invokedynamic:
			case invokeinterface:
				specialCaseStack.put(i.code, 4);
				break;
			default:
				break;
			}
		}

	}

	public static InsnList constructList(ByteBuffer buffer) {
		InsnList insns = new InsnList();
		int i = 0;
		while (buffer.hasRemaining()) {
			insns.add(construct(i, buffer));
			i++;
		}
		return insns;
	}

	public static InsnNode construct(int line, ByteBuffer buffer) {

		byte opcode = buffer.get();
		byte[] args = null;

		if (specialCaseStack.containsKey(opcode)) {
			args = new byte[specialCaseStack.get(opcode)];
			try {
				for (int i = 0; i < specialCaseStack.get(opcode); i++) {
					args[i] = buffer.get();
				}
			} catch (Exception e) {
			}
		}
		InsnNode node = null;
		Instruction ins = null;
		if (Instruction.valueOf(opcode) == null) {
			ins = Instruction.nop;
		} else {
			ins = Instruction.valueOf(opcode);
		}
		if (ins.insnType == null) {
			node = new GenericInsn(line, ins, args);
		} else {
			Constructor<? extends InsnNode> cons;
			try {
				cons = ins.insnType.getConstructor(int.class,
						Instruction.class, new byte[0].getClass());
				node = cons.newInstance(line, ins, args);
				
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return node;
	}

	public final Instruction opcode;
	public final byte[] args;
	public CodeAttribute code;

	public final int line;
	public final int argLength;

	public InsnNode(int line, Instruction instruction, byte[] args) {
		this.line = line;
		this.opcode = instruction;
		argLength = args == null ? 0 : args.length;
		this.args = args;

	}

	public String toString() {
		int i = 0;
		if (args != null) {
			i = args.length;
		}
		return opcode + "[" + i + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof InsnNode == false) {
			return false;
		}
		InsnNode b = (InsnNode) o;
		boolean argEqual = (args == null) ? b.args == null : args
				.equals(b.args);
		return opcode.equals(b.opcode) && argEqual;
	}

	public void accept(CodeVisitor visitor) {
		super.accept(visitor);
		visitor.enterInstruction(this);
	}

	public void accept(InstructionVisitor visitor) {
		String methodName = "visit_" + this.opcode.toString();
		try {
			Method m = visitor.getClass().getMethod(methodName, InsnNode.class);
			m.invoke(visitor, this);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block

		}

	}

	@Override
	public int hashCode() {
		int code = 0;
		code += opcode.ordinal();
		if (args != null) {
			for (byte b : args) {
				code += b;
			}
		}
		code += line;
		return code;
	}

}
