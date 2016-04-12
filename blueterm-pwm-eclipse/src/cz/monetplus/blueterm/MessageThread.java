package cz.monetplus.blueterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import cz.monetplus.blueterm.bprotocol.BProtocol;
import cz.monetplus.blueterm.bprotocol.BProtocolFactory;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.bprotocol.BProtocolTag;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.terminal.TerminalCommands;
import cz.monetplus.blueterm.terminal.TerminalPorts;
import cz.monetplus.blueterm.terminal.TerminalServiceBT;
import cz.monetplus.blueterm.terminal.TerminalState;
import cz.monetplus.blueterm.util.MonetUtils;

//import android.R.bool;

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
    private final Queue<MonetMessage> queue = new LinkedList<MonetMessage>();

    /**
     * Application context.
     */
    private final Activity activity;

    /**
     * Terminal port (example 33333).
     */
    private final int terminalPort;

    /**
     * Transaction input params.
     */
    private final TransactionIn transactionInputData;

    /**
     * Transaction output params.
     */
    private TransactionOutVx600 transactionOutputData = new TransactionOutVx600();

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
     * 
     */
    private TerminalState currentTerminalState;

    /**
     * @param activity
     *            Current activity.
     * @param terminalPort
     *            Terminal socket port.
     * @param transactionInputData
     *            Transaction input data.
     */
    public MessageThread(final Activity activity, int terminalPort,
            TransactionIn transactionInputData) {
        super();

        slipOutputpFraming = new ByteArrayOutputStream();
        slipOutputpFraming.reset();

        this.activity = activity;
        this.terminalPort = terminalPort;
        this.transactionInputData = transactionInputData;
        this.transactionOutputData.setMessage("Upppsss upadla ti knihovna.");
    }

    @Override
    public final void run() {
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
    public final TransactionOut getValue() {
        return transactionOutputData;
    }

    /**
     * Create and send pay request to terminal.
     */
    private void pay() {
        this.addMessageTermWrite(SLIPFrame.createFrame(new TerminalFrame(
                terminalPort, BProtocolMessages.getSale(
                        transactionInputData.getAmount(),
                        transactionInputData.getCurrency(),
                        transactionInputData.getInvoice())).createFrame()));
    }

    private void reversal() {
        this.addMessageTermWrite(SLIPFrame.createFrame(new TerminalFrame(
                terminalPort, BProtocolMessages.getReversal(
                        transactionInputData.getAmount(),
                        transactionInputData.getCurrency(),
                        transactionInputData.getInvoice(),
                        transactionInputData.getAuthCode())).createFrame()));
    }

    /**
     * Create and send handshake to terminal.
     */
    private void handshake() {
        this.addMessageTermWrite(SLIPFrame.createFrame(new TerminalFrame(
                terminalPort, BProtocolMessages.getHanshake()).createFrame()));
    }

    /**
     * Create and send handshake to terminal.
     */
    private void closeTotalBalancing() {
        this.addMessageTermWrite(SLIPFrame.createFrame(new TerminalFrame(
                terminalPort, BProtocolMessages.getCloseTotalBalancing())
                .createFrame()));
    }

    /**
     * Create and send app info request to terminal.
     */
    private void appInfo() {
        this.addMessageTermWrite(SLIPFrame.createFrame(new TerminalFrame(
                terminalPort, BProtocolMessages.getAppInfo()).createFrame()));
    }

    public final void addMessageTermWrite(byte[] createFrame) {
        this.addMessage(new MonetMessage(HandleMessages.MESSAGE_TERM_WRITE,
                createFrame, createFrame.length));
    }

    public final void addMessageToast(String string) {
        addMessage(new MonetMessage(HandleMessages.MESSAGE_TOAST, string));

    }

    /**
     * @param msg
     *            Message for addding to queue.
     */
    private void addMessage(MonetMessage msg) {
        queue.add(msg);
    }

    /**
     * @param service
     *            Terminal service serving bluetooth.
     */
    public final void setTerminalService(TerminalServiceBT service) {
        this.terminalService = service;
    }

    public final void handleMessage(final MonetMessage msg) {
        if (msg == null) {
            Log.e(TAG, "handleMessage got null message");
            return;
        }

        switch (msg.getMessage()) {

        case MESSAGE_STATE_CHANGE:
            handleStateChange(msg);
            break;

        case MESSAGE_TERM_SEND_COMMAND:
            break;
        // case HandleMessages.MESSAGE_SERVER_WRITE:
        // break;
        // case HandleMessages.MESSAGE_SERVER_READ:
        // break;

        case MESSAGE_TERM_WRITE:
            // Jedine misto v aplikaci pres ktere se posila do terminalu
            try {
                write2Terminal(msg.getData().buffer());
            } catch (IOException e) {
                e.printStackTrace();
                this.stopThread(MonetBTAPIError.WRITE_2_TERMINAL);
            }
            break;

        case MESSAGE_CONNECTED:
            // Send to terminal information about connection at server.
            connectionRequest(msg);
            break;

        case MESSAGE_TERM_READ:
            try {
                handleTermReceived(msg);
            } catch (IOException e) {
                e.printStackTrace();
                this.stopThread(MonetBTAPIError.READ_FROM_TERMINAL);
            }
            break;
        case MESSAGE_DEVICE_NAME:
            // Nemam tuseni k cemu bych to vyuzil
            break;
        case MESSAGE_TOAST:
            Log.i(TAG, msg.getToastMessage());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, msg.getToastMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            break;
        case MESSAGE_QUIT:
            Log.i(TAG, "MESSAGE_QUIT message: "
                    + msg.getErrorInfo().getMessage());
            this.stopThread(msg.getErrorInfo());
            break;
        default:
            Log.e(TAG,
                    "Invalid message handle code. For developer: fatal error");
            break;
        }
    }

    /**
     * Sends a message.
     * 
     * @param message
     *            A string of text to send.
     * @throws IOException
     *             Input output exception by write to terminal.
     */
    private void write2Terminal(byte[] message) throws IOException {
        // Check that we're actually connected before trying anything
        if (terminalService.getState() != TerminalState.STATE_CONNECTED) {

            this.addMessageToast(this.activity.getResources().getString(
                    R.string.not_connected));
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
    private void connectionRequest(MonetMessage msg) {
        byte[] status = new byte[1];
        status[0] = msg.getServerStatus();
        ServerFrame soFrame = new ServerFrame(
                TerminalCommands.TERM_CMD_SERVER_CONNECTED, serverConnectionID,
                status);
        TerminalFrame toFrame = new TerminalFrame(
                TerminalPorts.SERVER.getPortNumber(), soFrame.createFrame());

        this.addMessageTermWrite(SLIPFrame.createFrame(toFrame.createFrame()));
    }

    /**
     * Received message from terminal.
     * 
     * @param msg
     *            Messaget contains information read from terminal.
     * @throws IOException
     *             Input output exception (byte array).
     */
    private void handleTermReceived(MonetMessage msg) throws IOException {
        slipOutputpFraming.write(msg.getData().buffer());

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
                case MASTER:
                    // Tyhle zpravy zpracovavat, jsou pro tuhle
                    // aplikaci
                    BProtocol bprotocol = new BProtocolFactory()
                            .deserialize(termFrame.getData());

                    if (bprotocol.getProtocolType().equals("B0")) {
                        this.addMessageToast("Terminal working(B0)...");
                    }

                    if (bprotocol.getProtocolType().equals("B2")) {
                        executeB2(bprotocol);
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

    private void executeB2(BProtocol bprotocol) {
        transactionOutputData = new TransactionOutVx600();
        try {
            transactionOutputData.setResultCode(Integer.valueOf(bprotocol
                    .getTagMap().get(BProtocolTag.ResponseCode)));
        } catch (Exception e) {
            transactionOutputData.setResultCode(-1);
        }
        transactionOutputData.setMessage(bprotocol.getTagMap().get(
                BProtocolTag.ServerMessage));
        try {
            transactionOutputData.setAuthCode(bprotocol.getTagMap().get(
                    BProtocolTag.AuthCode));
        } catch (Exception e) {
            transactionOutputData.setAuthCode("Nezaslan");
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

        transactionOutputData.setBatchTotal(bprotocol.getTagMap().get(
                BProtocolTag.TotalsBatch1));

        this.stopThread(transactionOutputData.getResultCode(),
                transactionOutputData.getMessage());
    }

    /**
     * @param resultCode
     *            The result code.
     * @param message
     *            The result message.
     */
    private void stopThread(Integer resultCode, String message) {
        transactionOutputData.setResultCode(resultCode);
        transactionOutputData.setMessage(message);

        terminalService.stop();
        stopThread = true;

    }

    /**
     * @param result
     *            The error result.
     */
    private void stopThread(MonetBTAPIError result) {

        transactionOutputData.setResultCode(result.getCode());
        transactionOutputData.setMessage(result.getMessage());

        terminalService.stop();
        stopThread = true;
    }

    /**
     * @param msg
     *            MonetMessage, with new terminal state.
     */
    private void handleStateChange(MonetMessage msg) {
        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + getCurrentTerminalState()
                + " -> " + msg.getTerminalState());
        this.currentTerminalState = msg.getTerminalState();

        switch (this.currentTerminalState) {
        case STATE_CONNECTED:

            switch (msg.getTransactionCommand()) {
            case HANDSHAKE:
                handshake();
                break;
            case INFO:
                appInfo();
                break;
            case PAY:
                pay();
                break;
            case REVERSAL:
                reversal();
                break;
            case CLOSE_TOTAL_BALANCING:
                closeTotalBalancing();
                break;
            case ONLYCONNECT:
                break;
            case UNKNOWN:
                break;
            default:
                break;

            }
            break;
        default:
            break;
        }
    }

    private void handleServerMessage(TerminalFrame termFrame) {
        // sends the message to the server
        final ServerFrame serverFrame = new ServerFrame(termFrame.getData());

        Log.d(TAG, "Server command: " + serverFrame.getCommand());
        switch (serverFrame.getCommand()) {

        case TERM_CMD_ECHO:
            echoResponse(termFrame, serverFrame);
            break;

        case TERM_CMD_CONNECT:
            this.addMessageToast("Connecting to server...");
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
                    TerminalCommands.TERM_CMD_CONNECT_RES, serverFrame.getId(),
                    new byte[1]).createFrame());

            this.addMessageTermWrite(SLIPFrame.createFrame(responseTerminal
                    .createFrame()));

            break;

        case TERM_CMD_DISCONNECT:
            if (tcpThread != null) {
                tcpThread.interrupt();
                tcpThread = null;
            }
            break;

        case TERM_CMD_SERVER_WRITE:
            // Send data to server.
            if (tcpThread != null && tcpThread.isAlive()) {
                tcpThread.sendMessage(serverFrame.getData());
            } else {
                Log.e(TAG, "TCP thread isnt runnting >> " + tcpThread);
            }
        default:
            break;
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

        this.addMessageTermWrite(SLIPFrame.createFrame(responseTerminal
                .createFrame()));
    }

    /**
     * @return Current terminal state
     */
    public final synchronized TerminalState getCurrentTerminalState() {
        return currentTerminalState;
    }

    public final void addMessageTermRead(int length, byte[] buffer) {
        this.addMessage(new MonetMessage(HandleMessages.MESSAGE_TERM_READ,
                buffer, length));

    }

    public final void addMessageQuit(MonetBTAPIError error) {
        this.addMessage(new MonetMessage(HandleMessages.MESSAGE_QUIT, error));

    }

    public final void addMessageTerminalConnected(HandleMessages message,
            TerminalState terminalState, TransactionCommand command) {
        this.addMessage(new MonetMessage(message, terminalState, command));

    }

    public final void addMessage(HandleMessages message,
            TerminalState terminalState) {
        this.addMessage(new MonetMessage(message, terminalState, null));

    }

    public final void addMessageConnected(byte serverStatus) {
        this.addMessage(new MonetMessage(HandleMessages.MESSAGE_CONNECTED,
                serverStatus));

    }
}
