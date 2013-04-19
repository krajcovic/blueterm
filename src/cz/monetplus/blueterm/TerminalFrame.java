package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.util.Log;

public class TerminalFrame {

	private static final String TAG = "TerminalFrame";

	/**
	 * Port 2Bytes
	 */
	private int port;

	/**
	 * Protokol data
	 */
	private byte[] data;

	/**
	 * CRC - PPP-FCS (RFC 1134)
	 */
	private int crc;

	public TerminalFrame() {
		super();
	}

	public TerminalFrame(byte[] data) {
		super();

		parseFrame(data);
	}

	public TerminalFrame(int i, byte[] data) {
		this.setPort(i);
		this.setData(data);
		this.setCrc(CRCFCS.pppfcs(CRCFCS.PPPINITFCS, createCountedPart()));
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public int getCrc() {
		return crc;
	}

	public void setCrc(int crc) {
		this.crc = crc;
	}

	private byte[] createCountedPart() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			stream.write(ByteBuffer.allocate(2).putInt(this.getPort()).array());
			stream.write(this.getData());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return stream.toByteArray();
	}

	public byte[] createFrame() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			stream.write(ByteBuffer.allocate(2).putInt(this.getPort()).array());
			stream.write(this.getData());
			stream.write(ByteBuffer.allocate(2).putInt(this.getCrc()).array());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return stream.toByteArray();
	}

	public void parseFrame(byte[] data) {
		this.setPort(data[0] << 8 + data[1]);
		this.setData(Arrays.copyOfRange(data, 2, data.length - 1));
		this.setCrc(data[data.length - 1]);
	}
}
