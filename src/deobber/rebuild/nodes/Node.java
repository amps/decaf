package deobber.rebuild.nodes;

import deobber.rebuild.nodes.visitors.Visitor;

public class Node {

	public void accept(Visitor visitor) {
		visitor.enterNode(this);
	}
}
