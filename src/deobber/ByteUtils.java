package deobber;

public class ByteUtils {

	public static short toShort(byte b1, byte b2) {
		return (short) ((b1 << 8) | (b2 & 0xFF));
	}

	public static short toShort(byte[] args) {
		assert args.length == 2;
		return toShort(args[0], args[1]);
	}

}
