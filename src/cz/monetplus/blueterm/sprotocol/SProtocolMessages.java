package cz.monetplus.blueterm.sprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.monetplus.blueterm.xprotocol.MessageNumber;
import cz.monetplus.blueterm.xprotocol.ProtocolType;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolCustomerTag;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

public final class SProtocolMessages {

    /**
     * Private protocols.
     */
    private SProtocolMessages() {
        super();
    }

    public static byte[] getAppInfo() {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "80");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getSale(long amount, int currencyCode,
            String invoiceNumber) {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "00");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        // bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);
        bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.InvoiceNumber,
                invoiceNumber);

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getReturn(long amount, int currencyCode,
            String invoiceNumber) {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "04");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        // bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);
        bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.InvoiceNumber,
                invoiceNumber);

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getCardState() {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");
        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "07");
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getActivate() {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "15");

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getDeactivate() {
        // TODO: not finished
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "20");

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getLastTran() {
        // TODO: not finished
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "82");

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getParametersCall() {
        // TODO: not finished
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "90");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getHanshake() {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "95");
        return XProtocolFactory.serialize(bprotocol);
    }

    private static String getCurrentDateTimeForHeader() {
        SimpleDateFormat formater = new SimpleDateFormat("yyMMddHHmmss", Locale.US);
        return formater.format(new Date());

    }

    public static byte[] getTicketRequest(TicketCommand command) {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TicketRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getCustomerTagMap().put(
                XProtocolCustomerTag.TerminalTicketInformation,
                command.getTag().toString());
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getAck() {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.Ack, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        return XProtocolFactory.serialize(bprotocol);
    }
}