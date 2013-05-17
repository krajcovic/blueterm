package cz.monetplus.blueterm;

public class TransactionIn {
    private String blueHwAddress;
    
    private TransactionCommand command;

    private Integer amount;
    private String invoice;
    private Integer currency;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
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
}
