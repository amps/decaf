package io.amps.decaf.structure.visitors;

import io.amps.decaf.structure.Node;

public interface Visitor {
	public boolean enterNode(Node node);

	public boolean exitNode(Node node);
}
