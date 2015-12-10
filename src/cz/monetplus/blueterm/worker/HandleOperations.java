package cz.monetplus.blueterm.worker;

public enum HandleOperations {
    
    /**
     * 
     */
    GetBluetoothAddress,
    
    /**
     * 
     */
    SetupTerminal,
    
    /**
     * 
     */
    TerminalConnect,
    
    /**
     * 
     */
    TerminalConnecting,
    
    /**
     * 
     */
    TerminalConnected,
    
    /**
     * 
     */
    TerminalReady,
    //TerminalDisconnected,
    
    /**
     * 
     */
    TerminalRead,
    
    /**
     * 
     */
    TerminalWrite,
    
    /**
     * 
     */
    ShowMessage,
    // Connected,
    
    
    /**
     * 
     */
    CallConnect,
    
    /**
     * 
     */
    CallMbcaHandshake,
    
    /**
     * 
     */
    CallMbcaBalancing,
    
    /**
     * 
     */
    CallMbcaInfo,
    
    /**
     * 
     */
    CallMbcaGetLastTran,     
    
    /**
     * 
     */
    CallMbcaPay,
    
    /**
     * 
     */
    CallMbcaReversal,
    
    /**
     * 
     */
    CallMvtaHandshake,
    
    /**
     * 
     */
    CallMvtaInfo,
    
    /**
     * 
     */
    CallMvtaGetLastTran,
    
    /**
     * 
     */
    CallMvtaRecharging,
    
    
    /**
     * 
     */
    CallSmartShopActivate,
    
    /**
     * 
     */
    CallSmartShopDeactivate,
    
    /**
     * 
     */
    CallSmartShopPay,
    
    /**
     * 
     */
    CallSmartShopReturn, 
    
    /**
     * 
     */
    CallSmartShopRecharging,
    
    /**
     * 
     */
    CallSmartShopCardState,
    
    /**
     * 
     */
    CallSmartShopGetAppInfo,
    
    /**
     * 
     */
    CallSmartShopGetLastTran,
    
    /**
     * 
     */
    CallSmartShopParametersCall,
    
    /**
     * 
     */
    CallSmartShopHandshake,
    
    /**
     * 
     */
    CallMaintenanceUpdate,
    
    /**
     * 
     */
    ServerConnected,
    
    /**
     * 
     */
    CheckSign,
      
    /**
     * 
     */
    Exit,  
    ;
}
