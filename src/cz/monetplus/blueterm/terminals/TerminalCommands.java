package cz.monetplus.blueterm.terminals;

public class TerminalCommands {
    // Terminal commands.
    public static final byte EchoReq = 0x00;
    
    public static final byte EchoRes = (byte) 0x80;
    
    public static final byte ConnectReq = 0x01;
    
    public static final byte ConnectRes = (byte) 0x81;
    
    public static final byte DisconnectReq = 0x02;
    
    public static final byte ServerWrite = 0x03;
    
    public static final byte ServerRead = 0x04;
    
    public static final byte ServerConnected = 0x05;

    
    
    
    
    
    
}
