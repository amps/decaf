package zeroxff;

public class ByteUtils {

	public static short toShort(byte b1, byte b2) {
		return (short) ((b1 << 8) | (b2 & 0xFF));
	}

	public static Number toNumber(byte[] args) {
		switch (args.length) {
		case 1:
			return toByte(args[0]);
		case 2:
			return toShort(args[0], args[1]);
		case 4:
			return toInt(args[0], args[1], args[2], args[3]);
		default:
			throw new UnsupportedOperationException();
		}
	}

	public static int toInt(byte b1, byte b2, byte b3, byte b4) {
		return ((0xFF & b1) << 24) | ((0xFF & b2) << 16) | ((0xFF & b3) << 8)
				| (0xFF & b4);
	}

	public static short toByte(byte b) {
		return (short) (b & 0x000000FF);
	}
}
