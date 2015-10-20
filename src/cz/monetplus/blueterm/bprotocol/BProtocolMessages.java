package cz.monetplus.blueterm.bprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cz.monetplus.blueterm.Balancing;
import cz.monetplus.blueterm.xprotocol.MessageNumber;
import cz.monetplus.blueterm.xprotocol.ProtocolType;
import cz.monetplus.blueterm.xprotocol.XProtocol;
import cz.monetplus.blueterm.xprotocol.XProtocolFactory;
import cz.monetplus.blueterm.xprotocol.XProtocolTag;

public final class BProtocolMessages {

    /**
     * Private protocols.
     */
    private BProtocolMessages() {
        super();
    }

    public static byte[] getAppInfo() {

        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "80");

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getSale(long amount, int currencyCode,
            String invoiceNumber) {

        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "00");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getReturn(int amount, int currencyCode,
            String invoiceNumber) {

        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "04");
        bprotocol.getTagMap().put(XProtocolTag.Amount1, String.valueOf(amount));
        bprotocol.getTagMap().put(XProtocolTag.CurrencyCode2,
                String.valueOf(currencyCode));
        bprotocol.getTagMap().put(XProtocolTag.InvoiceNumber, invoiceNumber);

        // XProtocolFactory factory = new XProtocolFactory();

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getReversal(String authCode) {

        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "10");
        bprotocol.getTagMap().put(XProtocolTag.AuthCode, authCode);

        return XProtocolFactory.serialize(bprotocol);
    }

    public static byte[] getHanshake() {

        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "95");

        return XProtocolFactory.serialize(bprotocol);
    }

    private static String getCurrentDateTimeForHeader() {
        SimpleDateFormat formater = new SimpleDateFormat("yyMMddHHmmss");
        return formater.format(new Date());

    }

    public static byte[] getBalancing(Balancing balancing) {

        XProtocol bprotocol = new XProtocol(ProtocolType.BProtocol,
                MessageNumber.TransactionRequest, "01", "        ",
                getCurrentDateTimeForHeader(), 0, "A5A5");

        bprotocol.getTagMap().put(XProtocolTag.TransactionType, "60");

        String format = String.format(Locale.US,
                "%03d%03d%04d%c%016d%04d%c%016d", balancing.getShiftNumber(),
                balancing.getBatchNumber(), balancing.getDebitCount(),
                balancing.getDebitAmount() >= 0 ? '+' : '-',
                balancing.getDebitAmount(), balancing.getCreditCount(),
                balancing.getCreditAmount() >= 0 ? '+' : '-',
                balancing.getCreditAmount());

        bprotocol.getTagMap().put(XProtocolTag.TotalsBatch1, format);
        return XProtocolFactory.serialize(bprotocol);
    }
}
