package io.amps.decaf.tree;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode<T extends TreeNode<T>> {

	private final List<T> parents = new ArrayList<>();

	private final List<T> children = new ArrayList<>();

	@SuppressWarnings("unchecked")
	protected void addChild(final T node) {
		if (!children.contains(node)) {
			children.add(node);
		}
		node.addParent((T) this);

	}

	protected void addParent(final T node) {
		if (!parents.contains(node)) {
			parents.add(node);
		}
	}

	public T getChild(final int i) {
		return getChildren().get(i);
	}

	public List<T> getChildren() {
		return children;
	}

	public List<T> getParents() {
		return parents;
	}

	@SuppressWarnings("unchecked")
	protected void removeChild(final T node) {
		node.removeParent((T) this);
		children.remove(node);
	}

	protected void removeParent(final T node) {
		parents.remove(node);
	}
}
