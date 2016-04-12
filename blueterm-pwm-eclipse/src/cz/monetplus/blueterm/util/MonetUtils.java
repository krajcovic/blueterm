package cz.monetplus.blueterm.util;

/**
 * Functions for personal purposes.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public final class MonetUtils {

    public static final long THREAD_RUN_SLEEP = 200;

    /**
     * Private constructor.
     */
    private MonetUtils() {
        super();
    }

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

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);

        return c;
    }

    public static int getInt(byte low, byte high) {
        int ret = low & 0xFF;
        ret <<= 8;
        ret |= high & 0xFF;
        ret &= 0xFFFF;

        return ret;
    }

    public static byte getLow(int number) {
        return (byte) (number & 0xFF);
    }

    public static byte getHigh(int number) {
        return (byte) ((number >> 8) & 0xFF);
    }
}
