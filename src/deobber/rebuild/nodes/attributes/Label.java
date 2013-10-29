package deobber.rebuild.nodes.attributes;

import java.util.ArrayList;
import java.util.List;

public class Label {

	public static List<Label> constructList(byte[] code) {
		List<Label> labels = new ArrayList<>();
		for (int i = 0; i < code.length; i++) {
			labels.add(Label.construct(i));
		}
		return labels;
	}

	public static Label construct(int index) {
		return new Label(index);
	}

	public final int index;

	public Label(int index) {
		this.index = index;
	}

}
