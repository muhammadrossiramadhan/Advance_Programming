package model;

import exception.DataTidakValidException;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

/**
 * Kelas Abstrak (Base Class) yang merepresentasikan pesan informasi
 * pada papan rambu elektronik dinamis (Variable Message Sign / VMS).
 * Menerapkan konsep Abstraction, Encapsulation, dan Polymorphism.
 */
public abstract class PesanRambu {

    private final String kodeJalur;
    private final String modeAlgoritma;
    private final LinkedList<Perempatan> rute;
    private final LocalDateTime waktuDibuat;

    public PesanRambu(String kodeJalur, String modeAlgoritma, LinkedList<Perempatan> rute)
            throws DataTidakValidException {
        if (kodeJalur == null || kodeJalur.trim().isEmpty()) {
            throw new DataTidakValidException("Kode jalur pada pesan rambu tidak boleh kosong!");
        }
        if (rute == null || rute.isEmpty()) {
            throw new DataTidakValidException("Rute perempatan tidak boleh kosong!");
        }
        this.kodeJalur = kodeJalur;
        this.modeAlgoritma = (modeAlgoritma == null || modeAlgoritma.trim().isEmpty()) ? "TERCEPAT" : modeAlgoritma;
        this.rute = new LinkedList<>(rute);
        this.waktuDibuat = LocalDateTime.now();
    }

    /**
     * Mengembalikan pesan visual berformat HTML untuk papan rambu GUI.
     */
    public abstract String getPesanTeks();

    /**
     * Mengembalikan pesan teks bersih untuk tampilan konsol / terminal.
     */
    public abstract String getPesanTerminal();

    /**
     * Mengembalikan warna lampu latar papan rambu dinamis.
     */
    public abstract Color getWarnaLampu();

    /**
     * Mengembalikan kode heksadesimal warna lampu latar.
     */
    public abstract String getKodeWarnaHex();

    /**
     * Mengembalikan tingkat urgensi rambu (NORMAL, PERHATIAN, WASPADA, BAHAYA).
     */
    public abstract String getTingkatUrgensi();

    /**
     * Mengembalikan arahan atau tindakan yang disarankan kepada pengemudi.
     */
    public abstract String getRekomendasiTindakan();

    /**
     * Helper method untuk memformat rantai perempatan menjadi teks panah "A -> B -> C".
     */
    protected String formatRute() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rute.size(); i++) {
            sb.append(rute.get(i).getKode());
            if (i < rute.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    /**
     * Format log ringkas untuk pencatatan riwayat (LinkedList).
     */
    public String getPesanRingkas() {
        return String.format("[%s | %s] Jalur %s -> Rute: %s (%s)",
                getTingkatUrgensi(), modeAlgoritma, kodeJalur, formatRute(), getWaktuDibuatFormatted());
    }

    public String getKodeJalur() {
        return kodeJalur;
    }

    public String getModeAlgoritma() {
        return modeAlgoritma;
    }

    public LinkedList<Perempatan> getRute() {
        return new LinkedList<>(rute);
    }

    public LocalDateTime getWaktuDibuat() {
        return waktuDibuat;
    }

    public String getWaktuDibuatFormatted() {
        return waktuDibuat.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    @Override
    public String toString() {
        return getPesanTerminal();
    }
}
