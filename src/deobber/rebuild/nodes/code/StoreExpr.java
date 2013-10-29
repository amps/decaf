package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class StoreExpr extends Expr {

	public final Expr expr;
	public final MemExpr target;

	public StoreExpr(MemExpr target, Expr expr, Type type) {
		super(type);
		this.target = target;
		this.expr = expr;
	}

}
