package io.amps.decaf.structure;

import io.amps.decaf.Context;
import io.amps.decaf.structure.attributes.Attribute;
import io.amps.decaf.structure.attributes.CodeAttribute;
import io.amps.decaf.structure.cfg.Block;
import io.amps.decaf.structure.cfg.Block.BlockVisitor;
import io.amps.decaf.structure.cfg.BlockPath;
import io.amps.decaf.structure.ins.FieldInsn;
import io.amps.decaf.structure.ins.InstructionSearchable;
import io.amps.decaf.structure.visitors.StructureVisitor;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MethodNode extends MemberNode implements InstructionSearchable {

	public static MethodNode construct(final Context ctx,
			final ClassNode parent, final ConstantPool constantPool,
			final ByteBuffer buffer) {
		final short accessFlags = buffer.getShort();
		final short nameIndex = buffer.getShort();
		final short descIndex = buffer.getShort();
		final short attrCount = buffer.getShort();
		final String name = constantPool.getString(nameIndex);
		final String descStr = constantPool.getString(descIndex).replaceAll(
				"/", ".");
		final Descriptor desc = Descriptor.parse(ctx, descStr);
		final Descriptor ret = Descriptor.parse(ctx,
				descStr.substring(descStr.indexOf(")") + 1));

		final MethodNode node = new MethodNode(parent, name, desc, ret.get(0),
				accessFlags, constantPool);

		for (int j = 0; j < attrCount; j++) {
			final Attribute attr = Attribute.construct(node, constantPool,
					buffer);
			node.addAttribute(attr);
		}
		return node;
	}

	public final CodeAttribute code;
	public final Descriptor params;

	public MethodNode(final ClassNode classNode, final Method method) {
		super(classNode, method.getName(), (short) method.getModifiers(),
				method.getReturnType());
		params = new Descriptor();
		for (final Class<?> param : method.getParameterTypes()) {
			params.add(param);
		}
		code = new CodeAttribute(this, null);

	}

	public MethodNode(final ClassNode parent, final String name,
			final Descriptor params, final Class<?> ret, final short access,
			final ConstantPool constantPool) {
		super(parent, name, access, ret);
		this.params = params;
		code = new CodeAttribute(this, constantPool);

	}

	public void accept(final StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterMethod(this);

		visitor.exitMethod(this);
	}

	public void addTryCatch(final TryCatchNode tryCatchNode) {
		code.getTryCatchNodes().add(tryCatchNode);
	}

	public InsnNode getInsnAt(final int i) {
		return code.instructions.get(i);
	}

	@Override
	public Iterable<InsnNode> getInstructions() {
		return code.instructions;
	}

	public List<TryCatchNode> getTryCatchNodes() {
		return code.getTryCatchNodes();
	}

	public List<FieldInsn> getUsages() {
		final List<FieldInsn> insns = new ArrayList<>();
		for (final ClassNode node : parent.ctx.getClasses()) {
			for (final MethodNode mNode : node.methods(Mod.INSTANCE)) {
				for (final InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						insns.add((FieldInsn) iNode);
					}
				}
			}
			for (final MethodNode mNode : node.methods(Mod.STATIC)) {
				for (final InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						insns.add((FieldInsn) iNode);
					}
				}

			}
		}
		return insns;
	}

	@Override
	public boolean hasAccess(final int type) {
		return (access & type) == type;
	}

	@Override
	void onAttribute(final Attribute attr) {
		if (attr.getClass().equals(CodeAttribute.class)) {
			code.set((CodeAttribute) attr);
		}
	}

	public void printFields(final String name, final int before, final int after) {
		traverse(new BlockVisitor() {
			@Override
			public Object visit(final BlockPath path) {
				for (final Block p : path) {
					p.printFields(name, before, after);
				}
				return null;
			}
		});
	}

	@Override
	public String toString() {
		return parent.name + "." + name + "(" + params.toString() + ")" + "["
				+ desc.getSimpleName() + "]";
	}

	public Object traverse(final BlockVisitor blockVisitor) {
		return code.getFlow().new Traverser().traverse(blockVisitor);
	}
}
