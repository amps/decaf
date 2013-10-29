package deobber.rebuild.nodes;

import java.nio.ByteBuffer;

import deobber.Context;
import deobber.rebuild.nodes.attributes.Attribute;
import deobber.rebuild.nodes.attributes.ConstantValue;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public class FieldNode extends TreeNode {

	public static FieldNode construct(Context ctx, ConstantPool constantPool,
			ByteBuffer buffer) {

		short accessFlags = buffer.getShort();
		short nameIndex = buffer.getShort();
		short descIndex = buffer.getShort();
		String name = constantPool.getString(nameIndex).replaceAll("/", ".");
		String descStr = constantPool.getString(descIndex).replaceAll("/", ".");
		Class<?> desc = Descriptor.parse(ctx, descStr).get(0);

		FieldNode node = new FieldNode(name, accessFlags, desc);
		short attrCount = buffer.getShort();
		for (int j = 0; j < attrCount; j++) {
			Attribute attr = Attribute.construct(constantPool, buffer);
			node.addAttribute(attr);
		}
		return node;
	}

	public final String name;
	public final short access;
	public final Class<?> desc;
	public ConstantValue constant;

	public FieldNode(String name, short access, Class<?> desc) {
		this.name = name;
		this.access = access;
		this.desc = desc;
	}

	@Override
	void onAttribute(Attribute attr) {
		if (attr instanceof ConstantValue) {
			constant = (ConstantValue) attr;
		}
	}

	public void accept(StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterField(this);
		visitor.exitField(this);
	}

	public boolean hasAccess(int type) {
		return (access & type) == type;
	}

}
