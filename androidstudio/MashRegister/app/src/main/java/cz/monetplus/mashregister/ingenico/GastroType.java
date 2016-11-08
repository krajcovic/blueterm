package cz.monetplus.mashregister.ingenico;

/**
 * Created by krajcovic on 11/8/16.
 */
public enum GastroType {
    Disabled(-1),
    Full(0),
    Partial(1);

    private int id;

    GastroType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static GastroType valueOf(int id) {
        for (GastroType e : GastroType.class.getEnumConstants()) {
            if (e.getId() == id) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown "
                + GastroType.class.getName() + " enum id:"
                + String.format("%#08x", id));
    }
}
