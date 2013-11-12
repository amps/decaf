package io.amps.decaf.structure.cfg;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.attributes.CodeAttribute;
import io.amps.decaf.structure.attributes.InsnList;
import io.amps.decaf.structure.cfg.Block.BlockVisitor;
import io.amps.decaf.structure.ins.JumpInsn;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ControlFlow {

	public final class Traverser {

		private final Map<Block, Set<BlockPath>> discovered = new HashMap<>();
		private final Set<Block> explored = new HashSet<>();

		public Traverser() {

		}

		private void addVisitor(final Block block, final BlockPath path) {
			if (discovered.containsKey(block)) {
				discovered.get(block).add(path);
			} else {
				final Set<BlockPath> from = new HashSet<>();
				from.add(path);
				discovered.put(block, from);
			}
		}

		private boolean hasVisited(final Block block, final BlockPath path) {
			return discovered.containsKey(block)
					&& discovered.get(block).contains(path);
		}

		public Object traverse(final BlockVisitor blockVisitor) {

			discovered.clear();
			explored.clear();
			final BlockPath path = new BlockPath();
			if (blocks.size() == 0) {
				return null;
			}
			return traverseAct(blockVisitor, blocks.get(0), null, path);
		}

		private Object traverseAct(final BlockVisitor visitor,
				final Block block, final Block parent, final BlockPath p) {
			if (explored.contains(block)) {
				return null;
			}
			addVisitor(block, p);

			final BlockPath path = new BlockPath(p);
			path.add(block);

			if (block.isEnd()) {
				final Object found = visitor.visit(path);
				if (found != null) {
					return found;
				}
			} else {
				Collections.sort(block.children, new Comparator<Block>() {
					@Override
					public int compare(final Block o1, final Block o2) {
						if (block.isImmediate(o1) && block.isImmediate(o2)) {
							return 0;
						} else if (block.isImmediate(o1)) {
							return 1;
						} else if (block.isImmediate(o2)) {
							return -1;
						}
						return 0;
					}
				});
				for (final Block child : block.children) {
					if (hasVisited(child, path) == false) {
						final Object value = traverseAct(visitor, child, block,
								path);
						if (value != null) {
							return value;
						}
					}
				}
			}
			explored.add(block);
			return null;

		}
	}

	public final CodeAttribute code;
	Block currentBlock;
	Map<Integer, Block> blocks = new HashMap<>();

	private int line = 0, nextLine = 0;

	public ControlFlow(final CodeAttribute codeAttribute) {
		super();
		code = codeAttribute;

	}

	private Block blockAt(final int line) {
		if (blocks.containsKey(line)) {
			return blocks.get(line);
		}
		final Block newBlock = new Block(line);
		blocks.put(line, newBlock);
		return newBlock;
	}

	private void check() {
		if (blocks.containsKey(line)) {
			final Block nextBlock = blockAt(line);
			if (currentBlock.equals(nextBlock)) {
				return;
			}
			currentBlock.addChild(nextBlock, true);
			currentBlock = nextBlock;
		}
	}

	private void nextBlock(final boolean addAsChild) {
		final Block nextBlock = blockAt(nextLine);
		if (addAsChild) {
			currentBlock.addChild(nextBlock, true);
		}
		currentBlock = nextBlock;
	}

	public void parse() {
		blocks.clear();
		currentBlock = new Block(0);
		blocks.put(0, currentBlock);
		line = 0;
		final InsnList nodes = code.instructions;
		for (int i = 0; i < nodes.size(); i++) {
			final InsnNode iNode = nodes.get(i);
			nextLine = line + 1 + iNode.argLength;
			check();
			switch (iNode.opcode) {
			case if_acmpeq:
			case if_acmpne:
			case if_icmpeq:
			case if_icmpge:
			case if_icmpgt:
			case if_icmple:
			case if_icmplt:
			case if_icmpne:
			case ifeq:
			case ifge:
			case ifgt:
			case ifle:
			case iflt:
			case ifne:
			case ifnonnull:
			case ifnull: {
				final JumpInsn jin = (JumpInsn) iNode;
				currentBlock.add(iNode);
				final Block futureBlock = blockAt(line + jin.jump);
				currentBlock.addChild(futureBlock, false);
				blocks.put(line + jin.jump, futureBlock);
				nextBlock(true);
			}
				break;
			case goto_:
			case goto_w: {
				currentBlock.add(iNode);
				final JumpInsn jin = (JumpInsn) iNode;
				final Block futureBlock = blockAt(line + jin.jump);
				currentBlock.addChild(futureBlock, true);
				blocks.put(line + jin.jump, futureBlock);
				nextBlock(false);
			}
				break;
			case return_:
			case areturn:
			case dreturn:
			case ireturn:
			case freturn:
			case lreturn:
				currentBlock.add(iNode);
				nextBlock(false);

				break;
			default:
				currentBlock.add(iNode);
				break;
			}
			line = nextLine;
		}

		for (final Block block : blocks.values()) {
			block.reorg();
		}

	}

}
