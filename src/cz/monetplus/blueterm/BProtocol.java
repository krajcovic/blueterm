package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BProtocol {
	private static final byte STX = 0x02;
	private static final byte ETX = 0x03;
	private static final byte FS = 0x1c;

	public static byte[] getAppInfo() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		try {
			bout.write(STX);
			bout.write("B101        120221150138       4A5A5".getBytes());
			bout.write(FS);
			bout.write("T80".getBytes());
			bout.write(ETX);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bout.toByteArray();
	}
}
