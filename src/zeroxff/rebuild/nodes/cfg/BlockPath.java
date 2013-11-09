package zeroxff.rebuild.nodes.cfg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.attributes.InsnList;
import zeroxff.rebuild.nodes.ins.InsnSearcher;
import zeroxff.rebuild.nodes.ins.InstructionSearchable;

public class BlockPath extends ArrayList<Block> implements
		InstructionSearchable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2572338204210458546L;

	public BlockPath(BlockPath path) {
		super(path);
	}

	public BlockPath() {
		super();
	}

	public Iterable<InsnNode> allInstructions() {
		return new Iterable<InsnNode>() {

			@Override
			public Iterator<InsnNode> iterator() {
				return allInstructionsIterator();
			}
		};
	}

	public Iterator<InsnNode> allInstructionsIterator() {
		return new Iterator<InsnNode>() {
			int i = 0;
			int blockIndex = 0;
			Block currentBlock = (size() > 0) ? get(blockIndex) : null;

			@Override
			public boolean hasNext() {
				Block originalCurrent = currentBlock;
				int oi = i;
				int oBlockIndex = blockIndex;
				while (currentBlock == null
						|| currentBlock.instructions.size() == 0) {
					check();
					if (blockIndex >= size()) {
						break;
					}
				}
				check();
				boolean pass = false;
				if (currentBlock != null
						&& i + 1 < currentBlock.instructions.size()) {
					pass = true;
				} else if (blockIndex < size()) {
					pass = true;
				}
				i = oi;
				currentBlock = originalCurrent;
				blockIndex = oBlockIndex;
				return pass;
			}

			private InsnNode getValue() {
				int j = i;
				InsnNode val = currentBlock.instructions.get(j);
				i++;
				return val;
			}

			@Override
			public InsnNode next() {
				while (currentBlock == null
						|| currentBlock.instructions.size() == 0) {
					check();
				}

				check();
				return getValue();
			}

			private void check() {
				if (i >= currentBlock.instructions.size()) {
					if (blockIndex + 1 < size()) {
						currentBlock = get(blockIndex + 1);
						i = 0;
					}
					blockIndex++;
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public void search(InsnSearcher searcher) {
		for (Block block : this) {
			searcher.search(block);
		}
	}

	public void printFields(int before, int after) {
		for (Block block : this) {
			block.printFields(before, after);
		}
	}

	@Override
	public Iterable<InsnNode> getInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<InstructionSearchable> getInstructionSets() {
		return new Iterable<InstructionSearchable>() {

			@Override
			public Iterator<InstructionSearchable> iterator() {
				return new Iterator<InstructionSearchable>() {
					Iterator<Block> blockIter = BlockPath.this.iterator();

					@Override
					public boolean hasNext() {
						return blockIter.hasNext();
					}

					@Override
					public InstructionSearchable next() {
						return blockIter.next();
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}

}
