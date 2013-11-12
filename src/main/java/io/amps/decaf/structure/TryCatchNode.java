package io.amps.decaf.structure;

public class TryCatchNode extends Node {

	private final int start, end, handler;
	private final Class<?> type;

	public TryCatchNode(final int start, final int end, final int handler,
			final Class<?> type) {
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	public int getEnd() {
		return end;
	}

	public int getHandler() {
		return handler;
	}

	public int getStart() {
		return start;
	}

	public Class<?> getType() {
		return type;
	}

}
