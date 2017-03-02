package cz.monetplus.blueterm.requests;

import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.worker.HandleMessage;
import cz.monetplus.blueterm.worker.HandleOperations;
import cz.monetplus.blueterm.xprotocol.TicketCommand;

public class MbcaRequests implements Requests {
    /**
     * Create and send pay request to terminal.
     * @param transactionInputData 
     * @return 
     */
    public static HandleMessage pay(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getSale(transactionInputData.getAmount(),
                                        transactionInputData.getCurrency(),
                                        transactionInputData.getInvoice(), transactionInputData.getAlternateId(), transactionInputData.getGastroData()))
                        .createFrame())));
    }

    /**
     * Create and send pay request to terminal.
     * @param transactionInputData
     * @return
     */
    public static HandleMessage refund(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                        .getRefund(transactionInputData.getAmount(),
                                transactionInputData.getCurrency(),
                                transactionInputData.getInvoice(), transactionInputData.getAlternateId(), transactionInputData.getGastroData()))
                        .createFrame())));
    }
    
    /**
     * Create and send pay request to terminal.
     * @param transactionInputData 
     * @return 
     */
    public static HandleMessage reversal(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getReversal(transactionInputData.getAuthCode(), transactionInputData.getAlternateId()))
                        .createFrame())));
    }

    /**
     * Create and send handshake to terminal.
     */
    public static HandleMessage handshakeMbca() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getHanshake()).createFrame())));
    }

    /**
     * Create and send handshake to terminal.
     */
    public static HandleMessage balancing(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getBalancing()).createFrame())));
    }
    
    /**
     * Create and send call mbca parameters.
     */
    public static HandleMessage parameters(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getParametersCall()).createFrame())));
    }
    
    /**
    * Create and send app info request to terminal.
    */
   public static HandleMessage getLastTran() {
       return (new HandleMessage(HandleOperations.TerminalWrite,
               SLIPFrame.createFrame(new TerminalFrame(
                       TerminalPortApplications.MBCA
                               .getPortApplicationNumber(), BProtocolMessages
                               .getLastTran()).createFrame())));
   }

     /**
     * Create and send app info request to terminal.
     */
    public static HandleMessage appInfoMbca(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getAppInfo()).createFrame())));
    }

    public static HandleMessage appAccountInfo(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                        .getAccountInfo(transactionInputData.getAlternateId())).createFrame())));
    }

    @Override
    public HandleMessage ticketRequest(TicketCommand command) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getTicketRequest(command)).createFrame())));
    }

    @Override
    public HandleMessage ack() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MBCA
                                .getPortApplicationNumber(), BProtocolMessages
                                .getAck()).createFrame())));
    }
}
