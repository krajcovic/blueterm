package cz.monetplus.blueterm.vprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.monetplus.blueterm.xprotocol.MessageNumber;
import cz.monetplus.blueterm.xprotocol.ProtocolType;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolCustomerTag;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

public final class VProtocolMessages {

    /**
     * Private protocols.
     */
    private VProtocolMessages() {
        super();
    }

    public static byte[] getAppInfo() {

        XProtocol bprotocol = new XProtocol(ProtocolType.VProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "80");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getEmvRecharge(long amount, int currencyCode,
            String invoiceNumber, long ticketId, char rechargingType) {

        XProtocol bprotocol = new XProtocol(ProtocolType.VProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "17");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);
        bprotocol.getTagMap().put(XProtocolTag.SaleTicketId,
                String.valueOf(ticketId));
        bprotocol.getTagMap().put(XProtocolTag.RechargingType,
                String.valueOf(rechargingType));

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getHanshake() {

        XProtocol bprotocol = new XProtocol(ProtocolType.VProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "95");

        return XProtocolFactory.serialize(bprotocol);
    }

    private static String getCurrentDateTimeForHeader() {
        SimpleDateFormat formater = new SimpleDateFormat("yyMMddHHmmss");
        return formater.format(new Date());

    }

    public static byte[] getLastTran() {
        XProtocol bprotocol = new XProtocol(ProtocolType.VProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "82");
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getTicketRequest(TicketCommand command) {
        XProtocol bprotocol = new XProtocol(ProtocolType.VProtocol,
                MessageNumber.TicketRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getCustomerTagMap().put(
                XProtocolCustomerTag.TerminalTicketInformation,
                command.getTag().toString());
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getAck() {
        XProtocol bprotocol = new XProtocol(ProtocolType.VProtocol,
                MessageNumber.Ack, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        return XProtocolFactory.serialize(bprotocol);
    }
}
