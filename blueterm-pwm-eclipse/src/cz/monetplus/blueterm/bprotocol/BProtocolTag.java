package cz.monetplus.blueterm.bprotocol;

/**
 * @author "Dusan Krajcovic"
 * 
 */
public enum BProtocolTag {
    /**
     * Amount 1.
     */
    Amount1('B'),

    /**
     * Amount 2.
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
     * Sequence id.
     */
    SequenceId('i'),

    /**
     * Server message.
     */
    ServerMessage('g'),

    /**
     * Totals batch 1.
     */
    TotalsBatch1('l'),

    /**
     * Totals batch 2.
     */
    TotalsBatch2('m'),

    /**
     * Customer fid.
     */
    CustomerFid('9'),

    /**
     * Card type.
     */
    CardType('J');

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
    private BProtocolTag(Character tag) {
        this.tag = tag;
    }

    /**
     * @return Character tag.
     */
    public Character getTag() {
        return tag;
    }

    /**
     * @param tag
     *            Character tag.
     * @return Enum Bprotocol.
     */
    public static BProtocolTag tagOf(Character tag) {
        for (BProtocolTag e : BProtocolTag.class.getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + BProtocolTag.class.getName() + " enum tag:" + tag);
    }

}
