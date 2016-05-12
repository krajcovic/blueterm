package cz.monetplus.blueterm;

import cz.monetplus.blueterm.vprotocol.RechargingType;
import cz.monetplus.blueterm.xprotocol.TicketCommand;
import cz.monetplus.blueterm.xprotocol.TicketType;

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
     * Castecna platba.
     */
    private Boolean partialPayment;
    
    /**
     * Cislo tiketu.
     */
    private String ticketNumber;

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
     * Autorizacni kod
     */
    private String authCode;

    /**
     * Typ dobijeni
     */
    private RechargingType rechargingType;
    
    /**
     * Typy smartshop karet.
     */
    private Integer cardType;

    /**
     * Metody pro komunikaci s pokladnou
     */
    private PosCallbacks posCallbacks;
    
    /**
     * Which ticket print.
     */
    private TicketType ticketType;

    public TransactionIn(String blueHwAddress, TransactionCommand command, PosCallbacks posCallbacks) {
        super();

        this.blueHwAddress = blueHwAddress;
        this.command = command;       
        this.partialPayment = false;
        this.ticketNumber = "";
        this.posCallbacks = posCallbacks;
        this.cardType = null;
    }

    public void setPayment(Long amount, String invoice, Integer currency,
            Long tranId) {
        this.amount = amount;
        this.invoice = invoice;
        this.currency = currency;
        this.cardType = null;
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
        return rechargingType;
    }

    public void setRechargingType(RechargingType type) {
        this.rechargingType = type;
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

    public Boolean getPartialPayment() {
        return partialPayment;
    }

    public void setPartialPayment(Boolean partialPayment) {
        this.partialPayment = partialPayment;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public String toString() {
        return "TransactionIn{" +
                "blueHwAddress='" + blueHwAddress + '\'' +
                ", command=" + command +
                ", amount=" + amount +
                ", partialPayment=" + partialPayment +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", invoice='" + invoice + '\'' +
                ", currency=" + currency +
                ", tranId=" + tranId +
                ", authCode='" + authCode + '\'' +
                ", rechargingType=" + rechargingType +
                ", cardType=" + cardType +
                ", posCallbacks=" + posCallbacks +
                ", ticketType=" + ticketType +
                '}';
    }
}
