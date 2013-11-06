package deobber.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import deobber.Context;
import deobber.rebuild.Constants;
import deobber.rebuild.nodes.attributes.Attribute;
import deobber.rebuild.nodes.attributes.CodeAttribute;
import deobber.rebuild.nodes.attributes.InsnList;
import deobber.rebuild.nodes.attributes.Label;
import deobber.rebuild.nodes.cfg.Block;
import deobber.rebuild.nodes.cfg.Block.BlockVisitor;
import deobber.rebuild.nodes.cfg.BlockPath;
import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public class MethodNode extends MemberNode {

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
	public final Class<?> ret;
	public final ClassNode parent;

	public MethodNode(ClassNode parent, String name, Descriptor params,
			Class<?> ret, short access, ConstantPool constantPool) {
		super(parent, name, access);
		this.parent = parent;
		this.params = params;
		this.ret = ret;
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
		if (visitor instanceof CodeVisitor) {
			CodeVisitor cvisitor = (CodeVisitor) visitor;
			code.accept(cvisitor);
		}
		visitor.exitMethod(this);
	}

	public Label getLabel(int i) {
		return code.getLabel(i);
	}

	public boolean hasAccess(int type) {
		return (access & type) == type;
	}

	public String toString() {
		return parent.name + "." + name + "@" + params.toString()
				+ ret.getSimpleName();
	}

	public boolean isStatic() {
		return hasAccess(Constants.ACC_STATIC);
	}

	public List<InsnList> search(final Instruction... opcodes) {
		final List<InsnList> foundTotal = new ArrayList<>();
		code.getFlow().new Traverser().traverse(new BlockVisitor() {
			@Override
			public Object visit(Block block, Block parent, BlockPath path) {
				int position = 0;
				InsnList found = null;
				for (Iterator<InsnNode> inIt = path.allInstructions(); inIt
						.hasNext();) {
					InsnNode iNode = inIt.next();

					Instruction opcode = iNode.opcode;

					if (opcodes[position] == null
							|| opcode == opcodes[position]) {
						if (position == 0) {
							found = new InsnList();
						}
						found.add(iNode);
						position++;
						if (position == opcodes.length) {

							foundTotal.add(found);
							position = 0;
						}
					} else {
						position = 0;

					}
				}
				return null;
			}

		});
		return foundTotal;
	}
}
