package cz.monetplus.blueterm.terminal;

/**
 * Constants that indicate the current connection state.
 * 
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
public enum TerminalState {

    /**
     * 
     */
    UNDEFINED(-1),

    /**
     * We're doing nothing.
     */
    STATE_NONE(0),

    /**
     * Now listening for incoming connections.
     */
    STATE_LISTEN(1),

    /**
     * Now initiating an outgoing connection.
     */
    STATE_CONNECTING(2),

    /**
     * Now connected to a remote device.
     */
    STATE_CONNECTED(3);

    private final Integer code;

    private TerminalState(Integer state) {
        this.code = state;
    }

    public Integer getStateCode() {
        return code;
    }

    public static TerminalState valueOf(byte b) {
        for (TerminalState c : values()) {
            if (c.ordinal() == b) {
                return c;
            }
        }
        return null;
    }
}
