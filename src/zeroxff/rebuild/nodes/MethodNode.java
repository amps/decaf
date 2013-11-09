package zeroxff.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zeroxff.Context;
import zeroxff.rebuild.Mod;
import zeroxff.rebuild.nodes.attributes.Attribute;
import zeroxff.rebuild.nodes.attributes.CodeAttribute;
import zeroxff.rebuild.nodes.attributes.InsnList;
import zeroxff.rebuild.nodes.cfg.Block;
import zeroxff.rebuild.nodes.cfg.BlockPath;
import zeroxff.rebuild.nodes.cfg.Block.BlockVisitor;
import zeroxff.rebuild.nodes.ins.FieldInsn;
import zeroxff.rebuild.nodes.ins.InsnSearcher;
import zeroxff.rebuild.nodes.ins.InstructionSearchable;
import zeroxff.rebuild.nodes.visitors.StructureVisitor;

public class MethodNode extends MemberNode implements InstructionSearchable {

	public static MethodNode construct(Context ctx, ClassNode parent,
			ConstantPool constantPool, ByteBuffer buffer) {
		short accessFlags = buffer.getShort();
		short nameIndex = buffer.getShort();
		short descIndex = buffer.getShort();
		short attrCount = buffer.getShort();
		String name = constantPool.getString(nameIndex);
		String descStr = constantPool.getString(descIndex).replaceAll("/", ".");
		Descriptor desc = Descriptor.parse(ctx, descStr);
		Descriptor ret = Descriptor.parse(ctx,
				descStr.substring(descStr.indexOf(")") + 1));

		MethodNode node = new MethodNode(parent, name, desc, ret.get(0),
				accessFlags, constantPool);

		for (int j = 0; j < attrCount; j++) {
			Attribute attr = Attribute.construct(node, constantPool, buffer);
			node.addAttribute(attr);
		}
		return node;
	}

	public final CodeAttribute code;
	public final Descriptor params;
	public final ClassNode parent;

	public MethodNode(ClassNode parent, String name, Descriptor params,
			Class<?> ret, short access, ConstantPool constantPool) {
		super(parent, name, access, ret);
		this.parent = parent;
		this.params = params;
		code = new CodeAttribute(this, constantPool);

	}

	@Override
	void onAttribute(Attribute attr) {
		if (attr.getClass().equals(CodeAttribute.class)) {
			code.set((CodeAttribute) attr);
		}
	}

	public List<TryCatchNode> getTryCatchNodes() {
		return code.getTryCatchNodes();
	}

	public void addTryCatch(TryCatchNode tryCatchNode) {
		code.getTryCatchNodes().add(tryCatchNode);
	}

	public InsnNode getInsnAt(int i) {
		return code.instructions.get(i);
	}

	public void accept(StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterMethod(this);

		visitor.exitMethod(this);
	}

	@Override
	public boolean hasAccess(int type) {
		return (access & type) == type;
	}

	@Override
	public String toString() {
		return parent.name + "." + name + "@" + params.toString()
				+ desc.getSimpleName();
	}

	public List<FieldInsn> getUsages() {
		List<FieldInsn> insns = new ArrayList<>();
		for (ClassNode node : parent.ctx.getClasses()) {
			for (MethodNode mNode : node.methods(Mod.INSTANCE)) {
				for (InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						insns.add((FieldInsn) iNode);
					}
				}
			}
			for (MethodNode mNode : node.methods(Mod.STATIC)) {
				for (InsnNode iNode : mNode.code.instructions) {
					if (iNode instanceof FieldInsn) {
						insns.add((FieldInsn) iNode);
					}
				}

			}
		}
		return insns;
	}

	public InsnSearcher search(final Instruction... opcodes) {
		final InsnSearcher searcher = new InsnSearcher(opcodes);
		code.getFlow().new Traverser().traverse(new BlockVisitor() {
			@Override
			public Object visit(Block block, Block parent, BlockPath path) {
				searcher.search(path.allInstructions());
				return null;
			}

		});

		return searcher;
	}

	public Object traverse(BlockVisitor blockVisitor) {
		return code.getFlow().new Traverser().traverse(blockVisitor);
	}

	@Override
	public Iterable<InsnNode> getInstructions() {
		return code.instructions;
	}

	@Override
	public Iterable<InstructionSearchable> getInstructionSets() {
		// TODO Auto-generated method stub
		return null;
	}

}
