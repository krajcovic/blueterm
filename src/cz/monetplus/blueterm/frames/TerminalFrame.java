package cz.monetplus.blueterm.frames;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import cz.monetplus.blueterm.TerminalPorts;
import cz.monetplus.blueterm.util.CRCFCS;
import cz.monetplus.blueterm.util.MonetUtils;

import android.util.Log;

public class TerminalFrame {

	private static final String TAG = "TerminalFrame";

	/**
	 * Port 2Bytes
	 */
	private TerminalPorts port;

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

	public TerminalPorts getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = TerminalPorts.valueOf(port & 0xFFFF);
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

	public final void setCrc(final int crc) {
		this.crc = crc;
	}

	private byte[] createCountedPart() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			stream.write(MonetUtils.getHigh(this.getPort().getPortNumber()));
			stream.write(MonetUtils.getLow(this.getPort().getPortNumber()));
			stream.write(this.getData());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return stream.toByteArray();
	}

	public byte[] createFrame() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			// stream.write(ByteBuffer.allocate(2).putInt(this.getPort()).array());
			stream.write(MonetUtils.getHigh(this.getPort().getPortNumber()));
			stream.write(MonetUtils.getLow(this.getPort().getPortNumber()));
			stream.write(this.getData());
			// stream.write(ByteBuffer.allocate(2).putInt(this.getCrc()).array());
			stream.write((byte) this.getCrc() & 0xFF);
			stream.write((byte) (this.getCrc() >> 8) & 0xFF);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return stream.toByteArray();
	}

	public void parseFrame(byte[] data) {
		if (data != null && data.length > 4) {

			int d = data[data.length - 1] & 0xFF;
			d <<= 8;
			d += data[data.length - 2] & 0xFF;
			d &= 0xFFFF;
			this.setCrc(d);

			int countedCrc = CRCFCS.pppfcs(CRCFCS.PPPINITFCS, data,
					data.length - 2) & 0xFFFF;

			if (this.getCrc() == countedCrc) {
				this.setPort((data[0] << 8) + data[1]);
				this.setData(Arrays.copyOfRange(data, 2, data.length - 2));
			} else {
				Log.d(TAG, "Invalid CRC frame");
			}
		} else {
			Log.d(TAG, "Corrupted terminal frame");
		}
	}
}
