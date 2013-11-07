package zeroxff.pass;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import zeroxff.rebuild.nodes.ClassNode;

public class ClassTree {

	public static abstract class Visitor {
		public abstract Node visit(Node node, int depth);
	}

	public final class Node {
		public final Set<Node> edges = new HashSet<>();
		public final ClassNode cn;
		public Node parent;

		public Node() {
			cn = null;
		}

		public Node(ClassNode cn) {
			this.cn = cn;
		}

		public List<Node> outEdges() {
			return new ArrayList<Node>(edges);
		}

		public Node addEdge(Node node) {
			edges.add(node);
			return node;
		}

		public Node removeEdge(Node node) {
			edges.remove(node);
			return node;
		}

		public Node accept(Visitor visitor) {
			Set<Node> discovered = new HashSet<>();
			Stack<Node> stack = new Stack<>();
			Map<Node, Node> parents = new HashMap<>();
			stack.push(this);
			while (!stack.empty()) {
				Node top = stack.peek();
				Node origParent = top.parent;
				parents.put(top, top.parent);
				int depth = 0;
				for (Node check = top; check.parent != null;) {
					depth++;
					check = check.parent;
				}
				Node ret = visitor.visit(top, depth);
				if (ret != null) {
					return ret;
				}
				if (origParent != null && parents.get(top).equals(origParent)) {
					discovered.add(top);
				}
				stack.pop();
				for (Node edge : top.edges) {
					if (discovered.contains(edge)) {
						continue;
					}
					stack.push(edge);
				}
			}
			return null;
		}

		private void clean() {
			Visitor cleanVisitor = new Visitor() {

				@Override
				public Node visit(Node node, int depth) {

					Set<Node> toRemove = new HashSet<>();
					for (Node child : node.edges) {
						if (child.parent != null && !child.parent.equals(node)) {
							toRemove.add(child);
						}
					}

					for (Node rem : toRemove) {
						node.edges.remove(rem);
					}

					return null;
				}
			};
			accept(cleanVisitor);
		};

		public Visitor reformVisitor = new Visitor() {

			@Override
			public Node visit(Node node, int depth) {
				if (node.cn == null) {
					return null;
				}

				List<Class<?>> supers = node.getSupers();
				for (Node adj : nodes) {

					if (supers.contains(adj.cn.nodeClass)) {
						int newIndex = supers
								.indexOf(adj.cn.nodeClass);
						int curIndex = Integer.MAX_VALUE;
						if (node.parent != null && node.parent.cn != null) {
							curIndex = supers
									.indexOf(node.parent.cn.nodeClass);
						}

						if (newIndex < curIndex) {
							if (node.parent != null) {
								node.parent.removeEdge(node);
								node.parent.addEdge(adj);
								adj.parent = node.parent;
							}

							adj.addEdge(node);
							node.parent = adj;
						}
					}
				}

				return null;
			}

		};

		public List<Class<?>> getSupers() {
			List<Class<?>> supers = new ArrayList<>();
			Class<?> check = cn.nodeClass;
			while (check != null && check != Object.class) {
				check = check.getSuperclass();
				supers.add(check);
			}
			return supers;

		}

		private void reform() {
			accept(reformVisitor);
			clean();
		}

		public void display(Map<String, ?> map) {
			accept(createDisplayVisitor(map));
			// display(ids, -1);
		}

		private Visitor createDisplayVisitor(final Map<String, ?> ids) {
			final Node parentTotal = root;
			return new Visitor() {
				@Override
				public Node visit(Node node, int depth) {
					if (node.parent != null) {
						if (node.parent.equals(parentTotal)) {
							display(node, 0);
						}
					} else {
						display(node, 0);
					}
					return null;
				}

				private void display(Node node, int depth) {
					List<Node> edgeList = new ArrayList<>(node.edges);
					Collections.sort(edgeList, new Comparator<Node>() {

						@Override
						public int compare(Node a, Node b) {
							if (a.cn == null) {
								return -1;
							} else if (b.cn == null) {
								return 1;
							}
							return a.cn.name.compareTo(b.cn.name);
						}
					});
					if (node.cn != null) {
						for (int i = 0; i < depth; i++) {
							System.out.print("\t\t\t");
						}
						if (ids.containsKey(node.cn.name)) {
							System.out.print(ids.get(node.cn.name) + " ("
									+ node.cn.name + ")");
						} else {
							System.out.print(node.cn.name);

						}
						Class<?> clz = node.cn.nodeClass;

						String interfaces = "";
						for (Class<?> intr : clz.getInterfaces()) {
							interfaces += intr.getSimpleName() + ";";
						}

						System.out.printf(" (i:%b,a:%b,il:%s)",
								clz.isInterface(),
								Modifier.isAbstract(clz.getModifiers()),
								interfaces);

						System.out.println();

					}
					for (Node edge : edgeList) {
						display(edge, depth + 1);
					}

				}
			};

		}

		public Node find(final ClassNode cnode) {
			return accept(new Visitor() {
				@Override
				public Node visit(Node node, int depth) {
					if (node.cn != null && node.cn.equals(cnode)) {
						return node;
					}
					return null;
				}
			});
		}

	}

	public final Node root;
	public final Set<Node> nodes = new HashSet<>();

	public ClassTree() {
		root = new Node();

	}

	public void add(ClassNode node) {
		Node n = new Node(node);
		nodes.add(n);
		root.addEdge(n);

	}

	public void reform() {
		root.reform();
	}

	public Node find(ClassNode cnode) {
		return root.find(cnode);
	}

	public void display(Map<String, ?> map) {
		root.display(map);
	}

	public int countChildren(ClassNode node) {
		return find(node).edges.size();
	}

	public Node getChild(ClassNode node, int i) {
		return find(node).outEdges().get(i);
	}

}
