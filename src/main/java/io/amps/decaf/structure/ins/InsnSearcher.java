package io.amps.decaf.structure.ins;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.attributes.InsnList;
import io.amps.decaf.structure.ins.def.InsnDef;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class InsnSearcher {

	private enum Type {
		NODE, INS
	}

	public static final int FROM_START = 0x1;

	public static InsnSearcher search(final InstructionSearchable searchable,
			final InsnDef... nodes) {
		return new InsnSearcher(0, nodes).search(searchable);
	}

	public static InsnSearcher search(final InstructionSearchable searchable,
			final InsnNode from, final InsnDef... nodes) {
		return new InsnSearcher(0, nodes).search(searchable, from);
	}

	public static InsnSearcher search(final InstructionSearchable searchable,
			final Instruction... opcodes) {
		return new InsnSearcher(0, opcodes).search(searchable);
	}

	public static InsnSearcher search(final InstructionSearchable searchable,
			final int options, final InsnDef... nodes) {
		return new InsnSearcher(options, nodes).search(searchable);
	}

	public static InsnSearcher search(final InstructionSearchable searchable,
			final int options, final Instruction... opcodes) {
		return new InsnSearcher(options, opcodes).search(searchable);
	}

	public final Instruction[] opcodes;

	public final int options;

	public final InsnDef[] nodes;

	private final LinkedHashSet<InsnList> found = new LinkedHashSet<>();

	public final Type type;

	public InsnSearcher(final Instruction... opcodes) {
		this(0, opcodes);
	}

	public InsnSearcher(final int options, final InsnDef... nodes) {
		opcodes = null;
		this.options = options;
		this.nodes = nodes;
		type = Type.NODE;
	}

	public InsnSearcher(final int options, final Instruction... opcodes) {
		this.opcodes = opcodes;
		this.options = options;
		nodes = null;
		type = Type.INS;
	}

	public List<InsnList> found() {
		return new ArrayList<>(found);
	}

	public InsnList found(final int i) {
		final List<InsnList> f = found();
		if (i >= f.size()) {
			return null;
		}
		return f.get(i);
	}

	public InsnSearcher search(final InstructionSearchable searchable) {
		return search(searchable, (InsnNode) null);
	}

	public InsnSearcher search(final InstructionSearchable searchable,
			final InsnNode from) {
		found.clear();
		searchInstructions(searchable.getInstructions(), from);
		return this;
	}

	private void searchIns(final Iterable<InsnNode> instructions) {
		int position = 0;
		InsnList found = null;
		for (final InsnNode iNode : instructions) {

			final Instruction opcode = iNode.opcode;

			if (opcodes[position] == null || opcode == opcodes[position]
					|| opcode == opcodes[0]) {
				if (opcode == opcodes[0] && opcode != opcodes[position]) {
					position = 0;
				}
				if (position == 0) {
					found = new InsnList();
				}
				found.add(iNode);
				position++;
				if (position == opcodes.length) {
					this.found.add(found);
					position = 0;
				}
			} else {
				position = 0;
			}
		}
	}

	public void searchInstructions(final Iterable<InsnNode> instructions,
			final InsnNode from) {
		switch (type) {
		case INS:
			// TODO from
			searchIns(instructions);
			break;
		case NODE:
			searchNodes(instructions, from);
			break;
		default:
			break;
		}
	}

	private void searchNodes(final Iterable<InsnNode> instructions,
			final InsnNode from) {
		int position = 0;
		InsnList found = null;
		final Iterator<InsnNode> inIt = instructions.iterator();

		if (from != null) {
			for (; inIt.hasNext();) {
				final InsnNode iNode = inIt.next();
				if (from.equals(iNode)) {
					break;
				}
			}
		}

		for (; inIt.hasNext();) {
			final InsnNode iNode = inIt.next();

			if (nodes[position] == null || nodes[position].equals(iNode)
					|| nodes[0].equals(iNode)) {
				if (nodes[0].equals(iNode)
						&& nodes[position].equals(iNode) == false) {
					position = 0;
				}

				if (position == 0) {
					found = new InsnList();
				}
				found.add(iNode);
				position++;
				if (position == nodes.length) {
					this.found.add(found);
					position = 0;
				}
			} else {
				if (position > 0) {

				}
				position = 0;

			}
		}
	}

}
