package io.amps.decaf.structure.attributes;

import io.amps.decaf.structure.ConstantPool;
import io.amps.decaf.structure.MethodNode;
import io.amps.decaf.structure.Node;

import java.nio.ByteBuffer;

public class Attribute {

	public static Attribute construct(final Node node,
			final ConstantPool constantPool, final ByteBuffer buffer) {
		final short attrNameIndex = buffer.getShort();
		final int attrLength = buffer.getInt();
		final byte[] info = new byte[attrLength];
		buffer.get(info, 0, attrLength);
		final String type = constantPool.getString(attrNameIndex);
		final ByteBuffer infoBuffer = ByteBuffer.wrap(info);
		if (type.equals("Code")) {
			return CodeAttribute.construct((MethodNode) node, constantPool,
					infoBuffer);
		} else if (type.equals("ConstantValue")) {
			return ConstantValue.construct(constantPool, infoBuffer);
		} else if (type.equals("InnerClasses")) {
			return Attribute.construct(node, constantPool, infoBuffer);
		}
		return new Attribute();
	}

	Attribute() {

	}

}
