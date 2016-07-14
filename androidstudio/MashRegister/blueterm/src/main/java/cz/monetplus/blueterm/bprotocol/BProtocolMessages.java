package cz.monetplus.blueterm.bprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.monetplus.blueterm.Balancing;
import cz.monetplus.blueterm.xprotocol.MessageNumber;
import cz.monetplus.blueterm.xprotocol.ProtocolMessages;
import cz.monetplus.blueterm.xprotocol.ProtocolType;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolCustomerTag;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

public final class BProtocolMessages extends ProtocolMessages {

    /**
     * Private protocols.
     */
    private BProtocolMessages() {
        super();
    }

    public static byte[] getAppInfo() {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "80");

        return XProtocolFactory.serialize(bprotocol);
    }

    private static XProtocol getInstance() {
        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");
        return bprotocol;
    }

    public static byte[] getSale(long amount, int currencyCode,
                                 String invoiceNumber, Character alternateId) {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "00");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);

        if(alternateId != null) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, alternateId.toString());
        }

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getReturn(int amount, int currencyCode,
            String invoiceNumber, Character alternateId) {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "04");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);

        if(alternateId != null) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, alternateId.toString());
        }

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getReversal(String authCode, Character alternateId) {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "10");
        bprotocol.getTagMap().put(XProtocolTag.AuthCode, authCode);

        if(alternateId != null) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, alternateId.toString());
        }

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getHanshake() {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "95");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getLastTran() {
        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "82");
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getBalancing() {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "60");

        return XProtocolFactory.serialize(bprotocol);
    }
    
    public static byte[] getParametersCall() {
        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "90");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getTicketRequest(TicketCommand command) {
        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TicketRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getCustomerTagMap().put(
                XProtocolCustomerTag.TerminalTicketInformation,
                command.getTag().toString());
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getAck() {
        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.Ack, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getAccountInfo(Character alternateId) {
        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "84");

        if(alternateId != null && alternateId != 0) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, alternateId.toString());
        }

        return XProtocolFactory.serialize(bprotocol);
    }
}
