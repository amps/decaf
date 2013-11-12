package io.amps.decaf.tree;

public interface Tree<T extends TreeNode<T>> {
	void addNode(T node);
}
