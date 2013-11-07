package zeroxff.rebuild.nodes.visitors;

import zeroxff.rebuild.nodes.ClassNode;
import zeroxff.rebuild.nodes.FieldNode;
import zeroxff.rebuild.nodes.MethodNode;

public interface StructureVisitor extends Visitor {



	public boolean enterClass(ClassNode node);

	public void exitClass(ClassNode node);

	public boolean enterMethod(MethodNode node);

	public void exitMethod(MethodNode node);

	public void enterField(FieldNode node);

	public void exitField(FieldNode node);

}
