package cz.monetplus.blueterm;

import java.io.IOException;

import android.os.Handler;
import android.util.Log;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.server.TCPClient;

//public final class TCPconnectThread2 extends Thread {
//
//    private static final String TAG = "TCPconnectThread";
//
//    private byte[] serverIp;
//    private int serverPort;
//    private int connectionId;
//    private int timeout;
//    private Handler handler;
//    TerminalListener terminalListener;
//
//    public interface TerminalListener {
//
//        void s2t(byte[] data);
//    }
//
//    /**
//     * TCP client.
//     */
//    private TCPClient mTcpClient = null;
//
//    public TCPconnectThread(byte[] serverIp, int serverPort, int timeout,
//            int connectionId, Handler handler, TerminalListener terminalListener) {
//        super();
//        this.serverIp = serverIp;
//        this.serverPort = serverPort;
//        this.connectionId = connectionId;
//        this.timeout = timeout;
//        this.handler = handler;
//        this.terminalListener = terminalListener;
//
//        Log.d(TAG, "TCPconnectTask to " + (serverIp[0] & 0xff) + "."
//                + (serverIp[1] & 0xff) + "." + (serverIp[2] & 0xff) + "."
//                + (serverIp[3] & 0xff) + ":" + serverPort + "[" + connectionId
//                + "]");
//    }
//
//
//    public void send(byte[] sendData) {
//        if (mTcpClient != null) {
//            try {
//                mTcpClient.sendMessage(sendData);
//            } catch (IOException e) {
//                Log.d(TAG, e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        super.run();
//
//        // we create a TCPClient object and
//        mTcpClient = new TCPClient(serverIp, serverPort, timeout, handler,
//                new TCPClient.OnMessageReceived() {
//
//                    @Override
//                    // here the messageReceived method is implemented
//                    public void messageReceived(byte[] message) {
//                        // this method calls the onProgressUpdate
//                        // publishProgress(message);
//
//                        ServerFrame soFrame = new ServerFrame((byte) 0x04,
//                                connectionId, message);
//
//                        TerminalFrame termFrame = new TerminalFrame(
//                                TerminalPorts.SERVER.getPortNumber(),
//                                soFrame.createFrame());
//
//                        // send to terminal
//                        terminalListener.s2t(SLIPFrame
//                                .createFrame(termFrame.createFrame()));
//                    }
//                });
//        mTcpClient.run();
//    }
//
//    @Override
//    public void interrupt() {
//
//        if (mTcpClient != null) {
//            mTcpClient.stopClient();
//        }
//
//        super.interrupt();
//    }
//
//}
