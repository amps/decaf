package deobber.rebuild.nodes.ins;

import deobber.ByteUtils;
import deobber.rebuild.nodes.FieldNode;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.Instruction;

public class FieldInsn extends InsnNode {
	
	public final int index;

	public FieldInsn(int line, Instruction instruction, byte[] args) {
		super(line, instruction, args);
		index = ByteUtils.toShort(args);
	}
	
	public FieldNode getField() {
		return code.method.parent.constantPool.getMemberRef(index).getField();
	}
	
	

}
