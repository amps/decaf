package deobber.rebuild.nodes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.omg.CORBA.CTX_RESTRICT_SCOPE;

import deobber.Context;
import deobber.rebuild.Constants;
import deobber.rebuild.nodes.ConstantPool.MemberRef;

public abstract class MemberNode extends TreeNode {
	public final String name;
	public final short access;
	public final ClassNode parent;

	public MemberNode(ClassNode parent, String name, short access) {
		this.name = name;
		this.parent = parent;
		this.access = access;
	}

	public boolean hasAccess(int type) {
		return (access & type) == type;
	}

	public boolean isStatic() {
		return hasAccess(Constants.ACC_STATIC);
	}

	public Set<ClassNode> getReferences() {
		Set<ClassNode> refs = new HashSet<>();
		for (ClassNode node : parent.ctx.getClasses()) {
			for (Iterator<MemberRef> mrefs = node.constantPool.memberRefs(); mrefs
					.hasNext();) {
				MemberRef ref = mrefs.next();
				if (ref.getClassOwner() != null
						&& ref.getClassOwner().equals(parent.load())) {
					if (ref.getNameType().getName().equals(name)) {
						refs.add(node);
					}
				}
			}
		}
		return refs;
	}
}
