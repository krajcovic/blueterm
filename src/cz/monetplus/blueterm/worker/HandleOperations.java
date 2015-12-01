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
