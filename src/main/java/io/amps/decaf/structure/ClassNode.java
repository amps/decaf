package io.amps.decaf.structure;

import io.amps.decaf.Context;
import io.amps.decaf.structure.ConstantPool.ClassName;
import io.amps.decaf.structure.attributes.Attribute;
import io.amps.decaf.structure.visitors.StructureVisitor;
import io.amps.lib.Args;
import io.amps.lib.Arrays;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassNode extends TreeNode {
	public static ClassNode construct(final Context ctx, final ByteBuffer buffer) {
		return construct(ctx, buffer, null);
	}

	public static ClassNode construct(final Context ctx,
			final ByteBuffer buffer, final Class<?> clz) {
		buffer.getInt();
		buffer.getShort();
		buffer.getShort();
		final short constantPoolCount = buffer.getShort();
		final ConstantPool constantPool = ConstantPool.construct(ctx,
				constantPoolCount, buffer);
		buffer.getShort();
		final short this_class = buffer.getShort();
		final short super_class = buffer.getShort();
		final short interfaces_count = buffer.getShort();
		final List<Class<?>> interfaces = new ArrayList<>();
		for (int i = 0; i < interfaces_count; i++) {
			final short iface = buffer.getShort();
			final String name = ((ClassName) constantPool.get(iface)).getName()
					.replaceAll("/", ".");
			try {
				interfaces.add(ctx.loadClass(name));
			} catch (final ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		final String name = ((ClassName) constantPool.get(this_class))
				.getName().replaceAll("/", ".");
		String superName = null;

		if (constantPool.get(super_class).get() instanceof Class<?>) {
			superName = ((ClassName) constantPool.get(super_class)).getName()
					.replaceAll("/", ".");

		}
		ClassNode cNode = null;
		if (clz == null) {
			cNode = new ClassNode(ctx, name, superName, constantPool);
		} else {
			cNode = new ClassNode(ctx, clz, constantPool);
		}
		cNode.interfaces.addAll(interfaces);
		final short fields_count = buffer.getShort();

		for (int i = 0; i < fields_count; i++) {
			final FieldNode fNode = FieldNode.construct(ctx, cNode,
					constantPool, buffer);
			cNode.addFieldNode(fNode);
		}
		final short methods_count = buffer.getShort();
		for (int i = 0; i < methods_count; i++) {
			final MethodNode mNode = MethodNode.construct(ctx, cNode,
					constantPool, buffer);
			cNode.addMethodNode(mNode);
		}

		final short attr_count = buffer.getShort();
		for (int i = 0; i < attr_count; i++) {
			try {
				final Attribute attr = Attribute.construct(cNode, constantPool,
						buffer);
				cNode.addAttribute(attr);
			} catch (final Exception e) {
				// TODO e.print
			}
		}
		return cNode;
	}

	public static ClassNode construct(final Context ctx, Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		Class<?> baseClass = clazz;
		int depth = 0;
		while (baseClass.isArray()) {
			baseClass = baseClass.getComponentType();
			depth++;
		}
		if (baseClass.isPrimitive()) {
			if (baseClass == int.class) {
				baseClass = Integer.class;
			} else if (baseClass == char.class) {
				baseClass = Character.class;
			} else if (baseClass == boolean.class) {
				baseClass = Boolean.class;
			} else if (baseClass == byte.class) {
				baseClass = Byte.class;
			} else if (baseClass == short.class) {
				baseClass = Short.class;
			} else if (baseClass == long.class) {
				baseClass = Long.class;
			} else if (baseClass == float.class) {
				baseClass = Float.class;
			} else if (baseClass == double.class) {
				baseClass = Double.class;
			} else if (baseClass == void.class) {
				baseClass = Void.class;
			}

		}
		clazz = baseClass;
		final Class<?> trueClazz = Descriptor.arrayOf(clazz, depth);

		final String name = clazz.isMemberClass() || clazz.isAnonymousClass() ? clazz
				.getName() : clazz.getCanonicalName();
		if (ctx.getInput().containsKey(name)) {
			return construct(ctx, ByteBuffer.wrap(ctx.getInput().get(name)),
					clazz);
		}
		final ClassLoader loader = clazz.getClassLoader() == null ? ctx
				.getClass().getClassLoader() : clazz.getClassLoader();

		final InputStream is = loader.getResourceAsStream(name.replaceAll(
				"\\.", "/") + ".class");
		if (is == null) {
			return null;
		}
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		final byte[] data = new byte[16384];
		try {
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			buffer.flush();
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
		return construct(ctx, ByteBuffer.wrap(buffer.toByteArray()), trueClazz);
	}

	public final ConstantPool constantPool;
	public final Context ctx;
	public final List<MethodNode> methods = new ArrayList<>();
	public final List<FieldNode> fields = new ArrayList<>();
	public final String name;
	public final String superName;
	public Class<?> superClass;
	public final List<Class<?>> interfaces = new ArrayList<>();

	public final ClassNode superNode;

	public final Class<?> nodeClass;

	public ClassNode(final Context ctx, final Class<?> clz,
			final ConstantPool constantPool) {
		superNode = clz == Object.class ? null : construct(ctx,
				clz.getSuperclass());
		superName = superNode == null ? null : superNode.name;
		superClass = clz.getSuperclass();
		nodeClass = clz;
		name = clz.getName();
		this.ctx = ctx;
		this.constantPool = constantPool;
	}

	public ClassNode(final Context ctx, final String name,
			final String superName, final ConstantPool pool) {
		this.name = name;
		Class<?> tmpNodeClass = null;
		try {
			tmpNodeClass = ctx.loadClass(name);
		} catch (final ClassNotFoundException e) {

		}
		nodeClass = tmpNodeClass;
		this.superName = superName;

		try {
			superClass = ctx.loadClass(superName);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}

		superNode = ctx.getMappedClasses().containsKey(superName) ? ctx
				.getClassNode(superClass) : null;
		this.ctx = ctx;
		constantPool = pool;
	}

	public void accept(final StructureVisitor visitor) {
		super.accept(visitor);
		visitor.enterClass(this);
		for (final FieldNode node : fields) {
			node.accept(visitor);
		}
		for (final MethodNode node : methods) {
			node.accept(visitor);
		}
		visitor.exitClass(this);
	}

	public void addFieldNode(final FieldNode fNode) {
		fields.add(fNode);
	}

	public void addMethodNode(final MethodNode mNode) {
		methods.add(mNode);
	}

	public FieldNode field(final Mod... mods) {
		return fields(mods).get(0);
	}

	public List<FieldNode> fields(final Mod... mods) {
		final List<FieldNode> nodes = new ArrayList<>();
		gather(fields, nodes, mods);
		return nodes;

	}

	public FieldNode findFirstField(final Mod... mods) {
		return fields(mods).get(0);
	}

	private <A extends MemberNode> void gather(final Iterable<A> from,
			final List<A> nodes, final Mod... mods) {
		for (final A node : from) {
			if (Mod.check(node, mods)) {
				nodes.add(node);
			}
		}
	}

	public List<MethodNode> getMethodNodes() {
		return methods;
	}

	public boolean hasFieldTypes(final Mod mod, final Class<?>... clzs) {
		return hasFieldTypes(Args.var(mod), clzs);
	}

	public boolean hasFieldTypes(final Mod[] mods, final Class<?>... clzs) {
		final Set<FieldNode> found = new HashSet<>();
		main: for (final Class<?> clz : clzs) {
			final List<FieldNode> localFound = fields(Arrays.add(mods,
					Mod.ISCLASS(clz)));
			if (localFound.size() == 0) {
				return false;
			}
			for (final FieldNode fNode : localFound) {
				if (!found.contains(fNode)) {
					found.add(fNode);
					continue main;
				}
			}
			return false;
		}
		return true;
	}

	public boolean hasParent(final Class<?> parent) {
		final Class<?> self = nodeClass;
		Class<?> check = self;
		while (check != null) {
			if (check == parent && check != self) {
				return true;
			}
			for (final Class<?> inter : check.getInterfaces()) {
				if (inter == parent) {
					return true;
				}
			}
			check = check.getSuperclass();
		}
		return false;
	}

	public MethodNode method(final Mod... mods) {
		return methods(mods).get(0);
	}

	public List<MethodNode> methods(final Mod... mods) {
		final List<MethodNode> nodes = new ArrayList<>();
		gather(methods, nodes, mods);
		return nodes;

	}

	@Override
	void onAttribute(final Attribute attr) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return name;
	}

	/*
	 * public static ClassNode construct(Context ctx, Class<?> clazz) { if
	 * (clazz == null) { return null; } ClassNode classNode = new ClassNode(ctx,
	 * clazz, null); for (Field field : clazz.getDeclaredFields()) {
	 * classNode.fields.add(new FieldNode(classNode, field)); } for (Method
	 * method : clazz.getDeclaredMethods()) { classNode.methods.add(new
	 * MethodNode(classNode, method)); } return classNode; }
	 */
}
