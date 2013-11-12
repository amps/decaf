package io.amps.decaf.structure;

public class Mod {
	private abstract static class ModCheck {

		public abstract boolean check(MemberNode member);

	}

	public final static Mod STATIC = new Mod(new ModCheck() {

		@Override
		public boolean check(final MemberNode member) {
			return member.isStatic();
		}
	});
	public final static Mod INSTANCE = new Mod(new ModCheck() {

		@Override
		public boolean check(final MemberNode member) {
			return member.isStatic() == false;
		}
	});

	public static final Mod NOTINIT = new Mod(new ModCheck() {

		@Override
		public boolean check(final MemberNode member) {
			return member.name.equals("<clinit>") == false
					&& member.name.equals("<init>") == false;
		}
	});

	public static boolean check(final MemberNode mbNode, final Mod... mods) {
		for (final Mod mod : mods) {
			if (mod.isValid(mbNode) == false) {
				return false;
			}
		}
		return true;
	}

	public static Mod HASPARAMS(final Class<?>... param) {
		return new Mod(new ModCheck() {
			@Override
			public boolean check(final MemberNode member) {
				if (member instanceof MethodNode == false) {
					return false;
				}
				final MethodNode mNode = (MethodNode) member;
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

	public static Mod ISCLASS(final Class<?> type) {
		return new Mod(new ModCheck() {

			@Override
			public boolean check(final MemberNode member) {
				return member.desc == type;
			}
		});
	}

	public static Mod ISNAMED(final String name) {
		return new Mod(new ModCheck() {

			@Override
			public boolean check(final MemberNode member) {
				return member.name.equals(name);
			}
		});
	}

	public static Mod RETURNS(final Class<?> clz) {
		return new Mod(new ModCheck() {
			@Override
			public boolean check(final MemberNode member) {
				if (member instanceof MethodNode == false) {
					return false;
				}
				final MethodNode mNode = (MethodNode) member;
				return mNode.desc == clz;
			}
		});
	}

	private final ModCheck checker;

	private Mod(final ModCheck checker) {
		this.checker = checker;
	}

	public boolean isValid(final MemberNode mb) {
		return checker.check(mb);
	}
}
