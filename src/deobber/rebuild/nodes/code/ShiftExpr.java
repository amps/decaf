package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.CodeNode;
import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class ShiftExpr extends Expr {

	public enum Direction {
		LEFT, RIGHT_ARITHMETIC, RIGHT_LOGICAL
	}

	public final Direction dir;
	public Expr expr, bits;

	public ShiftExpr(Direction dir, Expr expr, Expr bits, Type type) {
		super(type);
		this.dir = dir;
		this.expr = expr;
		this.bits = bits;
	}

}
