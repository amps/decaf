package zeroxff.rebuild.nodes;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import zeroxff.rebuild.nodes.ConstantPool.MemberRef;

public abstract class MemberNode extends TreeNode {
	public final String name;
	public final short access;
	public final ClassNode parent;
	public final Class<?> desc;

	public MemberNode(ClassNode parent, String name, short access, Class<?> desc) {
		this.name = name;
		this.parent = parent;
		this.access = access;
		this.desc = desc;
	}

	public boolean hasAccess(int type) {
		return (access & type) == type;
	}

	public boolean isStatic() {
		return Modifier.isStatic(access);
	}

	public Set<ClassNode> getReferences() {
		Set<ClassNode> refs = new HashSet<>();
		for (ClassNode node : parent.ctx.getClasses()) {
			for (Iterator<MemberRef> mrefs = node.constantPool.memberRefs(); mrefs
					.hasNext();) {
				MemberRef ref = mrefs.next();
				if (ref.getClassOwner() != null
						&& ref.getClassOwner().equals(parent.nodeClass)) {
					if (ref.getNameType().getName().equals(name)) {
						refs.add(node);
					}
				}
			}
		}
		return refs;
	}

}
