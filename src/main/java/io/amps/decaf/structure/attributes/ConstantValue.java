package io.amps.decaf.structure.attributes;

import io.amps.decaf.structure.ConstantPool;

import java.nio.ByteBuffer;

public class ConstantValue extends Attribute {
	public static ConstantValue construct(final ConstantPool constantPool,
			final ByteBuffer data) {
		final short value_index = data.getShort();
		final Object value = constantPool.get(value_index).get();
		return new ConstantValue(value);

	}

	public final Object value;

	public ConstantValue(final Object value) {
		this.value = value;
	}
}
