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

    private static final String TAG = "BluetoothChat";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TERM_READ = 2;
    public static final int MESSAGE_TERM_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_CONNECTED = 6;
    public static final int MESSAGE_SERVER_READ = 12;
    public static final int MESSAGE_SERVER_WRITE = 13;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private static final int REQUEST_PREFERENCE = 4;
    
//    public static final String EXTRA_RESULT_CODE = "ResultCode";
//    public static final String EXTRA_SERVER_MESS = "ServerMessage";
//    public static final String EXTRA_AUTH_CODE = "AuthCode";
//    public static final String EXTRA_SEQ_ID = "SeqId";
//    public static final String EXTRA_CARD_NUMBER = "CardNumber";
//    public static final String EXTRA_CARD_TYPE = "CardType";
    

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private TerminalService mChatService = null;

    /**
     * 
     */
    private static ByteArrayOutputStream slipOutputpFraming;

    /**
     * in the arrayList we add the messaged received from server
     */
    private ArrayList<String> arrayList;

    /**
     * 
     */
    private ArrayAdapter<String> mAdapter;

    /**
     * 
     */
    private Context applicationContext;

    /**
     * 
     */
    private TransactionIn inputData;

    /**
     * mChatService
     */
    private TransactionOut outputData;

    /**
     * TCP client
     */
    private static TCPClient mTcpClient;

    private Boolean isFinish = false;

    // The Handler that gets information back from the BluetoothChatService
    private Handler mHandler;

    /**
     * @return
     */
    public Boolean isTransactionFinished() {
        return isFinish;
    }

    /**
     * @return
     */
    public TransactionOut getTransactionResult() {
        if (isFinish) {
            return outputData;
        }

        return null;
    }

    /**
     * @param context
     * @param in
     * @return
     */
    public Boolean doTransaction(Context context, TransactionIn in) {

        applicationContext = context;
        inputData = in;
        outputData = new TransactionOut();

        outputData.setServerMessage("Provadim komunikaci");

        if (create()) {
            if (start()) {
                connectDevice(inputData.getBlueHwAddress(), false);
                isFinish = false;

                return true;
            }
        }

        return false;

    }

    /**
     * Create objects and variables.
     * 
     * @return
     */
    private Boolean create() {
        Log.e(TAG, "+++ ON CREATE +++");

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        slipOutputpFraming = new ByteArrayOutputStream();
        // slipInputFraming = new ByteArrayInputStream(new byte[512]);

        arrayList = new ArrayList<String>();

        // relate the listView from java to the one created in xml
        // mList = (ListView)findViewById(R.id.list);
        mAdapter = new ArrayAdapter<String>(applicationContext,
                R.id.edit_text_out, arrayList);
        // mList.setAdapter(mAdapter);

        return true;
    }

    /**
     * check bluetooth and start setting
     * 
     * @return
     */
    public Boolean start() {
        Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            // Intent enableIntent = new Intent(
            // BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // applicationContext.startActivityForResult(enableIntent,
            // REQUEST_ENABLE_BT);
            Toast.makeText(applicationContext, "Bluetooth is not available",
                    Toast.LENGTH_LONG).show();
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) {
                setupChat();
                return true;
            }
        }

        return false;
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        mHandler = new Handler() {
            private byte[] idConnect;

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                    case TerminalService.STATE_CONNECTED:
                        switch (inputData.getCommand()) {
                        case HANDSHAKE:
                            handshake();
                            break;
                        case INFO:
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
                    case TerminalService.STATE_CONNECTING:
                        // mTitle.setText(R.string.title_connecting);
                        break;
                    case TerminalService.STATE_LISTEN:
                    case TerminalService.STATE_NONE:
                        // mTitle.setText(R.string.title_not_connected);
                        break;
                    }
                    break;
                case MESSAGE_SERVER_WRITE: {
                    // String hex = MonetUtils.bytesToHex((byte[]) msg.obj);
                    // mConversationArrayAdapter.add("SO: "
                    // + hex.substring(0, Math.min(hex.length(), 80)));
                }
                    break;

                case MESSAGE_SERVER_READ: {
                    // String hex = MonetUtils.bytesToHex((byte[]) msg.obj);
                    // mConversationArrayAdapter.add("SI: "
                    // + hex.substring(0, Math.min(hex.length(), 80)));
                }
                    break;

                case MESSAGE_TERM_WRITE: {
                    {
                        // String hex = MonetUtils.bytesToHex((byte[]) msg.obj);
                        // mConversationArrayAdapter.add("TO: "
                        // + hex.substring(0, Math.min(hex.length(), 80)));
                    }
                    break;
                }

                case MESSAGE_CONNECTED: {
                    // if (mTcpClient.isConnected()) {
                    byte[] status = new byte[1];
                    status[0] = (byte) msg.arg1;
                    ServerFrame soFrame = new ServerFrame((byte) 0x05,
                            idConnect, status);
                    TerminalFrame toFrame = new TerminalFrame(
                            TerminalPorts.SERVER.getPortNumber(),
                            soFrame.createFrame());

                    send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));

                    // if (status[0] != 0) {
                    // mInputTerminalLayout.setVisibility(View.VISIBLE);
                    // mProgressLayout.setVisibility(View.GONE);
                    // }

                    // }
                    break;
                }

                case MESSAGE_TERM_READ: {
                    byte[] readSlipFrame = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    // String readMessage = new String(readBuf, 0, msg.arg1);
                    // String readMessage = MonetUtils.bytesToHex(readSlipFrame,
                    // msg.arg1);
                    slipOutputpFraming.write(readSlipFrame, 0, msg.arg1);

                    // Check
                    if (SLIPFrame.isFrame(slipOutputpFraming.toByteArray())) {

                        TerminalFrame termFrame = new TerminalFrame(
                                SLIPFrame.parseFrame(readSlipFrame));

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

                                    isFinish = true;
                                    mChatService.stop();

                                }

                                break;
                            default:
                                // Nedelej nic, spatne data, format, nebo crc
                                Log.e(TAG, "Invalid port");
                                break;

                            }
                        }

                    } else {
                        Log.e(TAG, "Corrupted data. It's not slip frame.");
                    }

                    break;
                }
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    // mConnectedDeviceName =
                    // msg.getData().getString(DEVICE_NAME);
                    // Toast.makeText(
                    // BlueTermApplication.getAppContext(), //
                    // getApplicationContext()
                    // "Connected to " + mConnectedDeviceName,
                    // Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    // Toast.makeText(BlueTermApplication.getAppContext(), //
                    // getApplicationContext()
                    // msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
                    // .show();
                    // break;
                }
            }

            private void handeServerMessage(TerminalFrame termFrame) {
                // sends the message to the server
                ServerFrame serverFrame = new ServerFrame(termFrame.getData());

                Log.d(TAG, "Server command: " + serverFrame.getCommand());
                switch (serverFrame.getCommand()) {
                case 0x00: {
                    ServerFrame soFrame = new ServerFrame((byte) 0x80,
                            serverFrame.getId(), null);
                    TerminalFrame toFrame = new TerminalFrame(termFrame
                            .getPort().getPortNumber(), soFrame.createFrame());

                    send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));
                    break;
                }
                case 0x01: {
                    idConnect = serverFrame.getId();

                    int port = MonetUtils.getInt(serverFrame.getData()[4],
                            serverFrame.getData()[5]);

                    int timeout = MonetUtils.getInt(serverFrame.getData()[6],
                            serverFrame.getData()[7]);

                    // connect to the server
                    new TCPconnectTask(Arrays.copyOfRange(
                            serverFrame.getData(), 0, 4), port, timeout,
                            serverFrame.getIdInt()).execute("");

                    ServerFrame soFrame = new ServerFrame((byte) 0x81,
                            serverFrame.getId(), new byte[1]);
                    TerminalFrame toFrame = new TerminalFrame(termFrame
                            .getPort().getPortNumber(), soFrame.createFrame());

                    send2Terminal(SLIPFrame.createFrame(toFrame.createFrame()));

                    break;
                }
                case 0x02: {
                    if (mTcpClient != null) {
                        mTcpClient.stopClient();
                    }
                    break;
                }
                case 0x03: {
                    if (mTcpClient != null) {
                        try {
                            mTcpClient.sendMessage(serverFrame.getData());
                        } catch (IOException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
                }
                }

            }
        };

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new TerminalService(applicationContext, mHandler);

    }

    public void pay() {
        send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
                BProtocolMessages.getSale(inputData.getAmount(),
                        inputData.getCurrency(), inputData.getInvoice()))
                .createFrame()));
    }

    public void handshake() {
        send2Terminal(SLIPFrame.createFrame(new TerminalFrame(33333,
                BProtocolMessages.getHanshake()).createFrame()));
    }

    /**
     * Get the BluetoothDevice object
     * 
     * @param address
     * @param secure
     */
    private void connectDevice(String address, boolean secure) {
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    /**
     * Sends a message.
     * 
     * @param message
     *            A string of text to send.
     */
    private void send2Terminal(byte[] message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != TerminalService.STATE_CONNECTED) {
            Toast.makeText(applicationContext, R.string.not_connected,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            // byte[] send = message.getBytes();
            mChatService.write(message);

            // Reset out string buffer to zero and clear the edit text field
            // mOutStringBuffer.setLength(0);
            // mOutEditText.setText(mOutStringBuffer);
        }
    }

    /**
     * @author "Dusan Krajcovic"
     * 
     */
    public class TCPconnectTask extends AsyncTask<String, byte[], TCPClient> {

        private byte[] serverIp;
        private int serverPort;
        private int connectionId;
        private int timeout;

        private TCPconnectTask(byte[] serverIp, int serverPort, int timeout,
                int connectionId) {
            super();
            this.serverIp = serverIp;
            this.serverPort = serverPort;
            this.connectionId = connectionId;
            this.timeout = timeout;

            Log.d(TAG, "TCPconnectTask to " + serverIp[0] + "." + serverIp[1]
                    + "." + serverIp[2] + "." + serverIp[3] + ":" + serverPort
                    + "[" + connectionId + "]");
        }

        @Override
        protected TCPClient doInBackground(String... message) {

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
                            // Looper.loop();
                            // send to terminal
                            send2Terminal(SLIPFrame.createFrame(termFrame
                                    .createFrame()));
                        }
                    });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(byte[]... values) {
            super.onProgressUpdate(values);

            // Share the sent message back to the UI Activity
            mHandler.obtainMessage(BluetoothChat.MESSAGE_SERVER_READ, -1, -1,
                    values[0]).sendToTarget();

            // in the arrayList we add the messaged received from server
            arrayList.add(new String(values[0]));
            // notify the adapter that the data set has changed. This means that
            // new message received
            // from server was added to the list
            mAdapter.notifyDataSetChanged();
        }
    }
}
