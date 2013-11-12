package io.amps.decaf.structure.cfg;

import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.ins.InsnSearcher;
import io.amps.decaf.structure.ins.InstructionSearchable;
import io.amps.lib.iter.IteratorChain;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.Lists;

public class BlockPath extends LinkedHashSet<Block> implements
		InstructionSearchable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2572338204210458546L;

	public BlockPath() {
		super();
	}

	public BlockPath(final BlockPath path) {
		super(path);
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
		final IteratorChain<InsnNode> chain = new IteratorChain<>();
		for (final Block block : this) {
			chain.add(block.instructions);
		}
		return chain;

	}

	@Override
	public boolean equals(final Object o) {
		return super.equals(o);
	}

	public List<InsnNode> getInstructionList() {
		return Lists.newArrayList(allInstructions());
	}

	@Override
	public Iterable<InsnNode> getInstructions() {
		return allInstructions();
	}

	public void printFields(final String name, final int before, final int after) {
		for (final Block block : this) {
			block.printFields(name, before, after);
		}
	}

	public void search(final InsnSearcher searcher) {
		for (final Block block : this) {
			searcher.search(block);
		}
	}

}
