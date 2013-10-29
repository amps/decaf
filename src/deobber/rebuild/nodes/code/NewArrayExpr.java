package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class NewArrayExpr extends Expr {
	
	public final Expr size;
	public final Type elType;

	public NewArrayExpr(Expr size, Type elType, Type type) {
		super(type);
		this.size = size;
		this.elType = elType;
	}

}
