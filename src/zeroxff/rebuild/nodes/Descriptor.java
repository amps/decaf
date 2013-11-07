package zeroxff.rebuild.nodes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import zeroxff.Context;

public class Descriptor extends ArrayList<Class<?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8379585799658533838L;

	public static <T> Class<?> arrayOf(Class<T> clz, int dimensions) {
		return Array.newInstance(clz, new int[dimensions]).getClass();
	}

	public static Descriptor parse(Context ctx, String descriptor) {
		Descriptor desc = new Descriptor();
		int pos = 0;
		char[] d = descriptor.toCharArray();
		List<Class<?>> active = desc;
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
						Class<?> cl = ctx.loadClass(name);
						if (depth > 0) {
							active.add(Array.newInstance(cl, new int[depth])
									.getClass());
						} else {
							active.add(cl);
						}
					} catch (ClassNotFoundException e) {

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

	public boolean is(Class<?>... classes) {
		if (classes.length != size()) {
			return false;
		}
		for (int i = 0; i < classes.length; i++) {
			Class<?> check = classes[i];
			if (!check.equals(get(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean hasArrayDepth(Class<?> clz, int i) {
		int depth = 0;
		Class<?> chk = clz;
		while (chk.isArray()) {
			depth++;
			chk = chk.getComponentType();
		}

		return depth == i;
	}

	public static Class<?> getDeepArray(Class<?> clz, int i) {
		Class<?> chk = clz;
		for(int j = 0; j < i; j++) {
			chk = chk.getComponentType();
		}
		return chk;
	}

}
