package io.amps.decaf.structure.ins.def;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.InstructionSets;
import io.amps.decaf.structure.ins.LdcInsn;

import java.util.Set;

public class LdcInsnDef extends InsnDef {

	public final Object value;

	public LdcInsnDef(final Instruction opcode, final Object value) {
		super(opcode);
		this.value = value;
	}

	public LdcInsnDef(final Object value) {
		super(InstructionSets.LDC_INSNS);
		this.value = value;
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.LDC_INSNS;
	}

	@Override
	public boolean nodeEquals(final InsnNode node) {
		final LdcInsn<?> lin = (LdcInsn<?>) node;
		if (insnSet.contains(node.opcode) == false) {
			return false;
		}
		if (value.equals(lin.value) == false) {
			return false;
		}
		return true;
	}

}
