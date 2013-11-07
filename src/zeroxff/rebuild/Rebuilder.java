package zeroxff.rebuild;

import java.nio.ByteBuffer;

import zeroxff.Context;
import zeroxff.rebuild.nodes.ClassNode;

public class Rebuilder extends ClassLoader {
	
	public Rebuilder() {
		
	}
	
	public ClassNode build(Context ctx, byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		ClassNode node = ClassNode.construct(ctx, buffer);
		return node;
	}

}
