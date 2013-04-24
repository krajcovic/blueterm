package cz.monetplus.blueterm.bprotocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cz.monetplus.blueterm.util.MonetUtils;

public class BProtocolFactory {
	private static final byte STX = 0x02;

	private static final byte ETX = 0x03;

	private static final byte FS = 0x1c;

	public byte[] serialize(BProtocol bprotocol) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		try {

			byte[] optionalData = compileTags(bprotocol.getTagMap());
			bprotocol.setOptionalDataLen(MonetUtils.bytesToHex(ByteBuffer
					.allocate(2)
					.putShort((short) (optionalData.length & 0xFFFF)).array()));

			bout.write(STX);
			bout.write(fixString(bprotocol.getProtocolType(), 2).getBytes());
			bout.write(fixString(bprotocol.getProtocolVersion(), 2).getBytes());
			bout.write(fixString(bprotocol.getPosId(), 8).getBytes());
			bout.write(fixString(bprotocol.getTransactionDateTime(), 12)
					.getBytes());
			bout.write(fixString(bprotocol.getFlag(), 4, '0').getBytes());
			bout.write(fixString(bprotocol.getOptionalDataLen(), 4, '0')
					.getBytes());
			bout.write(fixString(bprotocol.getStandardCRC16(), 4, '0')
					.getBytes());
			bout.write(optionalData);
			bout.write(ETX);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bout.toByteArray();
	}

	public BProtocol deserialize(byte[] buffer) {

		BProtocol bprotocol = new BProtocol();

		try {
			// ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
			// bin.read(buffer, offset, length)

			bprotocol.setProtocolType(new String(Arrays.copyOfRange(buffer, 1,
					3), "UTF8"));
			bprotocol.setProtocolVersion(new String(Arrays.copyOfRange(buffer,
					3, 5), "UTF8"));
			bprotocol.setPosId(new String(Arrays.copyOfRange(buffer, 5, 13),
					"UTF8"));
			bprotocol.setTransactionDateTime(new String(Arrays.copyOfRange(
					buffer, 13, 25), "UTF8"));

			bprotocol.setFlag(new String(Arrays.copyOfRange(buffer, 25, 29),
					"UTF8"));
			bprotocol.setOptionalDataLen(new String(Arrays.copyOfRange(buffer,
					29, 33), "UTF8"));
			bprotocol.setStandardCRC16(new String(Arrays.copyOfRange(buffer,
					33, 37), "UTF8"));

			String dp = new String(Arrays.copyOfRange(buffer, 37,
					buffer.length - 2), "ISO-8859-2");
			String regex = "[\\x1c]";
			String[] split = dp.split(regex);

			for (String string : split) {
				if (string.length() > 1) {
					bprotocol.getTagMap().put(
							BProtocolTag.tagOf(string.charAt(0)),
							string.substring(1));
				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bprotocol;
	}

	private byte[] compileTags(HashMap<BProtocolTag, String> tagMap) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		Iterator<?> it = tagMap.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("unchecked")
			Map.Entry<BProtocolTag, String> pairs = (Entry<BProtocolTag, String>) it
					.next();
			// BProtocolTag tag = (BProtocolTag) pairs.getKey();
			try {
				bout.write(FS);
				bout.write(pairs.getKey().getTag().charValue());
				bout.write(pairs.getValue().toString().getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return bout.toByteArray();
	}

	/**
	 * Vyparsovani stringu s fixni delkou z bufferu.
	 * 
	 * @param buffer
	 *            buffer s daty
	 * @param pos
	 *            pozice pocatku stringu
	 * @param size
	 *            velikost vycitaneho stringu
	 * @return z bufferu vycteny string
	 */
	@SuppressWarnings("unused")
	private static String getFixedTrimedString(byte[] buffer, int pos, int size) {
		return new String(Arrays.copyOfRange(buffer, pos, pos + size)).trim();
	}

	private static String fixString(String inputStr, int size) {
		return fixString(inputStr, size, ' ');
	}

	/**
	 * Zafixovani stringu na danou delku.
	 * 
	 * @param inputStr
	 *            vstupni retezec
	 * @param size
	 *            velikost vycitaneho stringu
	 * @return pokud je vetsi je navracena pouze retezec pozadovane velikosti
	 *         nebo je vstupni retezec zarovnan na pozadovanou delku pripojenim
	 *         mezer.
	 */
	private static String fixString(String inputStr, int size, char fill) {
		String str = inputStr;

		if (str != null) {
			if (str.length() > size) {
				return str.substring(0, size - 1);
			}
		} else {
			str = "";
		}

		StringBuilder builder = new StringBuilder(str);

		while (builder.length() < size) {
			builder.append(fill);
		}

		return builder.toString();
	}

	/**
	 * Zafixovani ciselneho retezce na danou delku.
	 * 
	 * @param inputStr
	 *            vstupni retezec
	 * @param size
	 *            velikost vycitaneho stringu
	 * @return pokud je vetsi je navracena pouze retezec pozadovane velikosti
	 *         nebo je vstupni retezec zarovnan na pozadovanou delku prefixem z
	 *         nul.
	 */

	@SuppressWarnings("unused")
	private static String fixNumber(String inputStr, int size) {
		String str = inputStr;

		if (str != null) {
			if (str.length() > size) {
				return str.substring(0, size - 1);
			}
		} else {
			str = "";
		}

		StringBuilder builder = new StringBuilder(str);

		while (builder.length() < size) {
			builder.insert(0, '0');
		}

		return builder.toString();
	}
}
