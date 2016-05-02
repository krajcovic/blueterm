package cz.monetplus.blueterm.nprotocol;

import cz.monetplus.blueterm.xprotocol.MessageNumber;
import cz.monetplus.blueterm.xprotocol.ProtocolMessages;
import cz.monetplus.blueterm.xprotocol.ProtocolType;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolCustomerTag;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

public final class NProtocolMessages extends ProtocolMessages {

    /**
     * Private protocols.
     */
    private NProtocolMessages() {
        super();
    }

    /**
     * @return
     */
    public static byte[] getMaintenanceUpdate() {

        XProtocol bprotocol = new XProtocol(ProtocolType.NProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "90");

        return XProtocolFactory.serialize(bprotocol);
    }

    /**
     * @param command
     * @return
     */
    public static byte[] getTicketRequest(TicketCommand command) {
        XProtocol bprotocol = new XProtocol(ProtocolType.NProtocol,
                MessageNumber.TicketRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getCustomerTagMap().put(
                XProtocolCustomerTag.TerminalTicketInformation,
                command.getTag().toString());
        return XProtocolFactory.serialize(bprotocol);
    }

    /**
     * @return
     */
    public static byte[] getAck() {
        XProtocol bprotocol = new XProtocol(ProtocolType.NProtocol,
                MessageNumber.Ack, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        return XProtocolFactory.serialize(bprotocol);
    }
}
