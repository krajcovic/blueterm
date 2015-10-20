package cz.monetplus.blueterm.worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cz.monetplus.blueterm.Balancing;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.requests.MbcaRequests;
import cz.monetplus.blueterm.requests.MvtaRequests;
import cz.monetplus.blueterm.requests.SmartShopRequests;
import cz.monetplus.blueterm.server.ServerFrame;
import cz.monetplus.blueterm.terminals.TerminalCommands;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.terminals.TerminalServiceBTClient;
import cz.monetplus.blueterm.util.MonetUtils;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolCustomerTag;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

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

    private static volatile MessageThread instance = null;

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
    private BlockingQueue<HandleMessage> queue = new LinkedBlockingQueue<HandleMessage>();

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
    private TerminalServiceBTClient terminalService = null;

    private TicketCommand lastTicket;

    /**
     * Terminal to muze posilat po castech.
     */
    private static ByteArrayOutputStream slipOutputpFraming = null;

    /**
     * @param context
     * @param terminalPort
     * @param transactionInputData
     */
    private MessageThread(final Context context,
            TransactionIn transactionInputData) {
        super();

        slipOutputpFraming = new ByteArrayOutputStream();
        slipOutputpFraming.reset();

        applicationContext = context;
        this.transactionInputData = transactionInputData;
        this.transactionOutputData = new TransactionOut();

        addMessage(HandleOperations.GetBluetoothAddress);
    }

    public static MessageThread getInstance(final Context context,
            TransactionIn transactionInputData) throws Exception {
        if (instance == null) {
            synchronized (MessageThread.class) {
                if (instance == null) {
                    instance = new MessageThread(context, transactionInputData);
                }
            }
        } else {
            throw new Exception("Another thread communicate with blueterm");
        }
        
        return instance;
    }

    @Override
    public void run() {
        while (!stopThread) {
            // if (queue.peek() != null) {
            try {
                handleMessage(queue.take());
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
            // }
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

    public void handleMessage(HandleMessage msg) {

        Log.i(TAG, "Operation: " + msg.getOperation());
        transactionInputData.getPosCallbacks().progress(
                msg.getOperation().toString());

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
            addMessage(MbcaRequests.handshakeMbca());
            break;
        }
        case CallMbcaBalancing: {
            addMessage(MbcaRequests.balancingMbca(transactionInputData));
            break;
        }
        case CallMbcaInfo: {
            addMessage(MbcaRequests.appInfoMbca());
            break;
        }
        case CallMbcaPay: {
            addMessage(MbcaRequests.pay(transactionInputData));
            break;
        }
        case CallMbcaReversal: {
            addMessage(MbcaRequests.reversal(transactionInputData));
            break;
        }        
        case CallMvtaHandshake: {
            addMessage(MvtaRequests.handshakeMvta());
            break;
        }
        case CallMvtaInfo: {
            addMessage(MvtaRequests.appInfoMvta());
            break;
        }
        case CallMvtaRecharging: {
            addMessage(MvtaRequests.recharge(transactionInputData));
            break;
        }

        case CallSmartShopActivate: {
            addMessage(SmartShopRequests.activate());
            break;
        }

        case CallSmartShopDeactivate: {
            addMessage(SmartShopRequests.deactivate());
            break;
        }

        case CallSmartShopPay: {
            addMessage(SmartShopRequests.pay(transactionInputData));
            break;
        }

        case CallSmartShopReturn: {
            addMessage(SmartShopRequests.getReturn(transactionInputData));
            break;
        }

        case CallSmartShopCardState: {
            addMessage(SmartShopRequests.getCardState());
            break;
        }

        case CallSmartShopGetAppInfo: {
            addMessage(SmartShopRequests.getAppInfo());
            break;
        }

        case CallSmartShopGetLastTran: {
            this.addMessage(SmartShopRequests.getLastTrans());
            break;
        }

        case CallSmartShopParametersCall: {
            this.addMessage(SmartShopRequests.parametersCall());
            break;
        }

        case CallSmartShopHandshake: {
            this.addMessage(SmartShopRequests.handshake());
            break;
        }

        case TerminalWrite: {
            write2Terminal(msg.getBuffer().array());
            break;
        }

        case TerminalRead: {
            receiveTerminalPackets(msg);
            break;
        }

        case ServerConnected:
            serverConnected(msg);
            break;

        case ShowMessage:
            String message = new String(msg.getBuffer().array());
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                    .show();

            break;

        case CheckSign:
            if (transactionInputData.getPosCallbacks().isSingOk()) {
                // Sign is OK
                addMessage(SmartShopRequests
                        .ticketRequest(TicketCommand.Customer));
            } else {
                // Sign is Bad
                addMessage(HandleOperations.Exit);
            }
            break;

        case Exit:
            this.stopThread();
            instance = null;
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
        terminalService = new TerminalServiceBTClient(applicationContext, this,
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
                serverConnectionID, msg.getBuffer().array());
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
            slipOutputpFraming.write(msg.getBuffer().array());

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

                    case MBCA:
                    case MVTA:
                    case SMARTSHOP: {
                        XProtocol xprotocol = XProtocolFactory
                                .deserialize(termFrame.getData());

                        switch (xprotocol.getMessageNumber()) {
                        case Ack:
                            break;
                        case TransactionResponse:
                            ParseTransactionResponse(xprotocol);

                            if (isTicketFlagOn(xprotocol)) {
                                addMessage(SmartShopRequests
                                        .ticketRequest(TicketCommand.Merchant));
                                lastTicket = TicketCommand.Merchant;
                            } else {
                                addMessage(HandleOperations.Exit);
                            }
                            break;
                        case TicketResponse:
                            ParseTicketResponse(xprotocol);
                            addMessage(SmartShopRequests.ack());

                            if (xprotocol
                                    .getCustomerTagMap()
                                    .containsKey(
                                            XProtocolCustomerTag.TerminalTicketInformation)) {
                                TicketCommand ticketCommand = TicketCommand
                                        .tagOf(xprotocol
                                                .getCustomerTagMap()
                                                .get(XProtocolCustomerTag.TerminalTicketInformation)
                                                .charAt(0));
                                switch (ticketCommand) {
                                case Continue:
                                    addMessage(SmartShopRequests
                                            .ticketRequest(TicketCommand.Next));
                                    break;
                                case End:
                                    transactionInputData.getPosCallbacks()
                                            .ticketFinish();
                                    if (isSingFlagOn(xprotocol)) {
                                        addMessage(HandleOperations.CheckSign);
                                    } else {
                                        if (lastTicket == TicketCommand.Merchant) {
                                            addMessage(SmartShopRequests
                                                    .ticketRequest(TicketCommand.Customer));
                                            lastTicket = TicketCommand.Customer;

                                        } else {
                                            addMessage(HandleOperations.Exit);
                                        }
                                    }
                                    break;
                                default:
                                    break;

                                }
                            }

                            break;
                        default:
                            Log.w(TAG,
                                    "Unexpected messageNumber: "
                                            + xprotocol.getMessageNumber());
                            break;

                        }

                        break;
                    }

                    default:
                        Log.w(TAG, "Unsupported application port number: "
                                + termFrame.getPortApplication());
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

    private boolean checkBit(int value, int i) {
        return (value & (1L << i)) == (1L << i);
    }

    private boolean isTicketFlagOn(XProtocol xprotocol) {
        return checkBit(xprotocol.getFlag(), 1);
    }

    private boolean isSingFlagOn(XProtocol xprotocol) {
        return checkBit(xprotocol.getFlag(), 0);
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

    private void ParseTransactionResponse(XProtocol xprotocol) {
        // transactionOutputData = new TransactionOut();
        try {
            transactionOutputData.setResultCode(Integer.valueOf(xprotocol
                    .getTagMap().get(XProtocolTag.ResponseCode)));
        } catch (Exception e) {
            // transactionOutputData.setResultCode(-1);
            Log.w(TAG, "Missing ResponseCode TAG");
        }
        transactionOutputData.setMessage(xprotocol.getTagMap().get(
                XProtocolTag.ServerMessage));
        
        try {
            transactionOutputData.setAuthCode(xprotocol
                    .getTagMap().get(XProtocolTag.AuthCode));
        } catch (Exception e) {
            transactionOutputData.setAuthCode(null);
        }
        
        try {
            transactionOutputData.setSeqId(Integer.valueOf(xprotocol
                    .getTagMap().get(XProtocolTag.SequenceId)));
        } catch (Exception e) {
            transactionOutputData.setSeqId(0);
        }
        
        if(xprotocol.getTagMap().containsKey(XProtocolTag.TotalsBatch1)) {
            transactionOutputData.setBalancing(new Balancing(xprotocol
                    .getTagMap().get(XProtocolTag.TotalsBatch1)));
        }
        
        transactionOutputData.setCardNumber(xprotocol.getTagMap().get(
                XProtocolTag.PAN));
        transactionOutputData.setCardType(xprotocol.getTagMap().get(
                XProtocolTag.CardType));
    }

    private void ParseTicketResponse(XProtocol xprotocol) {
        for (String string : xprotocol.getTicketList()) {
            transactionInputData.getPosCallbacks().ticketLine(string);
        }
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
