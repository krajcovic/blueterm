package cz.monetplus.blueterm.worker;

import cz.monetplus.blueterm.TransactionIn;
import cz.monetplus.blueterm.TransactionOut;
import cz.monetplus.blueterm.terminals.TerminalServiceBT;
import cz.monetplus.blueterm.terminals.TerminalState;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

/**
 * Exported class for control from pos-system.
 * 
 * @author krajcovic
 * 
 */
public class MonetBTAPI {

    /**
     * String tag for logging.
     */
    private static final String TAG = "MonetBTAPI";

    // The Handler that gets information back from the BluetoothChatService
    private static MessageThread messageThread = null;

     /**
     * @param context
     *            Application context.
     * @param in
     *            Transcation input parameters.
     * @return true for corect connected device. false for some error.
     */
    public static final TransactionOut doTransaction(final Context context,
            final TransactionIn in) {

        messageThread = new MessageThread(context, in);
        messageThread.start();

        try {
            messageThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        return messageThread.getValue();
    }

    /**
     * 
     */
    public static final void doCancel() {
        if (messageThread != null) {
            messageThread.setOutputMessage("Cancel from user interface.");
            messageThread.addMessage(HandleOperations.Exit);
        }
    }
}
