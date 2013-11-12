package io.amps.decaf.pass;

import io.amps.decaf.ByteUtils;
import io.amps.decaf.Context;
import io.amps.decaf.structure.ClassNode;
import io.amps.decaf.structure.ConstantPool.MemberRef;
import io.amps.decaf.structure.FieldNode;
import io.amps.decaf.structure.InsnNode;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.Mod;
import io.amps.decaf.structure.visitors.InstructionVisitor;
import io.amps.lib.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DeDuplicate extends Pass implements InstructionVisitor {

	public Map<ClassNode, Set<MethodNode>> usedMethods, lastMethods;

	public Map<ClassNode, Set<FieldNode>> usedFields, lastFields;
	public Set<ClassNode> usedClasses, lastClasses;

	public DeDuplicate(final Context context) {
		super(context);
	}

	private void addClassUse(final ClassNode clz) {
		usedClasses.add(clz);
		addMethodUse(null, clz.method(Mod.ISNAMED("<init>")));
		addMethodUse(null, clz.method(Mod.ISNAMED("<clinit>")));
	}

	private void addFieldInsn(final InsnNode i) {
		final short index = ByteUtils.toShort(i.args[0], i.args[1]);
		final MemberRef ref = i.code.method.parent.constantPool
				.getMemberRef(index);
		final String className = ref.getClassOwner().getSimpleName();
		if (!context.getMappedClasses().containsKey(className)) {
			return;
		}
		final String methodName = ref.getNameType().getName();
		final ClassNode cnode = context.getMappedClasses().get(className);
		final FieldNode node = cnode.field(Mod.ISNAMED(methodName));
		addFieldUse(i, node);

		addClassUse(node.parent);
	}

	private void addFieldUse(final InsnNode i, final FieldNode field) {

		Set<FieldNode> set;
		if (usedFields.containsKey(field.parent)) {
			set = usedFields.get(field.parent);
		} else {
			set = new HashSet<>();
			usedFields.put(field.parent, set);
		}
		set.add(field);

	}

	private void addMethodInsn(final InsnNode i) {
		final short index = ByteUtils.toShort(i.args[0], i.args[1]);
		final MemberRef ref = i.code.method.parent.constantPool
				.getMemberRef(index);
		final String className = ref.getClassOwner().getSimpleName();
		if (!context.getMappedClasses().containsKey(className)) {
			return;
		}
		final String methodName = ref.getNameType().getName();
		final ClassNode cnode = context.getMappedClasses().get(className);
		final MethodNode node = cnode.method(Mod.ISNAMED(methodName));

		addMethodUse(i, node);
		addClassUse(node.parent);
	}

	private void addMethodUse(final InsnNode i, final MethodNode node) {
		if (node == null) {
			return;
		}
		Set<MethodNode> set;
		if (usedMethods.containsKey(node.parent)) {
			set = usedMethods.get(node.parent);
		} else {
			set = new HashSet<>();
			usedMethods.put(node.parent, set);
		}
		set.add(node);

	}

	private void addNew(final InsnNode i) {
		final short index = ByteUtils.toShort(i.args[0], i.args[1]);
		final Class<?> ref = i.code.method.parent.constantPool.getClass(index);
		final String className = ref.getSimpleName();

		if (!context.getMappedClasses().containsKey(className)) {
			return;
		}
		final ClassNode node = context.getMappedClasses().get(className);
		addClassUse(node);

		addMethodUse(null, node.method(Mod.ISNAMED("<init>")));
	}

	private int classPass() {

		final List<ClassNode> toRemove = new ArrayList<>();
		checkClasses: for (final ClassNode node : context.getClasses()) {
			if (!usedClasses.contains(node)) {
				for (final Class<?> interf : node.nodeClass.getInterfaces()) {
					final ClassNode interfNode = context.getClassNode(interf);
					if (interfNode == null) {
						continue;
					}
					if (usedClasses.contains(interfNode)) {
						continue checkClasses;
					}
				}

				toRemove.add(node);

			}
		}
		final int removedClasses = toRemove.size();
		for (final ClassNode rem : toRemove) {
			Log.log(Log.TRACE, "Removing class %s", rem.name);
			context.removeClass(rem);
		}
		return removedClasses;
	}

	@Override
	public void execute() {
		int fields = 0;
		final int classes = context.getClasses().size();
		int methods = 0;

		for (final ClassNode node : context.getClasses()) {
			fields += node.fields.size();
			methods += node.methods.size();
		}
		int removedMethods = 0;
		int removedFields = 0;
		int removedClasses = 0;

		final long time = System.currentTimeMillis(), originalTime = time;
		int[] pass = pass();
		while (pass[0] > 0 || pass[1] > 0 || pass[2] > 0) {
			removedMethods += pass[0];
			removedFields += pass[1];
			removedClasses += pass[2];
			pass = pass();

		}
		Log.log(Log.INFO, "Removed %d unused classes (%d%%)", removedClasses,
				Math.round((float) removedClasses / (float) classes * 100));
		Log.log(Log.INFO, "Removed %d unused methods (%d%%)", removedMethods,
				Math.round((float) removedMethods / (float) methods * 100));
		Log.log(Log.INFO, "Removed %d unused fields (%d%%)", removedFields,
				Math.round((float) removedFields / (float) fields * 100));
		Log.log(Log.INFO, "De-duplication took %d ms total",
				System.currentTimeMillis() - originalTime);

	}

	private int fieldPass() {
		int removedFields = 0;
		for (final ClassNode node : context.getClasses()) {
			if (!usedFields.containsKey(node)) {
				continue;
			}
			final Set<FieldNode> used = usedFields.get(node);
			final Set<FieldNode> toRemove = new HashSet<>();
			for (final FieldNode fnode : node.fields) {
				if (!used.contains(fnode)) {
					toRemove.add(fnode);
				}
			}
			for (final FieldNode rem : toRemove) {
				node.fields.remove(rem);
				removedFields++;
			}
		}
		return removedFields;
	}

	private int methodPass() {

		// TODO yum, hacky
		if (context.getMappedClasses().get("client")
				.method(Mod.ISNAMED("init")) != null) {
			addMethodUse(
					null,
					context.getMappedClasses().get("client")
							.method(Mod.ISNAMED("init")));
		}
		//

		int removedMethods = 0;
		for (final ClassNode node : context.getClasses()) {
			if (!usedMethods.containsKey(node)) {
				continue;
			}
			final Set<MethodNode> used = usedMethods.get(node);

			final Set<MethodNode> toRemove = new HashSet<>();
			for (final MethodNode fnode : node.methods) {

				if (!used.contains(fnode)) {
					toRemove.add(fnode);
				}
			}
			for (final MethodNode rem : toRemove) {
				Log.log(Log.TRACE, "Removing method %s.%s", rem.parent.name,
						rem.name);
				node.methods.remove(rem);
				removedMethods++;
			}
		}
		return removedMethods;
	}

	private int[] pass() {
		lastFields = usedFields;
		lastMethods = usedMethods;
		lastClasses = usedClasses;
		usedFields = new HashMap<>();
		usedMethods = new HashMap<>();
		usedClasses = new HashSet<>();
		Log.log(Log.TRACE, "DeDuplicator Pass");
		for (final ClassNode node : context.getClasses()) {
			for (final MethodNode mnode : node.methods) {
				mnode.code.accept(this);
			}
		}

		// TODO FIX
		final int classPass = classPass();
		final int methodPass = methodPass();
		final int fieldPass = fieldPass();
		return new int[] { methodPass, fieldPass, classPass };
	}

	@Override
	public void visit_aaload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aconst_null(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_anewarray(final InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_areturn(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_arraylength(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_athrow(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_baload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_bastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_bipush(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_breakpoint(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_caload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_castore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_checkcast(final InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_d2f(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_d2i(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_d2l(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dadd(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_daload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dcmpg(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dcmpl(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dconst_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dconst_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ddiv(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dmul(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dneg(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_drem(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dreturn(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dsub(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup_x1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup_x2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup2_x1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup2_x2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_f2d(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_f2i(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_f2l(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fadd(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_faload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fcmpg(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fcmpl(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fconst_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fconst_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fconst_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fdiv(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fmul(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fneg(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_frem(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_freturn(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fsub(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_getfield(final InsnNode i) {
		addFieldInsn(i);

	}

	@Override
	public void visit_getstatic(final InsnNode i) {
		addFieldInsn(i);
	}

	@Override
	public void visit_goto_(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_goto_w(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2b(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2c(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2d(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2f(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2l(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2s(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iadd(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iaload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iand(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_4(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_5(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_m1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_idiv(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_acmpeq(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_acmpne(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpeq(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpge(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpgt(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmple(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmplt(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpne(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifeq(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifge(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifgt(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifle(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iflt(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifne(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifnonnull(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifnull(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iinc(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_impdep1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_impdep2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_imul(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ineg(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_instanceof_(final InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_invokedynamic(final InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokeinterface(final InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokespecial(final InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokestatic(final InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokevirtual(final InsnNode i) {
		addMethodInsn(i);

	}

	@Override
	public void visit_ior(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_irem(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ireturn(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ishl(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ishr(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_isub(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iushr(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ixor(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_jsr(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_jsr_w(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_l2d(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_l2f(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_l2i(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ladd(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_laload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_land(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lcmp(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lconst_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lconst_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldc(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldc_w(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldc2_w(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldiv(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lmul(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lneg(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lookupswitch(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lor(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lrem(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lreturn(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lshl(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lshr(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_0(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_1(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_3(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lsub(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lushr(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lxor(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_monitorenter(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_monitorexit(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_multianewarray(final InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_new_(final InsnNode i) {
		addNew(i);

	}

	@Override
	public void visit_newarray(final InsnNode i) {

	}

	@Override
	public void visit_nop(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_pop(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_pop2(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_putfield(final InsnNode i) {
		addFieldInsn(i);
	}

	@Override
	public void visit_putstatic(final InsnNode i) {
		addFieldInsn(i);
	}

	@Override
	public void visit_ret(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_return_(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_saload(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_sastore(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_sipush(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_swap(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_tableswitch(final InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_wide(final InsnNode i) {
		// TODO Auto-generated method stub

	}

}
