package cz.monetplus.blueterm;

import com.verifone.vmf.api.VMF.ConnectionListener;

public class Vx600ConnectionListener implements ConnectionListener {
    MessageThread messageThread;

    public Vx600ConnectionListener(MessageThread messageThread) {

        this.messageThread = messageThread;
    }

    @Override
    public final void onConnectionEstablished() {
        if (messageThread != null) {
            messageThread.addMessageToast("VMF connected.");
        }
    }

    @Override
    public final void onConnectionFailed() {
        if (messageThread != null) {
            messageThread.addMessageToast(MonetBTAPIError.VMF_CONNECTION_FAILED
                    .getMessage());
            messageThread.addMessageQuit(MonetBTAPIError.VMF_CONNECTION_FAILED);
        }

    }

    @Override
    public final void onDisconnected(String arg0) {
        if (messageThread != null) {
            messageThread.addMessageToast("VMF disconected");
            messageThread.addMessageQuit(MonetBTAPIError.VMF_DISCONNECTED);
        }

    }
}
