package deobber.rebuild.nodes.attributes;

import java.nio.ByteBuffer;

import deobber.rebuild.nodes.ClassNode;
import deobber.rebuild.nodes.ConstantPool;

public class Attribute {

	
	Attribute() {

	}

	public static Attribute construct(ConstantPool constantPool,
			ByteBuffer buffer) {
		short attrNameIndex = buffer.getShort();
		int attrLength = buffer.getInt();
		byte[] info = new byte[attrLength];
		buffer.get(info, 0, attrLength);
		String type = constantPool.getString(attrNameIndex);
		ByteBuffer infoBuffer = ByteBuffer.wrap(info);
		if (type.equals("Code")) {
			return CodeAttribute.construct(constantPool, infoBuffer);
		} else if(type.equals("ConstantValue")) {
			return ConstantValue.construct(constantPool, infoBuffer);
		}
		return new Attribute();
	}

}
