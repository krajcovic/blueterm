package cz.monetplus.blueterm;

public class TransactionInBase implements TransactionIn {

    @Deprecated
    private String hostIP;
    @Deprecated
    private int hostPort;
    // Operace zaslana do terminalu
    private TransactionCommand command;

    // Částka zaslana na terminal při platbě
    private Long amount;

    // Varibilni symbol
    private String invoice;

    // ISO code meny
    private Integer currency;

    // Autorizacni kod pro reversal.
    private String authCode;

    public TransactionInBase(TransactionCommand command) {
        super();
        this.command = command;
    }

    @Override
    public final Long getAmount() {
        return amount;
    }

    @Override
    public final void setAmount(Long amount) {
        this.amount = amount;
    }

    @Override
    public final String getInvoice() {
        return invoice;
    }

    @Override
    public final void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    @Override
    public final Integer getCurrency() {
        return currency;
    }

    @Override
    public final void setCurrency(Integer currency) {
        this.currency = currency;
    }

    @Override
    public final TransactionCommand getCommand() {
        return command;
    }

    @Override
    public final void setCommand(TransactionCommand command) {
        this.command = command;
    }

    @Override
    public final String getHostIP() {
        return hostIP;
    }

    @Override
    public final void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    @Override
    public final int getHostPort() {
        return hostPort;
    }

    @Override
    public final void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    @Override
    public String getAuthCode() {
        return authCode;
    }

    @Override
    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

}
