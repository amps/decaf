package zeroxff;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import zeroxff.rebuild.nodes.ClassNode;
import zeroxff.rebuild.nodes.FieldNode;

public class Context {

	private Map<String, ClassNode> classes = new HashMap<>();
	private Map<String, byte[]> input = new HashMap<>();
	private ByteClassLoader loader;
	private Map<FieldNode, Integer> multipliers;

	public Context() {

	}

	public void addClass(String name, ClassNode node) {
		classes.put(name, node);
	}

	public Collection<ClassNode> getClasses() {
		return classes.values();
	}

	public Map<String, ClassNode> getMappedClasses() {
		return classes;
	}

	public Set<String> getNames() {
		return input.keySet();
	}

	public void setInput(Map<String, byte[]> input) {
		this.input = input;
		loader = new ByteClassLoader(input);
	}

	public Map<String, byte[]> getInput() {
		return input;
	}

	public byte[] getClassBytes(String name) {
		return input.get(name);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loader.loadClass(name);
	}

	public ClassNode getClassNode(java.lang.Class<?> clazz) {
		return classes.get(clazz.getCanonicalName());
	}

	public void removeClass(ClassNode rem) {
		classes.remove(rem.name);
	}

	public void setMultipliers(Map<FieldNode, Integer> multipliers) {
		this.multipliers = multipliers;
	}

	public int getMultiplier(FieldNode fnode) {
		return multipliers.get(fnode);
	}
	
	public boolean hasMultiplier(FieldNode fnode) {
		return multipliers.containsKey(fnode);
	}

}
