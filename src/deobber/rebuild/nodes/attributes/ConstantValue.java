package deobber.rebuild.nodes.attributes;

import java.nio.ByteBuffer;

import deobber.rebuild.nodes.ConstantPool;

public class ConstantValue extends Attribute {
	public final Object value;

	public ConstantValue(Object value) {
		this.value = value;
	}

	public static ConstantValue construct(ConstantPool constantPool,
			ByteBuffer data) {
		short value_index = data.getShort();
		Object value = constantPool.get(value_index).get();
		return new ConstantValue(value);

	}
}
