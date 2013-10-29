package deobber.rebuild.nodes;

public abstract class Expr extends CodeNode {

	public final Type type;

	public Expr(Type type) {
		this.type = type;
	}

}
