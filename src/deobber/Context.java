package deobber;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import deobber.rebuild.nodes.ClassNode;

public class Context {

	private Map<String, ClassNode> classes = new HashMap<>();
	private Map<String, byte[]> input = new HashMap<>();
	private ByteClassLoader loader;
	
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
	
	public Class loadClass(String name) throws ClassNotFoundException {
		return loader.loadClass(name);
	}

	

}
