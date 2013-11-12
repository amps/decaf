package io.amps.decaf.structure.visitors;

import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.structure.MethodNode;

public interface StructureVisitor extends Visitor {

	public boolean enterClass(ClassNode node);

	public void enterField(FieldNode node);

	public boolean enterMethod(MethodNode node);

	public void exitClass(ClassNode node);

	public void exitField(FieldNode node);

	public void exitMethod(MethodNode node);

}
