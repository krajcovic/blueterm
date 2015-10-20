package cz.monetplus.blueterm;

import cz.monetplus.blueterm.vprotocol.RechargingType;

/**
 * @author krajcovic
 *
 */
public class TransactionIn {
    /**
     * Hardwarova adresa BT zarizeni
     */
    private String blueHwAddress;

    /**
     * Volana operace
     */
    private TransactionCommand command;

    /**
     * Zasilana castka
     */
    private Long amount;

    /**
     * Variabilni symbol
     */
    private String invoice;

    /**
     * Mena
     */
    private Integer currency;

    /**
     * ID transakce
     */
    private Long tranId;
    
    /**
     * 
     */
    private String authCode;

    /**
     * Typ dobijeni
     */
    private RechargingType rechargintType;

    /**
     * Metody pro komunikaci s pokladnou
     */
    private PosCallbacks posCallbacks;

    /**
     * Hodnoty uzaverky, popis v B protokolu
     */
    private Balancing balancing;

    public TransactionIn(String blueHwAddress, TransactionCommand command, PosCallbacks posCallbacks) {
        super();

        this.blueHwAddress = blueHwAddress;
        this.command = command;

        this.posCallbacks = posCallbacks;
    }

    public void setPayment(Long amount, String invoice, Integer currency,
            Long tranId) {
        this.amount = amount;
        this.invoice = invoice;
        this.currency = currency;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public String getBlueHwAddress() {
        return blueHwAddress;
    }

    public TransactionCommand getCommand() {
        return command;
    }

    public Long getTranId() {
        return this.tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    public RechargingType getRechargingType() {
        return rechargintType;
    }

    public void setRechargingType(RechargingType type) {
        this.rechargintType = type;
    }

    public Balancing getBalancing() {
        return balancing;
    }

    public void setBalancing(Balancing balancing) {
        this.balancing = balancing;
    }

    public PosCallbacks getPosCallbacks() {
        return posCallbacks;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
 }
