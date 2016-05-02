package cz.monetplus.blueterm.vprotocol;

public enum RechargingType {
    Cash('0'),
    
    Card('1');

    Character tag;

    private RechargingType(Character tag) {
        this.tag = tag;
    }

    public Character getTag() {
        return tag;
    }

    public static RechargingType tagOf(Character tag) {
        for (RechargingType e : RechargingType.class.getEnumConstants()) {
            if (e.getTag().equals(tag)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + RechargingType.class.getName() + " enum tag:" + tag);
    }

}
