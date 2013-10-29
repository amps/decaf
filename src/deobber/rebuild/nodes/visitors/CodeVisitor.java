package deobber.rebuild.nodes.visitors;

import deobber.rebuild.nodes.IfStmtNode;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.JumpStmtNode;
import deobber.rebuild.nodes.StmtNode;
import deobber.rebuild.nodes.attributes.CodeAttribute;

public interface CodeVisitor extends Visitor {
	public boolean enterCode(CodeAttribute code);

	public void exitCode(CodeAttribute code);

	public boolean enterStatement(StmtNode node);

	public void exitStatement(StmtNode node);

	public boolean enterIfStmt(IfStmtNode node);

	public void exitIfStmt(IfStmtNode node);

	public boolean enterJumpStmt(JumpStmtNode node);

	public void exitJumpStmt(JumpStmtNode node);

	public boolean enterInstruction(InsnNode insnNode);
	public void exitInstruction(InsnNode insnNode);
}
