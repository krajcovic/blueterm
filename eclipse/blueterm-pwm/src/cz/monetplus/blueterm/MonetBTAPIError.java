package cz.monetplus.blueterm;

public enum MonetBTAPIError {
    /**
     * 
     */
    OK(0, "OK"),

    /**
     * 
     */
    BYPASS_FAIL(1, "ByPassReceiver failed"),

    /**
     * 
     */
    BYPASS_IO_EXCEPTION(2, "IOException by ByPassTCPServer."),

    /**
     * 
     */
    VMF_CONNECTION_FAILED(3, "VMF connection failed."),

    /**
     * 
     */
    VMF_DISCONNECTED(6, "VMF disconected"),

    /**
     * 
     */
    APPLINK_TIMEOUT(10, "Vx600 applink timeout."),

    /**
     * 
     */
    SERVER_SOCKET_NULL(11, "Server socket is null"),

    /**
     * 
     */
    BYPASS_EXCEPTION(12, "Exception by ByPassReceiverThread"),

    /**
     * 
     */
    WRITE_2_TERMINAL(13, "Some undefined error by writing to terminal."),

    /**
     * 
     */
    READ_FROM_TERMINAL(14, "Problem with read data from terminal"),

    /**
     * 
     */
    SERVER_COM_FAILED(15, "Sever communication failed."),

    /**
     * 
     */
    SOFT_DISCONNECT(100, "Manual soft disconnect.");

    /**
     * Error code.
     */
    private final Integer code;

    /**
     * Error message.
     */
    private final String message;

    private MonetBTAPIError(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
