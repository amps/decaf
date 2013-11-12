package io.amps.decaf.structure.ins.def;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.InstructionSets;
import io.amps.decaf.structure.ins.VarInsn;

import java.util.Set;

public class VarInsnDef extends InsnDef {

	public final int var;
	public final boolean hasVar;

	public VarInsnDef(final Instruction opcode) {
		super(opcode);
		hasVar = false;
		var = -1;
	}

	public VarInsnDef(final Instruction opcode, final int var) {
		super(opcode);
		hasVar = true;
		this.var = var;
	}

	public VarInsnDef(final Set<Instruction> insns) {
		super(insns);
		hasVar = false;
		var = -1;
	}

	public VarInsnDef(final Set<Instruction> insns, final int var) {
		super(insns);
		hasVar = true;
		this.var = var;
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.VAR_INSNS;
	}

	@Override
	public boolean nodeEquals(final InsnNode node) {
		final VarInsn vin = (VarInsn) node;
		if (insnSet.contains(node.opcode) == false) {
			return false;
		}
		if (hasVar && vin.var != var) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "=" + var;
	}
}
