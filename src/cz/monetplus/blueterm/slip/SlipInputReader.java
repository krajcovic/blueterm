package cz.monetplus.blueterm.slip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
            stream.read(tempBuffer);
        } while (tempBuffer[0] != SLIPFrame.END);

        slip.write(tempBuffer);
        do {
            stream.read(tempBuffer);
            slip.write(tempBuffer);
        } while (tempBuffer[0] != SLIPFrame.END);

        //Log.d(TAG, slip.toString());
        Log.d("term<<<", "Read from terminal: " + slip.size());
        Log.d("term<<<", MonetUtils.bytesToHex(slip.toByteArray()));
        return slip.toByteArray();
    }
}
