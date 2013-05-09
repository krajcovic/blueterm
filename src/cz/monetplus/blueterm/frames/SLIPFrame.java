package cz.monetplus.blueterm.frames;

import java.io.ByteArrayOutputStream;

/**
 * Simple SLIP utility class based on the SLIP RFC 1055
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class SLIPFrame {

    /**
     * indicates end of packet
     */
    public static byte END = (byte) 0xC0;

    /**
     * indicates byte stuffing
     */
    public static byte ESC = (byte) 0xDB;

    /**
     * ESC ESC_END means END data byte
     */
    public static byte ESC_END = (byte) 0xDC;

    /**
     * ESC ESC_ESC means ESC data byte
     */
    public static byte ESC_ESC = (byte) 0xDD;

    /**
     * Private constructor.
     */
    private SLIPFrame() {
        super();
    }

    /*
     * utility static method that takes a byte array of data and formats it into
     * a SLIP frame, ready for transmission. The returned frame does NOT contain
     * the END character used to denote the frame itself. It is typical that the
     * END character is sent before transmitting the frame and after
     * transmitting the frame.
     */
    public static byte[] createFrame(byte[] _data) {
        // worst case scenario is we have to sub every byte resulting in a
        // doubled buffer
        ByteArrayOutputStream bout = new ByteArrayOutputStream(
                _data.length * 2 + 2);

        bout.write((byte) END);

        for (int i = 0; i < _data.length; i++) {
            if (_data[i] == END) {
                bout.write((byte) ESC);
                bout.write((byte) ESC_END);
            } else if (_data[i] == ESC) {
                bout.write(ESC);
                bout.write(ESC_ESC);
            } else {
                bout.write(_data[i]);
            }
        }

        bout.write((byte) END);

        return bout.toByteArray();
    }

    /*
     * utility static method that takes a byte array coming from a SLIP frame in
     * order to "decode" it. The start/stop END characters should be stripped
     * prior to passing in the frame for processing. A byte array of the
     * original data will be returned.
     */

    public static byte[] parseFrame(byte[] data) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(data.length - 2);

        for (int i = 1; i < data.length - 1; i++) {
            // if we have and esc and another byte...
            if (data[i] == ESC && i + 1 < data.length) {
                i++;
                if (data[i] == ESC_END) {
                    bout.write(END);
                } else if (data[i] == ESC_ESC) {
                    bout.write(ESC);
                } else {
                    bout.write(data[i]);
                }
            } else {
                bout.write(data[i]);
            }
        }

        return bout.toByteArray();

    }

    public static byte[] getFirstFrame(byte[] data) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(data.length);

        for (byte b : data) {
            bout.write(b);
            if (b == END) {
                // return bout.toByteArray();
                break;
            }
        }

        return bout.toByteArray();
    }

    public static boolean isFrame(byte[] data) {

        for (byte b : data) {
            if (b == END) {
                return true;
            }
        }

        return false;
    }
}
