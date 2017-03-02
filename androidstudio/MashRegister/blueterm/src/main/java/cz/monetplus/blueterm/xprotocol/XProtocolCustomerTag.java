package cz.monetplus.blueterm.xprotocol;

public enum XProtocolCustomerTag {

    /**
     * Account/Virtual merchan id information.
     */
    AccountInfo('i'),
    
    /**
     * Restriction / cardType
     */
    Restriction('r'),

    /**
     * 
     */
    TerminalTicketInformation('t'),

    /**
     * Gastro data.
     */
    GastroData('G'),

    /**
     * 
     */
    TerminalTicketLine('T'),

    /**
     * Dlouhy variabilni symbol
     */
    InvoiceNumber('S'),

    /**
     * Castecna platba
     */
    SupportPartialPayment('P'),

    /**
     * Platba ticketem.
     */
    TicketNumber('T'),

    /**
     * Token generovany na zaklade pan karty.
     */
    CardToken('Z'),

    ;

    /**
     * Character tag.
     */
    private final Character tag;

    /**
     * Private constructor.
     * 
     * @param tag
     *            Character tag in B protocol.
     */
    private XProtocolCustomerTag(Character tag) {
        this.tag = tag;
    }

    public Character getTag() {
        return tag;
    }

    public static XProtocolCustomerTag tagOf(Character tag) {
        for (XProtocolCustomerTag e : XProtocolCustomerTag.class
                .getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + XProtocolCustomerTag.class.getName() + " enum tag:" + tag);
    }
}
