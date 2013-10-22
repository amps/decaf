package deobber;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import deobber.process.ControlFlowProcess;
import deobber.process.TryCatchProcess;

public class Deobber {

	private final Input input;

	public Deobber(Input input) throws Exception {
		this.input = input;
	}

	public void execute() {
		Map<String, byte[]> classesBytes = input.getClasses();
		Map<String, ClassNode> classes = new HashMap<>();
		for (String key : classesBytes.keySet()) {
			byte[] bytes = classesBytes.get(key);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cNode = new ClassNode();
			cr.accept(cNode, 0);
			classes.put(key, cNode);
		}
		if (classes == null) {
			throw new IllegalArgumentException();
		}
		Context context = new Context();
		new TryCatchProcess(context, classes).execute();
		new ControlFlowProcess(context, classes).execute();
	}

}
