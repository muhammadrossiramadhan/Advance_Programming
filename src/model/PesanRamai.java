package model;

import exception.DataTidakValidException;
import java.awt.Color;
import java.util.LinkedList;

/**
 * Representasi pesan rambu ketika kondisi jalan RAMAI.
 * Menampilkan lampu kuning/amber dan mengimbau kehati-hatian.
 */
public class PesanRamai extends PesanRambu {

    private static final Color WARNA_KUNING = new Color(202, 138, 4);
    private static final String HEX_KUNING = "#CA8A04";

    public PesanRamai(String kodeJalur, String modeAlgoritma, LinkedList<Perempatan> rute)
            throws DataTidakValidException {
        super(kodeJalur, modeAlgoritma, rute);
    }

    @Override
    public String getPesanTeks() {
        return "<html><center>KONDISI RAMAI (" + getModeAlgoritma() + ")<br>RUTE: " + formatRute() + "</center></html>";
    }

    @Override
    public String getPesanTerminal() {
        return "KONDISI RAMAI (" + getModeAlgoritma() + ") | RUTE: " + formatRute();
    }

    @Override
    public Color getWarnaLampu() {
        return WARNA_KUNING;
    }

    @Override
    public String getKodeWarnaHex() {
        return HEX_KUNING;
    }

    @Override
    public String getTingkatUrgensi() {
        return "RAMAI";
    }

    @Override
    public String getRekomendasiTindakan() {
        return "Volume kendaraan mulai meningkat. Harap kurangi kecepatan dan jaga jarak aman.";
    }
}
