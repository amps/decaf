package io.amps.decaf.structure.visitors;

import io.amps.decaf.structure.InsnNode;

public interface InstructionVisitor {
	public void visit_aaload(InsnNode i);

	public void visit_aastore(InsnNode i);

	public void visit_aconst_null(InsnNode i);

	public void visit_aload(InsnNode i);

	public void visit_aload_0(InsnNode i);

	public void visit_aload_1(InsnNode i);

	public void visit_aload_2(InsnNode i);

	public void visit_aload_3(InsnNode i);

	public void visit_anewarray(InsnNode i);

	public void visit_areturn(InsnNode i);

	public void visit_arraylength(InsnNode i);

	public void visit_astore(InsnNode i);

	public void visit_astore_0(InsnNode i);

	public void visit_astore_1(InsnNode i);

	public void visit_astore_2(InsnNode i);

	public void visit_astore_3(InsnNode i);

	public void visit_athrow(InsnNode i);

	public void visit_baload(InsnNode i);

	public void visit_bastore(InsnNode i);

	public void visit_bipush(InsnNode i);

	public void visit_breakpoint(InsnNode i);

	public void visit_caload(InsnNode i);

	public void visit_castore(InsnNode i);

	public void visit_checkcast(InsnNode i);

	public void visit_d2f(InsnNode i);

	public void visit_d2i(InsnNode i);

	public void visit_d2l(InsnNode i);

	public void visit_dadd(InsnNode i);

	public void visit_daload(InsnNode i);

	public void visit_dastore(InsnNode i);

	public void visit_dcmpg(InsnNode i);

	public void visit_dcmpl(InsnNode i);

	public void visit_dconst_0(InsnNode i);

	public void visit_dconst_1(InsnNode i);

	public void visit_ddiv(InsnNode i);

	public void visit_dload(InsnNode i);

	public void visit_dload_0(InsnNode i);

	public void visit_dload_1(InsnNode i);

	public void visit_dload_2(InsnNode i);

	public void visit_dload_3(InsnNode i);

	public void visit_dmul(InsnNode i);

	public void visit_dneg(InsnNode i);

	public void visit_drem(InsnNode i);

	public void visit_dreturn(InsnNode i);

	public void visit_dstore(InsnNode i);

	public void visit_dstore_0(InsnNode i);

	public void visit_dstore_1(InsnNode i);

	public void visit_dstore_2(InsnNode i);

	public void visit_dstore_3(InsnNode i);

	public void visit_dsub(InsnNode i);

	public void visit_dup(InsnNode i);

	public void visit_dup_x1(InsnNode i);

	public void visit_dup_x2(InsnNode i);

	public void visit_dup2(InsnNode i);

	public void visit_dup2_x1(InsnNode i);

	public void visit_dup2_x2(InsnNode i);

	public void visit_f2d(InsnNode i);

	public void visit_f2i(InsnNode i);

	public void visit_f2l(InsnNode i);

	public void visit_fadd(InsnNode i);

	public void visit_faload(InsnNode i);

	public void visit_fastore(InsnNode i);

	public void visit_fcmpg(InsnNode i);

	public void visit_fcmpl(InsnNode i);

	public void visit_fconst_0(InsnNode i);

	public void visit_fconst_1(InsnNode i);

	public void visit_fconst_2(InsnNode i);

	public void visit_fdiv(InsnNode i);

	public void visit_fload(InsnNode i);

	public void visit_fload_0(InsnNode i);

	public void visit_fload_1(InsnNode i);

	public void visit_fload_2(InsnNode i);

	public void visit_fload_3(InsnNode i);

	public void visit_fmul(InsnNode i);

	public void visit_fneg(InsnNode i);

	public void visit_frem(InsnNode i);

	public void visit_freturn(InsnNode i);

	public void visit_fstore(InsnNode i);

	public void visit_fstore_0(InsnNode i);

	public void visit_fstore_1(InsnNode i);

	public void visit_fstore_2(InsnNode i);

	public void visit_fstore_3(InsnNode i);

	public void visit_fsub(InsnNode i);

	public void visit_getfield(InsnNode i);

	public void visit_getstatic(InsnNode i);

	public void visit_goto_(InsnNode i);

	public void visit_goto_w(InsnNode i);

	public void visit_i2b(InsnNode i);

	public void visit_i2c(InsnNode i);

	public void visit_i2d(InsnNode i);

	public void visit_i2f(InsnNode i);

	public void visit_i2l(InsnNode i);

	public void visit_i2s(InsnNode i);

	public void visit_iadd(InsnNode i);

	public void visit_iaload(InsnNode i);

	public void visit_iand(InsnNode i);

	public void visit_iastore(InsnNode i);

	public void visit_iconst_0(InsnNode i);

	public void visit_iconst_1(InsnNode i);

	public void visit_iconst_2(InsnNode i);

	public void visit_iconst_3(InsnNode i);

	public void visit_iconst_4(InsnNode i);

	public void visit_iconst_5(InsnNode i);

	public void visit_iconst_m1(InsnNode i);

	public void visit_idiv(InsnNode i);

	public void visit_if_acmpeq(InsnNode i);

	public void visit_if_acmpne(InsnNode i);

	public void visit_if_icmpeq(InsnNode i);

	public void visit_if_icmpge(InsnNode i);

	public void visit_if_icmpgt(InsnNode i);

	public void visit_if_icmple(InsnNode i);

	public void visit_if_icmplt(InsnNode i);

	public void visit_if_icmpne(InsnNode i);

	public void visit_ifeq(InsnNode i);

	public void visit_ifge(InsnNode i);

	public void visit_ifgt(InsnNode i);

	public void visit_ifle(InsnNode i);

	public void visit_iflt(InsnNode i);

	public void visit_ifne(InsnNode i);

	public void visit_ifnonnull(InsnNode i);

	public void visit_ifnull(InsnNode i);

	public void visit_iinc(InsnNode i);

	public void visit_iload(InsnNode i);

	public void visit_iload_0(InsnNode i);

	public void visit_iload_1(InsnNode i);

	public void visit_iload_2(InsnNode i);

	public void visit_iload_3(InsnNode i);

	public void visit_impdep1(InsnNode i);

	public void visit_impdep2(InsnNode i);

	public void visit_imul(InsnNode i);

	public void visit_ineg(InsnNode i);

	public void visit_instanceof_(InsnNode i);

	public void visit_invokedynamic(InsnNode i);

	public void visit_invokeinterface(InsnNode i);

	public void visit_invokespecial(InsnNode i);

	public void visit_invokestatic(InsnNode i);

	public void visit_invokevirtual(InsnNode i);

	public void visit_ior(InsnNode i);

	public void visit_irem(InsnNode i);

	public void visit_ireturn(InsnNode i);

	public void visit_ishl(InsnNode i);

	public void visit_ishr(InsnNode i);

	public void visit_istore(InsnNode i);

	public void visit_istore_0(InsnNode i);

	public void visit_istore_1(InsnNode i);

	public void visit_istore_2(InsnNode i);

	public void visit_istore_3(InsnNode i);

	public void visit_isub(InsnNode i);

	public void visit_iushr(InsnNode i);

	public void visit_ixor(InsnNode i);

	public void visit_jsr(InsnNode i);

	public void visit_jsr_w(InsnNode i);

	public void visit_l2d(InsnNode i);

	public void visit_l2f(InsnNode i);

	public void visit_l2i(InsnNode i);

	public void visit_ladd(InsnNode i);

	public void visit_laload(InsnNode i);

	public void visit_land(InsnNode i);

	public void visit_lastore(InsnNode i);

	public void visit_lcmp(InsnNode i);

	public void visit_lconst_0(InsnNode i);

	public void visit_lconst_1(InsnNode i);

	public void visit_ldc(InsnNode i);

	public void visit_ldc_w(InsnNode i);

	public void visit_ldc2_w(InsnNode i);

	public void visit_ldiv(InsnNode i);

	public void visit_lload(InsnNode i);

	public void visit_lload_0(InsnNode i);

	public void visit_lload_1(InsnNode i);

	public void visit_lload_2(InsnNode i);

	public void visit_lload_3(InsnNode i);

	public void visit_lmul(InsnNode i);

	public void visit_lneg(InsnNode i);

	public void visit_lookupswitch(InsnNode i);

	public void visit_lor(InsnNode i);

	public void visit_lrem(InsnNode i);

	public void visit_lreturn(InsnNode i);

	public void visit_lshl(InsnNode i);

	public void visit_lshr(InsnNode i);

	public void visit_lstore(InsnNode i);

	public void visit_lstore_0(InsnNode i);

	public void visit_lstore_1(InsnNode i);

	public void visit_lstore_2(InsnNode i);

	public void visit_lstore_3(InsnNode i);

	public void visit_lsub(InsnNode i);

	public void visit_lushr(InsnNode i);

	public void visit_lxor(InsnNode i);

	public void visit_monitorenter(InsnNode i);

	public void visit_monitorexit(InsnNode i);

	public void visit_multianewarray(InsnNode i);

	public void visit_new_(InsnNode i);

	public void visit_newarray(InsnNode i);

	public void visit_nop(InsnNode i);

	public void visit_pop(InsnNode i);

	public void visit_pop2(InsnNode i);

	public void visit_putfield(InsnNode i);

	public void visit_putstatic(InsnNode i);

	public void visit_ret(InsnNode i);

	public void visit_return_(InsnNode i);

	public void visit_saload(InsnNode i);

	public void visit_sastore(InsnNode i);

	public void visit_sipush(InsnNode i);

	public void visit_swap(InsnNode i);

	public void visit_tableswitch(InsnNode i);

	public void visit_wide(InsnNode i);

}
