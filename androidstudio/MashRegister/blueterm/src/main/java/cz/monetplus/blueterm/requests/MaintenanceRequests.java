package cz.monetplus.blueterm.requests;

import cz.monetplus.blueterm.frames.SLIPFrame;
import cz.monetplus.blueterm.frames.TerminalFrame;
import cz.monetplus.blueterm.nprotocol.NProtocolMessages;
import cz.monetplus.blueterm.terminals.TerminalPortApplications;
import cz.monetplus.blueterm.worker.HandleMessage;
import cz.monetplus.blueterm.worker.HandleOperations;
import cz.monetplus.blueterm.xprotocol.TicketCommand;

public class MaintenanceRequests implements Requests {

    public static HandleMessage getMaintenanceUpdate() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MAINTENANCE
                                .getPortApplicationNumber(),
                        NProtocolMessages.getMaintenanceUpdate())
                                .createFrame())));
    }

    @Override
    public HandleMessage ticketRequest(TicketCommand command) {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MAINTENANCE
                                .getPortApplicationNumber(),
                        NProtocolMessages.getTicketRequest(command))
                                .createFrame())));
    }

    @Override
    public HandleMessage ack() {
        return (new HandleMessage(HandleOperations.TerminalWrite,
                SLIPFrame.createFrame(new TerminalFrame(
                        TerminalPortApplications.MAINTENANCE
                                .getPortApplicationNumber(),
                        NProtocolMessages.getAck()).createFrame())));
    }

}
