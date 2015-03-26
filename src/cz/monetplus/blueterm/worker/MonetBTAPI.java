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

    /**
     * 
     */
    // public static final String TOAST = "Messagebox";

    /**
     * Local Bluetooth adapter.
     */
    //private static BluetoothAdapter bluetoothAdapter = null;

    /**
     * Member object for the chat services.
     */
    private static TerminalServiceBT terminalService = null;

    /**
     * 
     */
    private static Context applicationContext = null;

    /**
     * Input transaction data.
     */
    private static TransactionIn inputData = null;

    /**
     * Output transaction data.
     */
    private static TransactionOut outputData = null;

    // The Handler that gets information back from the BluetoothChatService
    private static MessageThread messageThread = null;

    /**
     * @param context
     *            Application context.
     * @param in
     *            Transcation input parameters.
     * @return true for corect connected device. false for some error.
     */
//    public static final TransactionOut doTransactionEx(final Context context,
//            final TransactionIn in) {
//
//        applicationContext = context;
//        inputData = in;
//        outputData = new TransactionOut();
//
//        if (create()) {
//            if (start()) {
//                connectDevice(inputData.getBlueHwAddress(), false);
//
//                // Pockej dokud neskonci spojovani
//                while (terminalService.getState() == TerminalState.STATE_CONNECTING) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                }
//
//                if (terminalService.getState() == TerminalState.STATE_CONNECTED) {
//
//                    messageThread.addMessage(in.getCommand().getOperation());
//                            
////                            HandleMessage.MESSAGE_STATE_CHANGE,
////                            TerminalState.STATE_CONNECTED, in.getCommand()
////                                    .ordinal());
//                }
//                while (terminalService.getState() == TerminalState.STATE_CONNECTED) {
//                    // Zacni vykonavat smycku
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//
//                }
//
//                outputData = messageThread.getValue();
//            }
//        }
//
//        stop();
//
//        return outputData;
//    }
    
    /**
     * @param context
     *            Application context.
     * @param in
     *            Transcation input parameters.
     * @return true for corect connected device. false for some error.
     */
    public static final TransactionOut doTransaction(final Context context,
            final TransactionIn in) {

        applicationContext = context;
        inputData = in;
        outputData = new TransactionOut();
        
        messageThread = new MessageThread(applicationContext, inputData);

        // Initialize the BluetoothChatService to perform bluetooth connections
//        terminalService = new TerminalServiceBT(applicationContext,
//                messageThread, bluetoothAdapter);
        //messageThread.setTerminalService(terminalService);
        messageThread.start();
        
        messageThread.addMessage(HandleOperations.GetBluetoothAddress);
        
        try {
            messageThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            outputData = messageThread.getValue();
        }

//        if (create()) {
//            if (start()) {
//                connectDevice(inputData.getBlueHwAddress(), false);
//
//                // Pockej dokud neskonci spojovani
//                while (terminalService.getState() == TerminalState.STATE_CONNECTING) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                }
//
//                if (terminalService.getState() == TerminalState.STATE_CONNECTED) {
//
//                    messageThread.addMessage(in.getCommand().getOperation());
//                            
////                            HandleMessage.MESSAGE_STATE_CHANGE,
////                            TerminalState.STATE_CONNECTED, in.getCommand()
////                                    .ordinal());
//                }
//                while (terminalService.getState() == TerminalState.STATE_CONNECTED) {
//                    // Zacni vykonavat smycku
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//
//                }
//
//                outputData = messageThread.getValue();
//            }
//        }
//
//        stop();

        return outputData;
    }

    public static final void doCancel() {
        //stop();
        messageThread.setOutputMessage("Cancel from user interface.");
        messageThread.addMessage(HandleOperations.Exit);
    }

    /**
     * Create objects and variables.
     * 
     * @return true for corect creating.
     */
//    private static Boolean create() {
//        Log.e(TAG, "+++ ON CREATE +++");
//
//        // Get local Bluetooth adapter
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // relate the listView from java to the one created in xml
//        return true;
//    }

    /**
     * check bluetooth and start setting.
     * 
     * @return True for corect setup.
     */
//    private static Boolean start() {
//        Log.e(TAG, "++ ON START ++");
//
//        // If BT is not on, request that it be enabled.
//        // setupChat() will then be called during onActivityResult
////        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
////            outputData.setMessage("Bluetooth is not available");
////            // Otherwise, setup the chat session
////        } else {
//            if (terminalService == null) {
//                setupTerminal();
//                return true;
//            }
////        }
//
//        return false;
//    }

//    private static void stop() {
//        Log.e(TAG, "++ ON STOP ++");
//
//        if (terminalService != null) {
//            terminalService.stop();
//            terminalService = null;
//        }
//
//        if (messageThread != null) {
//            messageThread = null;
//        }
//    }

//    private static void setupTerminal() {
//        Log.d(TAG, "setupTerminal() creating handler");
//
//        messageThread = new MessageThread(applicationContext, inputData);
//
//        // Initialize the BluetoothChatService to perform bluetooth connections
//        terminalService = new TerminalServiceBT(applicationContext,
//                messageThread, bluetoothAdapter);
//        messageThread.setTerminalService(terminalService);
//        messageThread.start();
//    }

    /**
     * Get the BluetoothDevice object.
     * 
     * @param address
     *            HW address of bluetooth.
     * @param secure
     *            True for secure connection, false for insecure.
     */
//    private static void connectDevice(String address, boolean secure) {
//        // Get the BLuetoothDevice object
//        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
//
//        // Attempt to connect to the device
//        terminalService.connect(device, secure);
//    }

}
