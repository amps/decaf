package zeroxff.rebuild.nodes.ins.def;

import java.util.Set;

import zeroxff.rebuild.nodes.ClassNode;
import zeroxff.rebuild.nodes.FieldNode;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.InstructionSets;
import zeroxff.rebuild.nodes.ins.FieldInsn;

public class FieldInsnDef extends InsnDef {

	public final String owner;
	public final String name;
	public final String desc;

	public FieldInsnDef(Instruction opcode, String owner, String name,
			String desc) {
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	public FieldInsnDef(Instruction opcode, String owner, String name,
			Class<?> desc) {
		this(opcode, owner, name, desc.getName());
	}

	public FieldInsnDef(Instruction opcode, ClassNode node, String name,
			Class<?> desc) {
		this(opcode, node.name, name, desc);
	}

	@Override
	public boolean nodeEquals(InsnNode node) {
		FieldInsn fin = new FieldInsn(node);
		FieldNode fNode = fin.getField();
		if (owner == null || !fNode.parent.name.equals(owner)) {
			return false;
		}
		if (!(name == null || fNode.name.equals(name))) {
			return false;
		}
		if (!(desc == null || fNode.desc.getName().equals(desc))) {
			return false;
		}
		return true;
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.FIELD_INSNS;
	}

}
