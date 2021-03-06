package cz.monetplus.blueterm.terminals;

public class TerminalState {
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing

    public static final int STATE_LISTEN = 1; // now listening for incoming
                                              // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
                                                  // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
                                                 // device
}
