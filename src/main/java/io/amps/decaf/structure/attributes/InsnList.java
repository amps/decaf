package io.amps.decaf.structure.attributes;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;

import java.util.ArrayList;
import java.util.Set;

public class InsnList extends ArrayList<InsnNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2930249641262696237L;

	public boolean contains(final Instruction i) {
		for (final InsnNode n : this) {
			if (n.opcode == i) {
				return true;
			}
		}
		return false;

	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof InsnList == false) {
			return false;
		}
		final InsnList b = (InsnList) o;
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(b.get(i))) {
				return false;
			}
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	public <T extends InsnNode> T get(final Instruction opcode, final int num) {
		int i = 0;

		for (final InsnNode iNode : this) {
			if (iNode.opcode == opcode) {
				if (i == num) {
					return (T) iNode;
				}
				i++;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends InsnNode> T get(final Instruction opcode, final int num,
			final Class<T> type) {
		return (T) get(opcode, num);
	}

	public InsnNode get(final Set<Instruction> opcodes, final int num) {
		int i = 0;

		for (final InsnNode iNode : this) {
			if (opcodes.contains(iNode.opcode)) {
				if (i == num) {
					return iNode;
				}
				i++;
			}
		}
		return null;
	}

	public <T extends InsnNode> T get(final Set<Instruction> opcodes,
			final int num, final Class<T> type) {
		return type.cast(get(opcodes, num));
	}

	public int getByteLength() {
		int i = 0;

		for (final InsnNode node : this) {
			final int items = node.args == null ? 0 : node.args.length;
			i += 1 + items;
		}
		return i;
	}

	@Override
	public int hashCode() {
		int code = 0;
		for (final InsnNode insn : this) {
			code += insn.hashCode();
		}
		return code;
	}

	public int indexOfLine(final int line) {
		int i = 0;
		int j = 0;
		int last = 0;
		for (final InsnNode node : this) {
			if (i == line) {
				return last;
			}
			last = j;
			final int items = node.args == null ? 0 : node.args.length;
			i += 1 + items;
			j++;
		}
		return -1;
	}

}
