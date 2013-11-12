package io.amps.decaf.pass;

import io.amps.decaf.Context;
import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.TryCatchNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Based off of https://github.com/AlterRS/Deobfuscator/blob/master/src/alterrs/deob/trans/HandlerDeobfuscation.java
public class HandlerPass extends Pass {

	public HandlerPass(final Context context) {
		super(context);
	}

	@Override
	public void execute() {
		for (final ClassNode node : context.getClasses()) {
			for (final MethodNode mn : node.getMethodNodes()) {
				final Map<Integer, List<TryCatchNode>> tryCatchPositions = new HashMap<>();
				for (final TryCatchNode tc : mn.getTryCatchNodes()) {

					final int handlerPos = tc.getHandler();
					if (!tryCatchPositions.containsKey(handlerPos)) {
						final List<TryCatchNode> handlers = new LinkedList<>();
						handlers.add(tc);

						tryCatchPositions.put(handlerPos, handlers);
					} else {
						tryCatchPositions.get(handlerPos).add(tc);
					}
				}
				for (final Map.Entry<Integer, List<TryCatchNode>> entry : tryCatchPositions
						.entrySet()) {
					final List<TryCatchNode> tryCatches = entry.getValue();
					if (tryCatches.size() > 1) {
						int startPos = Integer.MAX_VALUE;
						int endPos = 0;

						final int handler = tryCatches.get(0).getHandler();

						final Set<Class<?>> types = new HashSet<Class<?>>();
						for (final TryCatchNode tc : tryCatches) {
							types.add(tc.getType());
						}

						for (final TryCatchNode tc : tryCatches) {
							final int startPos_ = tc.getStart();
							if (startPos_ < startPos) {
								startPos = startPos_;
							}

							final int endPos_ = tc.getEnd();
							if (endPos_ > endPos) {
								endPos = endPos_;
							}

							mn.getTryCatchNodes().remove(tc);
						}

						for (final Class<?> type : types) {
							mn.addTryCatch(new TryCatchNode(startPos, endPos,
									handler, type));
						}
					}

				}
			}

		}

	}
}
