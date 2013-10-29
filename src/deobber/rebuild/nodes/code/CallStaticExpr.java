package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class CallStaticExpr extends CallExpr {

	public CallStaticExpr(Expr[] params, MemberRef method, Type type) {
		super(params, method, type);
	}

}
