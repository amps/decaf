package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class ConstantExpr extends Expr {

	public final Object value;
	
	public ConstantExpr(Object value, Type type) {
		super(type);
		this.value = value;
	}
	
}
