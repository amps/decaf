package deobber;

import java.util.Map;

public class ByteClassLoader extends ClassLoader {
	private final Map<String, byte[]> extraClassDefs;

	public ByteClassLoader(Map<String, byte[]> defs) {
		super();
		this.extraClassDefs = defs;
	}

	@Override
	protected Class<?> findClass(final String name)
			throws ClassNotFoundException {
		byte[] classBytes = this.extraClassDefs.get(name);
	
		if (classBytes != null) {
			return defineClass(name, classBytes, 0, classBytes.length);
		}
		return super.findClass(name);
	}

}