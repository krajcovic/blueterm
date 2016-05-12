package cz.monetplus.blueterm.sprotocol;

import cz.monetplus.blueterm.xprotocol.MessageNumber;
import cz.monetplus.blueterm.xprotocol.ProtocolMessages;
import cz.monetplus.blueterm.xprotocol.ProtocolType;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolCustomerTag;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

public final class SProtocolMessages extends ProtocolMessages {

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
            String invoiceNumber, Boolean partialPayment, String ticketNumber, Integer cardType) {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "00");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));

        bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.InvoiceNumber,
                invoiceNumber);

        if(cardType != null) {
            bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.Restriction,
                    cardType.toString());
        }

        if (partialPayment != null && partialPayment == true) {
            bprotocol.getCustomerTagMap()
                    .put(XProtocolCustomerTag.SupportPartialPayment, "1");
        }

        if (ticketNumber != null && ticketNumber.length() > 0) {
            bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.TicketNumber,
                    ticketNumber);
        }

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getRecharging(long amount, int currencyCode,
            String invoiceNumber) {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "17");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));

        bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.InvoiceNumber,
                invoiceNumber);

//        if (partialPayment != null && partialPayment == true) {
//            bprotocol.getCustomerTagMap()
//                    .put(XProtocolCustomerTag.SupportPartialPayment, "1");
//        }

//        if (ticketNumber != null && ticketNumber.length() > 0) {
//            bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.TicketNumber,
//                    ticketNumber);
//        }

        return XProtocolFactory.serialize(bprotocol);
    }

    // public static byte[] getPartialPayment(long amount, int currencyCode,
    // String invoiceNumber) {
    //
    // XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
    // MessageNumber.TransactionRequest, "01", " ",
    // getCurrentDateTimeForHeader(), 0, "A5A5");
    //
    // bprotocol.getTagMap().put(XProtocolTag.TransactionType, "00");
    // bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
    // bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
    // String.valueOf(currencyCode));
    //
    // bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.SupportPartialPayment,
    // "1");
    //
    // bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.InvoiceNumber,
    // invoiceNumber);
    //
    // return XProtocolFactory.serialize(bprotocol);
    // }

    public static byte[] getReturn(long amount, int currencyCode,
            String invoiceNumber) {

        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "04");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
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
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getDeactivate() {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "20");
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getLastTran() {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "82");
        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getParametersCall() {
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

    public static byte[] getTip(long amount, int currencyCode) {
        XProtocol bprotocol = new XProtocol(ProtocolType.SProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "70");
        return XProtocolFactory.serialize(bprotocol);
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
