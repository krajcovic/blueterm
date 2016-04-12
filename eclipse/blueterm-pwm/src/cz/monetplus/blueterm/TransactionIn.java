package cz.monetplus.blueterm;

public interface TransactionIn {

    public abstract Long getAmount();

    public abstract void setAmount(Long amount);

    public abstract String getInvoice();

    public abstract void setInvoice(String invoice);

    public abstract String getAuthCode();

    public abstract void setAuthCode(String authCode);

    public abstract Integer getCurrency();

    public abstract void setCurrency(Integer currency);

    public abstract TransactionCommand getCommand();

    public abstract void setCommand(TransactionCommand command);

    public abstract String getHostIP();

    public abstract void setHostIP(String hostIP);

    public abstract int getHostPort();

    public abstract void setHostPort(int hostPort);

}
