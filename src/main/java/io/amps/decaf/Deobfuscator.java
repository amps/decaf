package io.amps.decaf;

import io.amps.decaf.pass.DeDuplicate;
import io.amps.decaf.pass.DeMultiplier;
import io.amps.decaf.structure.ClassNode;

import java.nio.ByteBuffer;

public class Deobfuscator {

	private final ByteClassInput input;

	public Deobfuscator(final ByteClassInput input) {
		this.input = input;
	}

	private ClassNode build(final Context ctx, final byte[] data) {
		final ByteBuffer buffer = ByteBuffer.wrap(data);
		final ClassNode node = ClassNode.construct(ctx, buffer);
		return node;
	}

	public Context execute() {
		final Context ctx = new Context();
		ctx.setInput(input.getClasses());

		for (final String name : ctx.getInput().keySet()) {
			final byte[] classBytes = ctx.getClassBytes(name);
			final ClassNode cNode = build(ctx, classBytes);
			ctx.addClass(name, cNode);
		}
		final DeMultiplier demul = new DeMultiplier(ctx);

		new DeDuplicate(ctx).execute();
		demul.execute();

		ctx.setMultipliers(demul.multipliers);

		return ctx;
	}
}
