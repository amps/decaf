package io.amps.decaf.structure.attributes;

import io.amps.decaf.structure.ConstantPool;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.TryCatchNode;
import io.amps.decaf.structure.cfg.ControlFlow;
import io.amps.decaf.structure.visitors.InstructionVisitor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeAttribute extends Attribute {

	public static CodeAttribute construct(final MethodNode mNode,
			final ConstantPool constantPool, final ByteBuffer data) {
		final CodeAttribute codeAttr = new CodeAttribute(mNode, constantPool);
		data.getShort();
		data.getShort();
		final int codeLength = data.getInt();
		final byte[] code = new byte[codeLength];

		data.get(code, 0, codeLength);
		final ByteBuffer codeBuffer = ByteBuffer.wrap(code);
		InsnNode.constructList(codeAttr, codeBuffer);
		final short exceptionTableLength = data.getShort();
		final List<TryCatchNode> tryCatches = codeAttr.tryCatches;
		for (int i = 0; i < exceptionTableLength; i++) {
			final short start = data.getShort();
			final short end = data.getShort();
			final short handler = data.getShort();
			data.getShort();
			final TryCatchNode node = new TryCatchNode(start, end, handler,
					null);
			tryCatches.add(node);
		}
		codeAttr.parse();
		return codeAttr;
	}

	static short createShortByTwoBytes(final byte a, final byte b) {
		return (short) ((short) (a << 8) | b & 0x000000FF);
	}

	public final InsnList instructions = new InsnList();
	private final List<TryCatchNode> tryCatches = new ArrayList<>();
	private ControlFlow flow;
	public final MethodNode method;

	public CodeAttribute(final MethodNode mNode, final ConstantPool constantPool) {
		method = mNode;
		flow = new ControlFlow(this);

	}

	public void accept(final InstructionVisitor visitor) {
		accept(visitor, Instruction.values());
	}

	public void accept(final InstructionVisitor visitor,
			final Instruction... ins) {
		final List<Instruction> insList = Arrays.asList(ins);
		for (final InsnNode node : instructions) {
			if (insList.contains(node.opcode)) {
				node.accept(visitor);
			}
		}
	}

	public ControlFlow getFlow() {
		return flow;
	}

	public List<TryCatchNode> getTryCatchNodes() {
		return tryCatches;
	}

	public int indexOf(final InsnNode node) {
		return instructions.indexOf(node);
	}

	private void parse() {
		flow.parse();
	}

	public void set(final CodeAttribute attr) {
		tryCatches.clear();
		instructions.clear();
		tryCatches.addAll(attr.tryCatches);
		flow = attr.getFlow();
		instructions.addAll(attr.instructions);
	}

}