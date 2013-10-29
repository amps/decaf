package deobber.rebuild.nodes;

import deobber.rebuild.nodes.cfg.Block;

public class IfCmpStmt extends IfStmtNode {

	public final Expr left, right;

	public IfCmpStmt(Comparison comp, Expr left, Expr right, Block trueBlock,
			Block falseBlock) {
		super(comp, trueBlock, falseBlock);
		this.left = left;
		this.right = right;
	}

}
