package zeroxff.rebuild.nodes.ins;

import zeroxff.ByteUtils;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;

public class JumpInsn extends InsnNode {

	public final int jump;

	public JumpInsn(int line, Instruction opcode, byte[] args) {
		super(line, opcode, args);
		jump = ByteUtils.toNumber(args).intValue();
	}

	public JumpInsn(InsnNode node) {
		this(node.line, node.opcode, node.args);
	}

}
