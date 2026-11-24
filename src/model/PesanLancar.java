package model;

import exception.DataTidakValidException;
import java.awt.Color;
import java.util.LinkedList;

/**
 * Representasi pesan rambu ketika kondisi jalan LANCAR.
 * Menampilkan lampu hijau dan mengonfirmasi rute normal.
 */
public class PesanLancar extends PesanRambu {

    private static final Color WARNA_HIJAU = new Color(46, 125, 50);
    private static final String HEX_HIJAU = "#2E7D32";

    public PesanLancar(String kodeJalur, String modeAlgoritma, LinkedList<Perempatan> rute)
            throws DataTidakValidException {
        super(kodeJalur, modeAlgoritma, rute);
    }

    @Override
    public String getPesanTeks() {
        return "<html><center>KONDISI LANCAR (" + getModeAlgoritma() + ")<br>RUTE: " + formatRute() + "</center></html>";
    }

    @Override
    public String getPesanTerminal() {
        return "KONDISI LANCAR (" + getModeAlgoritma() + ") | RUTE: " + formatRute();
    }

    @Override
    public Color getWarnaLampu() {
        return WARNA_HIJAU;
    }

    @Override
    public String getKodeWarnaHex() {
        return HEX_HIJAU;
    }

    @Override
    public String getTingkatUrgensi() {
        return "LANCAR";
    }

    @Override
    public String getRekomendasiTindakan() {
        return "Jalur lancar dan bebas hambatan. Silakan lanjutkan perjalanan.";
    }
}
