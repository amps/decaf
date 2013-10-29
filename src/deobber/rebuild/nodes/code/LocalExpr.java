package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Type;

public class LocalExpr extends VarExpr {
	
	public final boolean fromStack;

	public LocalExpr(int index, boolean fromStack, Type type) {
		super(index, type);
		this.fromStack = fromStack;
	}

}
