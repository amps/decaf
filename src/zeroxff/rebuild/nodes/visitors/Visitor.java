package zeroxff.rebuild.nodes.visitors;

import zeroxff.rebuild.nodes.Node;

public interface Visitor {
	public boolean enterNode(Node node);

	public boolean exitNode(Node node);
}
