package cz.monetplus.blueterm;

/**
 * @author krajcovic
 */
public class TransactionOut {

    /**
     * Result code.
     */
    private Integer resultCode;

    /**
     * Odpoved.
     */
    private String message;

    /**
     * Autorizacni kod.
     */
    private String authCode;

    /**
     * Sequence Id.
     */
    private Integer seqId;

    /**
     * Cislo karty.
     */
    private String cardNumber;

    /**
     * Token generovany na zaklade PAN karty.
     */
    private String cardToken;

    /**
     * Typ karty.
     */
    private String cardType;

    /**
     * Hodnoty uzaverky, popis v B protokolu.
     */
    private Balancing balancing;

    /**
     * Skutecne zaplacena castka
     */
    private Long amount;

    /**
     * Zbytek po platbe.
     */
    private Long remainPayment;

    /**
     * Vyzadovan tisk na pokladnim systemu.
     */
    private Boolean ticketRequired;

    /**
     * Vyzadovana kontrola podpisu.
     */
    private Boolean signRequired;


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

    public Balancing getBalancing() {
        return balancing;
    }

    public void setBalancing(Balancing balancing) {
        this.balancing = balancing;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public Long getRemainPayment() {
        return remainPayment;
    }

    public void setRemainPayment(Long remainPayment) {
        this.remainPayment = remainPayment;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Boolean getTicketRequired() {
        return ticketRequired;
    }

    public void setTicketRequired(Boolean ticketRequired) {
        this.ticketRequired = ticketRequired;
    }

    public Boolean getSignRequired() {
        return signRequired;
    }

    public void setSignRequired(Boolean signRequired) {
        this.signRequired = signRequired;
    }


    @Override
    public String toString() {
        return "TransactionOut{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", authCode='" + authCode + '\'' +
                ", seqId=" + seqId +
                ", cardNumber='" + cardNumber + '\'' +
                ", cardToken='" + cardToken + '\'' +
                ", cardType='" + cardType + '\'' +
                ", balancing=" + balancing +
                ", amount=" + amount +
                ", remainPayment=" + remainPayment +
                ", ticketRequired=" + ticketRequired +
                ", signRequired=" + signRequired +
                '}';
    }
}
