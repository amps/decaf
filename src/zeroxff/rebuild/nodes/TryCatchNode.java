package zeroxff.rebuild.nodes;


public class TryCatchNode extends Node {

	private int start, end, handler;
	private Class<?> type;

	public TryCatchNode(int start, int end, int handler, Class<?> type) {
		this.start = start;
		this.end = end;
		this.handler = handler;
		this.type = type;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public int getHandler() {
		return handler;
	}

	public Class<?> getType() {
		return type;
	}

}
