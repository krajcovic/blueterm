package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import cz.monetplus.blueterm.bprotocol.BProtocol;
import cz.monetplus.blueterm.bprotocol.BProtocolFactory;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.bprotocol.BProtocolTag;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.server.TCPClient;
import cz.monetplus.blueterm.util.MonetUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MonetBTAPI {

    private static final String TAG = "MonetBTAPI";

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    /**
     * Local Bluetooth adapter.
     */
    private BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the chat services.
     */
    private static TerminalServiceBT terminalService = null;

    /**
     * 
     */
    private static ByteArrayOutputStream slipOutputpFraming = null;

    /**
     * 
     */
    private static Context applicationContext = null;

    /**
     * Input transaction data.
     */
    private static TransactionIn inputData = null;

    /**
     * Output transaction data.
     */
    private static TransactionOut outputData = null;

    private static TCPServerThread tcpThread = null;

    // The Handler that gets information back from the BluetoothChatService
    private static Handler mHandler = null;

    /**
     * @param context
     *            Application context.
     * @param in
     *            Transcation input parameters.
     * @return true for corect connected device. false for some error.
     */
    public final TransactionOut doTransaction(final Context context,
            final TransactionIn in) {

        applicationContext = context;
        inputData = in;
        outputData = new TransactionOut();
        
        if (Looper.myLooper() == null) {
            Log.d(TAG, "Looper.prepare()");
            Looper.prepare();
        } else {
            Log.d(TAG, "Nevim proc je myLooper uz alocovan");
        }
        

        if (create()) {
            if (start()) {
                connectDevice(inputData.getBlueHwAddress(), false);

                // Pockej dokud neskonci spojovani
                while (terminalService.getState() == ConnectionState.STATE_CONNECTING) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                //
                if (terminalService.getState() == ConnectionState.STATE_CONNECTED) {
                    // Zacni vykonavat smycku
                    Looper.loop();
                } else {
                    // nepodarilo se spojit, tak vsechno uklidime
                    stop();
                }
            }
        }

        return outputData;

    }

    /**
     * Create objects and variables.
     * 
     * @return true for corect creating.
     */
    private Boolean create() {
        Log.e(TAG, "+++ ON CREATE +++");

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        slipOutputpFraming = new ByteArrayOutputStream();
        slipOutputpFraming.reset();

        // relate the listView from java to the one created in xml
        return true;
    }

    /**
     * check bluetooth and start setting.
     * 
     * @return True for corect setup.
     */
    private Boolean start() {
        Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(applicationContext, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            // Otherwise, setup the chat session
        } else {
            if (terminalService == null) {
                setupTerminal();
                return true;
            }
        }

        return false;
    }

    private void stop() {
        Log.e(TAG, "++ ON STOP ++");
        
        if (terminalService != null) {
            terminalService.stop();
            terminalService = null;
        }

        if (tcpThread != null) {
            tcpThread.interrupt();
            tcpThread = null;
        }

        if(mHandler != null) {
        mHandler.getLooper().quit();
        mHandler.removeCallbacks(null);
        mHandler = null;
        }
        
        Looper.myLooper().quit();
        Log.d(TAG, "Looper.myLooper().quit()");
    }

    private void setupTerminal() {
        Log.d(TAG, "setupTerminal()");
        mHandler = new Handler() {
            private byte[] idConnect;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case HandleMessages.MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                    case ConnectionState.STATE_CONNECTED:
                        // isConnected = true;
                        switch (inputData.getCommand()) {
                        case HANDSHAKE:
                            handshake();
                            break;
                        case INFO:
                            appInfo();
                            break;
                        case PAY:
                            pay();
                            break;
                        case UNKNOWN:
                            break;
                        default:
                            break;

                        }
                        break;
                    case ConnectionState.STATE_CONNECTING:
                    case ConnectionState.STATE_LISTEN:
                        break;
                    case ConnectionState.STATE_NONE:
                        // Looper.myLooper().quit();
                        break;
                    }
                    break;
                case HandleMessages.MESSAGE_SERVER_WRITE:
                    break;
                case HandleMessages.MESSAGE_SERVER_READ:
                    break;

                case HandleMessages.MESSAGE_TERM_WRITE:
                    break;

                case HandleMessages.MESSAGE_CONNECTED:
                    byte[] status = new byte[1];
                    status[0] = (byte) msg.arg1;
                    ServerFrame soFrame = new ServerFrame((byte) 0x05,
                            idConnect, status);
                    TerminalFrame toFrame = new TerminalFrame(
                            TerminalPorts.SERVER.getPortNumber(),
                            soFrame.createFrame());

                    send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));

                    break;

                case HandleMessages.MESSAGE_TERM_READ:
                    byte[] readSlipFrame = (byte[]) msg.obj;
                    slipOutputpFraming.write(readSlipFrame, 0, msg.arg1);

                    // Check
                    if (SLIPFrame.isFrame(slipOutputpFraming.toByteArray())) {

                        TerminalFrame termFrame = new TerminalFrame(
                                SLIPFrame.parseFrame(slipOutputpFraming.toByteArray()));
                        slipOutputpFraming.reset();

                        if (termFrame != null) {
                            switch (termFrame.getPort()) {
                            case UNDEFINED:
                                Log.d(TAG, "undefined port");
                                break;
                            case SERVER:
                                handeServerMessage(termFrame);
                                break;
                            case FLEET:
                                Log.d(TAG, "fleet data");
                                break;
                            case MAINTENANCE:
                                Log.d(TAG, "maintentace data");
                                break;
                            case MASTER:
                                // Tyhle zpravy zpracovavat, jsou pro tuhle
                                // aplikaci
                                BProtocolFactory factory = new BProtocolFactory();
                                BProtocol bprotocol = factory
                                        .deserialize(termFrame.getData());

                                if (bprotocol.getProtocolType().equals("B2")) {

                                    try {
                                        outputData
                                                .setResultCode(Integer
                                                        .valueOf(bprotocol
                                                                .getTagMap()
                                                                .get(BProtocolTag.ResponseCode)));
                                    } catch (Exception e) {
                                        outputData.setResultCode(-1);
                                    }
                                    outputData
                                            .setServerMessage(bprotocol
                                                    .getTagMap()
                                                    .get(BProtocolTag.ServerMessage));
                                    try {
                                        outputData
                                                .setAuthCode(Integer
                                                        .valueOf(bprotocol
                                                                .getTagMap()
                                                                .get(BProtocolTag.AuthCode)));
                                    } catch (Exception e) {
                                        outputData.setAuthCode(0);
                                    }
                                    try {
                                        outputData
                                                .setSeqId(Integer
                                                        .valueOf(bprotocol
                                                                .getTagMap()
                                                                .get(BProtocolTag.SequenceId)));
                                    } catch (Exception e) {
                                        outputData.setSeqId(0);
                                    }
                                    outputData.setCardNumber(bprotocol
                                            .getTagMap().get(BProtocolTag.PAN));
                                    outputData.setCardType(bprotocol
                                            .getTagMap().get(
                                                    BProtocolTag.CardType));

                                    stop();
//                                    Looper.myLooper().quit();

                                }

                                break;
                            default:
                                // Nedelej nic, spatne data, format, nebo
                                // crc
                                Log.e(TAG, "Invalid port");
                                break;

                            }
                        }

                    } else {
                        Log.e(TAG, "Corrupted data. It's not slip frame.");
                    }

                    break;
                case HandleMessages.MESSAGE_DEVICE_NAME:
                    break;
                case HandleMessages.MESSAGE_QUIT:
                    stop();
                    break;
                }

                // return false;
            }

            private void handeServerMessage(TerminalFrame termFrame) {
                // sends the message to the server
                final ServerFrame serverFrame = new ServerFrame(
                        termFrame.getData());
                ServerFrame responseServer = null;
                TerminalFrame responseTerminal = null;

                Log.d(TAG, "Server command: " + serverFrame.getCommand());
                switch (serverFrame.getCommand()) {
                case TerminalCommands.TERM_CMD_ECHO:
                    responseServer = new ServerFrame((byte) TerminalCommands.TERM_CMD_ECHO_RES,
                            serverFrame.getId(), null);
                    responseTerminal = new TerminalFrame(termFrame.getPort()
                            .getPortNumber(), responseServer.createFrame());

                    send2Terminal(SLIPFrame.createFrame(responseTerminal
                            .createFrame()));
                    break;

                case TerminalCommands.TERM_CMD_CONNECT:
                    idConnect = serverFrame.getId();

                    int port = MonetUtils.getInt(serverFrame.getData()[4],
                            serverFrame.getData()[5]);

                    int timeout = MonetUtils.getInt(serverFrame.getData()[6],
                            serverFrame.getData()[7]);

                    // connect to the server
                    tcpThread = new TCPServerThread(Arrays.copyOfRange(
                            serverFrame.getData(), 0, 4), port, timeout,
                            serverFrame.getIdInt());
                    Log.i(TAG, "TCP thread starting.");
                    tcpThread.start();

                    // TCPconnect connect = new
                    // TCPconnect(Arrays.copyOfRange(serverFrame.getData(),
                    // 0,
                    // 4),
                    // port, timeout, serverFrame.getIdInt());
                    // connect.doInBackground("");

                    responseServer = new ServerFrame(
                            (byte) TerminalCommands.TERM_CMD_CONNECT_RES, serverFrame.getId(),
                            new byte[1]);
                    responseTerminal = new TerminalFrame(termFrame.getPort()
                            .getPortNumber(), responseServer.createFrame());

                    send2Terminal(SLIPFrame.createFrame(responseTerminal
                            .createFrame()));

                    break;

                case TerminalCommands.TERM_CMD_DISCONNECT:

                    if (tcpThread != null) {
                        tcpThread.interrupt();
                        tcpThread = null;
                    }
                    break;

                case TerminalCommands.TERM_CMD_SEND:
                    tcpThread.send(serverFrame.getData());
                }

            }
        };
        // }

        // Initialize the BluetoothChatService to perform bluetooth connections
        terminalService = new TerminalServiceBT(applicationContext, mHandler);
    }

    private static void pay() {
        send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
                BProtocolMessages.getSale(inputData.getAmount(),
                        inputData.getCurrency(), inputData.getInvoice()))
                .createFrame()));
    }

    private static void handshake() {
        send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
                BProtocolMessages.getHanshake()).createFrame()));
    }

    private static void appInfo() {
        send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
                BProtocolMessages.getAppInfo()).createFrame()));
    }

    /**
     * Get the BluetoothDevice object.
     * 
     * @param address
     *            HW address of bluetooth.
     * @param secure
     *            True for secure connection, false for insecure.
     */
    private void connectDevice(String address, boolean secure) {
        // Get the BLuetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        
        // Attempt to connect to the device
        terminalService.connect(device, secure);
    }

    /**
     * Sends a message.
     * 
     * @param message
     *            A string of text to send.
     */
    private static void send2Terminal(byte[] message) {
        // Check that we're actually connected before trying anything
        if (terminalService.getState() != ConnectionState.STATE_CONNECTED) {
            Toast.makeText(applicationContext, R.string.not_connected,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            // byte[] send = message.getBytes();
            terminalService.write(message);

            // Reset out string buffer to zero and clear the edit text field
            // mOutStringBuffer.setLength(0);
            // mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * @author "Dusan Krajcovic"
     *
     */
    public final class TCPServerThread extends Thread {

        private static final String TAG = "arrayList";

        private byte[] serverIp;

        private int serverPort;
        
        private int connectionId;
        
        private int timeout;

        /**
         * TCP client.
         */
        private TCPClient mTcpClient = null;

        /**
         * @param serverIp
         * @param serverPort
         * @param timeout
         * @param connectionId
         */
        public TCPServerThread(byte[] serverIp, int serverPort, int timeout,
                int connectionId) {
            super();
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            this.connectionId = connectionId;
            this.timeout = timeout;

            Log.d(TAG, "TCPconnectTask to " + (serverIp[0] & 0xff) + "."
                    + (serverIp[1] & 0xff) + "." + (serverIp[2] & 0xff) + "."
                    + (serverIp[3] & 0xff) + ":" + serverPort + "["
                    + connectionId + "]");
        }

        /**
         * Send data to server.
         * @param sendData
         */
        public void send(byte[] sendData) {
            if (mTcpClient != null) {
                try {
                    mTcpClient.sendMessage(sendData);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
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
                            // this method calls the onProgressUpdate
                            // publishProgress(message);

                            ServerFrame soFrame = new ServerFrame((byte) 0x04,
                                    connectionId, message);

                            TerminalFrame termFrame = new TerminalFrame(
                                    TerminalPorts.SERVER.getPortNumber(),
                                    soFrame.createFrame());

                            // send to terminal
                            // TODO: tady bych asi mel odeslat data do handleru.
                            send2Terminal(SLIPFrame.createFrame(termFrame
                                    .createFrame()));
                            //mHandler.hasMessages(TERM_CMD_SEND, termFrame.createFrame());
                        }
                    });
            
            mTcpClient.run();
        }

        @Override
        public void interrupt() {

            if (mTcpClient != null) {
                mTcpClient.stopClient();
//                try {
//                    wait(500);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
                mTcpClient = null;
            }

            super.interrupt();
        }

    }
}
