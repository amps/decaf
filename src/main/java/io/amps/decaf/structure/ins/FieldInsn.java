package io.amps.decaf.structure.ins;

import static io.amps.decaf.structure.Instruction.getfield;
import static io.amps.decaf.structure.Instruction.getstatic;
import static io.amps.decaf.structure.Instruction.putfield;
import static io.amps.decaf.structure.Instruction.putstatic;
import io.amps.decaf.ByteUtils;
import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.attributes.CodeAttribute;

public class FieldInsn extends InsnNode {

	public final int index;

	public FieldInsn(final CodeAttribute code, final int line,
			final Instruction instruction, final byte[] args) {
		super(code, line, instruction, args);
		index = ByteUtils.toNumber(args).intValue();
	}

	public FieldInsn(final InsnNode node) {
		super(node);
		index = ByteUtils.toNumber(args).intValue();
	}

	public FieldNode getField() {
		return code.method.parent.constantPool.getMemberRef(index).getField();
	}

	public boolean isGet() {
		return opcode == getfield || opcode == getstatic;
	}

	public boolean isPut() {
		return opcode == putfield || opcode == putstatic;
	}

	@Override
	public String toString() {
		return super.toString() + "{" + index + "}" + "(" + getField() + ")";
	}
}
