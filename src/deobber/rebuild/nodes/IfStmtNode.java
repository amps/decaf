package deobber.rebuild.nodes;

import deobber.rebuild.nodes.cfg.Block;
import deobber.rebuild.nodes.visitors.CodeVisitor;

public class IfStmtNode extends JumpStmtNode {

	public enum Comparison {
		EQ, GE, GT, LE, LT, NE
	}

	public final Comparison comparison;
	public final Block trueBlock, falseBlock;

	public IfStmtNode(Comparison comp, Block trueBlock, Block falseBlock) {
		this.trueBlock = trueBlock;
		this.falseBlock = falseBlock;
		this.comparison = comp;
	}

	@Override
	public void accept(CodeVisitor visitor) {
		super.accept(visitor);
		visitor.enterIfStmt(this);
	}
}
