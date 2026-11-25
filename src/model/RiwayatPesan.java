package model;

import exception.DataTidakValidException;
import java.util.LinkedList;

/**
 * Mengelola catatan riwayat perubahan status papan rambu dinamis
 * secara kronologis menggunakan struktur data LinkedList.
 */
public class RiwayatPesan {
    private static final int BATAS_MAKSIMAL_DEFAULT = 200;

    private final LinkedList<PesanRambu> daftarRiwayat;
    private final int batasMaksimal;

    public RiwayatPesan() {
        this(BATAS_MAKSIMAL_DEFAULT);
    }

    public RiwayatPesan(int batasMaksimal) {
        this.daftarRiwayat = new LinkedList<>();
        this.batasMaksimal = batasMaksimal > 0 ? batasMaksimal : BATAS_MAKSIMAL_DEFAULT;
    }

    /**
     * Menambahkan entri PesanRambu baru ke bagian paling depan (terbaru di atas).
     *
     * @param pesan Objek PesanRambu hasil factory yang akan dicatat
     * @throws DataTidakValidException jika objek pesan bernilai null
     */
    public void catat(PesanRambu pesan) throws DataTidakValidException {
        if (pesan == null) {
            throw new DataTidakValidException("Objek pesan rambu tidak boleh null saat dicatat ke riwayat!");
        }
        daftarRiwayat.addFirst(pesan);
        while (daftarRiwayat.size() > batasMaksimal) {
            daftarRiwayat.removeLast();
        }
    }

    /**
     * Mengembalikan salinan daftar riwayat agar enkapsulasi internal tetap terjaga.
     */
    public LinkedList<PesanRambu> getDaftarRiwayat() {
        return new LinkedList<>(daftarRiwayat);
    }

    public PesanRambu getRiwayatTerbaru() {
        return daftarRiwayat.peekFirst();
    }

    public int getJumlahCatatan() {
        return daftarRiwayat.size();
    }

    public boolean isKosong() {
        return daftarRiwayat.isEmpty();
    }

    public void bersihkan() {
        daftarRiwayat.clear();
    }

    /**
     * Menyusun ringkasan seluruh riwayat perubahan pesan rambu dalam bentuk teks kronologis.
     */
    public String formatSemuaCatatan() {
        if (daftarRiwayat.isEmpty()) {
            return "Belum ada perubahan status rambu yang tercatat.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < daftarRiwayat.size(); i++) {
            PesanRambu pesan = daftarRiwayat.get(i);
            sb.append(i + 1)
              .append(". ")
              .append(pesan.getPesanRingkas())
              .append(" | ")
              .append(pesan.getRekomendasiTindakan())
              .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
