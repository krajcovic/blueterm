package cz.monetplus.blueterm;

import com.verifone.vmf.api.VMF.ConnectionListener;

/**
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
public class BTPrinterListener implements ConnectionListener {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.verifone.vmf.api.VMF.ConnectionListener#onConnectionEstablished()
     */
    @Override
    public void onConnectionEstablished() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.verifone.vmf.api.VMF.ConnectionListener#onConnectionFailed()
     */
    @Override
    public void onConnectionFailed() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.verifone.vmf.api.VMF.ConnectionListener#onDisconnected(java.lang.
     * String)
     */
    @Override
    public void onDisconnected(String arg0) {
        // TODO Auto-generated method stub

    }

}
