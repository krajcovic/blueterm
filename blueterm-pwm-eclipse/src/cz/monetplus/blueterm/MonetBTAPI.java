package cz.monetplus.blueterm;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.verifone.vmf.api.VMF;

import cz.monetplus.blueterm.terminal.TerminalServiceBT;
import cz.monetplus.blueterm.terminal.TerminalState;

/**
 * Exported class for control from pos-system.
 * 
 * @author krajcovic
 * 
 */
public class MonetBTAPI {

    /**
     * Socket port.
     */
    private static final int TERMINALPORT = 33333;

    /**
     * String tag for logging.
     */
    private static final String TAG = "MonetBTAPI";

    /**
     * Member object for the chat services.
     */
    private static TerminalServiceBT terminalService = null;

    /**
     * 
     */
    private static Activity activity = null;

    /**
     * Input transaction data.
     */
    private static TransactionIn inputData = null;

    /**
     * Output transaction data.
     */
    private static TransactionOut outputData = null;

    /**
     * The Handler that gets information back from the BluetoothChatService.
     */
    private static MessageThread messageThread = null;

    /**
     * @param act
     *            Current activity.
     * @return true if a terminal is connected.
     */
    public static final synchronized Boolean isTerminalConnected(
            final Activity act) {
        activity = act;
        Boolean isConnected = false;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Device does not support Bluetooth.");
            return false;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Log.e(TAG, "Device has not enabled Bluetooth.");
            return false;
        }

        // Tak tohle je hrozna hovadina
        // Kdyz navazu spojeni a hned ho ukoncim, dochazelo k problemum s
        // timeoutem uvnitr knihovny vmf
        // Ta pitoma knihovna si chce hned po navazani spojeni vymenit nejake
        // data, a kdyz mu to hned
        // diskonektnu, tak se z toho nezpamatuje, tak to delam na suda/licha v
        // jednom zapnu v druhem dotazu vypnu
        if (VMF.isVx600Connected()) {
            VMF.vmfDisconnectVx600();
            isConnected = true;
        } else {
            if (VMF.vmfConnectVx600(act, null, 1) == 0
                    && VMF.isVx600Connected()) {
                isConnected = true;
            }
        }

        Log.i(TAG, "Terminal VMF isConnected: " + isConnected);
        return isConnected;
    }

    /**
     * @param currentActivity
     *            Current activity.
     * @param in
     *            Transcation input parameters.
     * @return true for corect connected device. false for some error.
     */
    public static final synchronized TransactionOut doTransaction(
            final Activity currentActivity, final TransactionIn in) {

        activity = currentActivity;
        inputData = in;
        outputData = new TransactionOutVx600();

        if (VMF.isVx600Connected()) {
            VMF.vmfDisconnectVx600();
        }

        if (create()) {
            if (start()) {
                connectDevice();

                // TODO: Tohle predelat na threadMessage zpusob, nekdy v
                // budoucnu :)
                // Pockej dokud neskonci spojovani
                while (terminalService.getState() == TerminalState.STATE_CONNECTING) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                if (terminalService.getState() == TerminalState.STATE_CONNECTED) {

                    messageThread.addMessageTerminalConnected(
                            HandleMessages.MESSAGE_STATE_CHANGE,
                            TerminalState.STATE_CONNECTED, in.getCommand());
                }

                while (terminalService.getState() == TerminalState.STATE_CONNECTED) {
                    // Zacni vykonavat smycku
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "MonetBTAPI wait loop exception.");
                    }

                }

                if (messageThread != null) {
                    outputData = messageThread.getValue();
                }
            } else {
                Log.e(TAG, "crate failed");
            }
        } else {
            Log.e(TAG, "start failed");
        }

        stop();

        return outputData;
    }

    public static final void doCancel() {
        stop();
    }

    /**
     * Create objects and variables.
     * 
     * @return true for corect creating.
     */
    private static Boolean create() {
        Log.i(TAG, "+++ ON CREATE +++");

        return true;
    }

    /**
     * check bluetooth and start setting.
     * 
     * @return True for corect setup.
     */
    private static Boolean start() {
        Log.i(TAG, "++ ON START ++");

        // Bluetooth zapina aplikace.
        if (terminalService == null) {
            setupTerminal();
            return true;
        }

        return false;
    }

    private static void stop() {
        Log.i(TAG, "++ ON STOP ++");

        if (terminalService != null) {
            terminalService.stop();
            terminalService = null;
        }

        if (messageThread != null) {
            messageThread = null;
        }
    }

    private static void setupTerminal() {
        Log.i(TAG, "setupTerminal() creating handler");

        messageThread = new MessageThread(activity, TERMINALPORT, inputData);

        if (messageThread != null) {
            // Initialize the BluetoothChatService to perform bluetooth
            // connections
            terminalService = new TerminalServiceBT(activity, messageThread);
            Log.i(TAG, "TerminalServiceBT created");
            messageThread.setTerminalService(terminalService);
            Log.i(TAG, "TerminalServiceBT setted to messageThread");
            messageThread.start();
            Log.i(TAG, "messageThread starting");

            // TODO predelat zivotni cyklus, aby se tady nemuselo cekat.
            while (!messageThread.isAlive()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "Waiting for messageThread isAlive");
        } else {
            Log.e(TAG, "MessageThread not created!!!! TODO: WHY???");
        }
    }

    /**
     * Get the BluetoothDevice object.
     * 
     * @param address
     *            HW address of bluetooth.
     * @param secure
     *            True for secure connection, false for insecure.
     */
    private static void connectDevice(/* String address, boolean secure */) {
        // Get the BLuetoothDevice object
        // BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        // Attempt to connect to the device
        terminalService.connect(/*
                                 * bluetoothAdapter.getRemoteDevice(address),
                                 * secure
                                 */);
    }

}
