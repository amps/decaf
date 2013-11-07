package zeroxff.pass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import zeroxff.ByteUtils;
import zeroxff.Context;
import zeroxff.rebuild.Mod;
import zeroxff.rebuild.nodes.ClassNode;
import zeroxff.rebuild.nodes.FieldNode;
import zeroxff.rebuild.nodes.InsnNode;
import zeroxff.rebuild.nodes.MethodNode;
import zeroxff.rebuild.nodes.ConstantPool.MemberRef;
import zeroxff.rebuild.nodes.visitors.InstructionVisitor;
import lib.slog.Log;

public class DeDuplicate extends Pass implements InstructionVisitor {

	public DeDuplicate(Context context) {
		super(context);
	}

	public Map<ClassNode, Set<MethodNode>> usedMethods, lastMethods;
	public Map<ClassNode, Set<FieldNode>> usedFields, lastFields;
	public Set<ClassNode> usedClasses, lastClasses;

	@Override
	public void execute() {
		int fields = 0;
		int classes = context.getClasses().size();
		int methods = 0;

		for (ClassNode node : context.getClasses()) {
			fields += node.fields.size();
			methods += node.methods.size();
		}
		int removedMethods = 0;
		int removedFields = 0;
		int removedClasses = 0;

		long time = System.currentTimeMillis(), originalTime = time;
		int[] pass = pass();
		while (pass[0] > 0 || pass[1] > 0 || pass[2] > 0) {
			removedMethods += pass[0];
			removedFields += pass[1];
			removedClasses += pass[2];
			pass = pass();

		}
		Log.log(Log.INFO, "Removed %d unused classes (%d%%)", removedClasses,
				Math.round(((float) removedClasses / (float) classes) * 100));
		Log.log(Log.INFO, "Removed %d unused methods (%d%%)", removedMethods,
				Math.round(((float) removedMethods / (float) methods) * 100));
		Log.log(Log.INFO, "Removed %d unused fields (%d%%)", removedFields,
				Math.round(((float) removedFields / (float) fields) * 100));
		Log.log(Log.INFO, "De-duplication took %d ms total",
				(System.currentTimeMillis() - originalTime));

	}

	private int[] pass() {
		lastFields = usedFields;
		lastMethods = usedMethods;
		lastClasses = usedClasses;
		usedFields = new HashMap<>();
		usedMethods = new HashMap<>();
		usedClasses = new HashSet<>();
		Log.log(Log.TRACE, "DeDup Pass");
		for (ClassNode node : context.getClasses()) {
			for (MethodNode mnode : node.methods) {
				mnode.code.accept(this);
			}
		}

		int classPass = classPass();
		int methodPass = methodPass();
		int fieldPass = 0;// fieldPass();
		return new int[] { methodPass, fieldPass, classPass };
	}

	@SuppressWarnings("unused")
	private int fieldPass() {
		int removedFields = 0;
		for (ClassNode node : context.getClasses()) {
			if (!usedFields.containsKey(node)) {
				continue;
			}
			Set<FieldNode> used = usedFields.get(node);
			Set<FieldNode> toRemove = new HashSet<>();
			for (FieldNode fnode : node.fields) {
				if (!used.contains(fnode)) {
					toRemove.add(fnode);
				}
			}
			for (FieldNode rem : toRemove) {
				node.fields.remove(rem);
				removedFields++;
			}
		}
		return removedFields;
	}

	private int classPass() {

		List<ClassNode> toRemove = new ArrayList<>();
		checkClasses: for (ClassNode node : context.getClasses()) {
			if (!usedClasses.contains(node)) {
				for (Class<?> interf : node.nodeClass.getInterfaces()) {
					ClassNode interfNode = context.getClassNode(interf);
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
		int removedClasses = toRemove.size();
		for (ClassNode rem : toRemove) {
			Log.log(Log.TRACE, "Removing class %s", rem.name);
			context.removeClass(rem);
		}
		return removedClasses;
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
		for (ClassNode node : context.getClasses()) {
			if (!usedMethods.containsKey(node)) {
				continue;
			}
			Set<MethodNode> used = usedMethods.get(node);

			Set<MethodNode> toRemove = new HashSet<>();
			for (MethodNode fnode : node.methods) {

				if (!used.contains(fnode)) {
					toRemove.add(fnode);
				}
			}
			for (MethodNode rem : toRemove) {
				Log.log(Log.TRACE, "Removing method %s.%s", rem.parent.name,
						rem.name);
				node.methods.remove(rem);
				removedMethods++;
			}
		}
		return removedMethods;
	}

	@Override
	public void visit_nop(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aconst_null(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_m1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_4(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iconst_5(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lconst_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lconst_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fconst_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fconst_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fconst_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dconst_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dconst_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_bipush(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_sipush(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldc(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldc_w(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldc2_w(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iload_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lload_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fload_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dload_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aload_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iaload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_laload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_faload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_daload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aaload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_baload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_caload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_saload(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_istore_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lstore_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fstore_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dstore_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_0(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_astore_3(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_bastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_castore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_sastore(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_pop(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_pop2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup_x1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup_x2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup2_x1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dup2_x2(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_swap(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iadd(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ladd(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fadd(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dadd(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_isub(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lsub(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fsub(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dsub(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_imul(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lmul(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fmul(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dmul(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_idiv(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ldiv(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fdiv(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ddiv(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_irem(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lrem(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_frem(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_drem(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ineg(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lneg(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fneg(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dneg(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ishl(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lshl(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ishr(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lshr(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iushr(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lushr(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iand(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_land(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ior(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lor(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ixor(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lxor(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iinc(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2l(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2f(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2d(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_l2i(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_l2f(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_l2d(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_f2i(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_f2l(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_f2d(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_d2i(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_d2l(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_d2f(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2b(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2c(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_i2s(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lcmp(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fcmpl(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fcmpg(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dcmpl(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dcmpg(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifeq(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifne(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_iflt(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifge(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifgt(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifle(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpeq(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpne(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmplt(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpge(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmpgt(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_icmple(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_acmpeq(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_if_acmpne(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_goto_(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_jsr(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ret(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_tableswitch(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lookupswitch(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ireturn(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_lreturn(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_freturn(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_dreturn(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_areturn(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_return_(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_getstatic(InsnNode i) {
		addFieldInsn(i);
	}

	@Override
	public void visit_putstatic(InsnNode i) {
		addFieldInsn(i);
	}

	@Override
	public void visit_getfield(InsnNode i) {
		addFieldInsn(i);

	}

	@Override
	public void visit_putfield(InsnNode i) {
		addFieldInsn(i);
	}

	private void addFieldInsn(InsnNode i) {
		short index = ByteUtils.toShort(i.args[0], i.args[1]);
		MemberRef ref = i.code.method.parent.constantPool.getMemberRef(index);
		String className = ref.getClassOwner().getSimpleName();
		if (!context.getMappedClasses().containsKey(className)) {
			return;
		}
		String methodName = ref.getNameType().getName();
		ClassNode cnode = context.getMappedClasses().get(className);
		FieldNode node = cnode.field(Mod.ISNAMED(methodName));
		addFieldUse(i, node);

		addClassUse(node.parent);
	}

	private void addMethodUse(InsnNode i, MethodNode node) {
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

	private void addFieldUse(InsnNode i, FieldNode field) {

		Set<FieldNode> set;
		if (usedFields.containsKey(field.parent)) {
			set = usedFields.get(field.parent);
		} else {
			set = new HashSet<>();
			usedFields.put(field.parent, set);
		}
		set.add(field);

	}

	private void addMethodInsn(InsnNode i) {
		short index = ByteUtils.toShort(i.args[0], i.args[1]);
		MemberRef ref = i.code.method.parent.constantPool.getMemberRef(index);
		String className = ref.getClassOwner().getSimpleName();
		if (!context.getMappedClasses().containsKey(className)) {
			return;
		}
		String methodName = ref.getNameType().getName();
		ClassNode cnode = context.getMappedClasses().get(className);
		MethodNode node = cnode.method(Mod.ISNAMED(methodName));

		addMethodUse(i, node);
		addClassUse(node.parent);
	}

	@Override
	public void visit_invokevirtual(InsnNode i) {
		addMethodInsn(i);

	}

	@Override
	public void visit_invokespecial(InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokestatic(InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokeinterface(InsnNode i) {
		addMethodInsn(i);
	}

	@Override
	public void visit_invokedynamic(InsnNode i) {
		addMethodInsn(i);
	}

	private void addClassUse(ClassNode clz) {
		usedClasses.add(clz);
		addMethodUse(null, clz.method(Mod.ISNAMED("<init>")));
		addMethodUse(null, clz.method(Mod.ISNAMED("<clinit>")));
	}

	private void addNew(InsnNode i) {
		short index = ByteUtils.toShort(i.args[0], i.args[1]);
		Class<?> ref = i.code.method.parent.constantPool.getClass(index);
		String className = ref.getSimpleName();

		if (!context.getMappedClasses().containsKey(className)) {
			return;
		}
		ClassNode node = context.getMappedClasses().get(className);
		addClassUse(node);

		addMethodUse(null, node.method(Mod.ISNAMED("<init>")));
	}

	@Override
	public void visit_new_(InsnNode i) {
		addNew(i);

	}

	@Override
	public void visit_newarray(InsnNode i) {

	}

	@Override
	public void visit_anewarray(InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_arraylength(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_athrow(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_checkcast(InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_instanceof_(InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_monitorenter(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_monitorexit(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_wide(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_multianewarray(InsnNode i) {
		addNew(i);
	}

	@Override
	public void visit_ifnull(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_ifnonnull(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_goto_w(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_jsr_w(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_breakpoint(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_impdep1(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_impdep2(InsnNode i) {
		// TODO Auto-generated method stub

	}

}
