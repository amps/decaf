package deobber.pass;

import deobber.Context;
import deobber.rebuild.nodes.ClassNode;
import deobber.rebuild.nodes.FieldNode;
import deobber.rebuild.nodes.IfStmtNode;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.JumpStmtNode;
import deobber.rebuild.nodes.MethodNode;
import deobber.rebuild.nodes.Node;
import deobber.rebuild.nodes.StmtNode;
import deobber.rebuild.nodes.attributes.CodeAttribute;
import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public class ControlFlowPass extends Pass implements CodeVisitor, StructureVisitor {

	public ControlFlowPass(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		for(ClassNode node : context.getClasses()) {
			node.accept(this);
		}
	}

	@Override
	public boolean enterNode(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exitNode(Node node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enterClass(ClassNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitClass(ClassNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enterMethod(MethodNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitMethod(MethodNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enterCode(CodeAttribute code) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitCode(CodeAttribute code) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enterStatement(StmtNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitStatement(StmtNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enterIfStmt(IfStmtNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitIfStmt(IfStmtNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enterJumpStmt(JumpStmtNode node) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitJumpStmt(JumpStmtNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enterField(FieldNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitField(FieldNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean enterInstruction(InsnNode insnNode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void exitInstruction(InsnNode insnNode) {
		// TODO Auto-generated method stub
		
	}

}
