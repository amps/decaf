package zeroxff.rebuild.nodes.ins;

import zeroxff.ByteUtils;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;

public class IntInsn extends InsnNode {

	public final int operand;

	public IntInsn(int line, Instruction opcode, byte[] args) {
		super(line, opcode, args);
		short choose = 0;
		switch (opcode) {
		case sipush: {
			choose = ByteUtils.toNumber(args).shortValue();
		}
			break;
		case bipush: {
			choose = ByteUtils.toNumber(args).shortValue();
		}
		default:
			break;
		}
		operand = choose;
	}

	public IntInsn(InsnNode node) {
		this(node.line, node.opcode, node.args);
	}

}
