package cz.monetplus.blueterm.requests;

import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.worker.HandleMessage;
import cz.monetplus.blueterm.xprotocol.TicketCommand;

public interface Requests {
    
    public HandleMessage ack();
    
    public HandleMessage ticketRequest(TicketCommand command);
}
