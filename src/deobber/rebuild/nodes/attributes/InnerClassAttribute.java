package deobber.rebuild.nodes.attributes;

import java.nio.ByteBuffer;

import deobber.rebuild.nodes.ConstantPool;
import deobber.rebuild.nodes.MethodNode;

public class InnerClassAttribute extends Attribute {
	public static InnerClassAttribute construct(MethodNode mNode,
			ConstantPool constantPool, ByteBuffer data) {
		System.out.println(":))");
		return null;
	}
}
