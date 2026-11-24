package model;

import exception.DataTidakValidException;
import java.awt.Color;
import java.util.LinkedList;

/**
 * Representasi pesan rambu ketika kondisi jalan MACET.
 * Menampilkan lampu merah peringatan bahaya dan menginstruksikan pengalihan arus lalu lintas.
 */
public class PesanMacet extends PesanRambu {

    private static final Color WARNA_MERAH = new Color(198, 40, 40);
    private static final String HEX_MERAH = "#C62828";

    public PesanMacet(String kodeJalur, String modeAlgoritma, LinkedList<Perempatan> rute)
            throws DataTidakValidException {
        super(kodeJalur, modeAlgoritma, rute);
    }

    @Override
    public String getPesanTeks() {
        return "<html><center>JALUR " + getKodeJalur() + " MACET! (" + getModeAlgoritma() + ")<br>PENGALIHAN RUTE: " + formatRute() + "</center></html>";
    }

    @Override
    public String getPesanTerminal() {
        return "JALUR " + getKodeJalur() + " MACET! (" + getModeAlgoritma() + ") | PENGALIHAN RUTE: " + formatRute();
    }

    @Override
    public Color getWarnaLampu() {
        return WARNA_MERAH;
    }

    @Override
    public String getKodeWarnaHex() {
        return HEX_MERAH;
    }

    @Override
    public String getTingkatUrgensi() {
        return "MACET";
    }

    @Override
    public String getRekomendasiTindakan() {
        return "Kemacetan parah terjadi pada jalur " + getKodeJalur() + "! Pengemudi diwajibkan mengikuti rute pengalihan.";
    }
}
