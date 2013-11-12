package io.amps.decaf.structure;

import io.amps.decaf.structure.attributes.Attribute;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode extends Node {

	private final List<Attribute> attributes = new ArrayList<>();

	public void addAttribute(final Attribute attr) {
		attributes.add(attr);
		onAttribute(attr);
	}

	abstract <T extends Attribute> void onAttribute(T attr);

}
