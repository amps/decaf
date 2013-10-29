package deobber.rebuild.nodes;

import deobber.rebuild.nodes.visitors.CodeVisitor;

public class StmtNode extends CodeNode {
	public void accept(CodeVisitor visitor) {
		super.accept(visitor);
		visitor.enterStatement(this);
	}
}
