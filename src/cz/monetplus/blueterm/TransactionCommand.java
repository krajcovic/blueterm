package cz.monetplus.blueterm;

import cz.monetplus.blueterm.worker.HandleOperations;

public enum TransactionCommand {   
    ONLY_CONNECT(HandleOperations.CallConnect),

    MBCA_HANDSHAKE(HandleOperations.CallMbcaHandshake),
    
    MBCA_BALANCING(HandleOperations.CallMbcaBalancing),

    MBCA_PAY(HandleOperations.CallMbcaPay),

    MBCA_INFO(HandleOperations.CallMbcaInfo),

    MVTA_HANDSHAKE(HandleOperations.CallMvtaHandshake),
    
    MVTA_INFO(HandleOperations.CallMvtaInfo),
    
    MVTA_RECHARGE(HandleOperations.CallMvtaRecharging),
    
    SMART_SHOP_ACTIVATE(HandleOperations.CallSmartShopActivate),
    
    SMART_SHOP_DEACTIVATE(HandleOperations.CallSmartShopDeactivate),
    
    SMART_SHOP_GET_APP_INFO(HandleOperations.CallSmartShopGetAppInfo),
    
    SMART_SHOP_GET_LAST_TRAN(HandleOperations.CallSmartShopGetLastTran),
    
    SMART_SHOP_PARAMETRS_CALL(HandleOperations.CallSmartShopParametersCall),
    
    SMART_SHOP_HANDSHAKE(HandleOperations.CallSmartShopHandshake),
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
