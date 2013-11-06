package deobber.rebuild.nodes.cfg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import deobber.rebuild.nodes.InsnNode;

public class BlockPath extends ArrayList<Block> {

	public BlockPath(BlockPath path) {
		super(path);
	}

	public BlockPath() {
		super();
	}

	public Iterator<InsnNode> allInstructions() {
		return new Iterator<InsnNode>() {
			int i = 0;
			int blockIndex = 0;
			Block currentBlock = (size() > 0) ? get(blockIndex) : null;

			@Override
			public boolean hasNext() {
				if (currentBlock != null
						&& i + 1 < currentBlock.instructions.size()) {
					return true;
				} else if (blockIndex < size()) {
					return true;
				}
				return false;
			}

			@Override
			public InsnNode next() {
				int j = i;
				InsnNode val = currentBlock.instructions.get(j);
				i++;
				if (i >= currentBlock.instructions.size()) {
					if (blockIndex + 1 < size()) {
						currentBlock = get(blockIndex + 1);
						i = 0;
					}
					blockIndex++;
				}
				return val;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
