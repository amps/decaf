package zeroxff.rebuild.nodes;

import static zeroxff.rebuild.nodes.Instruction.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class InstructionSets {
	public static final Set<Instruction> OTHER_INSNS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(new Instruction[] {
					nop, aconst_null, iconst_m1, iconst_0, iconst_1, iconst_2,
					iconst_3, iconst_4, iconst_5, lconst_0, lconst_1, fconst_0,
					fconst_1, fconst_2, dconst_0, dconst_1, iaload, laload,
					faload, daload, aaload, baload, caload, saload, iastore,
					lastore, fastore, dastore, aastore, bastore, castore,
					sastore, pop, pop2, dup, dup_x1, dup_x2, dup2, dup2_x1,
					dup2_x2, swap, iadd, ladd, fadd, dadd, isub, lsub, fsub,
					dsub, imul, lmul, fmul, dmul, idiv, ldiv, fdiv, ddiv, irem,
					lrem, frem, drem, ineg, lneg, fneg, dneg, ishl, lshl, ishr,
					lshr, iushr, lushr, iand, land, ior, lor, ixor, lxor, i2l,
					i2f, i2d, l2i, l2f, l2d, f2i, f2l, f2d, d2i, d2l, d2f, i2b,
					i2c, i2s, lcmp, fcmpl, fcmpg, dcmpl, dcmpg, ireturn,
					lreturn, freturn, dreturn, areturn, return_, arraylength,
					athrow, monitorenter, monitorexit })));
	public static final Set<Instruction> INT_INSNS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(new Instruction[] {
					sipush, bipush, newarray })));
	public static final Set<Instruction> JUMP_INSNS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(new Instruction[] {
					ifeq, ifne, iflt, ifge, ifgt, ifle, if_icmpeq, if_icmpne,
					if_icmplt, if_icmpge, if_icmpgt, if_icmple, if_acmpeq,
					if_acmpne, goto_, goto_w, jsr, jsr_w, ifnull, ifnonnull })));
	public static final Set<Instruction> VAR_INSNS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(new Instruction[] {
					iload, iload_0, iload_1, iload_2, iload_3, lload, lload_0,
					lload_1, lload_2, lload_3, dload, dload_0, dload_1,
					dload_2, dload_3, fload, fload_0, fload_1, fload_2,
					fload_3, aload, aload_0, aload_1, aload_2, aload_3, istore,
					istore_0, istore_1, istore_2, istore_3, lstore, lstore_0,
					lstore_1, lstore_2, lstore_3, dstore, dstore_0, dstore_1,
					dstore_2, dstore_3, fstore, fstore_0, fstore_1, fstore_2,
					fstore_3, astore, astore_0, astore_1, astore_2, astore_3,
					ret, return_ })));
	public static final Set<Instruction> FIELD_INSNS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(new Instruction[] {
					getfield, putfield, getstatic, putstatic })));
}
