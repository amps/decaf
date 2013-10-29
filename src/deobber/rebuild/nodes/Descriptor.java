package deobber.rebuild.nodes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import deobber.Context;

public class Descriptor extends ArrayList<Class<?>> {

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
					active.add(Array.newInstance(Boolean.class, depth)
							.getClass());
				} else {
					active.add(Boolean.class);
				}
				depth = 0;
				pos++;

			}
				break;
			case 'B': {
				if (depth > 0) {
					active.add(Array.newInstance(Byte.class, depth).getClass());
				} else {
					active.add(Byte.class);
				}
				depth = 0;
				pos++;

			}
				break;
			case 'C': {
				if (depth > 0) {
					active.add(Array.newInstance(Character.class, depth)
							.getClass());
				} else {
					active.add(Character.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'S': {
				if (depth > 0) {
					active.add(Array.newInstance(Short.class, depth).getClass());
				} else {
					active.add(Short.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'I': {
				if (depth > 0) {
					active.add(Array.newInstance(Integer.class, depth)
							.getClass());
				} else {
					active.add(Integer.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'J': {
				if (depth > 0) {
					active.add(Array.newInstance(Long.class, depth).getClass());
				} else {
					active.add(Long.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'F': {
				if (depth > 0) {
					active.add(Array.newInstance(Float.class, depth).getClass());
				} else {
					active.add(Float.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'D': {
				if (depth > 0) {
					active.add(Array.newInstance(Double.class, depth)
							.getClass());
				} else {
					active.add(Double.class);
				}
				depth = 0;
				pos++;
			}
				break;
			case 'V': {
				if (depth > 0) {
					active.add(Array.newInstance(Void.class, depth).getClass());
				} else {
					active.add(Void.class);
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
							active.add(Array.newInstance(cl, depth).getClass());
						} else {
							active.add(cl);
						}
					} catch (ClassNotFoundException e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} else {
					try {
						if (depth > 0) {

							active.add(Array.newInstance(Class.forName(name),
									depth).getClass());
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

}
