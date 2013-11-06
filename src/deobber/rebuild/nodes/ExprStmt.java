package deobber.rebuild.nodes;

public class ExprStmt extends Stmt {

	public final Expr expr;

	public ExprStmt(Expr expr) {
		this.expr = expr;
	}
}
