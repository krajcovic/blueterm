package cz.monetplus.blueterm;

import cz.monetplus.blueterm.worker.HandleOperations;

public enum TransactionCommand {   
    ONLY_CONNECT(HandleOperations.CallConnect),

    MBCA_HANDSHAKE(HandleOperations.CallMbcaHandshake),

    MBCA_PAY(HandleOperations.CallMbcaPay),

    MBCA_INFO(HandleOperations.CallMbcaInfo),

    MVTA_HANDSHAKE(HandleOperations.CallMvtaHandshake),
    
    MVTA_INFO(HandleOperations.CallMvtaInfo),
    
    MVTA_RECHARGE(HandleOperations.CallMvtaRecharging),
    ;
    
    
    private HandleOperations operation;

    private TransactionCommand(HandleOperations operation) {
        this.setOperation(operation);
    }

    public HandleOperations getOperation() {
        return operation;
    }

    public void setOperation(HandleOperations operation) {
        this.operation = operation;
    }
    
    
}
