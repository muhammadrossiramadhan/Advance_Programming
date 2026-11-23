package model;

public class Perempatan {
    private final String kode;
    private final String nama;

    public Perempatan(String kode, String nama) {
        this.kode = kode;
        this.nama = nama;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    @Override
    public String toString() {
        return kode + " (" + nama + ")";
    }
}
