package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class CallMethodExpr extends CallExpr {

	public enum Kind {
		INTERFACE, NONVIRTUAL, VIRTUAL
	}

	public final Expr receiver;
	public final Kind kind;

	public CallMethodExpr(Kind kind, Expr receiver, Expr[] params,
			MemberRef method, Type type) {
		super(params, method, type);
		this.kind = kind;
		this.receiver = receiver;
	}

}
