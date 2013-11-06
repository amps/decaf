package deobber.rebuild.nodes;

import java.nio.ByteBuffer;

import deobber.Context;
import deobber.rebuild.Constants;
import deobber.rebuild.nodes.attributes.Attribute;
import deobber.rebuild.nodes.attributes.ConstantValue;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public class FieldNode extends MemberNode {

	public static FieldNode construct(Context ctx, ClassNode parent,
			ConstantPool constantPool, ByteBuffer buffer) {

		short accessFlags = buffer.getShort();
		short nameIndex = buffer.getShort();
		short descIndex = buffer.getShort();
		String name = constantPool.getString(nameIndex).replaceAll("/", ".");
		String descStr = constantPool.getString(descIndex).replaceAll("/", ".");
		Class<?> desc = Descriptor.parse(ctx, descStr).get(0);

		FieldNode node = new FieldNode(parent, name, accessFlags, desc);
		short attrCount = buffer.getShort();
		for (int j = 0; j < attrCount; j++) {
			Attribute attr = Attribute.construct(node, constantPool, buffer);
			node.addAttribute(attr);
		}
		return node;
	}

	public final Class<?> desc;
	public final ClassNode parent;
	public ConstantValue constant;

	public FieldNode(ClassNode parent, String name, short access, Class<?> desc) {
		super(parent, name, access);
		this.parent = parent;
		this.desc = desc;
	}

	@Override
	void onAttribute(Attribute attr) {
		if (attr instanceof ConstantValue) {
			constant = (ConstantValue) attr;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof FieldNode == false) {
			return false;
		}
		FieldNode b = (FieldNode) o;
		return name.equals(b.name) && access == b.access && desc == b.desc
				&& parent.name.equals(b.parent.name);
	}

	public void accept(StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterField(this);
		visitor.exitField(this);
	}

	@Override
	public String toString() {
		return parent.name + "." + name + "("+desc.getSimpleName()+")";
	}

}
