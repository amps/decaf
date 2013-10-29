package deobber.rebuild.nodes.attributes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import deobber.rebuild.nodes.ArithExprNode;
import deobber.rebuild.nodes.ArithExprNode.Op;
import deobber.rebuild.nodes.CodeNode;
import deobber.rebuild.nodes.ConstantPool;
import deobber.rebuild.nodes.ConstantPool.Constant;
import deobber.rebuild.nodes.ConstantPool.MemberRef;
import deobber.rebuild.nodes.Expr;
import deobber.rebuild.nodes.InsnNode;
import deobber.rebuild.nodes.TryCatchNode;
import deobber.rebuild.nodes.Type;
import deobber.rebuild.nodes.code.ArrayLengthExpr;
import deobber.rebuild.nodes.code.ArrayRefExpr;
import deobber.rebuild.nodes.code.CallStaticExpr;
import deobber.rebuild.nodes.code.ConstantExpr;
import deobber.rebuild.nodes.code.LocalExpr;
import deobber.rebuild.nodes.code.MemExpr;
import deobber.rebuild.nodes.code.NegExpr;
import deobber.rebuild.nodes.code.NewArrayExpr;
import deobber.rebuild.nodes.code.ReturnExpr;
import deobber.rebuild.nodes.code.ShiftExpr;
import deobber.rebuild.nodes.code.StackExpr;
import deobber.rebuild.nodes.code.ShiftExpr.Direction;
import deobber.rebuild.nodes.code.StoreExpr;
import deobber.rebuild.nodes.visitors.CodeVisitor;
import deobber.rebuild.nodes.visitors.InstructionVisitor;

public class CodeAttribute extends Attribute implements InstructionVisitor {

	public static CodeAttribute construct(ConstantPool constantPool,
			ByteBuffer data) {
		short maxStack = data.getShort();
		short maxLocals = data.getShort();
		int codeLength = data.getInt();
		byte[] code = new byte[codeLength];

		data.get(code, 0, codeLength);
		ByteBuffer codeBuffer = ByteBuffer.wrap(code);
		List<Label> labels = Label.constructList(code);
		List<InsnNode> instructions = InsnNode.constructList(codeBuffer);
		short exceptionTableLength = data.getShort();
		List<TryCatchNode> tryCatches = new ArrayList<>();
		for (int i = 0; i < exceptionTableLength; i++) {
			short start = data.getShort();
			short end = data.getShort();
			short handler = data.getShort();
			short type = data.getShort();
			TryCatchNode node = new TryCatchNode(labels.get(start),
					labels.get(end), labels.get(handler),
					constantPool.getClass(type));
			tryCatches.add(node);
		}
		return new CodeAttribute(constantPool, labels, instructions, tryCatches);
	}

	static short createShortByTwoBytes(byte a, byte b) {
		return (short) ((short) ((short) a << 8) | ((short) b & 0x000000FF));
	}

	private List<InsnNode> instructions;
	private List<CodeNode> code;
	private List<Label> labels;
	private List<TryCatchNode> tryCatches;
	private final ConstantPool constantPool;

	public CodeAttribute(ConstantPool constantPool) {
		this.constantPool = constantPool;
		instructions = new ArrayList<>();
		tryCatches = new ArrayList<>();
		code = new ArrayList<>();
		labels = new ArrayList<>();
		parse();
	}

	public CodeAttribute(ConstantPool constantPool, List<Label> labels,
			List<InsnNode> instructions, List<TryCatchNode> tryCatches) {
		this.constantPool = constantPool;
		this.instructions = instructions;
		this.labels = labels;
		code = new ArrayList<>();
		this.tryCatches = tryCatches;
		parse();
	}

	Stack<Expr> stack;
	Map<Integer, Expr> local;

	private void parse() {
		if(true) {
			return;
		}
		code = new ArrayList<>();
		stack = new Stack<>();
		local = new HashMap<>();
		for (int i = 0; i < instructions.size(); i++) {
			InsnNode insnNode = instructions.get(i);
			if (insnNode == null || insnNode.opcode == null) {
				continue;
			}
			insnNode.accept(this);
		}
		assert (instructions.size() == code.size());
	}

	public List<InsnNode> getInstructionNodes() {
		return instructions;
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

	@Override
	public void visit_nop(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_aconst_null(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(null, Type.NULL);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_m1(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(-1, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_0(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(0, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_1(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(1, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_2(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(2, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_3(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(3, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_4(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(4, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_iconst_5(InsnNode i) {
		ConstantExpr expr = new ConstantExpr(5, Type.INT);
		stack.push(expr);
		code.add(expr);
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
		int val = i.args[0];
		ConstantExpr expr = new ConstantExpr(val, Type.INT);
		stack.push(expr);
		code.add(expr);
	}

	@Override
	public void visit_sipush(InsnNode i) {
		byte a = i.args[0];
		byte b = i.args[1];
		short c = (short) ((a << 8) | b);
		// stack.push(c);

	}

	@Override
	public void visit_ldc(InsnNode i) {
		Constant cons = constantPool.get(i.args[0]);
		stack.push(new ConstantExpr(1, Type.INT));
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
		byte index = i.args[0];
		Expr localExpr = local.get(index);
		code.add(new LocalExpr(index, false, localExpr.type));
		stack.push(localExpr);
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
		byte index = 0;
		Expr localExpr = local.get(index);
		code.add(new LocalExpr(index, false, localExpr.type));
		stack.push(localExpr);
	}

	@Override
	public void visit_aload_1(InsnNode i) {
		byte index = 1;
		Expr localExpr = local.get(index);
		code.add(new LocalExpr(index, false, localExpr.type));
		stack.push(localExpr);
	}

	@Override
	public void visit_aload_2(InsnNode i) {
		byte index = 2;
		Expr localExpr = local.get(index);
		code.add(new LocalExpr(index, false, localExpr.type));
		stack.push(localExpr);
	}

	@Override
	public void visit_aload_3(InsnNode i) {
		byte index = 3;
		Expr localExpr = local.get(index);
		code.add(new LocalExpr(index, false, localExpr.type));
		stack.push(localExpr);
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
		Expr index = stack.pop();
		Expr ref = stack.pop();
		Expr value = new ArrayRefExpr(ref, index, ref.type, ref.type);
		stack.push(value);
		code.add(value);

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
		byte index = i.args[0];
		Expr obj = stack.pop();
		MemExpr mem = new LocalExpr(index, true, obj.type);
		StoreExpr expr = new StoreExpr(mem, obj, obj.type);
		code.add(mem);
		code.add(expr);
		stack.push(expr);
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
		byte index = 0;
		Expr obj = stack.pop();
		MemExpr mem = new LocalExpr(index, true, obj.type);
		StoreExpr expr = new StoreExpr(mem, obj, obj.type);
		code.add(mem);
		code.add(expr);
		stack.push(expr);
	}

	@Override
	public void visit_astore_1(InsnNode i) {
		byte index = 1;
		Expr obj = stack.pop();
		MemExpr mem = new LocalExpr(index, true, obj.type);
		StoreExpr expr = new StoreExpr(mem, obj, obj.type);
		code.add(mem);
		code.add(expr);
		stack.push(expr);
	}

	@Override
	public void visit_astore_2(InsnNode i) {
		byte index = 2;
		Expr obj = stack.pop();
		MemExpr mem = new LocalExpr(index, true, obj.type);
		StoreExpr expr = new StoreExpr(mem, obj, obj.type);
		code.add(mem);
		code.add(expr);
		stack.push(expr);
	}

	@Override
	public void visit_astore_3(InsnNode i) {
		byte index = 3;
		Expr obj = stack.pop();
		MemExpr mem = new LocalExpr(index, true, obj.type);
		StoreExpr expr = new StoreExpr(mem, obj, obj.type);
		code.add(mem);
		code.add(expr);
		stack.push(expr);
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
		Expr value = stack.pop();
		Expr index = stack.pop();
		Expr ref = stack.pop();
		ArrayRefExpr target = new ArrayRefExpr(ref, index, ref.type, ref.type);
		StoreExpr expr = new StoreExpr(target, value, value.type);
		code.add(value);
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
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode add = new ArithExprNode(Op.ADD, left, right, Type.INT);
		stack.push(add);
		code.add(add);
	}

	@Override
	public void visit_ladd(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_fadd(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode add = new ArithExprNode(Op.ADD, left, right, Type.FLOAT);
		stack.push(add);
		code.add(add);
	}

	@Override
	public void visit_dadd(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode add = new ArithExprNode(Op.ADD, left, right, Type.DOUBLE);
		stack.push(add);
		code.add(add);
	}

	@Override
	public void visit_isub(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.SUB, left, right, Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_lsub(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.SUB, left, right, Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_fsub(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.SUB, left, right, Type.FLOAT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_dsub(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.SUB, left, right, Type.DOUBLE);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_imul(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.MUL, left, right, Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_lmul(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.MUL, left, right, Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_fmul(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.MUL, left, right, Type.FLOAT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_dmul(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.MUL, left, right, Type.DOUBLE);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_idiv(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.DIV, left, right, Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_ldiv(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.DIV, left, right, Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_fdiv(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.DIV, left, right, Type.FLOAT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_ddiv(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.DIV, left, right, Type.DOUBLE);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_irem(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.REM, left, right, Type.INT);
		stack.push(op);
	}

	@Override
	public void visit_lrem(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.REM, left, right, Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_frem(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.REM, left, right, Type.FLOAT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_drem(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode op = new ArithExprNode(Op.REM, left, right, Type.DOUBLE);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_ineg(InsnNode i) {
		Expr left = (Expr) stack.pop();
		NegExpr op = new NegExpr(left, Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_lneg(InsnNode i) {
		Expr left = (Expr) stack.pop();
		NegExpr op = new NegExpr(left, Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_fneg(InsnNode i) {
		Expr left = (Expr) stack.pop();
		NegExpr op = new NegExpr(left, Type.FLOAT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_dneg(InsnNode i) {
		Expr left = (Expr) stack.pop();
		NegExpr op = new NegExpr(left, Type.DOUBLE);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_ishl(InsnNode i) {
		Expr expr = (Expr) stack.pop();
		Expr bits = (Expr) stack.pop();
		ShiftExpr op = new ShiftExpr(Direction.LEFT, expr, bits, Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_lshl(InsnNode i) {
		Expr expr = (Expr) stack.pop();
		Expr bits = (Expr) stack.pop();
		ShiftExpr op = new ShiftExpr(Direction.LEFT, expr, bits, Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_ishr(InsnNode i) {
		Expr expr = (Expr) stack.pop();
		Expr bits = (Expr) stack.pop();
		ShiftExpr op = new ShiftExpr(Direction.RIGHT_ARITHMETIC, expr, bits,
				Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_lshr(InsnNode i) {
		Expr expr = (Expr) stack.pop();
		Expr bits = (Expr) stack.pop();
		ShiftExpr op = new ShiftExpr(Direction.RIGHT_ARITHMETIC, expr, bits,
				Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_iushr(InsnNode i) {
		Expr expr = (Expr) stack.pop();
		Expr bits = (Expr) stack.pop();
		ShiftExpr op = new ShiftExpr(Direction.RIGHT_LOGICAL, expr, bits,
				Type.INT);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_lushr(InsnNode i) {
		Expr expr = (Expr) stack.pop();
		Expr bits = (Expr) stack.pop();
		ShiftExpr op = new ShiftExpr(Direction.RIGHT_LOGICAL, expr, bits,
				Type.LONG);
		stack.push(op);
		code.add(op);
	}

	@Override
	public void visit_iand(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode and = new ArithExprNode(Op.AND, left, right, Type.INT);
		stack.push(and);
		code.add(and);
	}

	@Override
	public void visit_land(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode and = new ArithExprNode(Op.ADD, left, right, Type.LONG);
		stack.push(and);
		code.add(and);
	}

	@Override
	public void visit_ior(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode or = new ArithExprNode(Op.IOR, left, right, Type.INT);
		stack.push(or);
		code.add(or);
	}

	@Override
	public void visit_lor(InsnNode i) {
		Expr left = (Expr) stack.pop();
		Expr right = (Expr) stack.pop();
		ArithExprNode or = new ArithExprNode(Op.IOR, left, right, Type.LONG);
		stack.push(or);
		code.add(or);
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
		ReturnExpr expr = new ReturnExpr(stack.peek().type);
		code.add(expr);
	}

	@Override
	public void visit_return_(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_getstatic(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_putstatic(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_getfield(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_putfield(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_invokevirtual(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_invokespecial(InsnNode i) {

	}

	@Override
	public void visit_invokestatic(InsnNode i) {
		Expr[] args = stack.toArray(new Expr[] {});
		stack.clear();
		short index = createShortByTwoBytes(i.args[0], i.args[1]);
		MemberRef method = constantPool.getMemberRef(index);
		CallStaticExpr op = new CallStaticExpr(args, method, method
				.getNameType().getType());
		code.add(op);
	}

	@Override
	public void visit_invokeinterface(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_invokedynamic(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_new_(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_newarray(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_anewarray(InsnNode i) {
		short index = createShortByTwoBytes(i.args[0], i.args[1]);
		Expr count = stack.pop();
		Type elType = constantPool.getClass(index);
		NewArrayExpr nae = new NewArrayExpr(count, elType, elType);
		code.add(nae);
	}

	@Override
	public void visit_arraylength(InsnNode i) {
		Expr ref = stack.pop();
		ArrayLengthExpr expr = new ArrayLengthExpr(ref, ref.type);
		code.add(expr);
		stack.push(expr);
	}

	@Override
	public void visit_athrow(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_checkcast(InsnNode i) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit_instanceof_(InsnNode i) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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

	public Label getLabel(int i) {
		return labels.get(i);
	}

	public int indexOf(Label label) {
		return labels.indexOf(label);
	}

}