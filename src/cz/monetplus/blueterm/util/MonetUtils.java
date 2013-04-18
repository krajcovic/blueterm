package cz.monetplus.blueterm.util;

public class MonetUtils {

	public static String bytesToHex(byte[] bytes) {
		return bytesToHex(bytes, bytes.length);
	}

	public static String bytesToHex(byte[] bytes, int len) {
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[len * 2];
		int v;
		for (int j = 0; j < len; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
