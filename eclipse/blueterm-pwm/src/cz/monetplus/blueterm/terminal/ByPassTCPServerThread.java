package cz.monetplus.blueterm.terminal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

import com.verifone.vmf.api.VMF;

import cz.monetplus.blueterm.MessageThread;
import cz.monetplus.blueterm.MonetBTAPIError;
import cz.monetplus.blueterm.util.MonetUtils;

/**
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
public class ByPassTCPServerThread extends TerminalsThread {

    /**
     * 
     */
    private static final String TAG = "ByPassTCPServerThread";

    /**
     * 
     */
    private static final int DESTINATION_ID = 128;

    /**
     * 
     */
    private ServerSocket serverSocket = null;

    /**
     * 
     */
    private ByPassReceiverThread commThread = null;

    /**
     * 
     */
    private final Integer listenPort;

    public ByPassTCPServerThread(MessageThread messageThread, Integer listenPort)
            throws Exception {
        super(messageThread);
        this.listenPort = listenPort;
    }

    @Override
    public final void run() {
        // Socket socket = null;

        try {
            serverSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            if (serverSocket != null) {
                serverSocket.setSoTimeout(2000);

                int vmfAppLinkSend = VMF.vmfAppLinkSend(DESTINATION_ID,
                        ("127.0.0.1:" + listenPort).getBytes(), 120 * 100);

                Socket socket = serverSocket.accept();

                if (commThread != null) {
                    commThread.interrupt();
                }

                commThread = new ByPassReceiverThread(messageThread, socket);
                commThread.start();

                setState(TerminalState.STATE_CONNECTED);

                while (!Thread.currentThread().isInterrupted()
                        && commThread != null && !commThread.isInterrupted()) {
                    try {
                        sleep(MonetUtils.THREAD_RUN_SLEEP);
                    } catch (InterruptedException e) {

                    }
                }
            } else {
                connectionLost(MonetBTAPIError.SERVER_SOCKET_NULL);
            }
        } catch (IOException e) {
            e.printStackTrace();
            connectionLost(MonetBTAPIError.BYPASS_IO_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            connectionLost(MonetBTAPIError.BYPASS_EXCEPTION);
        }
    }

    @Override
    public final void write(byte[] buffer) throws IOException {
        if (commThread != null && commThread.isAlive()) {
            commThread.write(buffer);
        } else {
            Log.e(TAG, "Communication thread isn't running.");
        }

    }

    @Override
    public final void interrupt() {
        Log.i(TAG, "ByPassTCPServer interrupt");
        if (this.commThread != null) {
            this.commThread.interrupt();
            this.commThread = null;
        }

        try {
            if (this.serverSocket != null) {
                this.serverSocket.close();
                this.serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.interrupt();

    }

}
