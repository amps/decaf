package io.amps.decaf.structure.ins;

import io.amps.decaf.structure.InsnNode;

public interface InstructionSearchable {

	public Iterable<InsnNode> getInstructions();

}
