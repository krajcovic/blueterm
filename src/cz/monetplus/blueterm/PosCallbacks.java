package cz.monetplus.blueterm;

import android.content.Context;

public interface PosCallbacks {

    /**
     * Posle tiskovy radek na tiskarnu, radek po radku
     * 
     * @param line
     * @return
     */
    public Boolean ticketLine(String line);
    
    /**
     * Ukonceni tisku nejakeho listku. 
     * Slouzi pro oddelni listku.
     */
    public void ticketFinish();
    
    /**
     * Debug information from blueterm.
     * 
     * @param line
     */
    public void progress(String line);

    /**
     * Je vyzadovana kontrola zakaznickeho podpisu pokladni
     * 
     * @return
     */
    public Boolean isSingOk();

    /**
     * Je vyzadavan kontrola online dostupnosti. Jakekoliv
     * 
     * @return
     */
    public Boolean isConnectivity();
}
