package io.amps.decaf.structure;

import io.amps.decaf.Context;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;

public class ConstantPool {

	public class ClassName extends Constant<Class<?>> {

		private final int ref;

		public ClassName(final int ref) {
			super(int.class);
			this.ref = ref;
		}

		@Override
		public Class<?> get() {
			final String name = getString(ref).replaceAll("/", ".");
			try {
				final Class<?> loaded = ctx.loadClass(name);
				if (loaded != null) {
					return loaded;
				}
				return getClass().getClassLoader().loadClass(name);
			} catch (final ClassNotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
			return null;
		}

		public String getName() {
			return getString(ref);
		}

	}

	public class Constant<T> {
		private final T value;
		public final Class<T> type;

		@SuppressWarnings("unchecked")
		public Constant(final T v) {
			this.value = v;
			type = (Class<T>) value.getClass();
		}

		public T get() {
			return value;
		}

	}

	public class MemberRef {

		private final short classIndex, ntIndex;

		public MemberRef(final short classIndex, final short ntIndex) {
			this.classIndex = classIndex;
			this.ntIndex = ntIndex;
		}

		public Class<?> getClassOwner() {
			return ConstantPool.this.getClass(classIndex);
		}

		public FieldNode getField() {
			final ClassNode cnode = ctx.getClassNode(getClassOwner());
			if (cnode == null) {
				return null;
			}

			ClassNode checkNode = cnode;
			while (checkNode != null) {
				for (final FieldNode fnode : checkNode.fields) {
					if (fnode.name.equals(getNameType().getName())
							&& fnode.desc == getNameType().getType()) {
						return fnode;
					}
				}
				checkNode = ctx.getClassNode(checkNode.superClass);
			}
			return null;
		}

		public MethodNode getMethod() {
			if (getClassOwner() == null) {
				return null;
			}
			ClassNode cnode = ctx.getClassNode(getClassOwner());
			if (cnode == null) {
				cnode = ClassNode.construct(ctx, getClassOwner());
			}

			ClassNode checkNode = cnode;
			while (checkNode != null) {
				for (final MethodNode mnode : checkNode.methods) {
					if (mnode.name.equals(getNameType().getName())
							&& mnode.desc == getNameType().getType()) {
						return mnode;
					}
				}
				checkNode = checkNode.superNode;
			}
			return null;
		}

		public NameType getNameType() {
			return ConstantPool.this.getNameType(ntIndex);
		}

	}

	public class NameType {

		private final short nameIndex, descIndex;

		public NameType(final short name, final short descriptor) {
			nameIndex = name;
			descIndex = descriptor;
		}

		public String getName() {
			return getString(nameIndex);
		}

		public Descriptor getParams() {
			final String descStr = getString(descIndex).replaceAll("/", ".");
			if (descStr.indexOf(")") > -1) {
				return Descriptor.parse(ctx, getString(descIndex));
			}
			return null;
		}

		public Class<?> getType() {
			final String descStr = getString(descIndex).replaceAll("/", ".");
			if (descStr.indexOf(")") > -1) {
				return Descriptor.parse(ctx,
						descStr.substring(descStr.indexOf(")") + 1)).get(0);
			}
			return Descriptor.parse(ctx, getString(descIndex)).get(0);
		}

	}

	public class StringRef extends Constant<String> {

		private final int ref;

		public StringRef(final int ref) {
			super("");
			this.ref = ref;
		}

		@Override
		public String get() {
			return getString(ref);
		}

	}

	public static ConstantPool construct(final Context ctx, final int size,
			final ByteBuffer buffer) {
		final ConstantPool constantPool = new ConstantPool(ctx, size);
		for (int i = 1; i < size; i++) {
			final byte tag = buffer.get();
			switch (tag) {
			case 7: {
				final short nameIndex = buffer.getShort();
				constantPool.addClass(i, nameIndex);
			}
				break;
			case 9: {
				final short classIndex = buffer.getShort();
				final short nameTypeIndex = buffer.getShort();
				constantPool.addFieldRef(i, classIndex, nameTypeIndex);
			}
				break;
			case 10: {
				final short classIndex = buffer.getShort();
				final short nameTypeIndex = buffer.getShort();

				constantPool.addMethodRef(i, classIndex, nameTypeIndex);
			}
				break;
			case 11: {
				final short classIndex = buffer.getShort();
				final short nameTypeIndex = buffer.getShort();

				constantPool
						.addInterfaceMethodRef(i, classIndex, nameTypeIndex);
			}
				break;
			case 8:
				final short strIndex = buffer.getShort();
				constantPool.addStringRef(i, strIndex);
				break;
			case 3: {
				final int bytes = buffer.getInt();
				constantPool.addInt(i, bytes);
			}
				break;
			case 4: {
				final int bytes = buffer.getInt();
				final float form = Float.intBitsToFloat(bytes);
				constantPool.addFloat(i, form);
			}
				break;
			case 5: {
				final int bytesHigh = buffer.getInt();
				i++;
				final int bytesLow = buffer.getInt();
				constantPool
						.addLong(i - 1, ((long) bytesHigh << 32) + bytesLow);
			}
				break;
			case 6: {
				final int bytesHigh = buffer.getInt();
				i++;
				final int bytesLow = buffer.getInt();
				final long l = ((long) bytesHigh << 32) + bytesLow;
				final double val = Double.longBitsToDouble(l);
				constantPool.addDouble(i - 1, val);
			}
				break;
			case 12: {
				final short nameIndex = buffer.getShort();
				final short descIndex = buffer.getShort();
				constantPool.addNameType(i, nameIndex, descIndex);
			}
				break;

			case 1:
				final short len = buffer.getShort();
				final byte[] contents = new byte[len];
				buffer.get(contents, 0, len);
				constantPool.addString(i, new String(contents));
				break;
			case 15:
				buffer.get();
				buffer.getShort();
				break;
			case 16:
				buffer.getShort();
				break;
			case 18:
				buffer.getShort();
				buffer.getShort();
				break;
			}
		}
		return constantPool;
	}

	public final Constant<?>[] pool;
	private final Context ctx;
	public final int size;

	public ConstantPool(final Context ctx, final int size) {
		this.ctx = ctx;
		this.size = size;
		pool = new Constant[size];
		Arrays.fill(pool, new Constant<Integer>(0));
	}

	public void addClass(final int i, final short strIndex) {
		pool[i] = new ClassName(strIndex);
	}

	public void addDouble(final int i, final double val) {
		pool[i] = new Constant<Double>(val);
	}

	public void addFieldRef(final int i, final short classIndex,
			final short nameTypeIndex) {
		pool[i] = new Constant<MemberRef>(new MemberRef(classIndex,
				nameTypeIndex));
	}

	public void addFloat(final int i, final float val) {
		pool[i] = new Constant<Float>(val);
	}

	public void addInt(final int i, final int val) {
		pool[i] = new Constant<Integer>(val);
	}

	public void addInterfaceMethodRef(final int i, final short classIndex,
			final short nameTypeIndex) {

		pool[i] = new Constant<MemberRef>(new MemberRef(classIndex,
				nameTypeIndex));
	}

	public void addLong(final int i, final long val) {
		pool[i] = new Constant<Long>(val);
	}

	public void addMethodRef(final int i, final short classIndex,
			final short nameTypeIndex) {

		pool[i] = new Constant<MemberRef>(new MemberRef(classIndex,
				nameTypeIndex));
	}

	public void addNameType(final int i, final short nameIndex,
			final short descIndex) {
		pool[i] = new Constant<NameType>(new NameType(nameIndex, descIndex));
	}

	public void addString(final int i, final String string) {
		pool[i] = new Constant<String>(string);
	}

	public void addStringRef(final int i, final short strIndex) {
		pool[i] = new StringRef(strIndex);
	}

	public int countRefs(final MemberRef ref) {
		int count = 0;
		for (final Iterator<MemberRef> mrefs = memberRefs(); mrefs.hasNext();) {
			final MemberRef nref = mrefs.next();
			if (nref.equals(ref)) {
				count++;
			}
		}
		return count;
	}

	public Constant<?> get(final int i) {
		return pool[i];
	}

	public Class<?> getClass(final int i) {
		final Object value = pool[i].get();
		if (value instanceof Integer && (Integer) value == 0) {
			return null;
		}
		return (Class<?>) value;
	}

	@SuppressWarnings("unchecked")
	public MemberRef getMemberRef(final int i) {
		return ((Constant<MemberRef>) pool[i]).get();
	}

	@SuppressWarnings("unchecked")
	public NameType getNameType(final int i) {
		return ((Constant<NameType>) pool[i]).get();
	}

	public String getString(final int i) {
		return (String) pool[i].get();
	}

	public boolean has(final int index) {
		return index > -1 && index < pool.length;
	}

	public Iterator<MemberRef> memberRefs() {
		return new Iterator<ConstantPool.MemberRef>() {
			int i = 0;
			boolean accessed = true;

			@Override
			public boolean hasNext() {

				if (accessed) {
					accessed = false;
					while (i < pool.length && get(i).type != MemberRef.class) {
						i++;
					}
					return i < pool.length && get(i).type == MemberRef.class;
				}

				return false;
			}

			@Override
			public MemberRef next() {
				if (!accessed) {
					accessed = true;
					return getMemberRef(i++);
				}
				return null;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
