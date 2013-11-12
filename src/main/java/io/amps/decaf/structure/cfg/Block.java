package io.amps.decaf.structure.cfg;

import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.ins.FieldInsn;
import io.amps.decaf.structure.ins.InstructionSearchable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Block implements InstructionSearchable {

	public static abstract class BlockVisitor {
		public abstract Object visit(BlockPath path);
	}

	public final List<InsnNode> instructions = new ArrayList<>();
	public final List<Block> children = new LinkedList<>();

	public final Set<Block> immediates = new HashSet<>();

	public final Set<Block> parents = new HashSet<>();

	public final int line;

	public Block(final int line) {
		this.line = line;
	}

	public void add(final InsnNode iNode) {
		instructions.add(iNode);

	}

	public void addChild(final Block block, final boolean immediate) {
		if (children.contains(block)) {
			return;
		}
		children.add(block);
		if (immediate) {
			immediates.add(block);
		}
		block.addParent(this);
	}

	public void addParent(final Block block) {
		parents.add(block);
	}

	@Override
	public Iterable<InsnNode> getInstructions() {
		return instructions;
	}

	public boolean isEmpty() {
		return children.size() == 1 && instructions.size() == 1
				&& instructions.get(0).opcode == Instruction.goto_;
	}

	public boolean isEnd() {
		if (instructions.size() == 0) {
			return false;
		}
		switch (instructions.get(instructions.size() - 1).opcode) {
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

	boolean isImmediate(final Block block) {
		return immediates.contains(block);
	}

	public void printFields(final String name, final int before, final int after) {
		for (int i = 0; i < instructions.size(); i++) {
			final InsnNode iNode = instructions.get(i);
			if (iNode instanceof FieldInsn) {
				final FieldInsn fin = (FieldInsn) iNode;
				final FieldNode fNode = fin.getField();
				if (!(name == null || fNode.name.equals(name))) {
					continue;
				}
				for (int j = before; j >= 1; j--) {
					try {
						final InsnNode at = instructions.get(i - j);
						System.out.println(at.line + ": " + at);
					} catch (final Exception e) {

					}
				}
				final InsnNode t = instructions.get(i);
				System.out.println("** " + t.line + ": " + t);
				for (int j = 1; j < after; j++) {
					try {
						final InsnNode at = instructions.get(i + j);
						System.out.println(at.line + ": " + at);
					} catch (final Exception e) {

					}
				}
				System.out.println(fNode);
				System.out.println("=============");
			}
		}

	}

	public void reorg() {

		final Set<Block> toRemove = new HashSet<>();
		for (final Block child : children) {
			if (child.isEmpty() || child.instructions.size() == 0) {
				toRemove.add(child);
			}
		}
		for (final Block rem : toRemove) {
			for (final Block parent : rem.parents) {
				parent.children.remove(rem);
				for (final Block child : rem.children) {
					final boolean immediate = rem.isImmediate(child);
					parent.addChild(child, immediate);
				}
			}
		}

	}

	@Override
	public String toString() {
		return line + "(" + instructions.size() + "):" + children.size();
	}

}
