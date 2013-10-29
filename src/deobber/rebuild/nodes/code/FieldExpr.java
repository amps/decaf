package deobber.rebuild.nodes.code;

import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.Type;

public class FieldExpr extends MemRefExpr {

	public final MemberRef field;

	public FieldExpr(MemberRef field, Type type) {
		super(type);
		this.field = field;
	}

}
