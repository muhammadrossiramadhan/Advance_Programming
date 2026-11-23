package algoritma;

import model.Jalur;

public class PemilihJalurTerpendek extends PemilihDijkstra {
    @Override
    protected double hitungBiaya(Jalur jalur) {
        return jalur.getWaktuNormal();
    }
}
