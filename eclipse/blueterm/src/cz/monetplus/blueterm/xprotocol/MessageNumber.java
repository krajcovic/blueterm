package cz.monetplus.blueterm.xprotocol;


public enum MessageNumber {
    Ack(0),
    TransactionRequest(1),
    TransactionResponse(2),
    TicketRequest(3),
    TicketResponse(4);
    
    private Integer number;

    private MessageNumber(Integer number) {
        this.setNumber(number);
    }

    public Integer getNumber() {
        return number;
    }

    private void setNumber(Integer number) {
        this.number = number;
    }
    
    public static MessageNumber numberOf(Integer tag) {
        for (MessageNumber e : MessageNumber.class.getEnumConstants()) {
            if (e.getNumber().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + MessageNumber.class.getName() + " enum tag:" + tag);
    }
    
}
