package deobber.rebuild.nodes.visitors;

import deobber.rebuild.nodes.ClassNode;
import deobber.rebuild.nodes.FieldNode;
import deobber.rebuild.nodes.MethodNode;
import deobber.rebuild.nodes.Node;
import deobber.rebuild.nodes.attributes.CodeAttribute;

public interface StructureVisitor extends Visitor {



	public boolean enterClass(ClassNode node);

	public void exitClass(ClassNode node);

	public boolean enterMethod(MethodNode node);

	public void exitMethod(MethodNode node);

	public void enterField(FieldNode node);

	public void exitField(FieldNode node);

}
