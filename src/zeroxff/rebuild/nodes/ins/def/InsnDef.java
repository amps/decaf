package zeroxff.rebuild.nodes.ins.def;

import java.util.Set;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.InstructionSets;

public class InsnDef {

	public final Instruction opcode;

	public InsnDef(Instruction opcode) {
		this.opcode = opcode;
	}

	protected boolean nodeEquals(InsnNode node) {
		return node.opcode == opcode;
	}

	public boolean equals(InsnNode node) {
		if (!isValidOpcode(node.opcode)) {
			return false;
		}
		return nodeEquals(node);
	}

	public boolean isValidOpcode(Instruction opcode) {
		return getValidOpcodes().contains(opcode);
	}

	public boolean isValidOpcode() {
		return isValidOpcode(opcode);
	}

	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.OTHER_INSNS;
	}
	
	public String toString() {
		return opcode.name()+"Def";
	}
}
