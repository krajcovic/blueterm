package cz.monetplus.blueterm.worker;

public enum HandleOperations {
    
    GetBluetoothAddress,
    SetupTerminal,
    
    TerminalConnect,
    TerminalConnecting,
    TerminalConnected,
    TerminalReady,
    //TerminalDisconnected,
    
    TerminalRead,
    TerminalWrite,
    
    ShowMessage,
    // Connected,
    
    
    CallConnect, 
    CallMbcaHandshake,
    CallMbcaBalancing,
    CallMbcaInfo,
    CallMbcaPay,
    CallMvtaHandshake, 
    CallMvtaInfo,
    CallMvtaRecharging,
    
    ServerConnected,
    
    // public static final int MESSAGE_SERVER_READ = 12; Nepouziva se
    // public static final int MESSAGE_SERVER_WRITE = 13; Data uz jsou odeslana,
    // jen kdybych s nemi chtel udelat jeste neco.
    
    Exit,      
    ;
}
