package io.amps.decaf.pass;

import io.amps.decaf.Context;

public abstract class Pass {

	protected Context context;

	public Pass(final Context context) {
		this.context = context;
	}

	public abstract void execute();

	public Context getContext() {
		return context;
	}

}
