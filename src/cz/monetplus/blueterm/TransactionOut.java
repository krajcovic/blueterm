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
    private String authCode;

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

    /**
     * Hodnoty uzaverky, popis v B protokolu
     */
    private Balancing balancing;

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

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
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
        if (balancing != null) {
            builder.append(balancing.toString());
        }
        builder.append("]");
        return builder.toString();
    }

    public Balancing getBalancing() {
        return balancing;
    }

    public void setBalancing(Balancing balancing) {
        this.balancing = balancing;
    }

}
