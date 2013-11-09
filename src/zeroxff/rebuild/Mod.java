package zeroxff.rebuild;

import zeroxff.rebuild.nodes.MemberNode;
import zeroxff.rebuild.nodes.MethodNode;

public class Mod {
	public final static Mod STATIC = new Mod(new ModCheck() {

		@Override
		public boolean check(MemberNode member) {
			return member.isStatic();
		}
	});
	public final static Mod INSTANCE = new Mod(new ModCheck() {

		@Override
		public boolean check(MemberNode member) {
			return member.isStatic() == false;
		}
	});
	public static final Mod NOTINIT = new Mod(new ModCheck() {

		@Override
		public boolean check(MemberNode member) {
			return member.name.equals("<clinit>") == false
					&& member.name.equals("<init>") == false;
		}
	});

	private abstract static class ModCheck {

		public abstract boolean check(MemberNode member);

	}

	private ModCheck checker;

	private Mod(ModCheck checker) {
		this.checker = checker;
	}

	public boolean isValid(MemberNode mb) {
		return checker.check(mb);
	}

	public static boolean check(MemberNode mbNode, Mod... mods) {
		for (Mod mod : mods) {
			if (mod.isValid(mbNode) == false) {
				return false;
			}
		}
		return true;
	}

	public static Mod ISCLASS(final Class<?> type) {
		return new Mod(new ModCheck() {

			@Override
			public boolean check(MemberNode member) {
				return member.desc == type;
			}
		});
	}

	public static Mod ISNAMED(final String name) {
		return new Mod(new ModCheck() {

			@Override
			public boolean check(MemberNode member) {
				return member.name.equals(name);
			}
		});
	}

	public static Mod HASPARAMS(final Class<?>... param) {
		return new Mod(new ModCheck() {
			@Override
			public boolean check(MemberNode member) {
				if (member instanceof MethodNode == false) {
					return false;
				}
				MethodNode mNode = (MethodNode) member;
				if (param.length != mNode.params.size()) {
					return false;
				}
				for (int i = 0; i < mNode.params.size(); i++) {
					if (mNode.params.get(i).equals(param[i]) == false) {
						return false;
					}
				}
				return true;
			}
		});
	}

	public static Mod RETURNS(final Class<?> clz) {
		return new Mod(new ModCheck() {
			@Override
			public boolean check(MemberNode member) {
				if (member instanceof MethodNode == false) {
					return false;
				}
				MethodNode mNode = (MethodNode) member;
				return mNode.desc == clz;
			}
		});
	}
}
