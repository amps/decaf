package io.amps.decaf.structure;

import io.amps.decaf.structure.attributes.CodeAttribute;

public class GenericInsn extends InsnNode {

	public GenericInsn(final CodeAttribute code, final int line,
			final Instruction opcode, final byte[] args) {
		super(code, line, opcode, args);
	}

}
