package cz.monetplus.blueterm;

import cz.monetplus.blueterm.vprotocol.RechargingType;


public class TransactionIn {
    private String blueHwAddress;
    
    private String hostIP;
    
//    private int hostPort;
    
    private TransactionCommand command;

    private Long amount;
    
    private String invoice;
    
    private Integer currency;
    
    private Long tranId;

    private RechargingType rechargintType;

    public TransactionIn() {
        super();
        this.amount = (long) 0;
        this.currency = 203;
        this.tranId = (long) 0;
        this.rechargintType = RechargingType.Cash;
    }

    public TransactionIn(String blueHwAddress, String hostIP,
            TransactionCommand command, Long amount, String invoice,
            Integer currency, Long tranId) {
        super();
        this.blueHwAddress = blueHwAddress;
        this.hostIP = hostIP;
        this.command = command;
        this.amount = amount;
        this.invoice = invoice;
        this.currency = currency;
        this.tranId = tranId;
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

    public String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

//    public int getHostPort() {
//        return hostPort;
//    }
//
//    public void setHostPort(int hostPort) {
//        this.hostPort = hostPort;
//    }

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
}
