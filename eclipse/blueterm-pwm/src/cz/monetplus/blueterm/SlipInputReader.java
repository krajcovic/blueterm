package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.util.ByteArrayBuffer;

import android.util.Log;

import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.util.MonetUtils;

/**
 * Read from stream a slip frames.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public final class SlipInputReader {

    /**
     * TAG for logging.
     */
    private static final String TAG = "SlipInputReader";

    private static Boolean exit;

    /**
     * Private constructor.
     */
    private SlipInputReader() {
        super();
    }

    /**
     * Read complete slipframes. From Slipframe.end to slipframe.end.
     * 
     * @param stream
     *            Stream from read.
     * @return Complete slipframes... must be checked for crc.
     * @throws IOException
     *             Problem with stream.
     */
    public static byte[] read(final InputStream stream) throws IOException {
        ByteArrayOutputStream slip = new ByteArrayOutputStream();

        byte[] tempBuffer = new byte[1];

        do {
            if (isExit() || stream == null) {
                return null;
            }
            
            if (stream.available() > 0) {
                stream.read(tempBuffer);
                if (tempBuffer[0] != 0) {
                    Log.d(TAG, MonetUtils.bytesToHex(tempBuffer));
                }
            }
        } while (tempBuffer[0] != SLIPFrame.END);

        slip.write(tempBuffer);
        do {
            if (isExit() || stream == null) {
                return null;
            }
            
            if (stream.available() > 0) {
                stream.read(tempBuffer);
                slip.write(tempBuffer);
            }
        } while (tempBuffer[0] != SLIPFrame.END);

        Log.d(TAG, slip.toString());
        Log.d(TAG, MonetUtils.bytesToHex(slip.toByteArray()));
        return slip.toByteArray();
    }

    public static Boolean isExit() {
        return exit;
    }

    public static void setExit(Boolean exit) {
        SlipInputReader.exit = exit;
    }
}
