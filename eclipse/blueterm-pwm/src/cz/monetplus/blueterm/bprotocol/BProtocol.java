package cz.monetplus.blueterm.bprotocol;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author "Dusan Krajcovic"
 */
public class BProtocol implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3442882190681419097L;

    /**
     * Protocol type.
     */
    private String protocolType;

    /**
     * Protocol version.
     */
    private String protocolVersion;

    /**
     * Pos id.
     */
    private String posId;

    /**
     * Transaction date time.
     */
    private String transactionDateTime;

    /**
     * Flags.
     */
    private String flag;

    /**
     * Optional data len in hexa.
     */
    private String optionalDataLen;

    /**
     * Control sum.
     */
    private String standardCRC16;

    /**
     * Tag map.
     */
    private HashMap<BProtocolTag, String> tagMap = new HashMap<BProtocolTag, String>();

    /**
     * Default constructor.
     */
    public BProtocol() {
        super();
    }

    /**
     * @param protocolType
     *            Protocol type.
     * @param protocolVersion
     *            Protocol version
     * @param posId
     *            Pos - cash Id.
     * @param transactionDateTime
     *            Transaction date time.
     * @param flag
     *            Flags.
     * @param standardCRC16
     *            Control sum.
     */
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

    /**
     * @return Protocol type.
     */
    public final String getProtocolType() {
        return protocolType;
    }

    /**
     * @param protocolType
     *            The protocol type.
     */
    public final void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * @return Protocol version.
     */
    public final String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * @param protocolVersion
     *            Protocol version.
     */
    public final void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * @return POS ID.
     */
    public final String getPosId() {
        return posId;
    }

    /**
     * @param posId
     *            The POS id.
     */
    public final void setPosId(String posId) {
        this.posId = posId;
    }

    /**
     * @return Transaction date time.
     */
    public final String getTransactionDateTime() {
        return transactionDateTime;
    }

    /**
     * @param transactionDateTime
     *            The String with date time of transaction.
     */
    public final void setTransactionDateTime(String transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
    }

    /**
     * @return Flag indicator.
     */
    public final String getFlag() {
        return flag;
    }

    /**
     * @param flag
     *            The flag indicator.
     */
    public final void setFlag(String flag) {
        this.flag = flag;
    }

    /**
     * @return Length of optional data.
     */
    public final String getOptionalDataLen() {
        return optionalDataLen;
    }

    /**
     * @param optionalDataLen
     *            String with length of optional data.
     */
    public final void setOptionalDataLen(String optionalDataLen) {
        this.optionalDataLen = optionalDataLen;
    }

    /**
     * @return String wint CRC.
     */
    public final String getStandardCRC16() {
        return standardCRC16;
    }

    /**
     * @param standardCRC16
     *            Standard CRC.
     */
    public final void setStandardCRC16(String standardCRC16) {
        this.standardCRC16 = standardCRC16;
    }

    /**
     * @return Map with tags.
     */
    public final HashMap<BProtocolTag, String> getTagMap() {
        return tagMap;
    }

    /**
     * @param tagMap
     *            The hasm map with b protocol tags.
     */
    public final void setTagMap(HashMap<BProtocolTag, String> tagMap) {
        this.tagMap = tagMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return this.getProtocolType() + " " + this.getProtocolVersion() + " "
                + this.getTransactionDateTime() + "\n" + tagMap.values();
    }

}
