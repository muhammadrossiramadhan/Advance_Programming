package algoritma;

import exception.DataTidakValidException;
import exception.RuteTidakDitemukanException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import model.Jalur;
import model.Perempatan;
import model.PetaKota;

public abstract class PemilihDijkstra implements PemilihJalur {

    protected abstract double hitungBiaya(Jalur jalur);

    private static class AntreanCek implements Comparable<AntreanCek> {
        String kodePerempatan;
        double totalBiaya;

        AntreanCek(String kodePerempatan, double totalBiaya) {
            this.kodePerempatan = kodePerempatan;
            this.totalBiaya = totalBiaya;
        }

        @Override
        public int compareTo(AntreanCek lain) {
            return Double.compare(this.totalBiaya, lain.totalBiaya);
        }
    }

    @Override
    public LinkedList<Perempatan> cariRute(PetaKota peta, String kodeAsal, String kodeTujuan)
            throws DataTidakValidException, RuteTidakDitemukanException {

        if (peta == null) {
            throw new DataTidakValidException("Objek peta kota tidak boleh null!");
        }
        peta.cariPerempatan(kodeAsal);
        peta.cariPerempatan(kodeTujuan);

        Map<String, Double> biayaTerbaik = new HashMap<>();
        Map<String, String> datangDari = new HashMap<>();

        for (String kode : peta.getSemuaPerempatan().keySet()) {
            biayaTerbaik.put(kode, Double.POSITIVE_INFINITY);
        }
        biayaTerbaik.put(kodeAsal, 0.0);

        PriorityQueue<AntreanCek> antrean = new PriorityQueue<>();
        antrean.add(new AntreanCek(kodeAsal, 0.0));

        while (!antrean.isEmpty()) {
            AntreanCek sekarang = antrean.poll();

            if (sekarang.totalBiaya > biayaTerbaik.get(sekarang.kodePerempatan)) {
                continue;
            }

            if (sekarang.kodePerempatan.equals(kodeTujuan)) {
                break;
            }

            for (Jalur jalur : peta.getJalurKeluarDari(sekarang.kodePerempatan)) {
                String kodeTetangga = jalur.getKe().getKode();
                double biayaLewatSini = sekarang.totalBiaya + hitungBiaya(jalur);

                if (biayaLewatSini < biayaTerbaik.get(kodeTetangga)) {
                    biayaTerbaik.put(kodeTetangga, biayaLewatSini);
                    datangDari.put(kodeTetangga, sekarang.kodePerempatan);
                    antrean.add(new AntreanCek(kodeTetangga, biayaLewatSini));
                }
            }
        }

        if (biayaTerbaik.get(kodeTujuan) == Double.POSITIVE_INFINITY) {
            throw new RuteTidakDitemukanException(
                "Tidak ada jalur yang bisa menghubungkan " + kodeAsal + " ke " + kodeTujuan + "!"
            );
        }

        LinkedList<Perempatan> ruteFinal = new LinkedList<>();
        String penelusur = kodeTujuan;
        while (penelusur != null) {
            ruteFinal.addFirst(peta.cariPerempatan(penelusur));
            penelusur = datangDari.get(penelusur);
        }

        return ruteFinal;
    }
}
