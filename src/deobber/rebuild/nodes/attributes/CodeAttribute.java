package deobber.rebuild.nodes.attributes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deobber.rebuild.nodes.CodeNode;
import deobber.rebuild.nodes.ConstantPool;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.Instruction;
import deobber.rebuild.nodes.MethodNode;
import deobber.rebuild.nodes.TryCatchNode;
import deobber.rebuild.nodes.cfg.ControlFlow;
import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.InstructionVisitor;

public class CodeAttribute extends Attribute {

	public static CodeAttribute construct(MethodNode mNode,
			ConstantPool constantPool, ByteBuffer data) {
		short maxStack = data.getShort();
		short maxLocals = data.getShort();
		int codeLength = data.getInt();
		byte[] code = new byte[codeLength];

		data.get(code, 0, codeLength);
		ByteBuffer codeBuffer = ByteBuffer.wrap(code);
		List<Label> labels = Label.constructList(code);
		InsnList instructions = InsnNode.constructList(codeBuffer);
		short exceptionTableLength = data.getShort();
		List<TryCatchNode> tryCatches = new ArrayList<>();
		for (int i = 0; i < exceptionTableLength; i++) {
			short start = data.getShort();
			short end = data.getShort();
			short handler = data.getShort();
			short type = data.getShort();
			TryCatchNode node = new TryCatchNode(labels.get(start),
					labels.get(end), labels.get(handler), null);
			tryCatches.add(node);
		}
		return new CodeAttribute(mNode, constantPool, labels, instructions,
				tryCatches);
	}

	static short createShortByTwoBytes(byte a, byte b) {
		return (short) ((short) ((short) a << 8) | ((short) b & 0x000000FF));
	}

	public final InsnList instructions;
	private List<CodeNode> code;
	private List<Label> labels;
	private List<TryCatchNode> tryCatches;
	private final ConstantPool constantPool;

	private ControlFlow flow;
	public final MethodNode method;

	public CodeAttribute(MethodNode mNode, ConstantPool constantPool) {
		this(mNode, constantPool, new ArrayList<Label>(), new InsnList(),
				new ArrayList<TryCatchNode>());
	}

	public CodeAttribute(MethodNode mNode, ConstantPool constantPool,
			List<Label> labels, InsnList instructions,
			List<TryCatchNode> tryCatches) {
		this.method = mNode;
		this.constantPool = constantPool;
		this.instructions = instructions;
		for (InsnNode i : instructions) {
			i.code = this;
		}
		this.labels = labels;
		code = new ArrayList<>();
		this.tryCatches = tryCatches;
		flow = new ControlFlow(this);
		parse();
	}

	private void parse() {
		getFlow().parse();
	}

	public void setInstructionNodes(List<InsnNode> nodes) {
		instructions.clear();
		instructions.addAll(nodes);
	}

	public List<TryCatchNode> getTryCatchNodes() {
		return tryCatches;
	}

	public int indexOf(InsnNode node) {
		return instructions.indexOf(node);
	}

	public void accept(CodeVisitor visitor) {
		visitor.enterCode(this);
		for (InsnNode node : instructions) {
			node.accept(visitor);
		}
		for (TryCatchNode node : tryCatches) {
			node.accept(visitor);
		}
		visitor.exitCode(this);
	}

	public void accept(InstructionVisitor visitor) {
		accept(visitor, Instruction.values());
	}

	public Label getLabel(int i) {
		return labels.get(i);
	}

	public int indexOf(Label label) {
		return labels.indexOf(label);
	}

	public void accept(InstructionVisitor visitor, Instruction... ins) {
		List<Instruction> insList = Arrays.asList(ins);
		for (InsnNode node : instructions) {
			if (insList.contains(node.opcode)) {
				node.accept(visitor);
			}
		}
	}

	public void set(CodeAttribute attr) {
		labels.addAll(attr.labels);
		tryCatches.addAll(attr.tryCatches);
		flow = attr.getFlow();
		instructions.addAll(attr.instructions);
	}

	public ControlFlow getFlow() {
		return flow;
	}

}