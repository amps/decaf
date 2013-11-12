package io.amps.decaf.structure;

import io.amps.decaf.structure.ConstantPool.MemberRef;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class MemberNode extends TreeNode {
	public final String name;
	public final short access;
	public final ClassNode parent;
	public final Class<?> desc;

	public MemberNode(final ClassNode parent, final String name,
			final short access, final Class<?> desc) {
		this.name = name;
		this.parent = parent;
		this.access = access;
		this.desc = desc;
	}

	public Set<ClassNode> getReferences() {
		final Set<ClassNode> refs = new HashSet<>();
		for (final ClassNode node : parent.ctx.getClasses()) {
			for (final Iterator<MemberRef> mrefs = node.constantPool
					.memberRefs(); mrefs.hasNext();) {
				final MemberRef ref = mrefs.next();
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

	public Set<ClassNode> getReferences(final ClassNode... in) {
		final Set<ClassNode> refs = new HashSet<>();
		for (final ClassNode node : in) {
			for (final Iterator<MemberRef> mrefs = node.constantPool
					.memberRefs(); mrefs.hasNext();) {
				final MemberRef ref = mrefs.next();
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

	public boolean hasAccess(final int type) {
		return (access & type) == type;
	}

	public boolean isStatic() {
		return Modifier.isStatic(access);
	}

}
