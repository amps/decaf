package zeroxff.rebuild.nodes;

import zeroxff.rebuild.nodes.visitors.Visitor;

public class Node {

	public void accept(Visitor visitor) {
		visitor.enterNode(this);
	}
}
