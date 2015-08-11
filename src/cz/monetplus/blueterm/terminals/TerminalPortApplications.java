package cz.monetplus.blueterm.terminals;

/**
 * Terminal ports, which identifieds a way of communication with terminal.
 * 
 * @author "Dusan Krajcovic"
 * 
 */
public enum TerminalPortApplications {
    /**
     * 
     */
    UNDEFINED(-1),

    /**
     * Communication for auth server.
     */
    SERVER(0),

    /**
     * 
     */
    MBCA(33333),

    /**
     * 
     */
    MVTA(33334),

    /**
     * 
     */
    FLEET(33335),

    /**
     * 
     */
    MAINTENANCE(33336),
    
    /**
     * 
     */
    SMARTSHOP(33338);

    /**
     * Numeric number of port number.
     */
    private int portApplicationNumber;

    TerminalPortApplications(int port) {
        this.setPortApplicationNumber(port);
    }

    public int getPortApplicationNumber() {
        return portApplicationNumber;
    }

    public void setPortApplicationNumber(int port) {
        this.portApplicationNumber = port;
    }

    public static TerminalPortApplications valueOf(int i) {
        for (TerminalPortApplications element : TerminalPortApplications.values()) {
            if (element.getPortApplicationNumber() == i) {
                return element;
            }
        }

        return UNDEFINED;
    }
}
