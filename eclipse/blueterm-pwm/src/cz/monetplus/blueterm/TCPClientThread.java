package cz.monetplus.blueterm;

import java.io.IOException;

import android.util.Log;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.server.TCPClient;
import cz.monetplus.blueterm.terminal.TerminalCommands;
import cz.monetplus.blueterm.terminal.TerminalPorts;

/**
 * @author "Dusan Krajcovic"
 * 
 */
public final class TCPClientThread extends Thread implements ObjectThreads {

    private static final String TAG = "TCPClientThread";

    private byte[] serverIp;

    private int serverPort;

    private int connectionId;

    private int timeout;

    private final MessageThread mHandler;

    /**
     * TCP client.
     */
    private TCPClient mTcpClient = null;

    public TCPClientThread(MessageThread mHandler) {
        super();
        this.mHandler = mHandler;
    }

    /**
     * @param serverIp
     *            Server IP address.
     * @param serverPort
     *            Server port.
     * @param timeout
     *            Timeout pro spojeni.
     * @param connectionId
     *            current connection ID.
     */
    public void setConnection(byte[] serverIp, int serverPort, int timeout,
            int connectionId) {

        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.connectionId = connectionId;
        this.timeout = timeout;

        Log.d(TAG, "TCPconnectTask to " + (serverIp[0] & 0xff) + "."
                + (serverIp[1] & 0xff) + "." + (serverIp[2] & 0xff) + "."
                + (serverIp[3] & 0xff) + ":" + serverPort + "[" + connectionId
                + "]");
    }

    /**
     * Send data to server.
     * 
     * @param sendData
     *            Buffer for sending data to server.
     */
    @Override
    public void sendMessage(byte[] sendData) {
        if (mTcpClient != null) {
            try {
                mTcpClient.sendMessage(sendData);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    public void run() {
        super.run();

        // we create a TCPClient object and
        mTcpClient = new TCPClient(serverIp, serverPort, timeout, mHandler,
                new TCPClient.OnMessageReceived() {

                    @Override
                    // here the messageReceived method is implemented
                    public void messageReceived(byte[] message) {
                        TerminalFrame termFrame = new TerminalFrame(
                                TerminalPorts.SERVER.getPortNumber(),
                                new ServerFrame(
                                        TerminalCommands.TERM_CMD_SERVER_READ,
                                        connectionId, message).createFrame());

                        // send to terminal
                        mHandler.addMessageTermWrite(SLIPFrame
                                .createFrame(termFrame.createFrame()));
                    }
                });

        mTcpClient.run();
    }

    @Override
    public void interrupt() {

        mHandler.addMessageToast("Disconecting from server.");

        if (mTcpClient != null) {
            mTcpClient.stopClient();
            mTcpClient = null;
        }

        super.interrupt();
    }

}
