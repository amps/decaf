package deobber.process;

import deobber.Context;

public interface Process {

	public Context getContext();

	public void execute();

}
