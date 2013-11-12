package io.amps.decaf.structure.ins;

import static io.amps.decaf.structure.Instruction.getfield;
import static io.amps.decaf.structure.Instruction.getstatic;
import static io.amps.decaf.structure.Instruction.putfield;
import static io.amps.decaf.structure.Instruction.putstatic;
import io.amps.decaf.ByteUtils;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.attributes.CodeAttribute;

public class MethodInsn extends InsnNode {

	public final int index;

	public MethodInsn(final CodeAttribute code, final int line,
			final Instruction instruction, final byte[] args) {
		super(code, line, instruction, args);
		index = ByteUtils.toShort(args[0], args[1]);
	}

	public MethodInsn(final InsnNode node) {
		this(node.code, node.line, node.opcode, node.args);
	}

	public MethodNode getMethod() {
		return code.method.parent.constantPool.getMemberRef(index).getMethod();
	}

	public boolean isGet() {
		return opcode == getfield || opcode == getstatic;
	}

	public boolean isPut() {
		return opcode == putfield || opcode == putstatic;
	}

	@Override
	public String toString() {
		return super.toString() + "{" + index + "}" + "(" + getMethod() + ")";
	}
}
