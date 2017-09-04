package cz.monetplus.blueterm.requests;

import cz.monetplus.blueterm.v1.TransactionIn;
import cz.monetplus.blueterm.bprotocol.BProtocolMessages;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.vprotocol.VProtocolMessages;
import cz.monetplus.blueterm.worker.HandleMessage;
import cz.monetplus.blueterm.worker.HandleOperations;
import cz.monetplus.blueterm.xprotocol.TicketCommand;

public class MvtaRequests implements Requests {

    /**
     * Create and send handshake to terminal.
     * @return 
     */
    public static HandleMessage handshakeMvta() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getHanshake()).createFrame())));
    }

    /**
     * Create and send app info request to terminal.
     * @return 
     */
    public static HandleMessage appInfoMvta() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getAppInfo()).createFrame())));
    }
    
    /**
     * Create and send pay request to terminal.
     * @param transactionInputData 
     * @return 
     */
    public static HandleMessage recharge(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
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
   public static HandleMessage getLastTran() {
       return (new HandleMessage(HandleOperations.TerminalWrite,
               SLIPFrame.createFrame(new TerminalFrame(
                       TerminalPortApplications.MBCA
                               .getPortApplicationNumber(), BProtocolMessages
                               .getLastTran()).createFrame())));
   }
    
    @Override
    public HandleMessage ticketRequest(TicketCommand command) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getTicketRequest(command)).createFrame())));
    }

    @Override
    public HandleMessage ack() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                                .getAck()).createFrame())));
    }

    /**
     * Create and send call mbca parameters.
     */
    public static HandleMessage parameters(TransactionIn transactionInputData) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MVTA
                                .getPortApplicationNumber(), VProtocolMessages
                        .getParametersCall()).createFrame())));
    }
    
}
