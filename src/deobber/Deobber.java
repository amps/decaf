package deobber;

import java.util.Map;

import deobber.pass.ControlFlowPass;
import deobber.pass.HandlerPass;
import deobber.rebuild.Rebuilder;
import deobber.rebuild.nodes.ClassNode;

public class Deobber {

	private final Input input;

	public Deobber(Input input) throws Exception {
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
		new HandlerPass(ctx).execute();
		new ControlFlowPass(ctx).execute();
		return ctx;
	}

}
