package deobber.process;

import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import deobber.Context;

public abstract class PhaseOneProcess extends ClassVisitor implements Process {

	protected final Context context;

	protected final Map<String, ClassNode> classes;

	public PhaseOneProcess(Context context, Map<String, ClassNode> classes) {
		super(Opcodes.ASM4);
		this.context = context;
		this.classes = classes;
	}

	@Override
	public Context getContext() {
		return context;
	}

	public void execute() {
		for (String key : classes.keySet()) {
			ClassNode cNode = classes.get(key);
			process(cNode);
		}
	}

	abstract void process(ClassNode node);

}
