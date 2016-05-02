package cz.monetplus.blueterm;

/**
 * Identificators for Handle message thread.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public enum HandleMessages {
    /**
     * 
     */
    MESSAGE_STATE_CHANGE(1),

    /**
     * 
     */
    MESSAGE_TERM_READ(2),

    /**
     * 
     */
    MESSAGE_TERM_WRITE(3),

    /**
     * 
     */
    MESSAGE_DEVICE_NAME(4),

    /**
     * 
     */
    MESSAGE_TOAST(5),

    /**
     * 
     */
    MESSAGE_CONNECTED(6),

    /**
     * 
     */
    MESSAGE_TERM_SEND_COMMAND(7),

    // public static final int MESSAGE_SERVER_READ = 12; Nepouziva se
    // public static final int MESSAGE_SERVER_WRITE = 13; Data uz jsou odeslana,
    // jen kdybych s nemi chtel udelat jeste neco.

    /**
     * 
     */
    MESSAGE_QUIT(99);

    private final Integer code;

    private HandleMessages(Integer messageCode) {
        this.code = messageCode;
    }

    public Integer getCommandCode() {
        return code;
    }

    public static HandleMessages valueOf(int what) {
        for (HandleMessages c : values()) {
            if (c.ordinal() == what) {
                return c;
            }
        }
        return null;
    }
}
