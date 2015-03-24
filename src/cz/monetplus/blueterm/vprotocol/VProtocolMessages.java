package cz.monetplus.blueterm.vprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.monetplus.blueterm.bprotocol.BProtocol;
import cz.monetplus.blueterm.bprotocol.BProtocolFactory;
import cz.monetplus.blueterm.bprotocol.BProtocolTag;

public final class VProtocolMessages {

    /**
     * Private protocols.
     */
    private VProtocolMessages() {
        super();
    }

    public static byte[] getAppInfo() {

        BProtocol bprotocol = new BProtocol("V1", "01", "        ",
                getCurrentDateTimeForHeader(), "    ", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "80");

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    public static byte[] getEmvRecharge(long amount, int currencyCode,
            String invoiceNumber, long ticketId, char rechargingType) {

        BProtocol bprotocol = new BProtocol("V1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "17");
        bprotocol.getTagMap().put(BProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(BProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(BProtocolTag.InvoiceNumber, invoiceNumber);
        bprotocol.getTagMap().put(BProtocolTag.SaleTicketId, String.valueOf(ticketId));
        bprotocol.getTagMap().put(BProtocolTag.RechargingType, String.valueOf(rechargingType));

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }
    
   

    public static byte[] getHanshake() {

        BProtocol bprotocol = new BProtocol("V1", "01", "        ",
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
