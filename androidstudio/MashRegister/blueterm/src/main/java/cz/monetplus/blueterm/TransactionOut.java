package cz.monetplus.blueterm;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Nepovinne, pevna delka odpovedi 10 bytu. Format #:MERCHANTID
     */
    private List<String> merchantId;

    /**
     * {9i}* - jednotlive informace, prvni znak - index, dalsi data
     0-pozice menu dcery (int-1..)
     1-jmeno menu dcery (text[20])
     2-parametry invoice (text[24])
     */
    private Map<Character, String> accountInfo;


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

    public List<String> getMerchantId() {
        return this.merchantId;
    }

    public void setMerchantId(String[] merchantId) {
        this.merchantId = Arrays.asList(merchantId);
    }

    public Map<Character, String> getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(String[] accountInfo) {
        this.accountInfo = new HashMap<>();
        for (String ai:accountInfo) {
            this.accountInfo.put(ai.charAt(0), ai.substring(1));
        }
    }

    @Override
    public String toString() {
        return "TransactionOut{" +
                (resultCode != null ? "resultCode=" + resultCode : "") +
                (message != null ? ", message='" + message + '\'' : "") +
                (authCode != null ? ", authCode='" + authCode + '\'' : "") +
                (seqId != null ? ", seqId=" + seqId : "") +
                (cardNumber != null ? ", cardNumber='" + cardNumber + '\'' : "") +
                (cardToken != null ? ", cardToken='" + cardToken + '\'' : "") +
                (cardType != null ? ", cardType='" + cardType + '\'' : "") +
                (balancing != null ? ", balancing=" + balancing : "") +
                (amount != null ? ", amount=" + amount : "") +
                (remainPayment != null ? ", remainPayment=" + remainPayment : "") +
                (ticketRequired != null ? ", ticketRequired=" + ticketRequired : "") +
                (signRequired != null ? ", signRequired=" + signRequired : "") +
                (merchantId != null ? ", merchantId='" + merchantId + '\'' : "") +
                (accountInfo != null ? ", accountInfo='" + accountInfo + '\'' : "") +
                '}';
    }
}
