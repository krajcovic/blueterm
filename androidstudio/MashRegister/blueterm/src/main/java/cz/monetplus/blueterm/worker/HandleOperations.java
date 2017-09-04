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
    CallMbcaAccountInfo,
    
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
    CallMbcaRefund,
    
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
    CallMvtaParameters,
    
    
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
