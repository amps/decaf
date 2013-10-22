package deobber.process;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public interface Visitor {

	public void visitMethod(ClassNode classNode, MethodNode methodNode);
	
}
