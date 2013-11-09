package zeroxff.rebuild.nodes.ins;

import zeroxff.rebuild.nodes.InsnNode;

public interface InstructionSearchable {

	public Iterable<InsnNode> getInstructions();

	public Iterable<InstructionSearchable> getInstructionSets();

}
