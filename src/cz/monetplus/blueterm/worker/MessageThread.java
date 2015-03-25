package cz.monetplus.blueterm.worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

//import android.R.bool;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cz.monetplus.blueterm.R;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.R.string;
import cz.monetplus.blueterm.bprotocol.BProtocol;
import cz.monetplus.blueterm.bprotocol.BProtocolFactory;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.bprotocol.BProtocolTag;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.terminals.TerminalCommands;
import cz.monetplus.blueterm.terminals.TerminalPorts;
import cz.monetplus.blueterm.terminals.TerminalServiceBT;
import cz.monetplus.blueterm.terminals.TerminalState;
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
        // this.terminalPort = terminalPort;
        this.transactionInputData = transactionInputData;
    }

    @Override
    public void run() {
        while (!stopThread) {
            if (queue.peek() != null) {
                handleMessage(queue.poll());
            }
        }

        if (tcpThread != null) {
            tcpThread.interrupt();
            tcpThread = null;
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

        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(TerminalPorts.MBCA
                        .getPortNumber(), BProtocolMessages.getSale(
                        transactionInputData.getAmount(),
                        transactionInputData.getCurrency(),
                        transactionInputData.getInvoice())).createFrame())));
    }

    /**
     * Create and send handshake to terminal.
     */
    private void handshakeMbca() {
        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(TerminalPorts.MBCA
                        .getPortNumber(), BProtocolMessages.getHanshake())
                        .createFrame())));
    }

    /**
     * Create and send pay request to terminal.
     */
    private void recharge() {
        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(TerminalPorts.MVTA
                        .getPortNumber(), VProtocolMessages.getEmvRecharge(
                        transactionInputData.getAmount(), transactionInputData
                                .getCurrency(), transactionInputData
                                .getInvoice(),
                        transactionInputData.getTranId(), transactionInputData
                                .getRechargingType().getTag())).createFrame())));
    }

    /**
     * Create and send app info request to terminal.
     */
    private void appInfoMbca() {
        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(TerminalPorts.MBCA
                        .getPortNumber(), BProtocolMessages.getAppInfo())
                        .createFrame())));
    }

    /**
     * Create and send handshake to terminal.
     */
    private void handshakeMvta() {
        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(TerminalPorts.MVTA
                        .getPortNumber(), VProtocolMessages.getHanshake())
                        .createFrame())));
    }

    /**
     * Create and send app info request to terminal.
     */
    private void appInfoMvta() {
        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(TerminalPorts.MVTA
                        .getPortNumber(), VProtocolMessages.getAppInfo())
                        .createFrame())));
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

    /**
     * @param service
     *            Terminal service serving bluetooth.
     */
    public void setTerminalService(TerminalServiceBT service) {
        this.terminalService = service;
    }

    // @Override
    public void handleMessage(HandleMessage msg) {

        switch (msg.getOperation()) {
        case CallMbcaHandshake:
            handshakeMbca();
            break;
        case CallMbcaInfo:
            appInfoMbca();
            break;
        case CallMbcaPay:
            pay();
            break;
        case CallMvtaHandshake:
            handshakeMvta();
            break;
        case CallMvtaInfo:
            appInfoMvta();
            break;
        case CallMvtaRecharging:
            recharge();
            break;

        case TerminalWrite:
            write2Terminal(msg.getBuffer().buffer());
            break;
        case TerminalRead:
            parseSlipPackets(msg);
            break;

        case ServerConnected:
            connectionRequest(msg);
            break;

        case ShowMessage:
            Toast.makeText(applicationContext,
                    new String(msg.getBuffer().buffer()), Toast.LENGTH_SHORT)
                    .show();
            break;

        case Exit:
            this.stopThread();
            break;
        case Connected:
            break;

        default:
            break;

        }
    }

    /**
     * Sends a message.
     * 
     * @param message
     *            A string of text to send.
     */
    private void write2Terminal(byte[] message) {
        // Check that we're actually connected before trying anything
        if (terminalService.getState() != TerminalState.STATE_CONNECTED) {

            // Toast.makeText(applicationContext, R.string.not_connected,
            // Toast.LENGTH_SHORT).show();
//            this.addMessage(HandleMessage.MESSAGE_TOAST, -1, -1,
//                    R.string.not_connected);
            this.addMessage(new HandleMessage(HandleOperations.ShowMessage, "Termina isn't connected."));
            return;
        }

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
    private void connectionRequest(HandleMessage msg) {
        // byte[] status = new byte[1];
        // status[0] = (byte) msg.arg1;
        // msg.getBuffer().buffer()
        ServerFrame soFrame = new ServerFrame(
                TerminalCommands.TERM_CMD_SERVER_CONNECTED, serverConnectionID,
                msg.getBuffer().buffer());
        TerminalFrame toFrame = new TerminalFrame(
                TerminalPorts.SERVER.getPortNumber(), soFrame.createFrame());

        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(toFrame.createFrame())));
        // this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        // SLIPFrame.createFrame(toFrame.createFrame()));
    }

    /**
     * Received message from terminal
     * 
     * @param msg
     *            Messaget contains information read from terminal.
     */
    private void parseSlipPackets(HandleMessage msg) {
        // slipOutputpFraming.write((byte[]) msg.obj, 0, msg.arg1);
        try {
            slipOutputpFraming.write(msg.getBuffer().buffer());

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
                        // messages for server
                        handleServerMessage(termFrame);
                        break;

                    case FLEET:
                        Log.d(TAG, "fleet data");
                        break;

                    case MAINTENANCE:
                        Log.d(TAG, "maintentace data");
                        break;

                    case MBCA: {
                        BProtocol bprotocol = new BProtocolFactory()
                                .deserialize(termFrame.getData());

                        if (bprotocol.getProtocolType().equals("B2")) {
                            ParseB2(bprotocol);
                        }
                    }
                        break;

                    case MVTA: {
                        BProtocol bprotocol = new BProtocolFactory()
                                .deserialize(termFrame.getData());

                        if (bprotocol.getProtocolType().equals("V2")) {
                            ParseB2(bprotocol);
                        }
                    }

                        break;

                    default:
                        // Nedelej nic, spatne data, format, nebo
                        // crc
                        Log.e(TAG,
                                String.format("Invalid port {}",
                                        termFrame.getPort()));
                        break;

                    }
                }

            } else {
                Log.e(TAG, "Corrupted data. It's not slip frame.");
            }

        } catch (IOException e) {
            Log.e(TAG, "Exception by parsing slip packets.");
        }
    }

    private void ParseB2(BProtocol bprotocol) {
        transactionOutputData = new TransactionOut();
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

        this.stopThread();
    }

    private void stopThread() {
        terminalService.stop();
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
        // sends the message to the server
        final ServerFrame serverFrame = new ServerFrame(termFrame.getData());

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
            tcpThread = new TCPClientThread(this);
            tcpThread.setConnection(
                    Arrays.copyOfRange(serverFrame.getData(), 0, 4), port,
                    timeout, serverFrame.getIdInt());
            Log.i(TAG, "TCP thread starting.");
            tcpThread.start();

            TerminalFrame responseTerminal = new TerminalFrame(termFrame
                    .getPort().getPortNumber(), new ServerFrame(
                    (byte) TerminalCommands.TERM_CMD_CONNECT_RES,
                    serverFrame.getId(), new byte[1]).createFrame());

//            this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
            this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                    SLIPFrame.createFrame(responseTerminal.createFrame())));

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
     *            Terminal frame.
     * @param serverFrame
     *            Server frame.
     */
    private void echoResponse(TerminalFrame termFrame,
            final ServerFrame serverFrame) {
        TerminalFrame responseTerminal = new TerminalFrame(termFrame.getPort()
                .getPortNumber(),
                new ServerFrame(TerminalCommands.TERM_CMD_ECHO_RES, serverFrame
                        .getId(), null).createFrame());

//        this.addMessage(HandleMessage.MESSAGE_TERM_WRITE, -1, -1,
        this.addMessage(new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(responseTerminal.createFrame())));
    }
}
