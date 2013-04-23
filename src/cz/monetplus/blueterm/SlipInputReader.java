package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import cz.monetplus.blueterm.util.MonetUtils;

public class SlipInputReader {

	private static final String TAG = "SlipInputReader";

	public static byte[] read(InputStream stream) throws IOException {
		// return super.read(buffer, offset, length);
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

		Log.d(TAG, slip.toString());
		Log.d(TAG, MonetUtils.bytesToHex(slip.toByteArray()));
		return slip.toByteArray();
	}
}
