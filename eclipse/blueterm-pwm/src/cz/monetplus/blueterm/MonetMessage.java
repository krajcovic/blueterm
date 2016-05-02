package cz.monetplus.blueterm;

import org.apache.http.util.ByteArrayBuffer;

import cz.monetplus.blueterm.terminal.TerminalState;

public class MonetMessage {

    private final HandleMessages message;

    private final ByteArrayBuffer data;

    private final String toastMessage;

    private final MonetBTAPIError errorInfo;

    private final TerminalState terminalState;

    private final TransactionCommand transactionCommand;

    private final byte serverStatus;

    /**
     * @param message
     */
    public MonetMessage(HandleMessages message) {

        this.message = message;
        this.data = null;
        this.toastMessage = null;
        this.errorInfo = null;
        this.terminalState = null;
        this.transactionCommand = null;
        this.serverStatus = 0;
    }

    /**
     * @param message
     *            Synchronization message
     * @param buffer
     * @param length
     */
    public MonetMessage(HandleMessages message, byte[] buffer, int length) {
        this.message = message;
        data = new ByteArrayBuffer(length);
        getData().append(buffer, 0, length);
        toastMessage = null;
        this.errorInfo = null;
        this.terminalState = null;
        this.transactionCommand = null;
        this.serverStatus = 0;
    }

    /**
     * @param message
     * @param string
     */
    public MonetMessage(HandleMessages message, String string) {

        this.message = message;
        this.toastMessage = string;
        data = null;
        this.errorInfo = null;
        this.terminalState = null;
        this.transactionCommand = null;
        this.serverStatus = 0;
    }

    /**
     * @param message
     * @param error
     */
    public MonetMessage(HandleMessages message, MonetBTAPIError error) {
        this.message = message;
        this.errorInfo = error;
        data = null;
        toastMessage = null;
        this.terminalState = null;
        this.transactionCommand = null;
        this.serverStatus = 0;
    }

    /**
     * @param message
     * @param terminalState
     * @param command
     */
    public MonetMessage(HandleMessages message, TerminalState terminalState,
            TransactionCommand command) {
        this.message = message;
        this.errorInfo = null;
        data = null;
        toastMessage = null;
        this.terminalState = terminalState;
        this.transactionCommand = command;
        this.serverStatus = 0;
    }

    /**
     * @param message
     * @param serverStatus
     */
    public MonetMessage(HandleMessages message, byte serverStatus) {
        this.message = message;
        this.errorInfo = null;
        data = null;
        toastMessage = null;
        this.terminalState = null;
        this.transactionCommand = null;
        this.serverStatus = serverStatus;
    }

    /**
     * @return
     */
    public final HandleMessages getMessage() {
        return message;
    }

    /**
     * @return
     */
    public final String getToastMessage() {
        return toastMessage;
    }

    /**
     * @return
     */
    public final MonetBTAPIError getErrorInfo() {
        return errorInfo;
    }

    /**
     * @return
     */
    public final TerminalState getTerminalState() {
        return terminalState;
    }

    /**
     * @return
     */
    public final TransactionCommand getTransactionCommand() {
        return transactionCommand == null ? TransactionCommand.UNKNOWN
                : transactionCommand;
    }

    /**
     * @return
     */
    public final ByteArrayBuffer getData() {
        return data;
    }

    /**
     * @return
     */
    public final byte getServerStatus() {
        return serverStatus;
    }
}
