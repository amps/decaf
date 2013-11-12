package io.amps.decaf.structure.ins;

import io.amps.decaf.ByteUtils;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.Instruction;
import io.amps.decaf.structure.attributes.CodeAttribute;

public class VarInsn extends InsnNode {

	public final int var;

	public VarInsn(final CodeAttribute code, final int line,
			final Instruction opcode, final byte[] args) {
		super(code, line, opcode, args);
		switch (opcode) {
		case istore:
		case dstore:
		case lstore:
		case fstore:
		case astore:
		case iload:
		case dload:
		case lload:
		case fload:
		case aload: {
			var = ByteUtils.toByte(args[0]);
		}
			break;
		case istore_0:
		case dstore_0:
		case lstore_0:
		case fstore_0:
		case astore_0:
		case iload_0:
		case dload_0:
		case lload_0:
		case fload_0:
		case aload_0: {
			var = 0;
		}
			break;
		case istore_1:
		case dstore_1:
		case lstore_1:
		case fstore_1:
		case astore_1:
		case iload_1:
		case dload_1:
		case lload_1:
		case fload_1:
		case aload_1: {
			var = 1;
		}
			break;
		case istore_2:
		case dstore_2:
		case lstore_2:
		case fstore_2:
		case astore_2:
		case iload_2:
		case dload_2:
		case lload_2:
		case fload_2:
		case aload_2: {
			var = 2;
		}
			break;
		case istore_3:
		case dstore_3:
		case lstore_3:
		case fstore_3:
		case astore_3:
		case iload_3:
		case dload_3:
		case lload_3:
		case fload_3:
		case aload_3: {
			var = 3;
		}
			break;
		default: {
			var = -1;
		}
			break;
		}
	}

	public VarInsn(final InsnNode node) {
		this(node.code, node.line, node.opcode, node.args);
	}

	@Override
	public String toString() {
		return super.toString() + "=" + var;
	}

}
