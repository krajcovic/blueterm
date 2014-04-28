package cz.monetplus.blueterm;

/**
 * Identificators for Handle message thread.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public class HandleMessages {
    /**
     * 
     */
    public static final int MESSAGE_STATE_CHANGE = 1;
    
    /**
     * 
     */
    public static final int MESSAGE_TERM_READ = 2;
    
    /**
     * 
     */
    public static final int MESSAGE_TERM_WRITE = 3;
    
    /**
     * 
     */
    public static final int MESSAGE_DEVICE_NAME = 4;
    
    /**
     * 
     */
    public static final int MESSAGE_TOAST = 5;
    
    /**
     * 
     */
    public static final int MESSAGE_CONNECTED = 6;
    
    // public static final int MESSAGE_SERVER_READ = 12; Nepouziva se
    // public static final int MESSAGE_SERVER_WRITE = 13; Data uz jsou odeslana,
    // jen kdybych s nemi chtel udelat jeste neco.
    
    /**
     * 
     */
    public static final int MESSAGE_QUIT = 99;
}
