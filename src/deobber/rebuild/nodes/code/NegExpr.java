package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class NegExpr extends Expr {

	public final Expr expr;

	public NegExpr(Expr expr, Type type) {
		super(type);
		this.expr = expr;
	}

}
