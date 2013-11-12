package io.amps.decaf.structure.ins.def;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.InstructionSets;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InsnDef {

	public final Set<Instruction> insnSet;

	public InsnDef(final Instruction opcode) {
		insnSet = Collections.unmodifiableSet(new HashSet<>(Arrays
				.asList(new Instruction[] { opcode })));
	}

	public InsnDef(final Set<Instruction> insnSet) {
		this.insnSet = insnSet;
	}

	public boolean equals(final InsnNode node) {
		if (!isValidOpcode(node.opcode)) {
			return false;
		}
		return nodeEquals(node);
	}

	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.OTHER_INSNS;
	}

	public boolean isValidOpcode() {
		return isValidOpcode(insnSet);
	}

	public boolean isValidOpcode(final Instruction opcode) {
		return getValidOpcodes().contains(opcode);
	}

	public boolean isValidOpcode(final Set<Instruction> check) {
		for (final Instruction checkIns : check) {
			if (!insnSet.contains(checkIns)) {
				return false;
			}
		}
		return true;
	}

	protected boolean nodeEquals(final InsnNode node) {
		return insnSet.contains(node.opcode);
	}

	@Override
	public String toString() {
		return insnSet + "Def";
	}
}
