package cz.monetplus.blueterm.xprotocol;

public enum TicketCommand {
    Merchant('M'), Customer('C'), Continue('1'), Next(' '), End('0');

    private Character tag;

    private TicketCommand(Character tag) {
        this.setTag(tag);
    }

    public Character getTag() {
        return tag;
    }

    public void setTag(Character tag) {
        this.tag = tag;
    }

    public static TicketCommand tagOf(Character tag) {
        for (TicketCommand e : TicketCommand.class.getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + TicketCommand.class.getName() + " enum tag:" + tag);
    }

    public static TicketCommand valueOf(TicketType ticketType) {
        for (TicketCommand e : TicketCommand.class.getEnumConstants()) {
            if (e.getTag().equals(ticketType.getTag())) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "Unknown " + TicketCommand.class.getName() + " enum tag:"
                        + ticketType.getTag());
    }

}
