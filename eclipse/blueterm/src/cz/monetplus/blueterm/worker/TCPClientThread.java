package cz.monetplus.blueterm.worker;

import java.io.IOException;

import android.util.Log;
import cz.monetplus.blueterm.common.ObjectThreads;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.server.TCPClient;
import cz.monetplus.blueterm.terminals.TerminalCommands;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;

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

    private MessageThread mHandler;

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
     *            TODO: dodelat timeout.
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
    public void sendMessage(byte[] sendData) {
        if (mTcpClient != null) {
            try {
                mTcpClient.sendMessage(sendData);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                failedConnection();
            }
        } else {
            failedConnection();
        }

    }

    /**
     * Send terminal empty data. for invalid connection
     */
    private void failedConnection() {

        TerminalFrame termFrame = new TerminalFrame(
                TerminalPortApplications.SERVER.getPortApplicationNumber(),
                new ServerFrame(TerminalCommands.ServerRead, connectionId,
                        new byte[0]).createFrame());

        mHandler.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(termFrame.createFrame())));
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
                                TerminalPortApplications.SERVER
                                        .getPortApplicationNumber(),
                                new ServerFrame(TerminalCommands.ServerRead,
                                        connectionId, message).createFrame());

                        // send to terminal
                        mHandler.addMessage(new HandleMessage(
                                HandleOperations.TerminalWrite, SLIPFrame
                                        .createFrame(termFrame.createFrame())));
                    }
                });

        mTcpClient.run();
    }

    public void interrupt() {

        if (mTcpClient != null) {
            failedConnection();
            mTcpClient.stopClient();
            mTcpClient = null;
        }

        super.interrupt();
    }

}
