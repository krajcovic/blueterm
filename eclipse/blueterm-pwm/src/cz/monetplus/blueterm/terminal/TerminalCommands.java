package cz.monetplus.blueterm.terminal;

public enum TerminalCommands {
    // Requests:

    /**
     * 
     */
    TERM_CMD_ECHO(0x00),

    /**
     * 
     */
    TERM_CMD_CONNECT(0x01),

    /**
     * 
     */
    TERM_CMD_DISCONNECT(0x02),

    /**
     * 
     */
    TERM_CMD_SERVER_WRITE(0x03),

    /**
     * 
     */
    TERM_CMD_SERVER_READ(0x04),

    /**
     * 
    */
    TERM_CMD_SERVER_CONNECTED(0x05),

    // Responses:
    /**
     * 
     */
    TERM_CMD_ECHO_RES(0x80),

    /**
     * 
     */
    TERM_CMD_CONNECT_RES(0x81);

    private final Integer code;

    private TerminalCommands(Integer commandCode) {
        this.code = commandCode;
    }

    public Integer getCommandCode() {
        return code;
    }

    public static TerminalCommands valueOf(byte b) {
        for (TerminalCommands c : values()) {
            if (c.ordinal() == b) {
                return c;
            }
        }
        return null;
    }
}
