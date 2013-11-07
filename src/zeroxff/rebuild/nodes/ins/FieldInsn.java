package zeroxff.rebuild.nodes.ins;

import static zeroxff.rebuild.nodes.Instruction.getfield;
import static zeroxff.rebuild.nodes.Instruction.getstatic;
import static zeroxff.rebuild.nodes.Instruction.putfield;
import static zeroxff.rebuild.nodes.Instruction.putstatic;
import zeroxff.ByteUtils;
import zeroxff.rebuild.nodes.FieldNode;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;

public class FieldInsn extends InsnNode {

	public final int index;

	public FieldInsn(int line, Instruction instruction, byte[] args) {
		super(line, instruction, args);
		index = ByteUtils.toShort(args);
	}

	public FieldNode getField() {
		return code.method.parent.constantPool.getMemberRef(index).getField();
	}

	public boolean isPut() {
		return opcode == putfield || opcode == putstatic;
	}

	public boolean isGet() {
		return opcode == getfield || opcode == getstatic;
	}

	@Override
	public String toString() {
		return super.toString() + "{" + index + "}" + "(" + getField() + ")";
	}
}
