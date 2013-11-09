package zeroxff.rebuild.nodes.ins.def;

import java.util.Set;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.InstructionSets;
import zeroxff.rebuild.nodes.ins.VarInsn;

public class VarInsnDef extends InsnDef {

	public final int var;
	public final boolean hasVar;

	public VarInsnDef(Instruction opcode, int var) {
		super(opcode);
		hasVar = true;
		this.var = var;
	}

	public VarInsnDef(Instruction opcode) {
		super(opcode);
		hasVar = false;
		this.var = -1;
	}

	@Override
	public boolean nodeEquals(InsnNode node) {
		VarInsn vin = new VarInsn(node);
		if (hasVar && vin.var != var) {
			return false;
		}
		return true;
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.VAR_INSNS;
	}

}
