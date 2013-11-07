package zeroxff;

import zeroxff.pass.DeDuplicate;
import zeroxff.pass.DeMultiplier;
import zeroxff.rebuild.Rebuilder;
import zeroxff.rebuild.nodes.ClassNode;

public class Deobber {

	private final Input input;

	public Deobber(Input input) {
		this.input = input;
	}

	public Context execute() {
		Context ctx = new Context();
		ctx.setInput(input.getClasses());
		Rebuilder builder = new Rebuilder();
		for (String name : ctx.getInput().keySet()) {
			byte[] classBytes = ctx.getClassBytes(name);
			ClassNode cNode = builder.build(ctx, classBytes);
			ctx.addClass(name, cNode);
		}
		DeMultiplier demul = new DeMultiplier(ctx);

		new DeDuplicate(ctx).execute();
		demul.execute();

		ctx.setMultipliers(demul.multipliers);

		return ctx;
	}

}
