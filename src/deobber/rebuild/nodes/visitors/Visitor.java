package deobber.rebuild.nodes.visitors;

import deobber.rebuild.nodes.Node;

public interface Visitor {
	public boolean enterNode(Node node);

	public boolean exitNode(Node node);
}
