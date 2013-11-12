package io.amps.decaf.structure.ins;

import io.amps.decaf.ByteUtils;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.attributes.CodeAttribute;

public class JumpInsn extends InsnNode {

	public final int jump;

	public JumpInsn(final CodeAttribute code, final int line,
			final Instruction opcode, final byte[] args) {
		super(code, line, opcode, args);
		jump = ByteUtils.toNumber(args).intValue();
	}

	public JumpInsn(final InsnNode node) {
		this(node.code, node.line, node.opcode, node.args);
	}

	@Override
	public String toString() {
		return opcode.name() + "#" + jump;
	}

}
