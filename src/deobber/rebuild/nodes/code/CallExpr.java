package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.CodeNode;
import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public abstract class CallExpr extends Expr {

	public final Expr[] params;
	public final MemberRef method;
	
	public CallExpr(Expr[] params, MemberRef method, Type type) {
		super(type);
		this.params = params;
		this.method = method;
	}

}
