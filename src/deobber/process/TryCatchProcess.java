package deobber.process;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import deobber.Context;

public class TryCatchProcess extends PhaseOneProcess implements Visitor {

	public TryCatchProcess(Context context, Map<String, ClassNode> classes) {
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
		
		Map<Integer, List<TryCatchBlockNode>> tryCatchPositions = new HashMap<>();
		for (Object tco : methodNode.tryCatchBlocks) {
			TryCatchBlockNode tc = (TryCatchBlockNode) tco;

			int handlerPos = methodNode.instructions.indexOf(tc.handler);
			if (!tryCatchPositions.containsKey(handlerPos)) {
				List<TryCatchBlockNode> handlers = new LinkedList<>();
				handlers.add(tc);
				tryCatchPositions.put(handlerPos, handlers);
			} else {
				tryCatchPositions.get(handlerPos).add(tc);
			}
		}
		for (Map.Entry<Integer, List<TryCatchBlockNode>> entry : tryCatchPositions
				.entrySet()) {
			List<TryCatchBlockNode> tryCatches = entry.getValue();
			if (tryCatches.size() > 1) {
				int startPos = Integer.MAX_VALUE;
				int endPos = 0;

				LabelNode handler = tryCatches.get(0).handler;

				Set<String> types = new HashSet<String>();
				for (TryCatchBlockNode tc : tryCatches) {
					types.add(tc.type);
				}

				for (TryCatchBlockNode tc : tryCatches) {
					int startPos_ = methodNode.instructions.indexOf(tc.start);
					if (startPos_ < startPos) {
						startPos = startPos_;
					}

					int endPos_ = methodNode.instructions.indexOf(tc.end);
					if (endPos_ > endPos) {
						endPos = endPos_;
					}

					methodNode.tryCatchBlocks.remove(tc);
				}

				for (String type : types) {
					LabelNode start = new LabelNode();
					methodNode.instructions.insert(
							methodNode.instructions.get(startPos), start);
					LabelNode end = new LabelNode();
					methodNode.instructions.insert(
							methodNode.instructions.get(endPos - 1), end);
					methodNode.tryCatchBlocks.add(new TryCatchBlockNode(start,
							end, handler, type));
				}
			}
		}
	}

}
