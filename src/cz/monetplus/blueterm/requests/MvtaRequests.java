package cz.monetplus.blueterm.requests;

import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.vprotocol.VProtocolMessages;
import cz.monetplus.blueterm.worker.HandleMessage;
import cz.monetplus.blueterm.worker.HandleOperations;

public class MvtaRequests {

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
    
}
