package deobber.process;

import java.util.ListIterator;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodNode;

import deobber.Context;

public class ControlFlowProcess extends PhaseOneProcess implements Visitor {

	public ControlFlowProcess(Context context, Map<String, ClassNode> classes) {
		super(context, classes);
	}

	@Override
	void process(ClassNode node) {
		for (Object m : node.methods) {
			MethodNode mnode = (MethodNode) m;
			visitMethod(node, mnode);
		}
	}

	@Override
	public void visitMethod(ClassNode classNode, MethodNode methodNode) {
		ListIterator insns = methodNode.instructions.iterator();
		while (insns.hasNext()) {
			AbstractInsnNode ins = (AbstractInsnNode) insns.next();
			if (ins.getType() == AbstractInsnNode.JUMP_INSN) {
				JumpInsnNode jmp = (JumpInsnNode) ins;
				int op = jmp.getOpcode();
				if (op == Opcodes.IF_ICMPEQ || op == Opcodes.IF_ICMPGE
						|| op == Opcodes.IF_ICMPGT || op == Opcodes.IF_ICMPLE
						|| op == Opcodes.IF_ICMPLT || op == Opcodes.IF_ICMPNE) {
					visitIfCmpStmt(classNode, methodNode, jmp);

				}
			}
		}
	}

	public void visitIfCmpStmt(ClassNode c, final MethodNode m,
			final JumpInsnNode ins) {
		final Object local;
		final Object ldc;
		if (ins.getPrevious() instanceof LocalExpr) {
			local = (LocalExpr) stmt.left();
			if (stmt.right() instanceof ConstantExpr) {
				ldc = (ConstantExpr) stmt.right();
			} else {
				ldc = null;
			}
			
		} else if (stmt.right() instanceof LocalExpr) {
			local = (LocalExpr) stmt.right();
			if (stmt.left() instanceof ConstantExpr) {
				ldc = (ConstantExpr) stmt.left();
			} else {
				ldc = null;
			}
		} else {
			local = null;
			ldc = null;
		}
	}
}
