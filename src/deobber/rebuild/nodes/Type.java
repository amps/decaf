package deobber.rebuild.nodes;

import deobber.Context;

public class Type {
	public static Type NULL = new Type();
	public final String descStr;
	public final Descriptor desc;

	private Type() {
		desc = null;
		descStr = null;
	}

	public Type(Context ctx, String desc) {
		descStr = desc;
		this.desc = Descriptor.parse(ctx, descStr);
	}
}