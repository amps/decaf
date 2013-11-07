package zeroxff.rebuild.nodes.cfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;

public class Block extends CFGNode {

	public final List<InsnNode> instructions = new ArrayList<>();

	public final List<Block> children = new LinkedList<>();
	public final Set<Block> immediates = new HashSet<>();

	public final Set<Block> parents = new HashSet<>();

	public final int line;

	public Block(int line) {
		this.line = line;
	}

	public void add(InsnNode iNode) {
		instructions.add(iNode);

	}

	public boolean isEnd() {
		if (instructions.size() == 0) {
			return false;
		}
		switch (instructions.get(instructions.size() - 1).opcode) {
		case ret:
		case return_:
		case areturn:
		case dreturn:
		case ireturn:
		case freturn:
		case lreturn:
			return true;
		default:
			return false;
		}
	}

	public boolean isEmpty() {
		return children.size() == 1 && instructions.size() == 1
				&& instructions.get(0).opcode == Instruction.goto_;
	}

	public void addChild(Block block, boolean immediate) {
		children.add(block);
		if (immediate) {
			immediates.add(block);
		}
		block.addParent(this);

	}

	public void addParent(Block block) {
		parents.add(block);
	}

	@Override
	public String toString() {
		return line + "(" + instructions.size() + "):" + children.size();
	}

	public static abstract class BlockVisitor {
		public abstract Object visit(Block block, Block parent, BlockPath path);
	}

	public final class Traverser {

		public final Block block;

		private final List<Block> discovered = new ArrayList<>();

		public Traverser() {
			this.block = Block.this;
		}

		public Object traverse(BlockVisitor visitor) {
			discovered.clear();
			BlockPath path = new BlockPath();

			return traverseAct(block, null, visitor, path);
		}

		private Object traverseAct(Block block, Block parent,
				BlockVisitor visitor, BlockPath path) {

			discovered.add(block);
			BlockPath pathCopy = new BlockPath(path);
			pathCopy.add(block);
			Object found = visitor.visit(block, parent, pathCopy);
			if (found != null) {
				return found;
			}

			Collections.sort(block.children, new Comparator<Block>() {
				@Override
				public int compare(Block o1, Block o2) {
					if (isImmediate(o1) && isImmediate(o2)) {
						return 0;
					} else if (isImmediate(o1)) {
						return 1;
					} else if (isImmediate(o2)) {
						return -1;
					}
					return 0;
				}
			});
			for (Block child : block.children) {
				if (!discovered.contains(child)) {
					traverseAct(child, block, visitor, pathCopy);
				}
			}
			return null;
		}
	}

	private boolean isImmediate(Block block) {
		return immediates.contains(block);
	}

	public void reorg() {

		Set<Block> toRemove = new HashSet<>();
		for (Block child : children) {
			if (child.isEmpty() || child.instructions.size() == 0) {
				toRemove.add(child);
			}
		}
		for (Block rem : toRemove) {
			for (Block parent : rem.parents) {
				parent.children.remove(rem);
				for (Block child : rem.children) {
					boolean immediate = rem.isImmediate(child);
					parent.addChild(child, immediate);
				}
			}
		}

	}
}
