package deobber.pass;

import deobber.Context;

public abstract class Pass {

	protected Context context;
	
	public Pass(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}

	public abstract void execute();

}