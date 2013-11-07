package zeroxff.pass;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zeroxff.Context;
import zeroxff.rebuild.nodes.ClassNode;
import zeroxff.rebuild.nodes.MethodNode;
import zeroxff.rebuild.nodes.TryCatchNode;

//Based off of https://github.com/AlterRS/Deobfuscator/blob/master/src/alterrs/deob/trans/HandlerDeobfuscation.java
public class HandlerPass extends Pass {

	public HandlerPass(Context context) {
		super(context);
	}

	@Override
	public void execute() {
		for (ClassNode node : context.getClasses()) {
			for (MethodNode mn : node.getMethodNodes()) {
				Map<Integer, List<TryCatchNode>> tryCatchPositions = new HashMap<>();
				for (TryCatchNode tc : mn.getTryCatchNodes()) {

					int handlerPos = tc.getHandler();
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

						int handler = tryCatches.get(0).getHandler();

						Set<Class<?>> types = new HashSet<Class<?>>();
						for (TryCatchNode tc : tryCatches) {
							types.add(tc.getType());
						}

						for (TryCatchNode tc : tryCatches) {
							int startPos_ = tc.getStart();
							if (startPos_ < startPos) {
								startPos = startPos_;
							}

							int endPos_ = tc.getEnd();
							if (endPos_ > endPos) {
								endPos = endPos_;
							}

							mn.getTryCatchNodes().remove(tc);
						}

						for (Class<?> type : types) {
							mn.addTryCatch(new TryCatchNode(startPos, endPos,
									handler, type));
						}
					}

				}
			}

		}

	}
}
