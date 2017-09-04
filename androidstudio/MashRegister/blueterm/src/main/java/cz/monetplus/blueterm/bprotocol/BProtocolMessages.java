package cz.monetplus.blueterm.bprotocol;

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
     * bit 1 = pokladna musi tisknout listecek
     * bit 15 je pozadovane potvrzeni prijeti zpravy
     */
    private static final Integer FLAG = 0x0000;

    /**
     * Private protocols.
     */
    private BProtocolMessages() {
        super();
    }

    private static XProtocol getInstance() {
        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), FLAG, "A5A5");

        return bprotocol;
    }

    public static byte[] getAppInfo() {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "80");

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getSale(long amount, int currencyCode,
                                 String invoiceNumber, Integer alternateId, String gastroData) {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "00");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));

        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));

        if(invoiceNumber != null && !invoiceNumber.isEmpty()) {
            bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);
        }

        if(alternateId != null && (alternateId != 0)) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, Integer.toString(alternateId));
        }

        if(gastroData != null && !gastroData.isEmpty()) {
            bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.GastroData, gastroData);
        }

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getRefund(long amount, int currencyCode,
                                   String invoiceNumber, Integer alternateId, String gastroData) {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "04");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));

        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));

        if(invoiceNumber != null && !invoiceNumber.isEmpty()) {
            bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);
        }

        if(alternateId != null && (alternateId != 0)) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, Integer.toString(alternateId));
        }

        if(gastroData != null && !gastroData.isEmpty()) {
            bprotocol.getCustomerTagMap().put(XProtocolCustomerTag.GastroData, gastroData);
        }

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getReversal(String authCode, Integer alternateId) {

        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "10");
        bprotocol.getTagMap().put(XProtocolTag.AuthCode, authCode);

        if(alternateId != null && (alternateId != 0)) {
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, Integer.toString(alternateId));
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

    public static byte[] getAccountInfo(Integer alternateId) {
        XProtocol bprotocol = getInstance();

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "84");

        if((alternateId != null) && (alternateId != 0)) {
//            String hexa = Integer.toString(alternateId, 16).toUpperCase();
            bprotocol.getTagMap().put(XProtocolTag.AlternateId, Integer.toString(alternateId));
        }

        return XProtocolFactory.serialize(bprotocol);
    }
}
