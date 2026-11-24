package model;

import exception.DataTidakValidException;
import java.awt.Color;
import java.util.LinkedList;

/**
 * Representasi pesan rambu ketika kondisi jalan PADAT.
 * Menampilkan lampu oranye dan menyarankan opsi rute alternatif.
 */
public class PesanPadat extends PesanRambu {

    private static final Color WARNA_ORANYE = new Color(230, 81, 0);
    private static final String HEX_ORANYE = "#E65100";

    public PesanPadat(String kodeJalur, String modeAlgoritma, LinkedList<Perempatan> rute)
            throws DataTidakValidException {
        super(kodeJalur, modeAlgoritma, rute);
    }

    @Override
    public String getPesanTeks() {
        return "<html><center>JALUR " + getKodeJalur() + " PADAT (" + getModeAlgoritma() + ")<br>GUNAKAN ALTERNATIF: " + formatRute() + "</center></html>";
    }

    @Override
    public String getPesanTerminal() {
        return "JALUR " + getKodeJalur() + " PADAT (" + getModeAlgoritma() + ") | ALTERNATIF: " + formatRute();
    }

    @Override
    public Color getWarnaLampu() {
        return WARNA_ORANYE;
    }

    @Override
    public String getKodeWarnaHex() {
        return HEX_ORANYE;
    }

    @Override
    public String getTingkatUrgensi() {
        return "PADAT";
    }

    @Override
    public String getRekomendasiTindakan() {
        return "Antrean kendaraan mendekati kapasitas maksimum. Disarankan memilih rute alternatif.";
    }
}
