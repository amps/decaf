package deobber.rebuild.nodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.InstructionVisitor;

public class InsnNode extends Node {

	private static Map<Byte, Integer> specialCaseStack = new HashMap<>();

	static {
		specialCaseStack.put((byte)0x10, 1);
		specialCaseStack.put((byte)0x11, 2);
		specialCaseStack.put((byte)0x12, 1);
		specialCaseStack.put((byte)0x13, 2);
		specialCaseStack.put((byte)0x14, 2);
		specialCaseStack.put((byte)0x15, 1);
		specialCaseStack.put((byte)0x16, 1);
		specialCaseStack.put((byte)0x17, 1);
		specialCaseStack.put((byte)0x18, 1);
		specialCaseStack.put((byte)0x19, 1);
		specialCaseStack.put((byte)0xbd, 2);
		specialCaseStack.put((byte)0x3a, 1);
		specialCaseStack.put((byte)0x10, 1);
		specialCaseStack.put((byte)0xc0, 2);
		specialCaseStack.put((byte)0x18, 1);
		specialCaseStack.put((byte)0x39, 1);
		specialCaseStack.put((byte)0x17, 1);
		specialCaseStack.put((byte)0x38, 1);
		specialCaseStack.put((byte)0xb4, 2);
		specialCaseStack.put((byte)0xb2, 2);
		specialCaseStack.put((byte)0xa7, 2);
		specialCaseStack.put((byte)0xc8, 4);
		specialCaseStack.put((byte)0x19, 1);
		specialCaseStack.put((byte)0xa5, 2);
		specialCaseStack.put((byte)0xa6, 2);
		specialCaseStack.put((byte)0x9f, 2);
		specialCaseStack.put((byte)0xa2, 2);
		specialCaseStack.put((byte)0xa3, 2);
		specialCaseStack.put((byte)0xa4, 2);

		specialCaseStack.put((byte)0xa1, 2);
		specialCaseStack.put((byte)0xba0, 2);
		specialCaseStack.put((byte)0x99, 2);

		specialCaseStack.put((byte)0x9c, 2);
		specialCaseStack.put((byte)0x9d, 2);
		specialCaseStack.put((byte)0x9e, 2);

		specialCaseStack.put((byte)0x9b, 2);
		specialCaseStack.put((byte)0x9a, 2);
		specialCaseStack.put((byte)0xc7, 2);

		specialCaseStack.put((byte)0xc6, 2);
		specialCaseStack.put((byte)0x84, 2);
		specialCaseStack.put((byte)0x15, 1);
		

		specialCaseStack.put((byte)0xb8, 2);

	}

	public static List<InsnNode> constructList(ByteBuffer buffer) {
		List<InsnNode> insns = new ArrayList<>();
		while (buffer.hasRemaining()) {
			insns.add(construct(buffer));
		}
		return insns;
	}

	public static InsnNode construct(ByteBuffer buffer) {

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
		InsnNode node = new InsnNode(opcode, args);

		return node;
	}

	public final Instruction opcode;
	public final byte[] args;

	public InsnNode(byte opcode, byte[] args) {
		this.opcode = Instruction.valueOf(opcode);
		this.args = args;
	}

	public String toString() {
		int i = 0;
		if (args != null) {
			i = args.length;
		}
		return opcode + "[" + i + "]";
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
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}

	}

}
