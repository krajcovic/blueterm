package cz.monetplus.blueterm.xprotocol;


public enum ProtocolType {
    BProtocol('B'),
    VProtocol('V'),
    SProtocol('S'),
    NProtocol('N');
    
    private Character tag;

    private ProtocolType(Character tag) {
        this.setTag(tag);
    }
    
    public static ProtocolType tagOf(Character tag) {
        for (ProtocolType e : ProtocolType.class.getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + ProtocolType.class.getName() + " enum tag:" + tag);
    }

    public Character getTag() {
        return tag;
    }

    private void setTag(Character tag) {
        this.tag = tag;
    }
}
