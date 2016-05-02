package cz.monetplus.blueterm.terminal;

import java.io.IOException;

import android.app.Activity;
import android.util.Log;

import com.verifone.vmf.api.VMF;
import com.verifone.vmf.api.VMF.AppLinkListener;

import cz.monetplus.blueterm.MessageThread;
import cz.monetplus.blueterm.MonetBTAPIError;
import cz.monetplus.blueterm.Vx600ConnectionListener;
import cz.monetplus.blueterm.util.MonetUtils;

/**
 * This thread runs during a connection with a remote device. It handles all
 * incoming and outgoing transmissions.
 */
public class ConnectedThread extends TerminalsThread {
    private static final int APP_ID = 1;

    private static final int LISTEN_PORT = 33333;

    private static final String TAG = "ConnectedThread";

    private final Activity activity;

    private ByPassTCPServerThread bypassServerThread;

    public ConnectedThread(MessageThread messageThread, Activity activity)
            throws Exception {
        super(messageThread);

        Log.d(TAG, "create ConnectedThread: ");
        this.activity = activity;

        String help = VMF.vmfGetVersionLib();
        Log.i(TAG, "libVmf Version: " + help);

        help = VMF.vmfPrtGetVersionLib();
        Log.i(TAG, "libPrt Version: " + help);

        VMF.setAppLinkListener(new AppLinkReceiver());
        // VMF.setPrinterDataListener(new DataReceiver());
    }

    private class AppLinkReceiver implements AppLinkListener {

        @Override
        public void onResponse(final byte[] recvBuf, final boolean timeOut) {
            if (recvBuf != null) {
                Log.i(TAG, "Received hex: " + MonetUtils.bytesToHex(recvBuf)
                        + "Timeout: " + timeOut);
            }

            if (timeOut) {
                messageThread.addMessageQuit(MonetBTAPIError.APPLINK_TIMEOUT);
            }
        }
    }

    @Override
    public final void run() {
        Log.i(TAG, "BEGIN mConnectedThread");

        try {
            bypassServerThread = new ByPassTCPServerThread(messageThread,
                    LISTEN_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            if (messageThread != null) {
                messageThread.addMessageQuit(MonetBTAPIError.BYPASS_EXCEPTION);
            } else {
                interrupt();
            }
            return;
        }

        Log.e(TAG, "calling vmfConnectVx600");
        if (VMF.vmfConnectVx600(activity, new Vx600ConnectionListener(
                messageThread), APP_ID) == 0) {
            Log.i(TAG, "vmfConnectVx600 VMF_OK");

            bypassServerThread.start();

            // Keep listening to the InputStream while connected
            while (!Thread.currentThread().isInterrupted()
                    && VMF.isVx600Connected() && bypassServerThread != null
                    && !bypassServerThread.isInterrupted()) {
                // todo: treba wait nebo tak neco.
                // todo: nebo kontrola, ze je vse v poradku
                // todo: nebo posilani ze jsem ready

                try {
                    sleep(MonetUtils.THREAD_RUN_SLEEP);
                } catch (InterruptedException e) {

                }
            }

            // Ukonci to.
            if (messageThread != null) {
                messageThread.addMessageQuit(MonetBTAPIError.OK);
            } else {
                interrupt();
            }
        } else {
            if (messageThread != null) {
                messageThread
                        .addMessageQuit(MonetBTAPIError.VMF_CONNECTION_FAILED);
            } else {
                interrupt();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see cz.monetplus.blueterm.terminal.ITerminalsThread#write(byte[])
     */
    @Override
    public final void write(byte[] buffer) throws IOException {
        bypassServerThread.write(buffer);
        // VMF.vmfAppLinkSend(DESTINATION_ID, buffer, 5000 * 1000);

    }

    @Override
    public final void interrupt() {
        Log.i(TAG, "ConnectedThread interrupt");
        VMF.setAppLinkListener(null);

        if (bypassServerThread != null) {
            // do {
            // try {
            bypassServerThread.interrupt();
            // bypassServerThread.join(1000);
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
            // } while (bypassServerThread.isAlive());
            bypassServerThread = null;
        }

        super.interrupt();

        // Zkus pockat az si VMF dokecas.
        try {
            sleep(2000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            // e1.printStackTrace();
        }

        // VMF.vmfDisconnectVx600();
        if (VMF.isVx600Connected()) {
            try {
                Log.e(TAG, "calling vmfDisconnectVx600");
                VMF.vmfDisconnectVx600();
                sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }

        }

    }
}
