package deobber.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import deobber.Context;
import deobber.rebuild.nodes.ConstantPool.ClassName;
import deobber.rebuild.nodes.attributes.Attribute;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public class ClassNode extends TreeNode {

	public static ClassNode construct(Context ctx, ByteBuffer buffer) {
		int magic = buffer.getInt();
		short versionMinor = buffer.getShort();
		short versionMajor = buffer.getShort();
		short constantPoolCount = buffer.getShort();
		ConstantPool constantPool = ConstantPool.construct(ctx,
				constantPoolCount, buffer);
		short access_flags = buffer.getShort();
		short this_class = buffer.getShort();
		short super_class = buffer.getShort();
		short interfaces_count = buffer.getShort();
		for (int i = 0; i < interfaces_count; i++) {
			short iface = buffer.getShort();
		}
		String name = ((ClassName) constantPool.get(this_class)).getName()
				.replaceAll("/", ".");
		String superName = ((ClassName) constantPool.get(super_class))
				.getName().replaceAll("/", ".");
		ClassNode cNode = new ClassNode(ctx, name, superName, constantPool);

		short fields_count = buffer.getShort();

		for (int i = 0; i < fields_count; i++) {
			FieldNode fNode = FieldNode.construct(ctx, constantPool, buffer);
			cNode.addFieldNode(fNode);
		}
		short methods_count = buffer.getShort();
		for (int i = 0; i < methods_count; i++) {
			MethodNode mNode = MethodNode.construct(ctx, constantPool, buffer);
			cNode.addMethodNode(mNode);
		}

		short attr_count = buffer.getShort();
		for (int i = 0; i < attr_count; i++) {
			Attribute attr = Attribute.construct(constantPool, buffer);
			cNode.addAttribute(attr);
		}
		return cNode;
	}

	public final ConstantPool constantPool;
	private final Context ctx;
	private final List<MethodNode> methods = new ArrayList<>();
	private final List<FieldNode> fields = new ArrayList<>();
	public final String name;
	public final String superName;
	public Class<?> superClass;

	public ClassNode(Context ctx, String name, String superName, ConstantPool pool) {
		this.name = name;
		this.superName = superName;
		try {
			superClass = ctx.loadClass(superName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.ctx = ctx;
		this.constantPool = pool;
	}


	public void addMethodNode(MethodNode mNode) {
		methods.add(mNode);
	}

	public List<MethodNode> getMethodNodes() {
		return methods;
	}

	public void addFieldNode(FieldNode fNode) {
		fields.add(fNode);
	}

	@Override
	void onAttribute(Attribute attr) {
		// TODO Auto-generated method stub

	}

	public void accept(StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterClass(this);
		for (FieldNode node : fields) {
			node.accept(visitor);
		}
		for (MethodNode node : methods) {
			node.accept(visitor);
		}
		visitor.exitClass(this);
	}

	public List<FieldNode> getFieldNodes() {
		return fields;
	}

}
