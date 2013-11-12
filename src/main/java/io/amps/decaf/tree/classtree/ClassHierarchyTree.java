package io.amps.decaf.tree.classtree;

import io.amps.decaf.Context;
import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.tree.Tree;

public class ClassHierarchyTree extends ClassHierarchyNode implements
		Tree<ClassHierarchyNode> {

	private final Context ctx;

	public ClassHierarchyTree(final Context ctx) {
		super(ctx, Object.class);
		this.ctx = ctx;
	}

	public void addNode(final Class<?> clz) {
		addNode(new ClassHierarchyNode(ctx, clz));
	}

	@Override
	public void addNode(final ClassHierarchyNode node) {
		walkAddNode(node);
		walkFixNodes();
	}

	public ClassHierarchyNode addNode(final ClassNode node) {
		final ClassHierarchyNode cNode = new ClassHierarchyNode(node);
		addNode(cNode);
		return cNode;
	}

}
