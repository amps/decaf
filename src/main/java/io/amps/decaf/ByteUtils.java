package io.amps.decaf;

public class ByteUtils {

	public static short toByte(final byte b) {
		return (short) (b & 0x000000FF);
	}

	public static int toInt(final byte b1, final byte b2, final byte b3,
			final byte b4) {
		return (0xFF & b1) << 24 | (0xFF & b2) << 16 | (0xFF & b3) << 8 | 0xFF
				& b4;
	}

	public static Number toNumber(final byte[] args) {
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

	public static short toShort(final byte b1, final byte b2) {
		return (short) ((short) (b1 << 8) | (short) (b2 & 0xFF));
	}
}
