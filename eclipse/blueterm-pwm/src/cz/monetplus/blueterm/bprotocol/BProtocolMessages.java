package cz.monetplus.blueterm.bprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
/**
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
public final class BProtocolMessages {

    /**
     * Private protocols.
     */
    private BProtocolMessages() {
        super();
    }

    /**
     * App info message in BProtocol.
     * 
     * @return Byte array of message.
     */
    public static byte[] getAppInfo() {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "    ", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "80");

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    /**
     * @param amount
     *            Amount of price.
     * @param currencyCode
     *            Currency code.
     * @param invoiceNumber
     *            Invoice number.
     * @return Byte array with message.
     */
    public static byte[] getSale(Long amount, int currencyCode,
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

    /**
     * @param amount
     *            Amount of price.
     * @param currencyCode
     *            Currency code.
     * @param invoiceNumber
     *            Invoice number.
     * @return Byte array with message.
     */
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

    /**
     * @param amount
     *            Amount of price.
     * @param currencyCode
     *            Currency code.
     * @param invoiceNumber
     *            Invoice number.
     * @return Byte array with message.
     */
    public static byte[] getReversal(Long amount, int currencyCode,
            String invoiceNumber, String authCode) {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "10");
        bprotocol.getTagMap().put(BProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(BProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(BProtocolTag.AuthCode, authCode);
        // bprotocol.getTagMap().put(BProtocolTag.InvoiceNumber, invoiceNumber);

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    /**
     * @return Byte array with message.
     */
    public static byte[] getHanshake() {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "95");

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    /**
     * @return Byte array with message.
     */
    public static byte[] getCloseTotalBalancing() {

        BProtocol bprotocol = new BProtocol("B1", "01", "        ",
                getCurrentDateTimeForHeader(), "0000", "A5A5");

        bprotocol.getTagMap().put(BProtocolTag.TransactionType, "60");

        BProtocolFactory factory = new BProtocolFactory();

        return factory.serialize(bprotocol);
    }

    /**
     * @return String with current date time format.
     */
    private static String getCurrentDateTimeForHeader() {
        SimpleDateFormat formater = new SimpleDateFormat("yyMMddHHmmss",
                Locale.getDefault());
        return formater.format(new Date());

    }
}
