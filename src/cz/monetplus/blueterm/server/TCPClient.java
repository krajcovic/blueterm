package cz.monetplus.blueterm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import cz.monetplus.blueterm.BluetoothChat;
import cz.monetplus.blueterm.util.MonetUtils;

import android.os.Handler;
import android.util.Log;

public class TCPClient {
    private static final String TAG = "TCPClient";
    private byte[] serverMessage;

    private byte[] serverIp; // your computer IP
                             // address
    private int serverPort;
    private OnMessageReceived mMessageListener = null;
    private Handler mHandler;
    private boolean mRun = false;

    OutputStream out;
    InputStream in;

    private int timeout;

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
    public TCPClient(byte[] ip, int port, int timeout, Handler handler,
            OnMessageReceived listener) {
        this.serverIp = ip;
        this.serverPort = port;
        this.mHandler = handler;
        this.timeout = timeout;
        mMessageListener = listener;
    }

    public void sendMessage(byte[] message) throws IOException {
        if (out != null) {
            out.write(message);
            out.flush();

            // Log.d(TAG, new String(message, "UTF-8"));
            Log.d(TAG, MonetUtils.bytesToHex(message));

            mHandler.obtainMessage(BluetoothChat.MESSAGE_SERVER_WRITE, -1, -1,
                    message).sendToTarget();
        }
    }

    public void stopClient() {
        mRun = false;
    }

    public void run() {

        mRun = true;

        try {
            // here you must put your computer's IP address.
            // InetAddress serverAddr = InetAddress.getByName(this.serverIp);
            InetAddress serverAddr = InetAddress.getByAddress(null,
                    this.serverIp);

            Log.e("TCP Client", "C: Connecting...");

            // create a socket to make the connection with the server
            // Socket socket = new Socket(serverAddr, this.serverPort);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddr, this.serverPort),
                    timeout);

            // SSLSocket socket = new SSLSocket(serverAddr, this.serverPort);

            try {

                // send the message to the server
                // out = new PrintWriter(new BufferedWriter(
                // new OutputStreamWriter(socket.getOutputStream())), true);
                out = socket.getOutputStream();

                Log.d(TAG, "TCP read starting");

                // receive the message which the server sends back
                // in = new BufferedReader(new InputStreamReader(
                // socket.getInputStream()));
                in = socket.getInputStream();

                mHandler.obtainMessage(BluetoothChat.MESSAGE_CONNECTED, 0, -1,
                        null).sendToTarget();

                // in this while the client listens for the messages sent by the
                // server
                while (mRun) {
                    int len = in.available();
                    if (len > 0) {
                        serverMessage = new byte[Math.min(len, 700)];
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
//                        Thread.sleep(100);
                    }

                }
                Log.d(TAG, "TCP read finished");
            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);
                if(mRun) {
                    // Uz koncime, takze nic nikam neposilej
                    mHandler.obtainMessage(BluetoothChat.MESSAGE_CONNECTED, 2, -1,
                        null).sendToTarget();
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
            mHandler.obtainMessage(BluetoothChat.MESSAGE_CONNECTED, 1, -1, null)
                    .sendToTarget();

        }

    }

    // Declare the interface. The method messageReceived(String message) will
    // must be implemented in the MyActivity
    // class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(byte[] message);
    }

    public boolean isConnected() {
        return mRun;
    }
}
