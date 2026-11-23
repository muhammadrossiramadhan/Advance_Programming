package algoritma;

import model.Jalur;

public class PemilihJalurTercepat extends PemilihDijkstra {
    @Override
    protected double hitungBiaya(Jalur jalur) {
        return jalur.getWaktuNormal() * jalur.cekKondisi().getPengaliMacet();
    }
}
