package io.amps.decaf.structure;

import io.amps.decaf.structure.ins.FieldInsn;
import io.amps.decaf.structure.ins.IntInsn;
import io.amps.decaf.structure.ins.JumpInsn;
import io.amps.decaf.structure.ins.LdcInsn;
import io.amps.decaf.structure.ins.MethodInsn;
import io.amps.decaf.structure.ins.VarInsn;

public enum Instruction {
	nop((byte) 0x00), aconst_null((byte) 0x01), iconst_m1((byte) 0x02), iconst_0(
			(byte) 0x03), iconst_1((byte) 0x04), iconst_2((byte) 0x05), iconst_3(
			(byte) 0x06), iconst_4((byte) 0x07), iconst_5((byte) 0x08), lconst_0(
			(byte) 0x09), lconst_1((byte) 0x0a), fconst_0((byte) 0x0b), fconst_1(
			(byte) 0x0c), fconst_2((byte) 0x0d), dconst_0((byte) 0x0e), dconst_1(
			(byte) 0x0f), bipush((byte) 0x10), sipush((byte) 0x11), ldc(
			(byte) 0x12), ldc_w((byte) 0x13), ldc2_w((byte) 0x14), iload(
			(byte) 0x15), lload((byte) 0x16), fload((byte) 0x17), dload(
			(byte) 0x18), aload((byte) 0x19), iload_0((byte) 0x1a), iload_1(
			(byte) 0x1b), iload_2((byte) 0x1c), iload_3((byte) 0x1d), lload_0(
			(byte) 0x1e), lload_1((byte) 0x1f), lload_2((byte) 0x20), lload_3(
			(byte) 0x21), fload_0((byte) 0x22), fload_1((byte) 0x23), fload_2(
			(byte) 0x24), fload_3((byte) 0x25), dload_0((byte) 0x26), dload_1(
			(byte) 0x27), dload_2((byte) 0x28), dload_3((byte) 0x29), aload_0(
			(byte) 0x2a), aload_1((byte) 0x2b), aload_2((byte) 0x2c), aload_3(
			(byte) 0x2d), iaload((byte) 0x2e), laload((byte) 0x2f), faload(
			(byte) 0x30), daload((byte) 0x31), aaload((byte) 0x32), baload(
			(byte) 0x33), caload((byte) 0x34), saload((byte) 0x35), istore(
			(byte) 0x36), lstore((byte) 0x37), fstore((byte) 0x38), dstore(
			(byte) 0x39), astore((byte) 0x3a), istore_0((byte) 0x3b), istore_1(
			(byte) 0x3c), istore_2((byte) 0x3d), istore_3((byte) 0x3e), lstore_0(
			(byte) 0x3f), lstore_1((byte) 0x40), lstore_2((byte) 0x41), lstore_3(
			(byte) 0x42), fstore_0((byte) 0x43), fstore_1((byte) 0x44), fstore_2(
			(byte) 0x45), fstore_3((byte) 0x46), dstore_0((byte) 0x47), dstore_1(
			(byte) 0x48), dstore_2((byte) 0x49), dstore_3((byte) 0x4a), astore_0(
			(byte) 0x4b), astore_1((byte) 0x4c), astore_2((byte) 0x4d), astore_3(
			(byte) 0x4e), iastore((byte) 0x4f), lastore((byte) 0x50), fastore(
			(byte) 0x51), dastore((byte) 0x52), aastore((byte) 0x53), bastore(
			(byte) 0x54), castore((byte) 0x55), sastore((byte) 0x56), pop(
			(byte) 0x57), pop2((byte) 0x58), dup((byte) 0x59), dup_x1(
			(byte) 0x5a), dup_x2((byte) 0x5b), dup2((byte) 0x5c), dup2_x1(
			(byte) 0x5d), dup2_x2((byte) 0x5e), swap((byte) 0x5f), iadd(
			(byte) 0x60), ladd((byte) 0x61), fadd((byte) 0x62), dadd(
			(byte) 0x63), isub((byte) 0x64), lsub((byte) 0x65), fsub(
			(byte) 0x66), dsub((byte) 0x67), imul((byte) 0x68), lmul(
			(byte) 0x69), fmul((byte) 0x6a), dmul((byte) 0x6b), idiv(
			(byte) 0x6c), ldiv((byte) 0x6d), fdiv((byte) 0x6e), ddiv(
			(byte) 0x6f), irem((byte) 0x70), lrem((byte) 0x71), frem(
			(byte) 0x72), drem((byte) 0x73), ineg((byte) 0x74), lneg(
			(byte) 0x75), fneg((byte) 0x76), dneg((byte) 0x77), ishl(
			(byte) 0x78), lshl((byte) 0x79), ishr((byte) 0x7a), lshr(
			(byte) 0x7b), iushr((byte) 0x7c), lushr((byte) 0x7d), iand(
			(byte) 0x7e), land((byte) 0x7f), ior((byte) 0x80), lor((byte) 0x81), ixor(
			(byte) 0x82), lxor((byte) 0x83), iinc((byte) 0x84), i2l((byte) 0x85), i2f(
			(byte) 0x86), i2d((byte) 0x87), l2i((byte) 0x88), l2f((byte) 0x89), l2d(
			(byte) 0x8a), f2i((byte) 0x8b), f2l((byte) 0x8c), f2d((byte) 0x8d), d2i(
			(byte) 0x8e), d2l((byte) 0x8f), d2f((byte) 0x90), i2b((byte) 0x91), i2c(
			(byte) 0x92), i2s((byte) 0x93),

	lcmp((byte) 0x94), fcmpl((byte) 0x95), fcmpg((byte) 0x96), dcmpl(
			(byte) 0x97), dcmpg((byte) 0x98), ifeq((byte) 0x99), ifne(
			(byte) 0x9a), iflt((byte) 0x9b), ifge((byte) 0x9c), ifgt(
			(byte) 0x9d), ifle((byte) 0x9e), if_icmpeq((byte) 0x9f), if_icmpne(
			(byte) 0xa0), if_icmplt((byte) 0xa1), if_icmpge((byte) 0xa2), if_icmpgt(
			(byte) 0xa3), if_icmple((byte) 0xa4), if_acmpeq((byte) 0xa5), if_acmpne(
			(byte) 0xa6), goto_((byte) 0xa7), jsr((byte) 0xa8), ret((byte) 0xa9), tableswitch(
			(byte) 0xaa), lookupswitch((byte) 0xab), ireturn((byte) 0xac), lreturn(
			(byte) 0xad), freturn((byte) 0xae), dreturn((byte) 0xaf), areturn(
			(byte) 0xb0), return_((byte) 0xb1), getstatic((byte) 0xb2), putstatic(
			(byte) 0xb3), getfield((byte) 0xb4), putfield((byte) 0xb5), invokevirtual(
			(byte) 0xb6), invokespecial((byte) 0xb7), invokestatic((byte) 0xb8), invokeinterface(
			(byte) 0xb9), invokedynamic((byte) 0xba), new_((byte) 0xbb), newarray(
			(byte) 0xbc), anewarray((byte) 0xbd), arraylength((byte) 0xbe), athrow(
			(byte) 0xbf), checkcast((byte) 0xc0), instanceof_((byte) 0xc1), monitorenter(
			(byte) 0xc2), monitorexit((byte) 0xc3), wide((byte) 0xc4), multianewarray(
			(byte) 0xc5), ifnull((byte) 0xc6), ifnonnull((byte) 0xc7), goto_w(
			(byte) 0xc8), jsr_w((byte) 0xc9),

	breakpoint((byte) 0xca), impdep1((byte) 0xfe), impdep2((byte) 0xff);
	public final byte code;
	public Class<? extends InsnNode> insnType;

	static {
		for (final Instruction code : Instruction.values()) {
			Class<? extends InsnNode> insnType = null;
			if (InstructionSets.VAR_INSNS.contains(code)) {
				insnType = VarInsn.class;
			} else if (InstructionSets.FIELD_INSNS.contains(code)) {
				insnType = FieldInsn.class;
			} else if (InstructionSets.JUMP_INSNS.contains(code)) {
				insnType = JumpInsn.class;
			} else if (InstructionSets.INT_INSNS.contains(code)) {
				insnType = IntInsn.class;
			} else if (InstructionSets.METHOD_INSNS.contains(code)) {
				insnType = MethodInsn.class;
			} else if (InstructionSets.LDC_INSNS.contains(code)) {
				insnType = LdcInsn.class;
			} else {
				insnType = InsnNode.class;
			}
			code.insnType = insnType;
		}
	}

	public static Instruction valueOf(final byte code) {
		for (final Instruction i : values()) {
			if (i.code == code) {
				return i;
			}
		}
		return null;
	}

	private Instruction(final byte code) {
		this.code = code;

	}

}
