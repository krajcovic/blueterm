package cz.monetplus.blueterm.bprotocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class BProtocol implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3442882190681419097L;

	// private static final byte STX = 0x02;
	//
	// private static final byte ETX = 0x03;
	//
	// private static final byte FS = 0x1c;

	private String protocolType;

	private String protocolVersion;

	private String posId;

	private String transactionDateTime;

	private String flag;

	/**
	 * TODO: predelat na int.
	 */
	private String optionalDataLen;

	private String standardCRC16;

	private HashMap<BProtocolTag, String> tagMap = new HashMap<BProtocolTag, String>();

	BProtocol() {
		super();
	}

	BProtocol(String protocolType, String protocolVersion, String posId,
			String transactionDateTime, String flag, String standardCRC16) {
		super();
		this.protocolType = protocolType;
		this.protocolVersion = protocolVersion;
		this.posId = posId;
		this.transactionDateTime = transactionDateTime;
		this.flag = flag;
		this.standardCRC16 = standardCRC16;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getPosId() {
		return posId;
	}

	public void setPosId(String posId) {
		this.posId = posId;
	}

	public String getTransactionDateTime() {
		return transactionDateTime;
	}

	public void setTransactionDateTime(String transactionDateTime) {
		this.transactionDateTime = transactionDateTime;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getOptionalDataLen() {
		return optionalDataLen;
	}

	public void setOptionalDataLen(String optionalDataLen) {
		this.optionalDataLen = optionalDataLen;
	}

	public String getStandardCRC16() {
		return standardCRC16;
	}

	public void setStandardCRC16(String standardCRC16) {
		this.standardCRC16 = standardCRC16;
	}

	public HashMap<BProtocolTag, String> getTagMap() {
		return tagMap;
	}

	public void setTagMap(HashMap<BProtocolTag, String> tagMap) {
		this.tagMap = tagMap;
	}

	@Override
	public String toString() {
		return this.getProtocolType() + "\n" + this.getProtocolVersion() + "\n"
				+ this.getTransactionDateTime() + "\n" + tagMap.values();
	}

}
