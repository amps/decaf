package io.amps.decaf.structure.ins;

import io.amps.decaf.ByteUtils;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.attributes.CodeAttribute;

public class IntInsn extends InsnNode {

	public final int operand;

	public IntInsn(final CodeAttribute code, final int line,
			final Instruction opcode, final byte[] args) {
		super(code, line, opcode, args);
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

	public IntInsn(final InsnNode node) {
		this(node.code, node.line, node.opcode, node.args);
	}

}
