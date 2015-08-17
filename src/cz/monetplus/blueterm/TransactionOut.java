package cz.monetplus.blueterm;

/**
 * @author krajcovic
 *
 */
public class TransactionOut {
    
    /**
     * 
     */
    private Integer resultCode;
    
    /**
     * 
     */
    private String message;
    
    /**
     * 
     */
    private Integer authCode;
    
    /**
     * 
     */
    private Integer seqId;
    
    /**
     * 
     */
    private String cardNumber;
    
    /**
     * 
     */
    private String cardType;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAuthCode() {
        return authCode;
    }

    public void setAuthCode(Integer authCode) {
        this.authCode = authCode;
    }

    public Integer getSeqId() {
        return seqId;
    }

    public void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TransactionOut [\n");
        if (resultCode != null) {
            builder.append("resultCode=").append(resultCode).append("\n ");
        }
        if (message != null) {
            builder.append("message=").append(message).append("\n ");
        }
        if (authCode != null) {
            builder.append("authCode=").append(authCode).append("\n ");
        }
        if (seqId != null) {
            builder.append("seqId=").append(seqId).append("\n ");
        }
        if (cardNumber != null) {
            builder.append("cardNumber=").append(cardNumber).append("\n ");
        }
        if (cardType != null) {
            builder.append("cardType=").append(cardType);
        }
        builder.append("]");
        return builder.toString();
    }  

}
