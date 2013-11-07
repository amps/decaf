package zeroxff.rebuild.nodes.cfg;

import java.util.ArrayList;
import java.util.Iterator;

import zeroxff.rebuild.nodes.InsnNode;

public class BlockPath extends ArrayList<Block> {

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
}
