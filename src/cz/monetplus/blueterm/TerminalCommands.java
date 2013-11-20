package cz.monetplus.blueterm;

public class TerminalCommands {
    // Terminal commands.
    public static final byte TERM_CMD_ECHO = 0x00;
    
    public static final byte TERM_CMD_ECHO_RES = (byte) 0x80;
    
    public static final byte TERM_CMD_CONNECT = 0x01;
    
    public static final byte TERM_CMD_CONNECT_RES = (byte) 0x81;
    
    public static final byte TERM_CMD_DISCONNECT = 0x02;
    
    public static final byte TERM_CMD_SERVER_WRITE = 0x03;
    
    public static final byte TERM_CMD_SERVER_READ = 0x04;
    
    public static final byte TERM_CMD_SERVER_CONNECTED = 0x05;

    
    
    
    
    
    
}
