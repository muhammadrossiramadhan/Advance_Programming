package model;

public enum KondisiJalan {
    LANCAR(1.0),
    RAMAI(1.5),
    PADAT(2.0),
    MACET(3.0);

    private final double pengaliMacet;

    KondisiJalan(double pengaliMacet) {
        this.pengaliMacet = pengaliMacet;
    }

    public double getPengaliMacet() {
        return pengaliMacet;
    }
}
