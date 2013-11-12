package io.amps.decaf.structure.ins.def;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.InstructionSets;
import io.amps.decaf.structure.ins.IntInsn;

import java.util.Set;

public class IntInsnDef extends InsnDef {

	public final int operand;

	public IntInsnDef(final Instruction opcode, final int operand) {
		super(opcode);
		this.operand = operand;
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.INT_INSNS;
	}

	@Override
	public boolean nodeEquals(final InsnNode node) {
		final IntInsn iin = (IntInsn) node;
		if (insnSet.contains(node.opcode) == false) {
			return false;
		}
		if (iin.operand != operand) {
			return false;
		}
		return true;
	}

}
