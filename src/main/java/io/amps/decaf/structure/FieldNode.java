package io.amps.decaf.structure;

import io.amps.decaf.Context;
import io.amps.decaf.structure.attributes.Attribute;
import io.amps.decaf.structure.attributes.ConstantValue;
import io.amps.decaf.structure.ins.FieldInsn;
import io.amps.decaf.structure.visitors.StructureVisitor;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FieldNode extends MemberNode {

	public static FieldNode construct(final Context ctx,
			final ClassNode parent, final ConstantPool constantPool,
			final ByteBuffer buffer) {

		final short accessFlags = buffer.getShort();
		final short nameIndex = buffer.getShort();
		final short descIndex = buffer.getShort();
		final String name = constantPool.getString(nameIndex).replaceAll("/",
				".");
		final String descStr = constantPool.getString(descIndex).replaceAll(
				"/", ".");
		final Class<?> desc = Descriptor.parse(ctx, descStr).get(0);

		final FieldNode node = new FieldNode(parent, name, accessFlags, desc);
		final short attrCount = buffer.getShort();
		for (int j = 0; j < attrCount; j++) {
			final Attribute attr = Attribute.construct(node, constantPool,
					buffer);
			node.addAttribute(attr);
		}
		return node;
	}

	public ConstantValue constant;

	public FieldNode(final ClassNode classNode, final Field field) {
		super(classNode, field.getName(), (short) field.getModifiers(), field
				.getType());
	}

	public FieldNode(final ClassNode parent, final String name,
			final short access, final Class<?> desc) {
		super(parent, name, access, desc);
	}

	public void accept(final StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterField(this);
		visitor.exitField(this);
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof FieldNode == false) {
			return false;
		}
		final FieldNode b = (FieldNode) o;
		return name.equals(b.name) && access == b.access && desc == b.desc
				&& parent.name.equals(b.parent.name);
	}

	public List<MethodNode> getUsages() {
		return getUsages(parent.ctx.getClasses(), true);
	}

	public List<MethodNode> getUsages(final boolean statics) {
		return getUsages(parent.ctx.getClasses(), statics);
	}

	public List<MethodNode> getUsages(final Collection<ClassNode> nodes,
			final boolean statics) {
		final List<MethodNode> insns = new ArrayList<>();
		for (final ClassNode node : nodes) {
			for (final MethodNode mNode : node.methods(Mod.INSTANCE)) {
				for (final InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						final FieldInsn fin = (FieldInsn) iNode;
						if (fin.getField() != null
								&& fin.getField().equals(this)) {
							insns.add(mNode);
							break;
						}
					}
				}
			}
			if (statics) {
				for (final MethodNode mNode : node.methods(Mod.STATIC)) {
					for (final InsnNode iNode : mNode.code.instructions) {
						if (iNode instanceof FieldInsn) {
							final FieldInsn fin = (FieldInsn) iNode;
							if (fin.getField() != null
									&& fin.getField().equals(this)) {
								insns.add(mNode);
								break;
							}
						}
					}
				}
			}
		}
		return insns;
	}

	@Override
	void onAttribute(final Attribute attr) {
		if (attr instanceof ConstantValue) {
			constant = (ConstantValue) attr;
		}
	}

	@Override
	public String toString() {
		return parent.name + "." + name + "[" + desc.getSimpleName() + "]";
	}
}
