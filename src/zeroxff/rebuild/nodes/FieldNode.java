package zeroxff.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import zeroxff.Context;
import zeroxff.rebuild.Mod;
import zeroxff.rebuild.nodes.attributes.Attribute;
import zeroxff.rebuild.nodes.attributes.ConstantValue;
import zeroxff.rebuild.nodes.ins.FieldInsn;
import zeroxff.rebuild.nodes.visitors.StructureVisitor;

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

	public final ClassNode parent;
	public ConstantValue constant;

	public FieldNode(ClassNode parent, String name, short access, Class<?> desc) {
		super(parent, name, access, desc);
		this.parent = parent;
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
		return parent.name + "." + name + "(" + desc.getSimpleName() + ")";
	}

	public List<MethodNode> getUsages() {
		List<MethodNode> insns = new ArrayList<>();
		for (ClassNode node : parent.ctx.getClasses()) {
			for (MethodNode mNode : node.methods(Mod.INSTANCE)) {
				for (InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						FieldInsn fin = (FieldInsn) iNode;
						if (fin.getField() != null
								&& fin.getField().equals(this)) {
							insns.add(mNode);
							break;
						}
					}
				}
			}

			for (MethodNode mNode : node.methods(Mod.STATIC)) {
				for (InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						FieldInsn fin = (FieldInsn) iNode;
						if (fin.getField() != null
								&& fin.getField().equals(this)) {
							insns.add(mNode);
							break;
						}
					}
				}
			}
		}
		return insns;
	}
}
