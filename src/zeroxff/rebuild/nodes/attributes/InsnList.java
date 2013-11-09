package zeroxff.rebuild.nodes.attributes;

import java.util.ArrayList;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;

public class InsnList extends ArrayList<InsnNode> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2930249641262696237L;

	public int getByteLength() {
		int i = 0;

		for (InsnNode node : this) {
			int items = node.args == null ? 0 : node.args.length;
			i += 1 + items;
		}
		return i;
	}

	public int indexOfLine(int line) {
		int i = 0;
		int j = 0;
		int last = 0;
		for (InsnNode node : this) {
			if (i == line) {
				return last;
			}
			last = j;
			int items = node.args == null ? 0 : node.args.length;
			i += 1 + items;
			j++;
		}
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof InsnList == false) {
			return false;
		}
		InsnList b = (InsnList) o;
		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(b.get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		int code = 0;
		for (InsnNode insn : this) {
			code += insn.hashCode();
		}
		return code;
	}

	public <T extends InsnNode> T get(Instruction opcode, int num) {
		int i = 0;

		for (InsnNode iNode : this) {
			if (iNode.opcode == opcode) {
				if (i == num) {
					return (T) iNode;
				}
				i++;
			}
		}
		return null;
	}

	public <T extends InsnNode> T get(Instruction opcode, int num, Class<T> type) {
		return (T) get(opcode, num);
	}

}
