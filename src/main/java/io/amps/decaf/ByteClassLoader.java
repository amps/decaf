package io.amps.decaf;

import java.util.Map;

public class ByteClassLoader extends ClassLoader {
	private final Map<String, byte[]> extraClassDefs;

	public ByteClassLoader(final Map<String, byte[]> defs) {
		super();
		extraClassDefs = defs;
	}

	@Override
	protected Class<?> findClass(final String name)
			throws ClassNotFoundException {
		final byte[] classBytes = extraClassDefs.get(name);

		if (classBytes != null) {
			return defineClass(name, classBytes, 0, classBytes.length);
		}
		return super.findClass(name);
	}

}