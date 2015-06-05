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
     * Typ dobijeni
     */
    private RechargingType rechargintType;

    /**
     * Hodnoty uzaverky, popis v B protokolu
     */
    private Balancing balancing;

    public TransactionIn() {
        super();
        this.amount = (long) 0;
        this.currency = 203;
        this.tranId = (long) 0;
        this.rechargintType = RechargingType.Cash;
        this.setBalancing(new Balancing());
    }

    public TransactionIn(String blueHwAddress, TransactionCommand command,
            Long amount, String invoice, Integer currency, Long tranId, Balancing balancing) {
        super();
        this.blueHwAddress = blueHwAddress;
        // this.hostIP = hostIP;
        this.command = command;
        this.amount = amount;
        this.invoice = invoice;
        this.currency = currency;
        this.tranId = tranId;
        this.setBalancing(balancing);
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

    public void setBlueHwAddress(String blueHwAddress) {
        this.blueHwAddress = blueHwAddress;
    }

    public TransactionCommand getCommand() {
        return command;
    }

    public void setCommand(TransactionCommand command) {
        this.command = command;
    }

    // public String getHostIP() {
    // return hostIP;
    // }

    // public void setHostIP(String hostIP) {
    // this.hostIP = hostIP;
    // }

    // public int getHostPort() {
    // return hostPort;
    // }
    //
    // public void setHostPort(int hostPort) {
    // this.hostPort = hostPort;
    // }

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
}
