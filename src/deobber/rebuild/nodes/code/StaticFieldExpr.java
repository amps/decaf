package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.Type;

public class StaticFieldExpr extends MemRefExpr {

	public final Expr object;
	public final MemberRef field;

	public StaticFieldExpr(Expr obj, MemberRef field, Type type) {
		super(type);
		this.object = obj;
		this.field = field;
	}

}
