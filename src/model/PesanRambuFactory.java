package model;

import exception.DataTidakValidException;
import exception.SistemRambuException;
import java.util.LinkedList;

/**
 * Creational Design Pattern: Factory Pattern.
 * Bertanggung jawab membuat objek konkret turunan PesanRambu secara terpusat
 * berdasarkan status KondisiJalan tanpa mengekspos logika instansiasi langsung ke UI atau modul lain.
 */
public class PesanRambuFactory {

    /**
     * Private constructor untuk mencegah instansiasi class factory statis.
     */
    private PesanRambuFactory() {
    }

    /**
     * Membuat objek turunan PesanRambu berdasarkan kondisi jalan.
     *
     * @param kondisi Kondisi lalu lintas jalan (LANCAR, RAMAI, PADAT, MACET)
     * @param kodeJalur Kode jalur yang diperiksa (contoh: "B-C")
     * @param modeAlgoritma Mode pemilihan jalur yang aktif ("TERCEPAT" atau "TERPENDEK")
     * @param rute Daftar perempatan rute hasil kalkulasi algoritma
     * @return Objek PesanRambu konkret (PesanLancar, PesanRamai, PesanPadat, atau PesanMacet)
     * @throws SistemRambuException jika parameter kondisi, kode, atau rute tidak valid
     */
    public static PesanRambu buatPesan(KondisiJalan kondisi, String kodeJalur,
                                       String modeAlgoritma, LinkedList<Perempatan> rute)
            throws SistemRambuException {

        if (kondisi == null) {
            throw new DataTidakValidException("Kondisi jalan tidak boleh null saat membuat pesan rambu!");
        }
        if (kodeJalur == null || kodeJalur.trim().isEmpty()) {
            throw new DataTidakValidException("Kode jalur pada pesan rambu tidak boleh kosong!");
        }
        if (rute == null || rute.isEmpty()) {
            throw new DataTidakValidException("Rute perempatan tidak boleh kosong!");
        }

        switch (kondisi) {
            case LANCAR:
                return new PesanLancar(kodeJalur, modeAlgoritma, rute);
            case RAMAI:
                return new PesanRamai(kodeJalur, modeAlgoritma, rute);
            case PADAT:
                return new PesanPadat(kodeJalur, modeAlgoritma, rute);
            case MACET:
                return new PesanMacet(kodeJalur, modeAlgoritma, rute);
            default:
                throw new DataTidakValidException("Kondisi jalan '" + kondisi + "' belum didukung oleh sistem pesan rambu!");
        }
    }

    /**
     * Overload helper: Membuat PesanRambu langsung dari objek Jalur.
     *
     * @param jalur Objek Jalur yang sedang disimulasikan
     * @param modeAlgoritma Mode algoritma yang sedang aktif
     * @param rute Rute hasil kalkulasi
     * @return Objek PesanRambu konkret
     * @throws SistemRambuException jika objek jalur null atau data rute tidak valid
     */
    public static PesanRambu buatPesan(Jalur jalur, String modeAlgoritma, LinkedList<Perempatan> rute)
            throws SistemRambuException {
        if (jalur == null) {
            throw new DataTidakValidException("Objek jalur tidak boleh null!");
        }
        return buatPesan(jalur.cekKondisi(), jalur.getKodeJalur(), modeAlgoritma, rute);
    }
}
