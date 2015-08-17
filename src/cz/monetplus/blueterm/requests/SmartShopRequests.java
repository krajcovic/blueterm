package cz.monetplus.blueterm.requests;

import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.sprotocol.SProtocolMessages;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.worker.HandleMessage;
import cz.monetplus.blueterm.worker.HandleOperations;
import cz.monetplus.blueterm.xprotocol.TicketCommand;

public class SmartShopRequests {

    public static HandleMessage activate() {
        return new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getActivate()).createFrame()));
    }

    public static HandleMessage deactivate() {
        return new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getDeactivate()).createFrame()));
    }
    
    /**
     * Create and send pay request to terminal.
     * @param transactionInputData 
     * @return 
     */
    public static HandleMessage pay(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getSale(transactionInputData.getAmount(),
                                        transactionInputData.getCurrency(),
                                        transactionInputData.getInvoice()))
                        .createFrame())));
    }
    
    /**
     * Create and send return request to terminal.
     * @param transactionInputData 
     * @return 
     */
    public static HandleMessage getReturn(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getReturn(transactionInputData.getAmount(),
                                        transactionInputData.getCurrency(),
                                        transactionInputData.getInvoice()))
                        .createFrame())));
    }
    
    public static HandleMessage getCardState() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getCardState()).createFrame())));
    }

    public static HandleMessage getAppInfo() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getAppInfo()).createFrame())));
    }

    public static HandleMessage getLastTrans() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getLastTran()).createFrame())));
    }

    public static HandleMessage parametersCall() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getParametersCall()).createFrame())));
    }

    public static HandleMessage handshake() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getHanshake()).createFrame())));
    }

    public static HandleMessage ticketRequest(TicketCommand command) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getTicketRequest(command)).createFrame())));
    }

    public static HandleMessage ack() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.SMARTSHOP
                                .getPortApplicationNumber(), SProtocolMessages
                                .getAck()).createFrame())));
    }

}
