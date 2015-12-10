package cz.monetplus.blueterm.xprotocol;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProtocolMessages {
    protected static String getCurrentDateTimeForHeader() {
        SimpleDateFormat formater = new SimpleDateFormat("yyMMddHHmmss",
                Locale.US);
        return formater.format(new Date());

    }
}
