package deobber.rebuild.nodes;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
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

	public ClassNode(Context ctx, String name, String superName,
			ConstantPool pool) {
		this.name = name;
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

	public Class<?> load() {
		try {
			return ctx.loadClass(name);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public boolean hasParent(Class<?> parent) {
		Class<?> self = this.load();
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

	public FieldNode field(String name) {
		// TODO cache
		for (FieldNode node : fields) {
			if (node.name.equals(name)) {
				return node;
			}
		}
		return null;
	}

	public String toString() {
		return name;
	}

	public List<FieldNode> findFields(boolean allowStatic, java.lang.Class<?> clz) {
		List<FieldNode> nodes = new ArrayList<>();
		for (FieldNode fnode : fields) {
			if (fnode.desc == clz && fnode.isStatic() == allowStatic) {
				nodes.add(fnode);
			}
		}
		return nodes;

	}

	public MethodNode method(String methodName) {
		// TODO cache
		for (MethodNode node : methods) {
			if (node.name.equals(methodName)) {
				return node;
			}
		}
		return null;
	}

	private <T extends MemberNode> Iterator<T> nonStatic(final List<T> nodes) {
		return new Iterator<T>() {
			int i = 0;

			@Override
			public boolean hasNext() {
				while (i < nodes.size() && nodes.get(i).isStatic()) {
					i++;
				}
				return i + 1 < nodes.size();
			}

			@Override
			public T next() {
				T val = nodes.get(i);
				i++;
				return val;

			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public Iterator<FieldNode> nonStaticFields() {
		return nonStatic(fields);
	}

	public Iterator<MethodNode> nonStaticMethods() {
		return nonStatic(methods);
	}

	public boolean hasFields(boolean allowStatic, Class<?>... clzs) {
		for (Class<?> clz : clzs) {
			if (findFields(allowStatic, clz).size() == 0) {
				return false;
			}
		}
		return true;
	}

}
