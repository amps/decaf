package deobber.rebuild.nodes;

public class Type {

	public static final Type NONSPEC = new Type("");
	public static final Type INT = new Type("I");
	public static final Type LONG = new Type("J");
	public static final Type FLOAT = new Type("F");
	public static final Type DOUBLE = new Type("D");
	public static final Type NULL = new Type("");
	


	public static Type get(String name) {
		return new Type(name);
	}

	public final String name;

	
	private Type(String name) {
		this.name = name;
	}

	public static Type fromDescriptor(Descriptor descriptor) {
		return new Type(descriptor.toString());
	}

}
