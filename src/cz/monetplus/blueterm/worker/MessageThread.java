package cz.monetplus.blueterm.worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.bprotocol.BProtocol;
import cz.monetplus.blueterm.bprotocol.BProtocolFactory;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.bprotocol.BProtocolTag;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.terminals.TerminalCommands;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.terminals.TerminalServiceBT;
import cz.monetplus.blueterm.util.MonetUtils;
import cz.monetplus.blueterm.vprotocol.VProtocolMessages;

/**
 * Thread for handling all messages.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class MessageThread extends Thread {

    /**
     * String tag for logging.
     */
    private static final String TAG = "MessageThread";

    /**
     * Server connection ID. Only one serverconnection.
     */
    private byte[] serverConnectionID = null;

    /**
     * TCP client thread for read and write.
     */
    private static TCPClientThread tcpThread = null;

    /**
     * Message queue for handling messages from threads.
     */
    private Queue<HandleMessage> queue = new LinkedList<HandleMessage>();

    /**
     * Application context.
     */
    private Context applicationContext;

    /**
     * Transaction input params.
     */
    private TransactionIn transactionInputData;

    /**
     * Transaction output params.
     */
    private TransactionOut transactionOutputData;

    /**
     * Stop this thread.
     */
    private boolean stopThread = false;

    /**
     * BluetoothAdapter
     */
    private static BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the chat services.
     */
    private TerminalServiceBT terminalService = null;

    /**
     * Terminal to muze posilat po castech.
     */
    private static ByteArrayOutputStream slipOutputpFraming = null;

    /**
     * @param context
     * @param terminalPort
     * @param transactionInputData
     */
    public MessageThread(final Context context,
            TransactionIn transactionInputData) {
        super();

        slipOutputpFraming = new ByteArrayOutputStream();
        slipOutputpFraming.reset();

        applicationContext = context;
        this.transactionInputData = transactionInputData;
        this.transactionOutputData = new TransactionOut();

        addMessage(HandleOperations.GetBluetoothAddress);
    }

    @Override
    public void run() {
        while (!stopThread) {
            if (queue.peek() != null) {
                handleMessage(queue.poll());
            }
        }
    }

    /**
     * Get result from current thread.
     * 
     * @return TransactionOut result Data.
     */
    public TransactionOut getValue() {
        return transactionOutputData;
    }

    /**
     * Create and send pay request to terminal.
     */
    private void pay() {
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getSale(transactionInputData.getAmount(),
                                        transactionInputData.getCurrency(),
                                        transactionInputData.getInvoice()))
                        .createFrame())));
    }

    /**
     * Create and send handshake to terminal.
     */
    private void handshakeMbca() {
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getHanshake()).createFrame())));
    }

    /**
     * Create and send pay request to terminal.
     */
    private void recharge() {
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getEmvRecharge(transactionInputData
                                        .getAmount(), transactionInputData
                                        .getCurrency(), transactionInputData
                                        .getInvoice(), transactionInputData
                                        .getTranId(), transactionInputData
                                        .getRechargingType().getTag()))
                        .createFrame())));
    }

    /**
     * Create and send app info request to terminal.
     */
    private void appInfoMbca() {
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getAppInfo()).createFrame())));
    }

    /**
     * Create and send handshake to terminal.
     */
    private void handshakeMvta() {
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getHanshake()).createFrame())));
    }

    /**
     * Create and send app info request to terminal.
     */
    private void appInfoMvta() {
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getAppInfo()).createFrame())));
    }

    /**
     * @param msg
     *            Message for addding to queue.
     */
    public void addMessage(HandleMessage msg) {
        queue.add(msg);
    }

    /**
     * @param operation
     */
    public void addMessage(HandleOperations operation) {
        addMessage(new HandleMessage(operation));

    }

    // /**
    // * @param service
    // * Terminal service serving bluetooth.
    // */
    // public void setTerminalService(TerminalServiceBT service) {
    // this.terminalService = service;
    // }

    // @Override
    public void handleMessage(HandleMessage msg) {

        Log.i(TAG, "Operation: " + msg.getOperation());
        switch (msg.getOperation()) {

        case GetBluetoothAddress: {
            getBluetoothAddress();
            break;
        }

        case SetupTerminal: {
            setupTerminal();
            break;
        }

        case TerminalConnect: {
            connectDevice(transactionInputData.getBlueHwAddress(), false);
            break;
        }
        case TerminalConnected: {
            connectedDevice();
            break;
        }

        case TerminalReady: {
            addMessage(transactionInputData.getCommand().getOperation());
            break;
        }

        case CallMbcaHandshake: {
            handshakeMbca();
            break;
        }
        case CallMbcaInfo: {
            appInfoMbca();
            break;
        }
        case CallMbcaPay: {
            pay();
            break;
        }
        case CallMvtaHandshake: {
            handshakeMvta();
            break;
        }
        case CallMvtaInfo: {
            appInfoMvta();
            break;
        }
        case CallMvtaRecharging: {
            recharge();
            break;
        }

        case TerminalWrite:
            write2Terminal(msg.getBuffer().buffer());
            break;
        case TerminalRead:
            receiveTerminalPackets(msg);
            break;

        case ServerConnected:
            serverConnected(msg);
            break;

        case ShowMessage:
            String message = new String(msg.getBuffer().buffer());
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                    .show();
            break;

        case Exit:
            this.stopThread();
            break;
        // case Connected:
        // break;

        default:
            break;

        }
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

    private void connectedDevice() {
        terminalService.connected();
    }

    private void setupTerminal() {
        terminalService = new TerminalServiceBT(applicationContext, this,
                bluetoothAdapter);

        this.addMessage(HandleOperations.TerminalConnect);
    }

    private void getBluetoothAddress() {
        // // Get local Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null)
            if (bluetoothAdapter.isEnabled()) {
                this.addMessage(HandleOperations.SetupTerminal);
            } else {
                this.setOutputMessage("Bluetooth is not enabled.");
                this.addMessage(HandleOperations.Exit);
            }
        else {
            this.setOutputMessage("Bluetooth is not supported on this hardware platform.");
            this.addMessage(HandleOperations.Exit);
        }

    }

    /**
     * Sends a message.
     * 
     * @param message
     *            A string of text to send.
     */
    private void write2Terminal(byte[] message) {
        // Check that there's actually something to send
        if (message.length > 0) {
            terminalService.write(message);
        }
    }

    /**
     * Send to terminal information about connection at server.
     * 
     * @param msg
     *            Contains status(arg1) about current connection to server.
     * */
    private void serverConnected(HandleMessage msg) {
        ServerFrame soFrame = new ServerFrame(TerminalCommands.ServerConnected,
                serverConnectionID, msg.getBuffer().buffer());
        TerminalFrame toFrame = new TerminalFrame(
                TerminalPortApplications.SERVER.getPortApplicationNumber(),
                soFrame.createFrame());

        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(toFrame.createFrame())));
    }

    /**
     * Received message from terminal
     * 
     * @param msg
     *            Messaget contains information read from terminal.
     */
    private void receiveTerminalPackets(HandleMessage msg) {
        try {
            slipOutputpFraming.write(msg.getBuffer().buffer());

            // Check
            if (SLIPFrame.isFrame(slipOutputpFraming.toByteArray())) {

                TerminalFrame termFrame = getTerminalFrame(slipOutputpFraming);

                if (termFrame != null) {
                    switch (termFrame.getPortApplication()) {
                    case SERVER: {
                        // messages for server
                        handleServerMessage(termFrame);
                        break;
                    }

                    case MBCA: {
                        BProtocol bprotocol = new BProtocolFactory()
                                .deserialize(termFrame.getData());
                        if (bprotocol.getProtocolType().equals("B2")) {
                            ParseB2(bprotocol);
                        }

                        break;
                    }

                    case MVTA: {
                        BProtocol bprotocol = new BProtocolFactory()
                                .deserialize(termFrame.getData());
                        if (bprotocol.getProtocolType().equals("V2")) {
                            ParseB2(bprotocol);
                        }
                        break;
                    }
                    default:
                        Log.w(TAG, "Unsupported application port number: " + termFrame.getPortApplication());
                        break;
                    }

                } else {
                    Log.e(TAG, "Corrupted data. It's not slip frame.");
                }
            }

        } catch (IOException e) {
            Log.e(TAG, "Exception by parsing slip packets.");
        }
    }

    /**
     * @param stream
     * @return
     */
    private TerminalFrame getTerminalFrame(ByteArrayOutputStream stream) {
        TerminalFrame termFrame = new TerminalFrame(SLIPFrame.parseFrame(stream
                .toByteArray()));
        stream.reset();
        return termFrame;
    }

    private void ParseB2(BProtocol bprotocol) {
        // transactionOutputData = new TransactionOut();
        try {
            transactionOutputData.setResultCode(Integer.valueOf(bprotocol
                    .getTagMap().get(BProtocolTag.ResponseCode)));
        } catch (Exception e) {
            transactionOutputData.setResultCode(-1);
        }
        transactionOutputData.setMessage(bprotocol.getTagMap().get(
                BProtocolTag.ServerMessage));
        try {
            transactionOutputData.setAuthCode(Integer.valueOf(bprotocol
                    .getTagMap().get(BProtocolTag.AuthCode)));
        } catch (Exception e) {
            transactionOutputData.setAuthCode(0);
        }
        try {
            transactionOutputData.setSeqId(Integer.valueOf(bprotocol
                    .getTagMap().get(BProtocolTag.SequenceId)));
        } catch (Exception e) {
            transactionOutputData.setSeqId(0);
        }
        transactionOutputData.setCardNumber(bprotocol.getTagMap().get(
                BProtocolTag.PAN));
        transactionOutputData.setCardType(bprotocol.getTagMap().get(
                BProtocolTag.CardType));

        // this.stopThread();
        addMessage(HandleOperations.Exit);
    }

    /**
     * 
     */
    private void stopThread() {

        // Zastav terminal
        if (terminalService != null) {
            terminalService.stop();
        }

        if (tcpThread != null) {
            tcpThread.interrupt();
            tcpThread = null;
        }

        stopThread = true;
    }

    // @Deprecated
    // private void handleStateChange(HandleMessage msg) {
    // Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
    // switch (msg.arg1) {
    // case TerminalState.STATE_CONNECTED:
    // if (msg.arg2 >= 0) {
    // switch (TransactionCommand.values()[msg.arg2]) {
    // case MBCA_HANDSHAKE:
    // handshakeMbca();
    // break;
    // case MBCA_INFO:
    // appInfoMbca();
    // break;
    // case MBCA_PAY:
    // pay();
    // break;
    // case MVTA_HANDSHAKE:
    // handshakeMvta();
    // break;
    // case MVTA_INFO:
    // appInfoMvta();
    // break;
    // case MVTA_RECHARGE:
    // recharge();
    // break;
    // case UNKNOWN:
    // break;
    // default:
    // break;
    //
    // }
    // }
    // break;
    // case TerminalState.STATE_CONNECTING:
    // case TerminalState.STATE_LISTEN:
    // break;
    // case TerminalState.STATE_NONE:
    // break;
    // }
    // }

    private void handleServerMessage(TerminalFrame termFrame) {
        final ServerFrame serverFrame = new ServerFrame(termFrame.getData());

        Log.d(TAG, "Server command: " + serverFrame.getCommand());

        switch (serverFrame.getCommand()) {
        case TerminalCommands.EchoReq:
            echoResponse(termFrame, serverFrame);
            break;

        case TerminalCommands.ConnectReq:
            serverConnectionID = serverFrame.getId();

            int port = MonetUtils.getInt(serverFrame.getData()[4],
                    serverFrame.getData()[5]);

            int timeout = MonetUtils.getInt(serverFrame.getData()[6],
                    serverFrame.getData()[7]);

            // connect to the server
            tcpThread = new TCPClientThread(this);
            tcpThread.setConnection(
                    Arrays.copyOfRange(serverFrame.getData(), 0, 4), port,
                    timeout, serverFrame.getIdInt());
            Log.i(TAG, "TCP thread starting.");
            tcpThread.start();

            TerminalFrame responseTerminal = new TerminalFrame(termFrame
                    .getPortApplication().getPortApplicationNumber(),
                    new ServerFrame((byte) TerminalCommands.ConnectRes,
                            serverFrame.getId(), new byte[1]).createFrame());
            this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                    SLIPFrame.createFrame(responseTerminal.createFrame())));

            break;

        case TerminalCommands.DisconnectReq:
            if (tcpThread != null) {
                tcpThread.interrupt();
                tcpThread = null;
            }
            break;

        case TerminalCommands.ServerWrite:
            // Send data to server.
            tcpThread.sendMessage(serverFrame.getData());
        }

    }

    /**
     * Terminal check this application.
     * 
     * @param termFrame
     *            Terminal frame.
     * @param serverFrame
     *            Server frame.
     */
    private void echoResponse(TerminalFrame termFrame,
            final ServerFrame serverFrame) {
        TerminalFrame responseTerminal = new TerminalFrame(termFrame
                .getPortApplication().getPortApplicationNumber(),
                new ServerFrame(TerminalCommands.EchoRes, serverFrame.getId(),
                        null).createFrame());
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(responseTerminal.createFrame())));
    }

    public void setOutputMessage(String message) {
        if (this.transactionOutputData != null) {
            this.transactionOutputData.setMessage(message);
        }

    }
}
