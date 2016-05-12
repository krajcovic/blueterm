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
     * Call MBCA parameters.
     */
    CallMbcaParameters,
    
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
    CallMbcaPrintTicket,
      
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
    CallMvtaPrintTicket,
    
    
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
    CallSmartShopTip,
    
    /**
     * 
     */
    CallSmartShopPrintTicket,
    
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
     * Finish all threads and communication with terminal.
     */
    Exit,  
    ;
}
