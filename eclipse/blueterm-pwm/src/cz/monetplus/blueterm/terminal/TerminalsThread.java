package cz.monetplus.blueterm.terminal;

import cz.monetplus.blueterm.HandleMessages;
import cz.monetplus.blueterm.MessageThread;
import cz.monetplus.blueterm.MonetBTAPIError;

/**
 * 
 * All threads for terminal communication extends TerminalsThread.
 * 
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
public abstract class TerminalsThread extends Thread implements
        ITerminalsThread {

    /**
     * Tag for debugging.
     */
    private static final String TAG = "TerminalsThread";

    /**
     * Synchronizing message thread.
     */
    protected final MessageThread messageThread;

    protected TerminalsThread(MessageThread messageThread) throws Exception {
        super();
        if (messageThread == null) {
            throw new Exception(
                    "MessageThread is NULL!!!! FATAL FATAL FATAL error.");
        }

        this.messageThread = messageThread;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * cz.monetplus.blueterm.terminal.ITerminalsThread#connectionLost(java.lang
     * .Integer, java.lang.String)
     */
    @Override
    public final void connectionLost(MonetBTAPIError error) {
        if (messageThread != null) {
            // Send a failure message back to the Activity
            messageThread.addMessageToast(error.getMessage());
            messageThread.addMessageQuit(error);
        }
    }

    /**
     * Set the current state of the chat connection.
     * 
     * @param newState
     *            An integer defining the current connection state.
     */
    @Override
    public final synchronized void setState(TerminalState newState) {

        // Give the new state to the Handler so the UI Activity can update
        if (messageThread != null) {
            messageThread.addMessage(HandleMessages.MESSAGE_STATE_CHANGE,
                    newState);
        }
    }
}
