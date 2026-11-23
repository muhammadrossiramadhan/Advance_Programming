package model;

import exception.DataTidakValidException;
import java.util.HashMap;
import java.util.LinkedList;

public class PetaKota {
    private final HashMap<String, Perempatan> daftarPerempatan = new HashMap<>();
    private final HashMap<String, Jalur> daftarJalur = new HashMap<>();
    private final HashMap<String, LinkedList<Jalur>> petaSambungan = new HashMap<>();

    public void tambahPerempatan(Perempatan perempatan) {
        daftarPerempatan.put(perempatan.getKode(), perempatan);
        petaSambungan.putIfAbsent(perempatan.getKode(), new LinkedList<>());
    }

    public void tambahJalur(Jalur jalur) throws DataTidakValidException {
        String kodeAsal = jalur.getDari().getKode();
        String kodeTujuan = jalur.getKe().getKode();

        if (!daftarPerempatan.containsKey(kodeAsal) || !daftarPerempatan.containsKey(kodeTujuan)) {
            throw new DataTidakValidException("Perempatan asal atau tujuan belum terdaftar di peta!");
        }

        daftarJalur.put(jalur.getKodeJalur(), jalur);
        petaSambungan.get(kodeAsal).add(jalur);
    }

    public Perempatan cariPerempatan(String kode) throws DataTidakValidException {
        Perempatan hasil = daftarPerempatan.get(kode);
        if (hasil == null) {
            throw new DataTidakValidException("Perempatan dengan kode '" + kode + "' tidak ditemukan!");
        }
        return hasil;
    }

    public Jalur cariJalur(String kodeJalur) throws DataTidakValidException {
        Jalur hasil = daftarJalur.get(kodeJalur);
        if (hasil == null) {
            throw new DataTidakValidException("Jalur dengan kode '" + kodeJalur + "' tidak ditemukan!");
        }
        return hasil;
    }

    public LinkedList<Jalur> getJalurKeluarDari(String kodePerempatan) {
        return petaSambungan.getOrDefault(kodePerempatan, new LinkedList<>());
    }

    public HashMap<String, Perempatan> getSemuaPerempatan() {
        return daftarPerempatan;
    }

    public HashMap<String, Jalur> getSemuaJalur() {
        return daftarJalur;
    }
}
