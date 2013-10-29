package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class ArrayRefExpr extends MemRefExpr {

	public final Expr array, index;
	public final Type elType, type;

	public ArrayRefExpr(Expr array, Expr index, Type elType, Type type) {
		super(type);
		this.array = array;
		this.index = index;
		this.elType = elType;
		this.type = type;
	}

}
