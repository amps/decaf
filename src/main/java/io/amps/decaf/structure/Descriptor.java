package io.amps.decaf.structure;

import io.amps.decaf.Context;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Descriptor extends ArrayList<Class<?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8379585799658533838L;

	public static <T> Class<?> arrayOf(final Class<T> clz, final int dimensions) {
		if (dimensions == 0) {
			return clz;
		}
		return Array.newInstance(clz, new int[dimensions]).getClass();
	}

	public static Class<?> getDeepArray(final Class<?> clz, final int i) {
		Class<?> chk = clz;
		for (int j = 0; j < i; j++) {
			chk = chk.getComponentType();
		}
		return chk;
	}

	public static boolean hasArrayDepth(final Class<?> clz, final int i) {
		int depth = 0;
		Class<?> chk = clz;
		while (chk.isArray()) {
			depth++;
			chk = chk.getComponentType();
		}

		return depth == i;
	}

	public static Descriptor parse(final Context ctx, final String descriptor) {
		final Descriptor desc = new Descriptor();
		int pos = 0;
		final char[] d = descriptor.toCharArray();
		final List<Class<?>> active = desc;
		int depth = 0;
		while (pos < descriptor.length()) {
			char at = d[pos];

			switch (at) {
			case 'Z': {

				if (depth > 0) {
					active.add(Array.newInstance(boolean.class, new int[depth])
							.getClass());
				} else {
					active.add(boolean.class);
				}
				depth = 0;
				pos++;

			}
				break;
			case 'B': {
				if (depth > 0) {
					active.add(Array.newInstance(byte.class, new int[depth])
							.getClass());
				} else {
					active.add(byte.class);
				}
				depth = 0;
				pos++;

			}
				break;
			case 'C': {
				if (depth > 0) {
					active.add(Array.newInstance(char.class, new int[depth])
							.getClass());
				} else {
					active.add(char.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'S': {
				if (depth > 0) {
					active.add(Array.newInstance(short.class, new int[depth])
							.getClass());
				} else {
					active.add(short.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'I': {
				if (depth > 0) {
					active.add(Array.newInstance(int.class, new int[depth])
							.getClass());
				} else {
					active.add(int.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'J': {
				if (depth > 0) {
					active.add(Array.newInstance(long.class, new int[depth])
							.getClass());
				} else {
					active.add(long.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'F': {
				if (depth > 0) {
					active.add(Array.newInstance(float.class, new int[depth])
							.getClass());
				} else {
					active.add(float.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'D': {
				if (depth > 0) {
					active.add(Array.newInstance(double.class, new int[depth])
							.getClass());
				} else {
					active.add(double.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'V': {
				if (depth > 0) {
					active.add(Array.newInstance(void.class, new int[depth])
							.getClass());
				} else {
					active.add(void.class);
				}
				depth = 0;
				pos++;

			}
				break;
			case 'L': {
				String name = "";
				at = d[++pos];
				while (at != ';') {
					name += at;
					at = d[++pos];
				}
				if (ctx.getNames().contains(name)) {
					try {
						final Class<?> cl = ctx.loadClass(name);
						if (depth > 0) {
							active.add(Array.newInstance(cl, new int[depth])
									.getClass());
						} else {
							active.add(cl);
						}
					} catch (final ClassNotFoundException e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					try {
						name = name.replaceAll("/", ".");
						if (depth > 0) {

							active.add(Array.newInstance(Class.forName(name),
									new int[depth]).getClass());
						} else {
							active.add(Class.forName(name));
						}
					} catch (NegativeArraySizeException
							| ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

				depth = 0;
				pos++;

			}
				break;
			case '[': {

				depth++;
				pos++;
			}
				break;
			case '(':
				depth = 0;
				pos++;
				break;
			case ')':
				pos = Integer.MAX_VALUE;
				break;
			default:
				pos++;
				break;
			}
		}
		return desc;
	}

	public Descriptor() {

	}

	public boolean has(final Class<?>... classes) {
		if (classes.length > size()) {
			return false;
		}
		for (int i = 0; i < classes.length; i++) {
			final Class<?> check = classes[i];
			if (!check.equals(get(i))) {
				return false;
			}
		}
		return true;
	}

	public boolean is(final Class<?>... classes) {
		if (classes.length != size()) {
			return false;
		}
		for (int i = 0; i < classes.length; i++) {
			final Class<?> check = classes[i];
			if (!check.equals(get(i))) {
				return false;
			}
		}
		return true;
	}

}
