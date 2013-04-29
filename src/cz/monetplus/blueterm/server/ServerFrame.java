package cz.monetplus.blueterm.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.util.Log;

public class ServerFrame {
	private static final String TAG = "ServerFrame";
	private byte command;
	private byte[] id = new byte[2];
	private byte[] data;

	public ServerFrame(byte command, byte[] id, byte[] data) {
		super();
		this.command = command;
		this.id = id;
		this.data = data;
	}

	public ServerFrame(byte command, int id, byte[] data) {
		super();
		this.command = command;
		this.id[1] = (byte) (id & 0xff);
		this.id[0] = (byte) ((id >> 8) & 0xff);
		this.data = data;
	}

	public ServerFrame(byte[] buffer) {
		parseFrame(buffer);
	}

	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	public byte[] getId() {
		return id;
	}

	public int getIdInt() {
		int tmp = id[0] & 0xFF;
		tmp <<= 8;
		tmp = id[1] & 0xFF;
		tmp &= 0xFFFF;
		return tmp;
	}

	public void setId(byte[] id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void parseFrame(byte[] buffer) {
		if (buffer != null) {
			command = buffer[0];
			id[0] = buffer[1];
			id[1] = buffer[2];
			data = Arrays.copyOfRange(buffer, 3, buffer.length);
		}
	}

	public byte[] createFrame() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			// stream.write(ByteBuffer.allocate(2).putInt(this.getPort()).array());
			stream.write(this.getCommand());
			stream.write(this.getId());
			//stream.write(this.getId());
			if (this.getData() != null) {
				stream.write(this.getData());
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

		return stream.toByteArray();
	}
}
