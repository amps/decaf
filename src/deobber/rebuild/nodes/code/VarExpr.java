package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Type;

public class VarExpr extends MemExpr {
	
	public final int index;

	public VarExpr(int index, Type type) {
		super(type);
		this.index = index;
		// TODO Auto-generated constructor stub
	}

}
