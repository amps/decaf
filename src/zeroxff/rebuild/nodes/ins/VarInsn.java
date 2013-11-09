package zeroxff.rebuild.nodes.ins;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;

public class VarInsn extends InsnNode {

	public final int var;

	public VarInsn(int line, Instruction opcode, byte[] args) {
		super(line, opcode, args);
		switch (opcode) {
		default:
			var = -1;
			break;
		}
	}

	public VarInsn(InsnNode node) {
		this(node.line, node.opcode, node.args);
	}

}
