package cz.monetplus.blueterm;

public enum TransactionCommand {
    /**
     * 
     */
    UNKNOWN,

    /**
     * 
     */
    HANDSHAKE,

    /**
     * Payment
     */
    PAY,

    /**
     * Storno last payment.
     */
    REVERSAL,

    /**
     * Total balancing.
     */
    CLOSE_TOTAL_BALANCING,

    /**
     * Application master info(version).
     */
    INFO,

    /**
     * Connect only to terminal and wait for operation from terminal. Stahovani
     * klicu.
     */
    ONLYCONNECT,
}
