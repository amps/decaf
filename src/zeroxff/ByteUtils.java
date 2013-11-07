package zeroxff;

public class ByteUtils {

	public static short toShort(byte b1, byte b2) {
		return (short) ((b1 << 8) | (b2 & 0xFF));
	}

	public static short toShort(byte[] args) {
		switch (args.length) {
		case 1:
			return toShort(args[0]);
		case 2:
			return toShort(args[0], args[1]);
		default:
			throw new UnsupportedOperationException();
		}
	}

	public static short toShort(byte b) {
		return (short) (b & 0x000000FF);
	}
}
