package cz.monetplus.blueterm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.util.Log;
import cz.monetplus.blueterm.MessageThread;
import cz.monetplus.blueterm.MonetBTAPIError;
import cz.monetplus.blueterm.util.MonetUtils;

/**
 * Class executing communication with server.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class TCPClient {

    /**
     * TAG for debugging.
     */
    private static final String TAG = "TCPClient";

    private byte[] serverMessage;

    /**
     * Server IP address.
     */
    private final byte[] serverIp;

    /**
     * Server port.
     */
    private final int serverPort;

    /**
     * Listener for received messages.
     */
    private OnMessageReceived mMessageListener = null;

    /**
     * Message handler.
     */
    private final MessageThread mHandler;

    private boolean isRunning = false;

    private OutputStream out;

    private InputStream in;

    private final int timeout;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages
     * received from server
     * 
     * @param ip
     *            Server IP address.
     * @param port
     *            Server port.
     * @param timeout
     *            Timeout for connection.
     * @param handler
     *            Message handler.
     * @param listener
     *            Receive message listener.
     */
    public TCPClient(byte[] ip, int port, int timeout, MessageThread handler,
            OnMessageReceived listener) {
        this.serverIp = ip;
        this.serverPort = port;
        this.mHandler = handler;
        this.timeout = timeout;
        mMessageListener = listener;
    }

    /**
     * @param message
     *            Byte buffer with data to send to stream.
     * @throws IOException
     *             Socket exception.
     */
    public final void sendMessage(byte[] message) throws IOException {
        if (out != null) {
            Log.d(TAG, MonetUtils.bytesToHex(message));
            out.write(message);
            out.flush();
        }
    }

    public final void stopClient() {
        isRunning = false;
    }

    public final void run() {

        Socket socket = null;
        isRunning = true;

        try {
            // here you must put your computer's IP address.
            // InetAddress serverAddr = InetAddress.getByName(this.serverIp);
            InetAddress serverAddr = InetAddress.getByAddress(null,
                    this.serverIp);

            Log.e("TCP Client", "C: Connecting...");

            // create a socket to make the connection with the server
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddr, this.serverPort),
                    timeout);

            try {

                out = socket.getOutputStream();

                Log.d(TAG, "TCP read starting");

                in = socket.getInputStream();

                mHandler.addMessageConnected((byte) 0);
                mHandler.addMessageToast("Connected to server.");

                // in this while the client listens for the messages sent by the
                // server
                while (isRunning) {
                    int len = in.available();
                    if (len > 0) {
                        serverMessage = new byte[Math.min(len, 1024)];
                        len = in.read(serverMessage);
                        Log.d(TAG, "TCP read " + len + " bytes. Expected "
                                + serverMessage.length + " bytes");

                        if (serverMessage != null && serverMessage.length > 0
                                && mMessageListener != null) {
                            // call the method messageReceived from MyActivity
                            // class
                            mMessageListener.messageReceived(serverMessage);
                        }
                        serverMessage = null;
                    } else {
                        // Log.d(TAG, "Sleeping tread");
                        // Thread.sleep(100);
                    }

                }
                Log.d(TAG, "TCP read finished");
            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);
                e.printStackTrace();

                if (isRunning) {
                    // Selhala necekane tcp comunikace, takze to ukoncime, cele.
                    mHandler.addMessageQuit(MonetBTAPIError.SERVER_COM_FAILED);
                }

            } finally {
                // the socket must be closed. It is not possible to reconnect to
                // this socket
                // after it is closed, which means a new socket instance has to
                // be created.
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
            e.printStackTrace();
            // Nepodarilo se navazat spojeni.
            mHandler.addMessageConnected((byte) 1);
        }

        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    // Declare the interface. The method messageReceived(String message) will
    // must be implemented in the MyActivity
    // class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(byte[] message);
    }

    public final boolean isConnected() {
        return isRunning;
    }
}
