package io.amps.decaf.tree.classtree;

import io.amps.decaf.Context;
import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.tree.TreeNode;

import com.google.common.collect.ImmutableList;

public class ClassHierarchyNode extends TreeNode<ClassHierarchyNode> {

	private final ClassNode node;

	public ClassHierarchyNode(final ClassNode node) {
		this.node = node;
	}

	public ClassHierarchyNode(final Context ctx, final Class<?> clz) {
		node = ClassNode.construct(ctx, clz);
	}

	public ClassNode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return node.toString();
	}

	protected boolean walkAddNode(final ClassHierarchyNode toAdd) {
		boolean topped = false;
		if (toAdd.getNode().hasParent(getNode().nodeClass)
				|| toAdd.getNode().nodeClass.isInterface()
				&& getNode().nodeClass == Object.class) {
			addChild(toAdd);
			topped = true;
		}
		for (final ClassHierarchyNode child : getChildren()) {
			if (child.walkAddNode(toAdd)) {
				if (!child.getNode().nodeClass.isInterface()) {
					removeChild(toAdd);
				}
				return true;
			}
		}
		return topped;
	}

	protected void walkFixNodes() {
		for (final ClassHierarchyNode child : ImmutableList
				.copyOf(getChildren())) {
			walkAddNode(child);
		}
	}

}
