package deobber.rebuild.nodes;

import deobber.rebuild.nodes.attributes.Label;

public class TryCatchNode extends Node {

	private Label start, end, handler;
	private Type type;

	public TryCatchNode(Label start, Label end, Label handler, Type type) {
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	public Label getStart() {
		return start;
	}

	public Label getEnd() {
		return end;
	}

	public Label getHandler() {
		return handler;
	}

	public Type getType() {
		return type;
	}

}
