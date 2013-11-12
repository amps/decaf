package io.amps.decaf.structure.ins;

import io.amps.decaf.ByteUtils;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.attributes.CodeAttribute;

public class LdcInsn<T> extends InsnNode {

	public final T value;

	public final int index;

	@SuppressWarnings("unchecked")
	public LdcInsn(final CodeAttribute code, final int line,
			final Instruction opcode, final byte[] args) {
		super(code, line, opcode, args);
		switch (opcode) {
		case ldc: {
			index = ByteUtils.toByte(args[0]);
		}
			break;
		case ldc2_w:
		case ldc_w: {
			index = ByteUtils.toShort(args[0], args[1]);
		}
			break;
		default:
			index = -1;
			break;
		}
		T v = null;
		try {
			v = index == -1 ? null : (T) code.method.parent.constantPool.get(
					index).get();
		} catch (final Exception e) {
			// TODO fix Tableswitch insn
		}
		value = v;
	}

	public LdcInsn(final InsnNode node) {
		this(node.code, node.line, node.opcode, node.args);
	}

}
