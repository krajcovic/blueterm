package cz.monetplus.blueterm.bprotocol;

public enum BProtocolTag {
	Amount1('B'),

	Amount2('C'),

	AlternateId('D'),

	ApprovalCode('F'),

	CurrencyCode('I'),

	CurrencyCode2('E'),

	PAN('P'),

	ResponseCode('R'),

	InvoiceNumber('S'),

	TransactionType('T'),

	SequenceId('i'),

	ServerMessage('g'),

	TotalsBatch1('l'),

	TotalsBatch2('m'),

	CustomerFid('9');

	private final Character tag;

	private BProtocolTag(Character tag) {
		this.tag = tag;
	}

	public Character getTag() {
		return tag;
	}

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
