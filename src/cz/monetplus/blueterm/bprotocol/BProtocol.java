package cz.monetplus.blueterm.bprotocol;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author "Dusan Krajcovic"
 * 
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
     * 
     */
    BProtocol() {
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
    public BProtocol(String protocolType, String protocolVersion, String posId,
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
        return this.getProtocolType() + " " + this.getProtocolVersion() + " "
                + this.getTransactionDateTime() + "\n" + tagMap.values();
    }

}
