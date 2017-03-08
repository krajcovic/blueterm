package cz.monetplus.blueterm;

import cz.monetplus.blueterm.xprotocol.TicketCommand;

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
     * @param lastTicket
     */
    public void ticketFinish(Character lastTicket);
    
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
    public Boolean isSignOk();

    /**
     * Je vyzadavan kontrola online dostupnosti. Jakekoliv
     * 
     * @return
     */
    public Boolean isConnectivity();
}
