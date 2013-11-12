package io.amps.decaf;

import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.tree.classtree.ClassHierarchyTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Context {

	private final Map<String, ClassNode> classes = new HashMap<>();
	private Map<String, byte[]> input = new HashMap<>();
	private ByteClassLoader loader;
	private Map<FieldNode, Integer> multipliers;

	private final ClassHierarchyTree classTree = new ClassHierarchyTree(this);
	private final Map<String, Object> map = new HashMap<>();

	public Context() {

	}

	public void addClass(final String name, final ClassNode node) {
		classes.put(name, node);
	}

	public Object get(final String key) {
		return map.get(key);
	}

	public byte[] getClassBytes(final String name) {
		return input.get(name);
	}

	public Collection<ClassNode> getClasses() {
		return classes.values();
	}

	public ClassNode getClassNode(final java.lang.Class<?> clazz) {
		return getClassNode(clazz, false);
	}

	public ClassNode getClassNode(final java.lang.Class<?> clazz,
			final boolean createIfNull) {
		if (classes.containsKey(clazz.getCanonicalName())) {
			return classes.get(clazz.getCanonicalName());
		}
		if (createIfNull) {
			return ClassNode.construct(this, clazz);
		}
		return null;
	}

	public ClassHierarchyTree getClassTree() {
		return classTree;
	}

	public Map<String, byte[]> getInput() {
		return input;
	}

	public Map<String, ClassNode> getMappedClasses() {
		return classes;
	}

	public int getMultiplier(final FieldNode fnode) {
		return multipliers.get(fnode);
	}

	public Set<String> getNames() {
		return input.keySet();
	}

	public boolean hasMultiplier(final FieldNode fnode) {
		return multipliers.containsKey(fnode);
	}

	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (loader == null) {
			return null;
		}
		return loader.loadClass(name);
	}

	public List<Class<?>> loadInput() {
		final List<Class<?>> clzs = new ArrayList<>();
		for (final String name : input.keySet()) {
			try {
				final Class<?> clz = loadClass(name);
				clzs.add(clz);
				classes.put(clz.getCanonicalName(),
						ClassNode.construct(this, clz));
			} catch (final ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return clzs;
	}

	public void removeClass(final ClassNode rem) {
		classes.remove(rem.name);
	}

	public void set(final String key, final Object value) {
		map.put(key, value);
	}

	public void setInput(final Map<String, byte[]> input) {
		this.input = input;
		loader = new ByteClassLoader(input);
	}

	public void setMultipliers(final Map<FieldNode, Integer> multipliers) {
		this.multipliers = multipliers;
	}

}
