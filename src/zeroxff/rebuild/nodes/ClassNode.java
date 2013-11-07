package zeroxff.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import zeroxff.Context;
import zeroxff.rebuild.Mod;
import zeroxff.rebuild.nodes.ConstantPool.ClassName;
import zeroxff.rebuild.nodes.attributes.Attribute;
import zeroxff.rebuild.nodes.visitors.StructureVisitor;
import lib.Args;
import lib.addons.Arrays;

public class ClassNode extends TreeNode {

	public static ClassNode construct(Context ctx, ByteBuffer buffer) {
		buffer.getInt();
		buffer.getShort();
		buffer.getShort();
		short constantPoolCount = buffer.getShort();
		ConstantPool constantPool = ConstantPool.construct(ctx,
				constantPoolCount, buffer);
		buffer.getShort();
		short this_class = buffer.getShort();
		short super_class = buffer.getShort();
		short interfaces_count = buffer.getShort();
		List<Class<?>> interfaces = new ArrayList<>();
		for (int i = 0; i < interfaces_count; i++) {
			short iface = buffer.getShort();
			String name = ((ClassName) constantPool.get(iface)).getName()
					.replaceAll("/", ".");
			try {
				interfaces.add(ctx.loadClass(name));
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		String name = ((ClassName) constantPool.get(this_class)).getName()
				.replaceAll("/", ".");
		String superName = ((ClassName) constantPool.get(super_class))
				.getName().replaceAll("/", ".");
		ClassNode cNode = new ClassNode(ctx, name, superName, constantPool);
		cNode.interfaces.addAll(interfaces);
		short fields_count = buffer.getShort();

		for (int i = 0; i < fields_count; i++) {
			FieldNode fNode = FieldNode.construct(ctx, cNode, constantPool,
					buffer);
			cNode.addFieldNode(fNode);
		}
		short methods_count = buffer.getShort();
		for (int i = 0; i < methods_count; i++) {
			MethodNode mNode = MethodNode.construct(ctx, cNode, constantPool,
					buffer);
			cNode.addMethodNode(mNode);
		}

		short attr_count = buffer.getShort();
		for (int i = 0; i < attr_count; i++) {
			Attribute attr = Attribute.construct(cNode, constantPool, buffer);
			cNode.addAttribute(attr);
		}
		return cNode;
	}

	public final ConstantPool constantPool;
	final Context ctx;
	public final List<MethodNode> methods = new ArrayList<>();
	public final List<FieldNode> fields = new ArrayList<>();
	public final String name;
	public final String superName;
	public Class<?> superClass;
	public final List<Class<?>> interfaces = new ArrayList<>();
	public final ClassNode superNode;

	public final Class<?> nodeClass;

	public ClassNode(Context ctx, String name, String superName,
			ConstantPool pool) {
		this.name = name;
		Class<?> tmpNodeClass = null;
		try {
			tmpNodeClass = ctx.loadClass(name);
		} catch (ClassNotFoundException e) {

		}
		nodeClass = tmpNodeClass;
		this.superName = superName;
		try {
			superClass = ctx.loadClass(superName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		superNode = ctx.getMappedClasses().containsKey(superName) ? ctx
				.getClassNode(superClass) : null;
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

	public boolean hasParent(Class<?> parent) {
		Class<?> self = this.nodeClass;
		Class<?> check = self;
		while (check.getSuperclass() != null) {
			if (check.equals(parent) && !check.equals(self)) {
				return true;
			}
			for (Class<?> inter : check.getInterfaces()) {
				if (inter.equals(parent)) {
					return true;
				}
			}
			check = check.getSuperclass();
		}
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<FieldNode> fields(Mod... mods) {
		List<FieldNode> nodes = new ArrayList<>();
		gather(fields, nodes, mods);
		return nodes;

	}

	public List<MethodNode> methods(Mod... mods) {
		List<MethodNode> nodes = new ArrayList<>();
		gather(methods, nodes, mods);
		return nodes;

	}

	public MethodNode method(Mod... mods) {
		return methods(mods).get(0);
	}

	public FieldNode field(Mod... mods) {
		return fields(mods).get(0);
	}

	private <A extends MemberNode> void gather(Iterable<A> from, List<A> nodes,
			Mod... mods) {
		for (A node : from) {
			if (Mod.check(node, mods)) {
				nodes.add(node);
			}
		}
	}

	public boolean hasFieldTypes(Mod mod, Class<?>... clzs) {
		return hasFieldTypes(Args.var(mod), clzs);
	}

	public boolean hasFieldTypes(Mod[] mods, Class<?>... clzs) {
		Set<FieldNode> found = new HashSet<>();
		main: for (Class<?> clz : clzs) {
			List<FieldNode> localFound = fields(Arrays.add(mods,
					Mod.ISCLASS(clz)));
			if (localFound.size() == 0) {
				return false;
			}
			for (FieldNode fNode : localFound) {
				if (!found.contains(fNode)) {
					found.add(fNode);
					continue main;
				}
			}
			return false;
		}
		return true;
	}

	public FieldNode findFirstField(Mod... mods) {
		return fields(mods).get(0);
	}

}
