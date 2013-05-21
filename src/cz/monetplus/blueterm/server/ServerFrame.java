package cz.monetplus.blueterm.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import android.util.Log;

/**
 * Frame of server communication.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class ServerFrame {
    /**
     * Tag for logging.
     */
    private static final String TAG = "ServerFrame";

    /**
     * Command in server communication.
     */
    private byte command;

    /**
     * Session id.
     */
    private byte[] id = new byte[2];

    /**
     * Data inside frame to/from server.
     */
    private byte[] data;

    /**
     * Constructor.
     * 
     * @param command
     *            Server command.
     * @param id
     *            Sessiong id.
     * @param data
     *            Data.
     */
    public ServerFrame(byte command, byte[] id, byte[] data) {
        super();
        this.command = command;
        this.id = id;
        this.data = data;
    }

    /**
     * Constructor.
     * 
     * @param command
     *            Server command.
     * @param id
     *            Sessiong id.
     * @param data
     *            Data.
     */
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
            stream.write(this.getCommand());
            stream.write(this.getId());
            if (this.getData() != null) {
                stream.write(this.getData());
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return stream.toByteArray();
    }
}
