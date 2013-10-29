package deobber.rebuild.nodes;

import java.util.ArrayList;
import java.util.List;

import deobber.rebuild.nodes.attributes.Attribute;
import deobber.rebuild.nodes.visitors.StructureVisitor;

public abstract class TreeNode extends Node {

	private final List<Attribute> attributes = new ArrayList<>();

	public void addAttribute(Attribute attr) {
		attributes.add(attr);
		onAttribute(attr);
	}

	abstract <T extends Attribute> void onAttribute(T attr);

	

}
