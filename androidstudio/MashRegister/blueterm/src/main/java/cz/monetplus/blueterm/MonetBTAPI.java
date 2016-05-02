package cz.monetplus.blueterm;

import cz.monetplus.blueterm.worker.HandleOperations;
import cz.monetplus.blueterm.worker.MessageThread;
import android.content.Context;
import android.util.Log;

import java.util.Locale;

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
     * @throws Exception
     */
    public static final TransactionOut doTransaction(final Context context,
            final TransactionIn in) throws Exception {

        // messageThread = new MessageThread(context, in);
        messageThread = MessageThread.getInstance(context, in);
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

    public static String getPin(String terminalName) {
        return String.format(Locale.getDefault(), "%06d", getDynamicPin(terminalName));
    }

    private static long getDynamicPin(String terminalName) {
        long hash = 3735927486l;// 0xDEADBABE & 0xFFFFFFFF;
        byte[] array = terminalName.getBytes();
        for (byte b : array) {
            hash += b;
            // long bad = ((hash * 2^10) & 0xFFFFFFFFl);
            // long ok = ((hash << 10) & 0xFFFFFFFFl);
            hash = ((hash & 0xFFFFFFFFl) + ((hash << 10) & 0xFFFFFFFFl)) & 0xFFFFFFFFl;
            hash ^= ((hash >> 6) & 0xFFFFFFFFl);
        }

        return (hash % 1000000) & 0xFFFFFF;
    }

}
