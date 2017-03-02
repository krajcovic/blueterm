package cz.monetplus.blueterm.worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import cz.monetplus.blueterm.Balancing;
import cz.monetplus.blueterm.TransactionCommand;
import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.requests.MaintenanceRequests;
import cz.monetplus.blueterm.requests.MbcaRequests;
import cz.monetplus.blueterm.requests.MvtaRequests;
import cz.monetplus.blueterm.requests.Requests;
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

    /**
     * Type of ticket command.
     */
    private TicketCommand lastTicket;

    /**
     * Check sign after merchant ticket. To jesteli se ma kontrolovat podpis na
     * listku se neposila v odpovedi na listek, ale v odpovedi na transakci
     * Takze si to musim zapamatovat, a vyvolat to az po samotnem vytisknuti.
     */
    // private Boolean checkSignFlag = false;

    /**
     * Terminal to muze posilat po castech.
     */
    private static ByteArrayOutputStream slipOutputpFraming = null;

    /**
     * @param context
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
    public TransactionOut getResult() {
        return transactionOutputData;
    }

    /**
     * @param msg Message for addding to queue.
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

    private void addMessage(HandleOperations operation, Requests request) {
        HandleMessage handleMessage = new HandleMessage(operation);
        handleMessage.setRequest(request);
        addMessage(handleMessage);
    }

    public void handleMessage(HandleMessage msg) {

        Log.i(TAG, "Operation: " + msg.getOperation());
        transactionInputData.getPosCallbacks()
                .progress(msg.getOperation().toString());

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
                addMessage(MbcaRequests.balancing(transactionInputData));
                break;
            }

            case CallMbcaParameters: {
                addMessage(MbcaRequests.parameters(transactionInputData));
                break;
            }

            case CallMbcaInfo: {
                addMessage(MbcaRequests.appInfoMbca(transactionInputData));
                break;
            }

            case CallMbcaAccountInfo: {
                addMessage(MbcaRequests.appAccountInfo(transactionInputData));
                break;
            }

            case CallMbcaGetLastTran: {
                addMessage(MbcaRequests.getLastTran());
                break;
            }

            case CallMbcaPay: {
                addMessage(MbcaRequests.pay(transactionInputData));
                break;
            }

            case CallMbcaRefund: {
                addMessage(MbcaRequests.refund(transactionInputData));
                break;
            }

            case CallMbcaReversal: {
                addMessage(MbcaRequests.reversal(transactionInputData));
                break;
            }

            case CallMbcaPrintTicket: {
                addMessage(new MbcaRequests().ticketRequest(TicketCommand
                        .valueOf(transactionInputData.getTicketType())));
            }

            case CallMvtaHandshake: {
                addMessage(MvtaRequests.handshakeMvta());
                break;
            }
            case CallMvtaInfo: {
                addMessage(MvtaRequests.appInfoMvta());
                break;
            }

            case CallMvtaGetLastTran: {
                addMessage(MvtaRequests.getLastTran());
                break;
            }

            case CallMvtaRecharging: {
                addMessage(MvtaRequests.recharge(transactionInputData));
                break;
            }

            case CallMvtaPrintTicket: {
                addMessage(new MvtaRequests().ticketRequest(TicketCommand
                        .valueOf(transactionInputData.getTicketType())));
            }

            case CallMvtaParameters: {
                addMessage(MvtaRequests.parameters(transactionInputData));
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
                addMessage(SmartShopRequests.getSale(transactionInputData));
                break;
            }

            case CallSmartShopRecharging: {
                addMessage(SmartShopRequests.getRecharging(transactionInputData));
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

            case CallSmartShopTip: {
                this.addMessage(SmartShopRequests.tip(transactionInputData));
                break;
            }

            case CallSmartShopPrintTicket: {
                addMessage(new MbcaRequests().ticketRequest(TicketCommand
                        .valueOf(transactionInputData.getTicketType())));
            }

            case CallMaintenanceUpdate: {
                addMessage(MaintenanceRequests.getMaintenanceUpdate());
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
                checkSign(msg.getRequest());
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

    private void checkSign(Requests request) {
        if (!transactionOutputData.getSignRequired() || transactionInputData.getPosCallbacks().isSignOk()) {
            // Sign is OK
            lastTicket = TicketCommand.Customer;
            addMessage(request.ticketRequest(lastTicket));
        } else {
            // Sign is Bad
            addMessage(HandleOperations.Exit);
        }
    }

    /**
     * Get the BluetoothDevice object.
     *
     * @param address HW address of bluetooth.
     * @param secure  True for secure connection, false for insecure.
     */
    private void connectDevice(String address, boolean secure) {
        // Get the BLuetoothDevice object
        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

            // Attempt to connect to the device
            terminalService.connect(device, secure);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            this.setOutputMessage(e.getMessage());
            this.addMessage(HandleOperations.Exit);
        }
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
            this.setOutputMessage(
                    "Bluetooth is not supported on this hardware platform.");
            this.addMessage(HandleOperations.Exit);
        }

    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
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
     * @param msg Contains status(arg1) about current connection to server.
     */
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
     * @param msg Messaget contains information read from terminal.
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

                        case MBCA: {
                            TTResponse(new MbcaRequests(), termFrame);
                            break;
                        }
                        case MVTA: {
                            TTResponse(new MvtaRequests(), termFrame);
                            break;
                        }
                        case SMARTSHOP: {
                            TTResponse(new SmartShopRequests(), termFrame);
                            break;
                        }
                        case MAINTENANCE: {
                            TTResponse(new MaintenanceRequests(), termFrame);
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

    private void TTResponse(Requests request, TerminalFrame termFrame) {
        XProtocol xprotocol = XProtocolFactory.deserialize(termFrame.getData());

        switch (xprotocol.getMessageNumber()) {
            case Ack:
                break;
            case TransactionResponse:
                transactionResponse(request, xprotocol);
                break;
            case TicketResponse:
                ticketResponse(request, xprotocol);

                break;
            default:
                Log.w(TAG, "Unexpected messageNumber: "
                        + xprotocol.getMessageNumber());
                break;

        }
    }

    private void ticketResponse(Requests request, XProtocol xprotocol) {
        String ticketLine = xprotocol.getCustomerTagMap().get(XProtocolCustomerTag.TerminalTicketLine);
        if(ticketLine != null) {
            printTicket(ticketLine.split(XProtocolFactory.REGULAR_MULTI_FID_SEPARATOR));
        }

        addMessage(request.ack());

        if (xprotocol.getCustomerTagMap()
                .containsKey(XProtocolCustomerTag.TerminalTicketInformation)) {
            TicketCommand ticketCommand = TicketCommand
                    .tagOf(xprotocol.getCustomerTagMap()
                            .get(XProtocolCustomerTag.TerminalTicketInformation)
                            .charAt(0));
            switch (ticketCommand) {
                case Continue:
                    addMessage(request.ticketRequest(TicketCommand.Next));
                    break;
                case End:
                    transactionInputData.getPosCallbacks().ticketFinish();
                    if (lastTicket == TicketCommand.Merchant) {
                        addMessage(HandleOperations.CheckSign, request);
                    } else {
                        addMessage(HandleOperations.Exit);
                    }

                    break;
                default:
                    break;

            }
        }
    }

    private void transactionResponse(Requests request, XProtocol xprotocol) {
        // ParseTransactionResponse(xprotocol);
        this.transactionOutputData = XProtocolFactory.parse(xprotocol);

        if (this.transactionOutputData.getTicketRequired()) {
            lastTicket = TicketCommand.Merchant;
            addMessage(request.ticketRequest(lastTicket));
        } else {
            addMessage(HandleOperations.Exit);
        }

        // To jesteli se ma kontrolovat podpis na listku se neposila v odpovedi
        // na listek, ale v odpovedi na transakci
        // Takze si to musim zapamatovat, a vyvolat to az po samotnem
        // vytisknuti.
        //checkSignFlag = isSignFlagOn(xprotocol);
//        if (this.transactionOutputData.getSignRequired()) {
//            if (!this.transactionOutputData.getTicketRequired()) {
//                addMessage(HandleOperations.CheckSign, request);
//            }
//        }
    }

    /**
     * @param stream
     * @return
     */
    private TerminalFrame getTerminalFrame(ByteArrayOutputStream stream) {
        TerminalFrame termFrame = new TerminalFrame(
                SLIPFrame.parseFrame(stream.toByteArray()));
        stream.reset();
        return termFrame;
    }

    // private void ParseTransactionResponse(XProtocol xprotocol) {
    // try {
    // transactionOutputData.setResultCode(Integer.valueOf(
    // xprotocol.getTagMap().get(XProtocolTag.ResponseCode)));
    // } catch (Exception e) {
    // // transactionOutputData.setResultCode(-1);
    // Log.w(TAG, "Missing ResponseCode TAG");
    // }
    // transactionOutputData.setMessage(
    // xprotocol.getTagMap().get(XProtocolTag.ServerMessage));
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.AuthCode)) {
    // transactionOutputData.setAuthCode(
    // xprotocol.getTagMap().get(XProtocolTag.AuthCode));
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.SequenceId)) {
    // transactionOutputData.setSeqId(Integer.valueOf(
    // xprotocol.getTagMap().get(XProtocolTag.SequenceId)));
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.TotalsBatch1)) {
    // transactionOutputData.setBalancing(new Balancing(
    // xprotocol.getTagMap().get(XProtocolTag.TotalsBatch1)));
    // }
    //
    // if (xprotocol.getCustomerTagMap()
    // .containsKey(XProtocolCustomerTag.CardToken)) {
    // transactionOutputData.setCardToken(xprotocol.getCustomerTagMap()
    // .get(XProtocolCustomerTag.CardToken));
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.PAN)) {
    // transactionOutputData
    // .setCardNumber(xprotocol.getTagMap().get(XProtocolTag.PAN));
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.PAN)
    // && !xprotocol.getCustomerTagMap()
    // .containsKey(XProtocolCustomerTag.CardToken)) {
    // transactionOutputData.setCardToken(
    // "000000000000000000000000000000000000000000000000");
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.CardType)) {
    // transactionOutputData.setCardType(
    // xprotocol.getTagMap().get(XProtocolTag.CardType));
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.RemainPayment)) {
    // transactionOutputData.setRemainPayment(Long.valueOf(xprotocol
    // .getTagMap().get(XProtocolTag.RemainPayment).toString()));
    // }
    //
    // if (xprotocol.getTagMap().containsKey(XProtocolTag.Amount1)) {
    // transactionOutputData.setAmount(Long.valueOf(xprotocol.getTagMap()
    // .get(XProtocolTag.Amount1).toString()));
    // }
    //
    // transactionOutputData.setTicketRequired(isTicketFlagOn(xprotocol));
    // transactionOutputData.setSignRequired(isSignFlagOn(xprotocol));
    // }

    private Boolean printTicket(List<String> list) {
        for (String string : list) {
            Boolean ticketLine = transactionInputData.getPosCallbacks()
                    .ticketLine(string);
            if (ticketLine == Boolean.FALSE) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    private Boolean printTicket(String[] list) {
        for (String string : list) {
                Boolean ticketLine = transactionInputData.getPosCallbacks()
                        .ticketLine(string);
                if (ticketLine == Boolean.FALSE) {
                    return Boolean.FALSE;
                }
        }

        return Boolean.TRUE;
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

                TerminalFrame responseTerminal = new TerminalFrame(
                        termFrame.getPortApplication().getPortApplicationNumber(),
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
     * @param termFrame   Terminal frame.
     * @param serverFrame Server frame.
     */
    private void echoResponse(TerminalFrame termFrame,
                              final ServerFrame serverFrame) {
        TerminalFrame responseTerminal = new TerminalFrame(
                termFrame.getPortApplication().getPortApplicationNumber(),
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
