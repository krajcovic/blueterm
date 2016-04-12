package cz.monetplus.blueterm;

/**
 * Base class for transaction results.
 * 
 * @author "Dusan Krajcovic dusan.krajcovic [at] monetplus.cz"
 * 
 */
public class TransactionOutBase implements TransactionOut {

    // Navratovy kod informujici o problému v blueterm a nebo v terminálu. O =
    // OK
    private Integer resultCode;

    // Popis navratového kodu
    private String message;

    // Autorizacni kod po platbě
    private String authCode;

    // Sekvencni cislo platby
    private Integer seqId;

    // Cislo pouzite katy
    private String cardNumber;

    // Typ pouzite karty pri platbe
    protected String cardType;

    /**
     * Toto pole je tvořeno skupinou polí reprezentující součty terminálu při
     * uzávěrce. Obsahuje shift and batch ID, spolu s počty debitních,
     * creditních operací a součty částek těchto operací. Autorizační server
     * ukládá tyto součty a spolu se svými součty do transakčního logu.Tyto
     * součty obsahují znak znaménka (+ nebo -) na první pozici pole hodnty.
     */
    private String batchTotal;

    public TransactionOutBase() {
        super();
    }

    @Override
    public final Integer getResultCode() {
        return resultCode;
    }

    @Override
    public final void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    @Override
    public final void setMessage(String message) {
        this.message = message;
    }

    @Override
    public final String getAuthCode() {
        return authCode;
    }

    @Override
    public final void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    @Override
    public final Integer getSeqId() {
        return seqId;
    }

    @Override
    public final void setSeqId(Integer seqId) {
        this.seqId = seqId;
    }

    @Override
    public final String getCardNumber() {
        return cardNumber;
    }

    @Override
    public final void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public final void setCardType(String cardType) {
        this.cardType = cardType;
    }

    @Override
    public final String getCardType() {
        return cardType;
    }

    @Override
    public String getBatchTotal() {
        return batchTotal;
    }

    @Override
    public void setBatchTotal(String batchTotal) {
        this.batchTotal = batchTotal;
    }

}
