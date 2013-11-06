package deobber.rebuild.nodes.attributes;

import java.nio.ByteBuffer;

import deobber.rebuild.nodes.ConstantPool;
import deobber.rebuild.nodes.MethodNode;
import deobber.rebuild.nodes.Node;

public class Attribute {

	Attribute() {

	}

	public static Attribute construct(Node node, ConstantPool constantPool,
			ByteBuffer buffer) {
		short attrNameIndex = buffer.getShort();
		int attrLength = buffer.getInt();
		byte[] info = new byte[attrLength];
		buffer.get(info, 0, attrLength);
		String type = constantPool.getString(attrNameIndex);
		ByteBuffer infoBuffer = ByteBuffer.wrap(info);
		
		if (type.equals("Code")) {
			return CodeAttribute.construct((MethodNode) node, constantPool,
					infoBuffer);
		} else if (type.equals("ConstantValue")) {
			return ConstantValue.construct(constantPool, infoBuffer);
		} else if (type.equals("InnerClasses")) {
			return InnerClassAttribute
					.construct(node, constantPool, infoBuffer);
		}
		return new Attribute();
	}

}
