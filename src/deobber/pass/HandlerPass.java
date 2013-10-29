package deobber.pass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deobber.Context;
import deobber.rebuild.nodes.ClassNode;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.MethodNode;
import deobber.rebuild.nodes.TryCatchNode;
import deobber.rebuild.nodes.Type;
import deobber.rebuild.nodes.attributes.Label;

public class HandlerPass extends Pass {

	public HandlerPass(Context context) {
		super(context);
	}

	public void execute() {
		for (ClassNode node : context.getClasses()) {
			for (MethodNode mn : node.getMethodNodes()) {
				Map<Integer, List<TryCatchNode>> tryCatchPositions = new HashMap<>();
				for (TryCatchNode tc : mn.getTryCatchNodes()) {

					int handlerPos = mn.getCode().indexOf(tc.getHandler());
					if (!tryCatchPositions.containsKey(handlerPos)) {
						List<TryCatchNode> handlers = new LinkedList<>();
						handlers.add(tc);

						tryCatchPositions.put(handlerPos, handlers);
					} else {
						tryCatchPositions.get(handlerPos).add(tc);
					}
				}
				for (Map.Entry<Integer, List<TryCatchNode>> entry : tryCatchPositions
						.entrySet()) {
					List<TryCatchNode> tryCatches = entry.getValue();
					if (tryCatches.size() > 1) {
						int startPos = Integer.MAX_VALUE;
						int endPos = 0;

						Label handler = tryCatches.get(0).getHandler();

						Set<Type> types = new HashSet<Type>();
						for (TryCatchNode tc : tryCatches) {
							types.add(tc.getType());
						}

						for (TryCatchNode tc : tryCatches) {
							int startPos_ = mn.getCode().indexOf(tc.getStart());
							if (startPos_ < startPos) {
								startPos = startPos_;
							}

							int endPos_ = mn.getCode().indexOf(tc.getEnd());
							if (endPos_ > endPos) {
								endPos = endPos_;
							}

							mn.getTryCatchNodes().remove(tc);
						}

						for (Type type : types) {
							mn.addTryCatch(new TryCatchNode(mn
									.getLabel(startPos), mn.getLabel(endPos),
									handler, type));
						}
					}

				}
			}

		}

	}
}
