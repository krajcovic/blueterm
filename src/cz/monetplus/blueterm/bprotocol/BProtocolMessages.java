package cz.monetplus.blueterm.bprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class BProtocolMessages {

    /**
     * Private protocols.
     */
    private BProtocolMessages() {
        super();
    }

    public static byte[] getAppInfo() {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "    ", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "80");

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    public static byte[] getSale(int amount, int currencyCode,
            String invoiceNumber) {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "00");
        bprotocol.getTagMap().put(BProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(BProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(BProtocolTag.InvoiceNumber, invoiceNumber);

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }
    
    public static byte[] getReturn(int amount, int currencyCode,
            String invoiceNumber) {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "04");
        bprotocol.getTagMap().put(BProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(BProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(BProtocolTag.InvoiceNumber, invoiceNumber);

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }
    
    public static byte[] getReversal(int amount, int currencyCode,
            String invoiceNumber) {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "10");
        bprotocol.getTagMap().put(BProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(BProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(BProtocolTag.InvoiceNumber, invoiceNumber);

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    public static byte[] getHanshake() {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "95");

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    private static String getCurrentDateTimeForHeader() {
        SimpleDateFormat formater = new SimpleDateFormat("yyMMddHHmmss");
        return formater.format(new Date());

    }
}
