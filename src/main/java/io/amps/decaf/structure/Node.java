package io.amps.decaf.structure;

import io.amps.decaf.structure.visitors.Visitor;

public class Node {

	public void accept(final Visitor visitor) {
		visitor.enterNode(this);
	}
}
