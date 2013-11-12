package io.amps.decaf.structure;

import io.amps.decaf.structure.attributes.CodeAttribute;
import io.amps.decaf.structure.visitors.InstructionVisitor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class InsnNode extends Node {
	private static Map<Byte, Integer> specialCaseStack = new HashMap<>();

	// TODO Move into enum
	static {
		for (final Instruction i : Instruction.values()) {
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
			case tableswitch:
				specialCaseStack.put(i.code, 4);
				break;
			default:
				break;
			}
		}

	}

	public static InsnNode construct(final CodeAttribute code, final int line,
			final ByteBuffer buffer) {

		final byte opcode = buffer.get();
		byte[] args = null;

		if (specialCaseStack.containsKey(opcode)) {
			args = new byte[specialCaseStack.get(opcode)];
			try {
				for (int i = 0; i < specialCaseStack.get(opcode); i++) {
					args[i] = buffer.get();
				}
			} catch (final Exception e) {
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
			node = new GenericInsn(code, line, ins, args);
		} else {
			Constructor<? extends InsnNode> cons;
			try {
				// System.out.println(ins+"::"+ins.insnType);
				cons = ins.insnType.getConstructor(CodeAttribute.class,
						int.class, Instruction.class, new byte[0].getClass());
				node = cons.newInstance(code, line, ins, args);

			} catch (final Exception e) {
				e.printStackTrace();
			}

		}

		return node;
	}

	public static void constructList(final CodeAttribute code,
			final ByteBuffer buffer) {
		int i = 0;
		while (buffer.hasRemaining()) {
			code.instructions.add(construct(code, i, buffer));
			i++;
		}
	}

	public final Instruction opcode;
	public final byte[] args;
	public final CodeAttribute code;

	public final int line;
	public final int argLength;

	public InsnNode(final CodeAttribute code, final int line,
			final Instruction instruction, final byte[] args) {
		this.code = code;
		this.line = line;
		opcode = instruction;
		argLength = args == null ? 0 : args.length;
		this.args = args;

	}

	public InsnNode(final InsnNode node) {
		this(node.code, node.line, node.opcode, node.args);
	}

	public void accept(final InstructionVisitor visitor) {
		final String methodName = "visit_" + opcode.toString();
		try {
			final Method m = visitor.getClass().getMethod(methodName,
					InsnNode.class);
			m.invoke(visitor, this);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		} catch (final InvocationTargetException e) {
			// TODO Auto-generated catch block

		}

	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Instruction) {
			return (Instruction) o == opcode;
		}
		if (o instanceof InsnNode == false) {
			return false;
		}
		final InsnNode b = (InsnNode) o;
		final boolean argEqual = args == null ? b.args == null : args
				.equals(b.args);
		return opcode.equals(b.opcode) && argEqual;
	}

	public InsnNode getNext() {
		return code.instructions.get(line + 1);
	}

	public InsnNode getNext(final Instruction opcode) {
		int i = line + 1;
		InsnNode f = null;
		while (i < code.instructions.size()
				&& (f = code.instructions.get(i)).opcode != opcode) {
			i++;
		}
		return f.opcode == opcode ? f : null;
	}

	public InsnNode getPrevious() {
		return code.instructions.get(line - 1);
	}

	@Override
	public int hashCode() {
		int code = 0;
		code += opcode.ordinal();
		if (args != null) {
			for (final byte b : args) {
				code += b;
			}
		}
		code += line;
		return code;
	}

	@Override
	public String toString() {
		int i = 0;
		if (args != null) {
			i = args.length;
		}
		return line + ":" + opcode + "[" + i + "]";
	}
}
