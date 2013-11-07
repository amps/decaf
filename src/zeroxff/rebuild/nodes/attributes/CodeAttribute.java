package zeroxff.rebuild.nodes.attributes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import zeroxff.rebuild.nodes.ConstantPool;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.Instruction;
import zeroxff.rebuild.nodes.MethodNode;
import zeroxff.rebuild.nodes.TryCatchNode;
import zeroxff.rebuild.nodes.cfg.ControlFlow;
import zeroxff.rebuild.nodes.visitors.InstructionVisitor;

public class CodeAttribute extends Attribute {

	public static CodeAttribute construct(MethodNode mNode,
			ConstantPool constantPool, ByteBuffer data) {
		data.getShort();
		data.getShort();
		int codeLength = data.getInt();
		byte[] code = new byte[codeLength];

		data.get(code, 0, codeLength);
		ByteBuffer codeBuffer = ByteBuffer.wrap(code);
		InsnList instructions = InsnNode.constructList(codeBuffer);
		short exceptionTableLength = data.getShort();
		List<TryCatchNode> tryCatches = new ArrayList<>();
		for (int i = 0; i < exceptionTableLength; i++) {
			short start = data.getShort();
			short end = data.getShort();
			short handler = data.getShort();
			data.getShort();
			TryCatchNode node = new TryCatchNode(start, end, handler, null);
			tryCatches.add(node);
		}
		return new CodeAttribute(mNode, constantPool, instructions, tryCatches);
	}

	static short createShortByTwoBytes(byte a, byte b) {
		return (short) ((short) (a << 8) | (b & 0x000000FF));
	}

	public final InsnList instructions;
	private List<TryCatchNode> tryCatches;
	private ControlFlow flow;
	public final MethodNode method;

	public CodeAttribute(MethodNode mNode, ConstantPool constantPool) {
		this(mNode, constantPool, new InsnList(), new ArrayList<TryCatchNode>());
	}

	public CodeAttribute(MethodNode mNode, ConstantPool constantPool,
			InsnList instructions, List<TryCatchNode> tryCatches) {
		this.method = mNode;
		this.instructions = instructions;
		for (InsnNode i : instructions) {
			i.code = this;
		}
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

	public void accept(InstructionVisitor visitor) {
		accept(visitor, Instruction.values());
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
		tryCatches.addAll(attr.tryCatches);
		flow = attr.getFlow();
		instructions.addAll(attr.instructions);
	}

	public ControlFlow getFlow() {
		return flow;
	}

}