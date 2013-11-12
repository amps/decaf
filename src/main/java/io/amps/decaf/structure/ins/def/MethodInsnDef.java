package io.amps.decaf.structure.ins.def;

import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.InstructionSets;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.ins.MethodInsn;

import java.util.Set;

public class MethodInsnDef extends InsnDef {

	public final String owner;
	public final String name;
	public final String desc;
	public final String params;

	public MethodInsnDef(final Instruction opcode) {
		this(opcode, (String) null, null, null, (String[]) null);
	}

	public MethodInsnDef(final Instruction opcode, final Class<?> owner,
			final String method, final Class<?> desc, final Class<?>... params) {
		this(opcode, owner.getName(), method, desc, params);
	}

	public MethodInsnDef(final Instruction opcode, final ClassNode node,
			final String name, final Class<?> desc, final Class<?>... params) {
		this(opcode, node.name, name, desc);
	}

	public MethodInsnDef(final Instruction opcode, final String owner,
			final String name, final Class<?> desc, final Class<?>... params) {
		this(opcode, owner, name, desc == null ? null : desc.getName(), params);
	}

	public MethodInsnDef(final Instruction opcode, final String owner,
			final String name, final String desc, final Class<?>... params) {
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.desc = desc;
		final StringBuilder sb = new StringBuilder();
		for (final Class<?> param : params) {
			sb.append(param.getName());
		}
		this.params = sb.toString();
	}

	public MethodInsnDef(final Instruction opcode, final String owner,
			final String name, final String desc, final String... params) {
		super(opcode);
		this.owner = owner;
		this.name = name;
		this.desc = desc;
		final StringBuilder sb = new StringBuilder();
		for (final String param : params) {
			sb.append(param);
		}
		this.params = sb.toString();
	}

	@Override
	public Set<Instruction> getValidOpcodes() {
		return InstructionSets.METHOD_INSNS;
	}

	@Override
	public boolean nodeEquals(final InsnNode node) {
		final MethodInsn min = (MethodInsn) node;
		final MethodNode mNode = min.getMethod();
		if (mNode == null) {
			// TODO Why is it null, shouldnt happen
			return false;
		}
		if (insnSet.contains(node.opcode) == false) {
			return false;
		}
		if (owner == null || !mNode.parent.name.equals(owner)) {
			return false;
		}
		if (!(name == null || mNode.name.equals(name))) {
			return false;
		}
		if (!(desc == null || mNode.desc.getName().equals(desc))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return super.toString() + "=" + owner + "." + name;
	}

}
