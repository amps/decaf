package deobber.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import deobber.Context;
import deobber.rebuild.nodes.attributes.Attribute;
import deobber.rebuild.nodes.attributes.CodeAttribute;
import deobber.rebuild.nodes.attributes.Label;
import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public class MethodNode extends TreeNode {

	public static MethodNode construct(Context ctx, ConstantPool constantPool,
			ByteBuffer buffer) {
		short accessFlags = buffer.getShort();
		short nameIndex = buffer.getShort();
		short descIndex = buffer.getShort();
		short attrCount = buffer.getShort();
		String name = constantPool.getString(nameIndex);
		String descStr = constantPool.getString(descIndex).replaceAll("/", ".");
		Descriptor desc = Descriptor.parse(ctx, descStr);
		Descriptor ret = Descriptor.parse(ctx,
				descStr.substring(descStr.indexOf(")") + 1));

		MethodNode node = new MethodNode(name, desc, ret.get(0), accessFlags, constantPool);

		for (int j = 0; j < attrCount; j++) {
			Attribute attr = Attribute.construct(constantPool, buffer);
			node.addAttribute(attr);
		}
		return node;
	}

	private CodeAttribute code;
	public final String name;
	public final Descriptor params;
	public final Class<?> ret;
	public final short access;

	public MethodNode(String name, Descriptor params, Class<?> ret, short access,
			ConstantPool constantPool) {
		this.name = name;
		this.params = params;
		this.ret = ret;
		this.access = access;
		code = new CodeAttribute(constantPool);
	}

	@Override
	void onAttribute(Attribute attr) {
		if (attr.getClass().equals(CodeAttribute.class)) {
			code = (CodeAttribute) attr;
		}
	}

	public CodeAttribute getCode() {
		return code;
	}

	public List<TryCatchNode> getTryCatchNodes() {
		return code.getTryCatchNodes();
	}

	public void addTryCatch(TryCatchNode tryCatchNode) {
		code.getTryCatchNodes().add(tryCatchNode);
	}

	public InsnNode getInsnAt(int i) {
		return code.getInstructionNodes().get(i);
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

}
