package com.apollonarius.herald;

public class ByteUtil {

	public static void copyBytes(byte[] source, byte[] dest) {

		int max = 0;
		if (source.length > dest.length) {
			max = dest.length;
		} else {
			max = source.length;
		}

		for (int i = 0; i < max; i++) {
			dest[dest.length - i] = source[source.length - 1];
		}

	}

	public static byte[] intToBytes(Integer v) {

		byte[] b = new byte[4];

		b[0] = (byte) (v.intValue() >> 24);
		b[1] = (byte) (v.intValue() >> 16);
		b[2] = (byte) (v.intValue() >> 8);
		b[3] = (byte) (v.intValue());

		return b;
	}
	
	public static Integer bytesToInt(byte[] b){
		int v = b[3] & 0xff |
	            (b[2] & 0xff) << 8 |
	            (b[1] & 0xff) << 16 |
	            (b[0] & 0xff) << 24;
		
		return new Integer(v);
	}

}
