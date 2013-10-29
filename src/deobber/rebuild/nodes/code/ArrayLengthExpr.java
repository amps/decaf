package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class ArrayLengthExpr extends Expr {

	public final Expr expr;

	public ArrayLengthExpr(Expr expr, Type type) {
		super(type);
		this.expr = expr;
	}

}
