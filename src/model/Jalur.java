package model;

import exception.DataTidakValidException;

public class Jalur {
    private final String kodeJalur;
    private final Perempatan dari;
    private final Perempatan ke;
    private final double waktuNormal;
    private final int kapasitasMaksimal;
    private int jumlahKendaraan;

    public Jalur(String kodeJalur, Perempatan dari, Perempatan ke,
                 double waktuNormal, int kapasitasMaksimal) throws DataTidakValidException {
        if (waktuNormal <= 0 || kapasitasMaksimal <= 0) {
            throw new DataTidakValidException("Waktu normal dan kapasitas jalur harus lebih dari 0!");
        }
        this.kodeJalur = kodeJalur;
        this.dari = dari;
        this.ke = ke;
        this.waktuNormal = waktuNormal;
        this.kapasitasMaksimal = kapasitasMaksimal;
        this.jumlahKendaraan = 0;
    }

    public void setJumlahKendaraan(int jumlahBaru) throws DataTidakValidException {
        if (jumlahBaru < 0) {
            throw new DataTidakValidException(
                "Jumlah kendaraan di jalur " + kodeJalur + " tidak boleh negatif (" + jumlahBaru + ")!"
            );
        }
        this.jumlahKendaraan = jumlahBaru;
    }

    public double hitungRasioKepadatan() {
        return (double) jumlahKendaraan / kapasitasMaksimal;
    }

    public KondisiJalan cekKondisi() {
        double rasio = hitungRasioKepadatan();
        if (rasio < 0.50) {
            return KondisiJalan.LANCAR;
        } else if (rasio < 0.75) {
            return KondisiJalan.RAMAI;
        } else if (rasio < 1.00) {
            return KondisiJalan.PADAT;
        } else {
            return KondisiJalan.MACET;
        }
    }

    public int getSisaKapasitas() {
        return kapasitasMaksimal - jumlahKendaraan;
    }

    public String getKodeJalur() {
        return kodeJalur;
    }

    public Perempatan getDari() {
        return dari;
    }

    public Perempatan getKe() {
        return ke;
    }

    public double getWaktuNormal() {
        return waktuNormal;
    }

    public int getJumlahKendaraan() {
        return jumlahKendaraan;
    }

    public int getKapasitasMaksimal() {
        return kapasitasMaksimal;
    }
}
