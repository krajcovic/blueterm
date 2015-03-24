package cz.monetplus.blueterm;

public enum TransactionCommand {
    UNKNOWN,
    
    ONLY_CONNECT,

    MBCA_HANDSHAKE,

    MBCA_PAY,

    MBCA_INFO,

    MVTA_HANDSHAKE,
    
    MVTA_INFO,
    
    MVTA_RECHARGE,

    ;
}
