package cz.monetplus.blueterm.xprotocol;

/**
 * @author "Dusan Krajcovic"
 *
 */
/**
 * @author "Dusan Krajcovic"
 *
 */
/**
 * @author "Dusan Krajcovic"
 * 
 */
public enum XProtocolTag {
    /**
     * 
     */
    Amount1('B'),

    /**
     * 
     */
    Amount2('C'),

    /**
     * 
     */
    AlternateId('D'),

    /**
     * 
     */
    AuthCode('F'),

    /**
     * 
     */
    CurrencyCode('I'),

    /**
     * 
     */
    CurrencyCode2('E'),
    
    /**
     * Identifikator pokladniho uctu
     */
    SaleTicketId('N'),

    /**
     * 
     */
    PAN('P'),

    /**
     * 
     */
    /**
     * 
     */
    ResponseCode('R'),

    /**
     * 
     */
    InvoiceNumber('S'),

    /**
     * 
     */
    TransactionType('T'),

    /**
     * 
     */
    SequenceId('i'),

    /**
     * 
     */
    ServerMessage('g'),

    /**
     * 
     */
    TotalsBatch1('l'),

    /**
     * 
     */
    TotalsBatch2('m'),

    /**
     * 
     */
    CustomerFid('9'),

    /**
     * 
     */
    CardType('J'),
    
    /**
     * pole nese informaci o způsobu dobití karty:
         0 dobití provedeno hotovostí 
         1 dobití provedeno platební kartou
     */
    RechargingType('4');

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
    private XProtocolTag(Character tag) {
        this.tag = tag;
    }

    public Character getTag() {
        return tag;
    }

    public static XProtocolTag tagOf(Character tag) {
        for (XProtocolTag e : XProtocolTag.class.getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + XProtocolTag.class.getName() + " enum tag:" + tag);
    }

}
