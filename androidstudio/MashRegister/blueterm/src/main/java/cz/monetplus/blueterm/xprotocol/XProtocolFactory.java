package cz.monetplus.blueterm.xprotocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.util.Log;

import cz.monetplus.blueterm.Balancing;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.util.MonetUtils;

public class XProtocolFactory {
    private static final byte STX = 0x02;

    private static final byte ETX = 0x03;

    private static final byte FS = 0x1c;

    private static final byte GS = 0x1d;

    private static final String TAG = "XProtocolFactory";
    public static final String MULTI_FID_SEPARATOR = "|";
    public static final String REGULAR_MULTI_FID_SEPARATOR = "\\|";

    private static boolean checkBit(int value, int i) {
        return (value & (1L << i)) == (1L << i);
    }

    private static boolean isTicketFlagOn(XProtocol xprotocol) {
        return checkBit(xprotocol.getFlag(), 1);
    }

    private static boolean isSignFlagOn(XProtocol xprotocol) {
        return checkBit(xprotocol.getFlag(), 0);
    }

    public static TransactionOut parse(XProtocol protocol) {
        TransactionOut tran = new TransactionOut();

        try {
            tran.setResultCode(Integer.valueOf(
                    protocol.getTagMap().get(XProtocolTag.ResponseCode)));
        } catch (Exception e) {
            Log.w(TAG, "Missing ResponseCode TAG");
        }
        tran.setMessage(protocol.getTagMap().get(XProtocolTag.ServerMessage));

        if (protocol.getTagMap().containsKey(XProtocolTag.AuthCode)) {
            tran.setAuthCode(protocol.getTagMap().get(XProtocolTag.AuthCode));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.SequenceId)) {
            tran.setSeqId(Integer.valueOf(
                    protocol.getTagMap().get(XProtocolTag.SequenceId)));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.TotalsBatch1)) {
            tran.setBalancing(new Balancing(
                    protocol.getTagMap().get(XProtocolTag.TotalsBatch1)));
        }

        if (protocol.getCustomerTagMap()
                .containsKey(XProtocolCustomerTag.CardToken)) {
            tran.setCardToken(protocol.getCustomerTagMap()
                    .get(XProtocolCustomerTag.CardToken));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.PAN)) {
            tran.setCardNumber(protocol.getTagMap().get(XProtocolTag.PAN));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.PAN)
                && !protocol.getCustomerTagMap()
                .containsKey(XProtocolCustomerTag.CardToken)) {
            tran.setCardToken(
                    "000000000000000000000000000000000000000000000000");
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.CardType)) {
            tran.setCardType(protocol.getTagMap().get(XProtocolTag.CardType));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.RemainPayment)) {
            tran.setRemainPayment(Long.valueOf(protocol.getTagMap()
                    .get(XProtocolTag.RemainPayment).toString()));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.Amount1)) {
            tran.setAmount(Long.valueOf(
                    protocol.getTagMap().get(XProtocolTag.Amount1).toString()));
        }

        if (protocol.getTagMap().containsKey(XProtocolTag.AlternateId)) {
            tran.setMerchantId(protocol.getTagMap().get(XProtocolTag.AlternateId).split(XProtocolFactory.REGULAR_MULTI_FID_SEPARATOR));
        }

        tran.setTicketRequired(isTicketFlagOn(protocol));
        tran.setSignRequired(isSignFlagOn(protocol));

        return tran;
    }

    public static byte[] serialize(XProtocol bprotocol) {
        Log.i(TAG, bprotocol.toString());
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        try {

            byte[] optionalData = compileTags(bprotocol.getTagMap(),
                    bprotocol.getCustomerTagMap());
            bprotocol.setOptionalDataLen(MonetUtils.bytesToHex(ByteBuffer
                    .allocate(2)
                    .putShort((short) (optionalData.length & 0xFFFF)).array()));

            bout.write(STX);
            bout.write(
                    fixString(bprotocol.getProtocolType().getTag().toString(),
                            1).getBytes());
            bout.write(fixString(
                    bprotocol.getMessageNumber().getNumber().toString(), 1)
                    .getBytes());
            bout.write(fixString(bprotocol.getProtocolVersion(), 2).getBytes());
            bout.write(fixString(bprotocol.getPosId(), 8).getBytes());
            bout.write(fixString(bprotocol.getTransactionDateTime(), 12)
                    .getBytes());
            bout.write(fixString(bprotocol.getFlag().toString(), 4, '0')
                    .getBytes());
            bout.write(fixString(bprotocol.getOptionalDataLen(), 4, '0')
                    .getBytes());
            bout.write(
                    fixString(bprotocol.getStandardCRC16(), 4, '0').getBytes());
            bout.write(optionalData);
            bout.write(ETX);
        } catch (IOException e) {
            Log.e(TAG, "Serialize xprotocol", e);
        }

        return bout.toByteArray();
    }

    /**
     * @param buffer
     * @return
     */
    public static XProtocol deserialize(byte[] buffer) {

        XProtocol bprotocol = new XProtocol();

        try {
            String tmp = new String(Arrays.copyOfRange(buffer, 1, 2), "UTF8");
            bprotocol.setProtocolType(ProtocolType.tagOf(tmp.charAt(0)));
            bprotocol.setMessageNumber(MessageNumber.numberOf(Integer.valueOf(
                    new String(Arrays.copyOfRange(buffer, 2, 3), "UTF8"))));
            bprotocol.setProtocolVersion(
                    new String(Arrays.copyOfRange(buffer, 3, 5), "UTF8"));
            bprotocol.setPosId(
                    new String(Arrays.copyOfRange(buffer, 5, 13), "UTF8"));
            bprotocol.setTransactionDateTime(
                    new String(Arrays.copyOfRange(buffer, 13, 25), "UTF8"));

            bprotocol.setFlag(Integer.valueOf(
                    new String(Arrays.copyOfRange(buffer, 25, 29), "UTF8")));
            bprotocol.setOptionalDataLen(
                    new String(Arrays.copyOfRange(buffer, 29, 33), "UTF8"));
            bprotocol.setStandardCRC16(
                    new String(Arrays.copyOfRange(buffer, 33, 37), "UTF8"));

            String[] split = splitTags(
                    Arrays.copyOfRange(buffer, 37, buffer.length - 1),
                    "ISO-8859-2", "[\\x1c]");

            for (String element : split) {
                if (element != null && element.length() > 0) {
                    XProtocolTag tagOf = XProtocolTag.tagOf(element.charAt(0));
                    if (tagOf.equals(XProtocolTag.CustomerFid)) {
                        deserializeCustomer(bprotocol, element);
                    } else {
                        if (bprotocol.getTagMap().containsKey(tagOf)) {
                            // Uz je v mape
                            String value = bprotocol.getTagMap().get(tagOf);
                            value += MULTI_FID_SEPARATOR + element.substring(1);
                            bprotocol.getTagMap().put(tagOf, value);
                        } else {
                            // Jeste neni v mape.
                            bprotocol.getTagMap().put(tagOf, element.substring(1));
                        }

                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Desearilize xprotocol", e);
        }

        Log.i(TAG, bprotocol.toString());
        return bprotocol;
    }

    private static void deserializeCustomer(XProtocol xprotocol, String element)
            throws UnsupportedEncodingException {
        String[] customerTags = splitTags(Arrays.copyOfRange(element.getBytes(),
                1, element.getBytes().length), "UTF-8", "[\\x1d]");
        for (String customerElement : customerTags) {
            if (customerElement != null && customerElement.length() > 0) {
                XProtocolCustomerTag tagOf = XProtocolCustomerTag
                        .tagOf(customerElement.charAt(0));

                if (xprotocol.getCustomerTagMap().containsKey(tagOf)) {
                    String oldValue = xprotocol.getCustomerTagMap().get(tagOf);
                    oldValue += MULTI_FID_SEPARATOR + customerElement.substring(1);
                    xprotocol.getCustomerTagMap().put(tagOf, oldValue);
                } else {
                    xprotocol.getCustomerTagMap().put(tagOf,
                            customerElement.substring(1));
                }
            }
        }
    }

    private static String[] splitTags(byte[] buffer, String codepage,
                                      String regex) throws UnsupportedEncodingException {
        String dp = new String(buffer, codepage);
        String[] split = dp.split(regex);
        return split;
    }

    private static byte[] compileTags(HashMap<XProtocolTag, String> tagMap,
                                      HashMap<XProtocolCustomerTag, String> customerTagMap)
            throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Iterator<?> it = tagMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<XProtocolTag, String> pairs = (Entry<XProtocolTag, String>) it
                    .next();
            try {
                bout.write(FS);
                bout.write(pairs.getKey().getTag().charValue());
                bout.write(pairs.getValue().toString().getBytes());
            } catch (IOException e) {
                Log.e(TAG, "BProtocolFactory compileTags", e);
            }
        }

        byte[] customerPart = compileCustomerTags(customerTagMap);
        if (customerPart != null && customerPart.length > 0) {
            bout.write(FS);
            bout.write(XProtocolTag.CustomerFid.getTag());
            bout.write(customerPart);
        }

        return bout.toByteArray();
    }

    private static byte[] compileCustomerTags(
            HashMap<XProtocolCustomerTag, String> tagMap) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Iterator<?> it = tagMap.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<XProtocolCustomerTag, String> pairs = (Entry<XProtocolCustomerTag, String>) it
                    .next();
            try {
                bout.write(GS);
                bout.write(pairs.getKey().getTag().charValue());
                bout.write(pairs.getValue().toString().getBytes());
            } catch (IOException e) {
                Log.e(TAG, "BProtocolFactory compileTags", e);
            }
        }

        return bout.toByteArray();
    }

    /**
     * Vyparsovani stringu s fixni delkou z bufferu.
     *
     * @param buffer buffer s daty
     * @param pos    pozice pocatku stringu
     * @param size   velikost vycitaneho stringu
     * @return z bufferu vycteny string
     */
    @SuppressWarnings("unused")
    private static String getFixedTrimedString(byte[] buffer, int pos,
                                               int size) {
        return new String(Arrays.copyOfRange(buffer, pos, pos + size)).trim();
    }

    private static String fixString(String inputStr, int size) {
        return fixString(inputStr, size, ' ');
    }

    /**
     * Zafixovani stringu na danou delku.
     *
     * @param inputStr vstupni retezec
     * @param size     velikost vycitaneho stringu
     * @param fill     Fill free space with char.
     * @return pokud je vetsi je navracena pouze retezec pozadovane velikosti
     * nebo je vstupni retezec zarovnan na pozadovanou delku pripojenim
     * mezer.
     */
    private static String fixString(String inputStr, int size, char fill) {
        String str = inputStr;

        if (str != null) {
            if (str.length() > size) {
                return str.substring(0, size - 1);
            }
        } else {
            str = "";
        }

        StringBuilder builder = new StringBuilder(str);

        while (builder.length() < size) {
            builder.append(fill);
        }

        return builder.toString();
    }

    /**
     * Zafixovani ciselneho retezce na danou delku.
     *
     * @param inputStr vstupni retezec
     * @param size     velikost vycitaneho stringu
     * @return pokud je vetsi je navracena pouze retezec pozadovane velikosti
     * nebo je vstupni retezec zarovnan na pozadovanou delku prefixem z
     * nul.
     */

    @SuppressWarnings("unused")
    private static String fixNumber(String inputStr, int size) {
        String str = inputStr;

        if (str != null) {
            if (str.length() > size) {
                return str.substring(0, size - 1);
            }
        } else {
            str = "";
        }

        StringBuilder builder = new StringBuilder(str);

        while (builder.length() < size) {
            builder.insert(0, '0');
        }

        return builder.toString();
    }
}
