package io.amps.decaf.structure.ins.def;

import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.InstructionSets;
import io.amps.decaf.structure.ins.FieldInsn;

import java.util.Set;

public class FieldInsnDef extends InsnDef {

	public final String owner;
	public final String name;
	public final String desc;

	public FieldInsnDef(final Instruction opcode) {
		this(opcode, (String) null, null, (String) null);
	}

	public FieldInsnDef(final Instruction opcode, final ClassNode node,
			final String name, final Class<?> desc) {
		this(opcode, node.name, name, desc);
	}

	public FieldInsnDef(final Instruction opcode, final String owner,
			final String name, final Class<?> desc) {
		this(opcode, owner, name, desc == null ? null : desc.getName());
	}

	public FieldInsnDef(final Instruction opcode, final String owner,
			final String name, final String desc) {
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.FIELD_INSNS;
	}

	@Override
	public boolean nodeEquals(final InsnNode node) {
		final FieldInsn fin = (FieldInsn) node;
		final FieldNode fNode = fin.getField();
		if (insnSet.contains(node.opcode) == false) {
			return false;
		}
		if (!(owner == null || fNode.parent.name.equals(owner))) {
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
	public String toString() {
		return super.toString() + "=" + owner + "." + name;
	}
}
