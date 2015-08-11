package cz.monetplus.blueterm.xprotocol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author "Dusan Krajcovic"
 * 
 */
public class XProtocol implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3442882190681419097L;

    /**
     * Protocol type.
     */
    private ProtocolType protocolType;

    /**
     * 
     */
    private MessageNumber messageNumber;

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
    private Integer flag;

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
    private HashMap<XProtocolTag, String> tagMap = new HashMap<XProtocolTag, String>();

    /**
     * Tag map.
     */
    private HashMap<XProtocolCustomerTag, String> customerTagMap = new HashMap<XProtocolCustomerTag, String>();
    
    /**
     * Rows for printing.
     */
    private List<String> ticketList = new ArrayList<String>();

    /**
     * 
     */
    XProtocol() {
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
    public XProtocol(ProtocolType protocolType, MessageNumber messageNumber,
            String protocolVersion, String posId, String transactionDateTime,
            Integer flag, String standardCRC16) {
        super();
        this.protocolType = protocolType;
        this.setMessageNumber(messageNumber);
        this.protocolVersion = protocolVersion;
        this.posId = posId;
        this.transactionDateTime = transactionDateTime;
        this.flag = flag;
        this.standardCRC16 = standardCRC16;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(ProtocolType protocolType) {
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

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
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

    public HashMap<XProtocolTag, String> getTagMap() {
        return tagMap;
    }

//    public void setTagMap(HashMap<XProtocolTag, String> tagMap) {
//        this.tagMap = tagMap;
//    }

    @Override
    public String toString() {
        return this.getProtocolType() + " " + this.getProtocolVersion() + " "
                + this.getTransactionDateTime() + "\n" + tagMap.values()
                + "\n9:" + customerTagMap.values();
    }

    public MessageNumber getMessageNumber() {
        return messageNumber;
    }

    public void setMessageNumber(MessageNumber messageNumber) {
        this.messageNumber = messageNumber;
    }

    public HashMap<XProtocolCustomerTag, String> getCustomerTagMap() {
        return customerTagMap;
    }

    public List<String> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<String> ticketList) {
        this.ticketList = ticketList;
    }

//    public void setCustomerTagMap(
//            HashMap<XProtocolCustomerTag, String> customerTagMap) {
//        this.customerTagMap = customerTagMap;
//    }

}
