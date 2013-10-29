package deobber.rebuild.nodes;

import java.nio.ByteBuffer;
import java.util.Arrays;

import deobber.Context;

public class ConstantPool {

	public static ConstantPool construct(Context ctx, int size, ByteBuffer buffer) {
		ConstantPool constantPool = new ConstantPool(ctx, size);
		for (int i = 1; i < size; i++) {
			byte tag = buffer.get();
			switch (tag) {
			case 7: {
				short nameIndex = buffer.getShort();
				constantPool.addClass(i, nameIndex);
			}
				break;
			case 9: {
				short classIndex = buffer.getShort();
				short nameTypeIndex = buffer.getShort();
				constantPool.addFieldRef(i, classIndex, nameTypeIndex);
			}
				break;
			case 10: {
				short classIndex = buffer.getShort();
				short nameTypeIndex = buffer.getShort();

				constantPool.addMethodRef(i, classIndex, nameTypeIndex);
			}
				break;
			case 11: {
				short classIndex = buffer.getShort();
				short nameTypeIndex = buffer.getShort();

				constantPool
						.addInterfaceMethodRef(i, classIndex, nameTypeIndex);
			}
				break;
			case 8:
				short strIndex = buffer.getShort();
				constantPool.addStringRef(i, strIndex);
				break;
			case 3: {
				int bytes = buffer.getInt();
				constantPool.addInt(i, bytes);
			}
				break;
			case 4: {
				int bytes = buffer.getInt();
				float form = Float.intBitsToFloat(bytes);
				constantPool.addFloat(i, form);
			}
				break;
			case 5: {
				int bytesHigh = buffer.getInt();
				i++;
				int bytesLow = buffer.getInt();
				constantPool.addLong(i, ((long) bytesHigh << 32) + bytesLow);
			}
				break;
			case 6: {
				int bytesHigh = buffer.getInt();
				i++;
				int bytesLow = buffer.getInt();
				long l = ((long) bytesHigh << 32) + bytesLow;
				double val = Double.longBitsToDouble(l);
				constantPool.addDouble(i, val);
			}
				break;
			case 12: {
				short nameIndex = buffer.getShort();
				short descIndex = buffer.getShort();
				constantPool.addNameType(i, nameIndex, descIndex);
			}
				break;

			case 1:
				short len = buffer.getShort();
				byte[] contents = new byte[len];
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

	public class Constant<T> {
		private final T value;
		public final Class<T> type;

		public Constant(T v) {
			this.value = v;
			type = (Class<T>) value.getClass();
		}

		public T get() {
			return value;
		}

	}

	public class StringRef extends Constant<String> {

		private int ref;

		public StringRef(int ref) {
			super("");
			this.ref = ref;
		}

		@Override
		public String get() {
			return getString(ref);
		}

	}

	public class ClassName extends Constant<Type> {

		private int ref;

		public ClassName(int ref) {
			super(Type.NONSPEC);
			this.ref = ref;
		}

		@Override
		public Type get() {
			return Type.get(getString(ref));
		}
		
		public String getName() {
			return getString(ref);
		}

	}

	public class NameType {

		private final short nameIndex, descIndex;

		public NameType(short name, short descriptor) {
			this.nameIndex = name;
			this.descIndex = descriptor;
		}

		public String getName() {
			return getString(nameIndex);
		}

		public Descriptor getDescriptor() {
			return Descriptor.parse(ctx, getString(descIndex));
		}

		public Type getType() {
			Descriptor descriptor = getDescriptor();
			return Type.fromDescriptor(descriptor);
		}

	}

	public class MemberRef {

		private final short classIndex, ntIndex;

		public MemberRef(short classIndex, short ntIndex) {
			this.classIndex = classIndex;
			this.ntIndex = ntIndex;
		}

		public Type getClassOwner() {
			return ConstantPool.this.getClass(classIndex);
		}

		public NameType getNameType() {
			return ConstantPool.this.getNameType(ntIndex);
		}

	}

	private Constant<?>[] pool;
	private final Context ctx;
	public final int size;

	public ConstantPool(Context ctx, int size) {
		this.ctx = ctx;
		this.size = size;
		pool = new Constant[size];
		Arrays.fill(pool, new Constant<Integer>(0));
	}

	public void addString(int i, String string) {
		pool[i] = new Constant<String>(string);
	}

	public void addFieldRef(int i, short classIndex, short nameTypeIndex) {
		pool[i] = new Constant<MemberRef>(new MemberRef(classIndex,
				nameTypeIndex));
	}

	public void addMethodRef(int i, short classIndex, short nameTypeIndex) {

		pool[i] = new Constant<MemberRef>(new MemberRef(classIndex,
				nameTypeIndex));
	}

	public void addInterfaceMethodRef(int i, short classIndex,
			short nameTypeIndex) {

		pool[i] = new Constant<MemberRef>(new MemberRef(classIndex,
				nameTypeIndex));
	}

	public String getString(int i) {
		return (String) pool[i].value;
	}

	public Constant<?> get(int i) {
		return pool[i];
	}

	public boolean has(int index) {
		return index > -1 && index < pool.length;
	}

	public void addInt(int i, int val) {
		pool[i] = new Constant<Integer>(val);
	}

	public void addNameType(int i, short nameIndex, short descIndex) {
		pool[i] = new Constant<NameType>(new NameType(nameIndex, descIndex));
	}

	public void addFloat(int i, float val) {
		pool[i] = new Constant<Float>(val);
	}

	public void addLong(int i, long val) {
		pool[i] = new Constant<Long>(val);
	}

	public void addDouble(int i, double val) {
		pool[i] = new Constant<Double>(val);
	}

	public void addStringRef(int i, short strIndex) {
		pool[i] = new StringRef(strIndex);
	}

	public void addClass(int i, short strIndex) {
		pool[i] = new ClassName(strIndex);
	}

	public NameType getNameType(int i) {
		return ((Constant<NameType>) pool[i]).value;
	}
	
	public MemberRef getMemberRef(int i) {
		return ((Constant<MemberRef>)pool[i]).value;
	}

	public Type getClass(int i) {
		Object value = pool[i].value;
		if (value instanceof Integer && (Integer) value == 0) {
			return Type.NONSPEC;
		}
		return (Type) value;
	}

}
