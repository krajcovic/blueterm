package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


/**
 * Exported class for control from pos-system.
 * @author krajcovic
 *
 */
public class MonetBTAPI {
    
    /**
     * Socket port 
     */
    private static final int TERMINALPORT = 33333;

    /**
     * String tag for logging.
     */
    private static final String TAG = "MonetBTAPI";
   
    public static final String TOAST = "Messagebox";

    /**
     * Local Bluetooth adapter.
     */
    private static BluetoothAdapter bluetoothAdapter = null;

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

    private static TCPClientThread tcpThread = null;

    // The Handler that gets information back from the BluetoothChatService
    private static Handler mHandler = null;

    /**
     * @param context
     *            Application context.
     * @param in
     *            Transcation input parameters.
     * @return true for corect connected device. false for some error.
     */
    public final static TransactionOut doTransaction(final Context context,
            final TransactionIn in) {

        applicationContext = context;
        inputData = in;
        outputData = new TransactionOut();

//        if (Looper.myLooper() == null) {
//            Log.d(TAG, "Looper.prepare()");
//            Looper.prepare();
//        } else {
//            Log.d(TAG, "Nevim proc je myLooper uz alocovan");
//        }
        
        Looper.prepare();

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

                if (terminalService.getState() == ConnectionState.STATE_CONNECTED) {
                    // Zacni vykonavat smycku
                    Looper.loop();
                } 
//                else {
//                    // nepodarilo se spojit, tak vsechno uklidime
//                    stop();
//                }
            }
        }
        
        stop();

        return outputData;

    }

    /**
     * Create objects and variables.
     * 
     * @return true for corect creating.
     */
    private static Boolean create() {
        Log.e(TAG, "+++ ON CREATE +++");

        // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
    private static Boolean start() {
        Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
//            Toast.makeText(applicationContext, "Bluetooth is not available",
//                    Toast.LENGTH_LONG).show();
            outputData.setMessage("Bluetooth is not available");
            // Otherwise, setup the chat session
        } else {
            if (terminalService == null) {
                setupTerminal();
                return true;
            }
        }

        return false;
    }

    private static void stop() {
        Log.e(TAG, "++ ON STOP ++");

        if (terminalService != null) {
            terminalService.stop();
            terminalService = null;
        }

        if (tcpThread != null) {
            tcpThread.interrupt();
            tcpThread = null;
        }

        if (mHandler != null) {
            //mHandler.getLooper().quit();
            //mHandler.removeCallbacks(null);
            mHandler = null;
        }

        //Looper.getMainLooper().quit();
        Looper.myLooper().quit();
        Log.d(TAG, "Looper.myLooper().quit()");
    }

    private static void setupTerminal() {
        Log.d(TAG, "setupTerminal() creating handler");
               
        mHandler = new Handler() {
            private byte[] serverConnectionID;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case HandleMessages.MESSAGE_STATE_CHANGE:
                    handleStateChange(msg);
                    break;
                case HandleMessages.MESSAGE_SERVER_WRITE:
                    break;
                case HandleMessages.MESSAGE_SERVER_READ:
                    break;

                case HandleMessages.MESSAGE_TERM_WRITE:
                    // Jedine misto v aplikaci pres ktere se posila do terminalu
                    write2Terminal((byte[]) msg.obj);
                    break;

                case HandleMessages.MESSAGE_CONNECTED:
                    // Send to terminal information about connection at server.
                    connectionRequest(msg);
                    break;

                case HandleMessages.MESSAGE_TERM_READ:
                    handleTermReceived(msg);
                    break;
                case HandleMessages.MESSAGE_DEVICE_NAME:
                    // Nemam tuseni k cemu bych to vyuzil
                    break;
                case HandleMessages.MESSAGE_TOAST:
                    Toast.makeText(applicationContext, msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case HandleMessages.MESSAGE_QUIT:
                    stop();
                    break;
                }
            }

            /**
             * Send to terminal information about connection at server.
             * 
             * @param msg
             * */
            private void connectionRequest(Message msg) {
                byte[] status = new byte[1];
                status[0] = (byte) msg.arg1;
                ServerFrame soFrame = new ServerFrame(
                        TerminalCommands.TERM_CMD_SERVER_CONNECTED,
                        serverConnectionID, status);
                TerminalFrame toFrame = new TerminalFrame(
                        TerminalPorts.SERVER.getPortNumber(),
                        soFrame.createFrame());

                mHandler.obtainMessage(HandleMessages.MESSAGE_TERM_WRITE, -1,
                        -1, SLIPFrame.createFrame(toFrame.createFrame()))
                        .sendToTarget();
            }

            /**
             * Received message from terminal
             * 
             * @param msg
             */
            private void handleTermReceived(Message msg) {
                byte[] readSlipFrame = (byte[]) msg.obj;
                slipOutputpFraming.write(readSlipFrame, 0, msg.arg1);

                // Check
                if (SLIPFrame.isFrame(slipOutputpFraming.toByteArray())) {

                    TerminalFrame termFrame = new TerminalFrame(
                            SLIPFrame.parseFrame(slipOutputpFraming
                                    .toByteArray()));
                    slipOutputpFraming.reset();

                    if (termFrame != null) {
                        switch (termFrame.getPort()) {
                        case UNDEFINED:
                            Log.d(TAG, "undefined port");
                            break;
                        case SERVER:
                            // messages for server
                            handleServerMessage(termFrame);
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
                            BProtocol bprotocol = new BProtocolFactory()
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
                                outputData.setMessage(bprotocol
                                        .getTagMap().get(
                                                BProtocolTag.ServerMessage));
                                try {
                                    outputData.setAuthCode(Integer
                                            .valueOf(bprotocol.getTagMap().get(
                                                    BProtocolTag.AuthCode)));
                                } catch (Exception e) {
                                    outputData.setAuthCode(0);
                                }
                                try {
                                    outputData.setSeqId(Integer
                                            .valueOf(bprotocol.getTagMap().get(
                                                    BProtocolTag.SequenceId)));
                                } catch (Exception e) {
                                    outputData.setSeqId(0);
                                }
                                outputData.setCardNumber(bprotocol.getTagMap()
                                        .get(BProtocolTag.PAN));
                                outputData.setCardType(bprotocol.getTagMap()
                                        .get(BProtocolTag.CardType));

                                stop();
                                // Looper.myLooper().quit();

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
            }

            private void handleStateChange(Message msg) {
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
            }

            private void handleServerMessage(TerminalFrame termFrame) {
                // sends the message to the server
                final ServerFrame serverFrame = new ServerFrame(
                        termFrame.getData());

                // TerminalFrame responseTerminal = null;

                Log.d(TAG, "Server command: " + serverFrame.getCommand());
                switch (serverFrame.getCommand()) {
                case TerminalCommands.TERM_CMD_ECHO:
                    echoResponse(termFrame, serverFrame);
                    break;

                case TerminalCommands.TERM_CMD_CONNECT:
                    serverConnectionID = serverFrame.getId();

                    int port = MonetUtils.getInt(serverFrame.getData()[4],
                            serverFrame.getData()[5]);

                    int timeout = MonetUtils.getInt(serverFrame.getData()[6],
                            serverFrame.getData()[7]);

                    // connect to the server
                    tcpThread = new TCPClientThread();
                    tcpThread.setConnection(Arrays.copyOfRange(
                            serverFrame.getData(), 0, 4), port, timeout,
                            serverFrame.getIdInt());
                    Log.i(TAG, "TCP thread starting.");
                    tcpThread.start();

                    TerminalFrame responseTerminal = new TerminalFrame(
                            termFrame.getPort().getPortNumber(),
                            new ServerFrame(
                                    (byte) TerminalCommands.TERM_CMD_CONNECT_RES,
                                    serverFrame.getId(), new byte[1])
                                    .createFrame());

                    mHandler.obtainMessage(
                            HandleMessages.MESSAGE_TERM_WRITE,
                            -1,
                            -1,
                            SLIPFrame.createFrame(responseTerminal
                                    .createFrame())).sendToTarget();

                    break;

                case TerminalCommands.TERM_CMD_DISCONNECT:

                    if (tcpThread != null) {
                        tcpThread.interrupt();
                        tcpThread = null;
                    }
                    break;

                case TerminalCommands.TERM_CMD_SERVER_WRITE:
                    // Send data to server.
                    tcpThread.sendMessage(serverFrame.getData());
                }

            }

            /**
             * Terminal check this application.
             * 
             * @param termFrame
             * @param serverFrame
             */
            private void echoResponse(TerminalFrame termFrame,
                    final ServerFrame serverFrame) {
                TerminalFrame responseTerminal = new TerminalFrame(termFrame
                        .getPort().getPortNumber(), new ServerFrame(
                        TerminalCommands.TERM_CMD_ECHO_RES,
                        serverFrame.getId(), null).createFrame());

                mHandler.obtainMessage(HandleMessages.MESSAGE_TERM_WRITE, -1,
                        -1,
                        SLIPFrame.createFrame(responseTerminal.createFrame()))
                        .sendToTarget();
            }
        };

        // Initialize the BluetoothChatService to perform bluetooth connections
        terminalService = new TerminalServiceBT(applicationContext, mHandler);
    }

    /**
     * Create and send pay request to terminal.
     */
    private static void pay() {
        mHandler.obtainMessage(
                HandleMessages.MESSAGE_TERM_WRITE,
                -1,
                -1,
                SLIPFrame.createFrame(new TerminalFrame(
                        TERMINALPORT,
                        BProtocolMessages.getSale(inputData.getAmount(),
                                inputData.getCurrency(), inputData.getInvoice()))
                        .createFrame())).sendToTarget();
    }

    /**
     * Create and send handshake to terminal.
     */
    private static void handshake() {
        mHandler.obtainMessage(
                HandleMessages.MESSAGE_TERM_WRITE,
                -1,
                -1,
                SLIPFrame.createFrame(new TerminalFrame(TERMINALPORT,
                        BProtocolMessages.getHanshake()).createFrame()))
                .sendToTarget();
    }

    /**
     * Create and send app info request to terminal.
     */
    private static void appInfo() {
        mHandler.obtainMessage(
                HandleMessages.MESSAGE_TERM_WRITE,
                -1,
                -1,
                SLIPFrame.createFrame(new TerminalFrame(TERMINALPORT,
                        BProtocolMessages.getAppInfo()).createFrame()))
                .sendToTarget();
    }

    /**
     * Get the BluetoothDevice object.
     * 
     * @param address
     *            HW address of bluetooth.
     * @param secure
     *            True for secure connection, false for insecure.
     */
    private static void connectDevice(String address, boolean secure) {
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
    private static void write2Terminal(byte[] message) {
        // Check that we're actually connected before trying anything
        if (terminalService.getState() != ConnectionState.STATE_CONNECTED) {
            Toast.makeText(applicationContext, R.string.not_connected,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length > 0) {
            terminalService.write(message);
        }
    }

    /**
     * @author "Dusan Krajcovic"
     * 
     */
    public static final class TCPClientThread extends Thread {

        private static final String TAG = "TCPClientThread";

        private byte[] serverIp;

        private int serverPort;

        private int connectionId;

        private int timeout;

        /**
         * TCP client.
         */
        private TCPClient mTcpClient = null;
        
        public TCPClientThread() {
            super();
        }

        /**
         * @param serverIp
         * @param serverPort
         * @param timeout
         * @param connectionId
         */
        public void setConnection(byte[] serverIp, int serverPort, int timeout,
                int connectionId) {
            
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
         * 
         * @param sendData
         */
        public void sendMessage(byte[] sendData) {
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
                            TerminalFrame termFrame = new TerminalFrame(
                                    TerminalPorts.SERVER.getPortNumber(),
                                    new ServerFrame(
                                            TerminalCommands.TERM_CMD_SERVER_READ,
                                            connectionId, message)
                                            .createFrame());

                            // send to terminal
                            mHandler.obtainMessage(
                                    HandleMessages.MESSAGE_TERM_WRITE,
                                    -1,
                                    -1,
                                    SLIPFrame.createFrame(termFrame
                                            .createFrame())).sendToTarget();
                        }
                    });

            mTcpClient.run();
        }

        public void interrupt() {

            if (mTcpClient != null) {
                mTcpClient.stopClient();
                // try {
                // wait(500);
                // } catch (InterruptedException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                mTcpClient = null;
            }

            super.interrupt();
        }

    }
}
