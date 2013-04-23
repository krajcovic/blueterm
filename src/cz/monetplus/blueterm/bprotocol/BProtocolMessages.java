package cz.monetplus.blueterm.bprotocol;

public class BProtocolMessages {
	public static byte[] getAppInfo() {

		BProtocol bprotocol = new BProtocol("B1", "01", "        ",
				"120221150138", "    ", "A5A5");

		bprotocol.getTagMap().put(BProtocolTag.TransactionType, "80");

		BProtocolFactory factory = new BProtocolFactory();

		return factory.serialize(bprotocol);
	}

	public static byte[] getSale(int amount, String invoiceNumber) {

		BProtocol bprotocol = new BProtocol("B1", "01", "12345678",
				"120221150138", "0000", "A5A5");

		bprotocol.getTagMap().put(BProtocolTag.TransactionType, "00");
		// bprotocol.getTagMap().put(BProtocolTag.ApprovalCode, "12345678");
		bprotocol.getTagMap().put(BProtocolTag.Amount1, String.valueOf(amount));
		bprotocol.getTagMap().put(BProtocolTag.CurrencyCode2,
				String.valueOf(203));
		bprotocol.getTagMap().put(BProtocolTag.InvoiceNumber, invoiceNumber);

		BProtocolFactory factory = new BProtocolFactory();

		return factory.serialize(bprotocol);
	}

	public static byte[] getHanshake() {

		BProtocol bprotocol = new BProtocol("B1", "01", "12345678",
				"120221150138", "0000", "A5A5");

		bprotocol.getTagMap().put(BProtocolTag.TransactionType, "95");

		BProtocolFactory factory = new BProtocolFactory();

		return factory.serialize(bprotocol);
	}
}
