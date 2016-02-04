package cz.monetplus.blueterm.xprotocol;

public enum TicketType {
    Merchant('M'),
    Customer('C');
    
    private Character tag;

    private TicketType(Character tag) {
        this.setTag(tag);
    }

    public Character getTag() {
        return tag;
    }

    public void setTag(Character tag) {
        this.tag = tag;
    }
    
    public static TicketType tagOf(Character tag) {
        for (TicketType e : TicketType.class.getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + TicketType.class.getName() + " enum tag:" + tag);
    }
    
}
