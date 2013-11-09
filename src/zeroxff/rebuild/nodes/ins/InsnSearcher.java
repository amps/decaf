package zeroxff.rebuild.nodes.ins;

import java.util.ArrayList;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.attributes.InsnList;
import zeroxff.rebuild.nodes.ins.def.InsnDef;

public class InsnSearcher extends ArrayList<InsnList> {

	public static InsnSearcher search(InstructionSearchable searchable,
			Instruction... opcodes) {
		return new InsnSearcher(0, opcodes).search(searchable);
	}

	public static InsnSearcher search(InstructionSearchable searchable,
			int options, Instruction... opcodes) {
		return new InsnSearcher(options, opcodes).search(searchable);
	}

	public static InsnSearcher search(InstructionSearchable searchable,
			InsnDef... nodes) {
		return new InsnSearcher(0, nodes).search(searchable);
	}

	public static InsnSearcher search(InstructionSearchable searchable,
			int options, InsnDef... nodes) {
		return new InsnSearcher(options, nodes).search(searchable);
	}

	public final Instruction[] opcodes;
	public final int options;
	public final InsnDef[] nodes;

	private enum Type {
		NODE, INS
	}

	public final Type type;

	public static final int FROM_START = 0x1;

	public InsnSearcher(int options, Instruction... opcodes) {
		this.opcodes = opcodes;
		this.options = options;
		nodes = null;
		type = Type.INS;
	}

	public InsnSearcher(int options, InsnDef... nodes) {
		this.opcodes = null;
		this.options = options;
		this.nodes = nodes;
		type = Type.NODE;
	}

	public InsnSearcher(Instruction... opcodes) {
		this(0, opcodes);
	}

	private boolean hasOption(int check) {
		return (options & check) == check;
	}

	public InsnSearcher search(InstructionSearchable searchable) {
		Iterable<InstructionSearchable> sets = searchable.getInstructionSets();
		if (sets == null) {
			search(searchable.getInstructions());
		} else {
			for (InstructionSearchable s : sets) {
				search(s.getInstructions());
			}
		}
		return this;
	}

	public void search(Iterable<InsnNode> instructions) {
		switch (type) {
		case INS:
			searchIns(instructions);
			break;
		case NODE:
			searchNodes(instructions);
			break;
		default:
			break;
		}
	}

	private void searchNodes(Iterable<InsnNode> instructions) {
		int position = 0;
		InsnList found = null;
		int i = 0;
		for (InsnNode iNode : instructions) {
			System.out.println(nodes[position] + " VS " + iNode);
			if (nodes[position] == null || nodes[position].equals(iNode)
					|| nodes[0].equals(iNode)) {
				if (nodes[0].equals(iNode)
						&& nodes[position].equals(iNode) == false) {
					position = 0;
				}
				System.out.println("GOT " + nodes[position] + " BY " + iNode);
				if (position == 0) {
					found = new InsnList();
				}
				found.add(iNode);
				position++;
				if (position == nodes.length) {
					add(found);
					position = 0;
				}
			} else {
				position = 0;

			}
			i++;
		}
	}

	private void searchIns(Iterable<InsnNode> instructions) {
		int position = 0;
		InsnList found = null;
		int i = 0;
		for (InsnNode iNode : instructions) {

			Instruction opcode = iNode.opcode;

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
					add(found);
					position = 0;
				}
			} else {
				position = 0;
			}
			i++;
		}
	}

}
