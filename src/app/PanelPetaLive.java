package app;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import model.Jalur;
import model.KondisiJalan;
import model.Perempatan;
import model.PetaKota;

public class PanelPetaLive extends JPanel {
    private static final double LOGICAL_W = 1070.0;
    private static final double LOGICAL_H = 580.0;

    private final PetaKota peta;
    private LinkedList<Perempatan> ruteAktif = new LinkedList<>();
    private boolean kameraIkutiMobil = false;
    private boolean modeTercepat = true;
    private double faseDenyut = 0.0;

    private int indeksSegmen = 0;
    private double progresSegmen = 0.0;
    private boolean tibaDiTujuan = false;
    private double posisiMobilX = 175.0;
    private double posisiMobilY = 255.0;
    private double sudutMobilRad = 0.0;
    private String kodeJalurSaatIni = "A-B";
    private KondisiJalan kondisiSaatIni = KondisiJalan.LANCAR;
    private int kecepatanKmh = 60;
    private String arahBerikutnya = "B (Jl. Braga)";

    private final Map<String, Point2D.Double> koordinatPerempatan = new HashMap<>();
    private final Map<String, List<Point2D.Double>> jalurPolyline = new HashMap<>();

    public PanelPetaLive(PetaKota peta) {
        this.peta = peta;
        setBackground(Color.WHITE);
        inisialisasiKoordinat();
    }

    private void inisialisasiKoordinat() {
        koordinatPerempatan.put("A", new Point2D.Double(175, 255));
        koordinatPerempatan.put("B", new Point2D.Double(350, 255));
        koordinatPerempatan.put("C", new Point2D.Double(535, 255));
        koordinatPerempatan.put("D", new Point2D.Double(685, 255));
        koordinatPerempatan.put("E", new Point2D.Double(520, 125));

        jalurPolyline.put("A-B", buatTitik(175, 255, 350, 255));
        jalurPolyline.put("B-C", buatTitik(350, 255, 535, 255));
        jalurPolyline.put("C-D", buatTitik(535, 255, 685, 255));
        jalurPolyline.put("B-E", buatTitik(350, 255, 350, 125, 520, 125));
        jalurPolyline.put("E-D", buatTitik(520, 125, 685, 125, 685, 255));
        jalurPolyline.put("A-C", buatTitik(175, 255, 175, 425, 535, 425, 535, 255));
    }

    private List<Point2D.Double> buatTitik(double... xy) {
        List<Point2D.Double> hasil = new ArrayList<>();
        for (int i = 0; i < xy.length; i += 2) {
            hasil.add(new Point2D.Double(xy[i], xy[i + 1]));
        }
        return hasil;
    }

    public void setRuteAktif(LinkedList<Perempatan> rute) {
        if (rute == null || rute.isEmpty()) {
            this.ruteAktif = new LinkedList<>();
            return;
        }
        this.ruteAktif = new LinkedList<>(rute);
        if (indeksSegmen >= Math.max(1, ruteAktif.size() - 1)) {
            resetMobil();
        }
        repaint();
    }

    public void setKameraIkutiMobil(boolean ikuti) {
        this.kameraIkutiMobil = ikuti;
        repaint();
    }

    public void setModeTercepat(boolean modeTercepat) {
        this.modeTercepat = modeTercepat;
        repaint();
    }

    public void resetMobil() {
        indeksSegmen = 0;
        progresSegmen = 0.0;
        tibaDiTujuan = false;
        if (!ruteAktif.isEmpty()) {
            Point2D.Double p = koordinatPerempatan.get(ruteAktif.getFirst().getKode());
            if (p != null) {
                posisiMobilX = p.x;
                posisiMobilY = p.y;
            }
        } else {
            posisiMobilX = 175.0;
            posisiMobilY = 255.0;
        }
        sudutMobilRad = 0.0;
        perbaruiInfoSegmen();
        repaint();
    }

    public void updateAnimasi(double dtDetik) {
        faseDenyut += dtDetik * 5.0;

        if (ruteAktif == null || ruteAktif.size() < 2) {
            repaint();
            return;
        }

        if (tibaDiTujuan) {
            kecepatanKmh = 0;
            repaint();
            return;
        }

        if (indeksSegmen >= ruteAktif.size() - 1) {
            tibaDiTujuan = true;
            kecepatanKmh = 0;
            repaint();
            return;
        }

        perbaruiInfoSegmen();

        double lajuDasar = 0.36;
        double pengali = kondisiSaatIni.getPengaliMacet();
        double lajuEfektif = lajuDasar / Math.pow(pengali, 1.35);

        progresSegmen += lajuEfektif * dtDetik;
        if (progresSegmen >= 1.0) {
            indeksSegmen++;
            progresSegmen = 0.0;
            if (indeksSegmen >= ruteAktif.size() - 1) {
                tibaDiTujuan = true;
                Point2D.Double akhir = koordinatPerempatan.get(ruteAktif.getLast().getKode());
                if (akhir != null) {
                    posisiMobilX = akhir.x;
                    posisiMobilY = akhir.y;
                }
                kecepatanKmh = 0;
                arahBerikutnya = "Tiba di " + ruteAktif.getLast().toString();
                repaint();
                return;
            }
            perbaruiInfoSegmen();
        }

        List<Point2D.Double> titikJalur = jalurPolyline.get(kodeJalurSaatIni);
        if (titikJalur != null && titikJalur.size() >= 2) {
            hitungPosisiDiPolyline(titikJalur, progresSegmen);
        }

        repaint();
    }

    private void perbaruiInfoSegmen() {
        if (ruteAktif == null || ruteAktif.size() < 2 || indeksSegmen >= ruteAktif.size() - 1) {
            return;
        }
        Perempatan dari = ruteAktif.get(indeksSegmen);
        Perempatan ke = ruteAktif.get(indeksSegmen + 1);
        kodeJalurSaatIni = dari.getKode() + "-" + ke.getKode();
        arahBerikutnya = ke.getKode() + " (" + ke.getNama() + ")";
        try {
            Jalur j = peta.cariJalur(kodeJalurSaatIni);
            kondisiSaatIni = j.cekKondisi();
        } catch (Exception ex) {
            kondisiSaatIni = KondisiJalan.LANCAR;
        }

        switch (kondisiSaatIni) {
            case LANCAR:
                kecepatanKmh = 62;
                break;
            case RAMAI:
                kecepatanKmh = 38;
                break;
            case PADAT:
                kecepatanKmh = 18;
                break;
            case MACET:
            default:
                kecepatanKmh = 7;
                break;
        }
    }

    private void hitungPosisiDiPolyline(List<Point2D.Double> pts, double t) {
        double totalPanjang = 0.0;
        double[] panjangSegmen = new double[pts.size() - 1];
        for (int i = 0; i < pts.size() - 1; i++) {
            double dx = pts.get(i + 1).x - pts.get(i).x;
            double dy = pts.get(i + 1).y - pts.get(i).y;
            panjangSegmen[i] = Math.hypot(dx, dy);
            totalPanjang += panjangSegmen[i];
        }
        if (totalPanjang <= 0.001) {
            return;
        }
        double target = t * totalPanjang;
        double akumulasi = 0.0;
        for (int i = 0; i < panjangSegmen.length; i++) {
            if (akumulasi + panjangSegmen[i] >= target || i == panjangSegmen.length - 1) {
                double lokal = (target - akumulasi) / Math.max(0.001, panjangSegmen[i]);
                lokal = Math.max(0.0, Math.min(1.0, lokal));
                Point2D.Double p1 = pts.get(i);
                Point2D.Double p2 = pts.get(i + 1);
                posisiMobilX = p1.x + (p2.x - p1.x) * lokal;
                posisiMobilY = p1.y + (p2.y - p1.y) * lokal;
                sudutMobilRad = Math.atan2(p2.y - p1.y, p2.x - p1.x);
                return;
            }
            akumulasi += panjangSegmen[i];
        }
    }

    public String getKodeJalurSaatIni() {
        return kodeJalurSaatIni;
    }

    public KondisiJalan getKondisiSaatIni() {
        return kondisiSaatIni;
    }

    public int getKecepatanKmh() {
        return kecepatanKmh;
    }

    public String getArahBerikutnya() {
        return arahBerikutnya;
    }

    public double getSudutMobilRad() {
        return sudutMobilRad;
    }

    public boolean isTibaDiTujuan() {
        return tibaDiTujuan;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        double skala = Math.min(getWidth() / LOGICAL_W, getHeight() / LOGICAL_H);
        double offsetX = (getWidth() - LOGICAL_W * skala) / 2.0;
        double offsetY = (getHeight() - LOGICAL_H * skala) / 2.0;

        g2.translate(offsetX, offsetY);
        g2.scale(skala, skala);

        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, (int) LOGICAL_W, (int) LOGICAL_H);

        gambarJudul(g2);
        gambarLegenda(g2);

        Rectangle2D.Double bingkaiLuar = new Rectangle2D.Double(22, 48, 730, 510);
        Rectangle2D.Double bingkaiDalam = new Rectangle2D.Double(28, 54, 718, 498);

        Shape klipAsli = g2.getClip();
        g2.clip(bingkaiDalam);

        AffineTransform transformAsli = g2.getTransform();
        if (kameraIkutiMobil) {
            double zoom = 1.95;
            double pusatX = bingkaiDalam.getCenterX();
            double pusatY = bingkaiDalam.getCenterY();
            g2.translate(pusatX, pusatY);
            g2.scale(zoom, zoom);
            g2.translate(-posisiMobilX, -posisiMobilY);
        }

        gambarSekatBlokKota(g2);
        gambarEmpatJalurMasukKiri(g2);
        gambarSemuaJalur(g2);
        gambarSorotanRuteAktif(g2);
        gambarSilangBiru(g2);
        gambarPapanRambuDinamis(g2);
        gambarPanahArus(g2);
        gambarLabelJalur(g2);
        gambarPerempatan(g2);
        gambarPinTujuanD(g2);
        gambarMobil(g2);

        g2.setTransform(transformAsli);
        g2.setClip(klipAsli);

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2.2f));
        g2.draw(bingkaiLuar);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(bingkaiDalam);

        g2.dispose();
    }

    private void gambarJudul(Graphics2D g2) {
        g2.setColor(new Color(20, 28, 45));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        String judul = "Pemetaan Perempatan & Jalur Malang (Final)";
        FontMetrics fm = g2.getFontMetrics();
        int x = (int) ((LOGICAL_W - fm.stringWidth(judul)) / 2.0);
        g2.drawString(judul, x, 34);
    }

    private void gambarSekatBlokKota(Graphics2D g2) {
        Color isiBlok = new Color(240, 243, 248);
        Color garisBlok = new Color(190, 198, 210);

        double[][] blok = {
            {44, 68, 108, 108},
            {198, 68, 130, 160},
            {374, 146, 136, 86},
            {556, 146, 108, 86},
            {44, 344, 108, 185},
            {198, 280, 314, 122},
            {556, 280, 170, 122},
            {198, 448, 314, 82},
            {556, 448, 170, 82}
        };

        g2.setStroke(new BasicStroke(1.2f));
        for (double[] b : blok) {
            RoundRectangle2D.Double r = new RoundRectangle2D.Double(b[0], b[1], b[2], b[3], 6, 6);
            g2.setColor(isiBlok);
            g2.fill(r);
            g2.setColor(garisBlok);
            g2.draw(r);
        }
    }

    private void gambarEmpatJalurMasukKiri(Graphics2D g2) {
        int[] posisiY = {196, 232, 272, 308};
        Color marun = new Color(153, 27, 27);
        Color marunGelap = new Color(105, 16, 16);

        for (int y : posisiY) {
            g2.setColor(new Color(220, 224, 230));
            g2.setStroke(new BasicStroke(12f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(46, y, 152, y);

            for (int x = 50; x <= 134; x += 14) {
                g2.setColor(marun);
                g2.fillRect(x, y - 5, 10, 10);
                g2.setColor(marunGelap);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(x, y - 5, 10, 10);
            }
            gambarPanahKecil(g2, 148, y, 0.0, new Color(210, 32, 32));
        }
    }

    private void gambarSemuaJalur(Graphics2D g2) {
        for (Map.Entry<String, List<Point2D.Double>> entry : jalurPolyline.entrySet()) {
            String kode = entry.getKey();
            List<Point2D.Double> pts = entry.getValue();

            KondisiJalan kondisi = KondisiJalan.LANCAR;
            try {
                kondisi = peta.cariJalur(kode).cekKondisi();
            } catch (Exception ignored) {
            }

            if (kondisi == KondisiJalan.PADAT) {
                g2.setColor(new Color(251, 146, 60, 140));
            } else if (kondisi == KondisiJalan.MACET) {
                g2.setColor(new Color(248, 113, 113, 155));
            } else {
                g2.setColor(new Color(225, 229, 235));
            }
            g2.setStroke(new BasicStroke(18f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            gambarPolyline(g2, pts);

            if (kondisi == KondisiJalan.PADAT) {
                g2.setColor(new Color(234, 88, 12));
                g2.setStroke(new BasicStroke(2.2f));
            } else if (kondisi == KondisiJalan.MACET) {
                g2.setColor(new Color(220, 38, 38));
                g2.setStroke(new BasicStroke(2.2f));
            } else {
                g2.setColor(new Color(145, 153, 166));
                g2.setStroke(new BasicStroke(1.5f));
            }
            gambarPolyline(g2, pts);

            gambarBlokKondisiJalur(g2, pts, kondisi);
        }
    }

    private void gambarBlokKondisiJalur(Graphics2D g2, List<Point2D.Double> pts, KondisiJalan kondisi) {
        double jarakBlok;
        Color warnaIsi;
        Color warnaTepi;
        int ukuran = 9;

        double denyut = 0.5 + 0.5 * Math.sin(faseDenyut);
        if (kondisi == KondisiJalan.MACET) {
            jarakBlok = 14.0;
            int merah = (int) (195 + 55 * denyut);
            int hijau = (int) (20 + 22 * denyut);
            warnaIsi = new Color(Math.min(255, merah), hijau, 28);
            warnaTepi = new Color(127, 29, 29);
        } else if (kondisi == KondisiJalan.PADAT) {
            jarakBlok = 14.0;
            int merah = (int) (220 + 35 * denyut);
            int hijau = (int) (85 + 35 * denyut);
            warnaIsi = new Color(Math.min(255, merah), hijau, 12);
            warnaTepi = new Color(154, 52, 18);
        } else if (kondisi == KondisiJalan.RAMAI) {
            jarakBlok = 22.0;
            warnaIsi = new Color(217, 119, 6);
            warnaTepi = new Color(146, 64, 14);
        } else {
            jarakBlok = 34.0;
            warnaIsi = new Color(255, 255, 255);
            warnaTepi = new Color(55, 90, 140);
        }

        for (int i = 0; i < pts.size() - 1; i++) {
            Point2D.Double p1 = pts.get(i);
            Point2D.Double p2 = pts.get(i + 1);
            double dx = p2.x - p1.x;
            double dy = p2.y - p1.y;
            double panjang = Math.hypot(dx, dy);
            double ux = dx / Math.max(1.0, panjang);
            double uy = dy / Math.max(1.0, panjang);

            for (double d = 24.0; d < panjang - 22.0; d += jarakBlok) {
                double cx = p1.x + ux * d;
                double cy = p1.y + uy * d;
                g2.setColor(warnaIsi);
                g2.fillRect((int) (cx - ukuran / 2.0), (int) (cy - ukuran / 2.0), ukuran, ukuran);
                g2.setColor(warnaTepi);
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawRect((int) (cx - ukuran / 2.0), (int) (cy - ukuran / 2.0), ukuran, ukuran);
            }
        }
    }

    private void gambarSorotanRuteAktif(Graphics2D g2) {
        if (ruteAktif == null || ruteAktif.size() < 2) {
            return;
        }
        g2.setColor(new Color(37, 99, 235, 85));
        g2.setStroke(new BasicStroke(22f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < ruteAktif.size() - 1; i++) {
            String kode = ruteAktif.get(i).getKode() + "-" + ruteAktif.get(i + 1).getKode();
            List<Point2D.Double> pts = jalurPolyline.get(kode);
            if (pts != null) {
                gambarPolyline(g2, pts);
            }
        }
    }

    private void gambarPolyline(Graphics2D g2, List<Point2D.Double> pts) {
        GeneralPath path = new GeneralPath();
        path.moveTo(pts.get(0).x, pts.get(0).y);
        for (int i = 1; i < pts.size(); i++) {
            path.lineTo(pts.get(i).x, pts.get(i).y);
        }
        g2.draw(path);
    }

    private void gambarSilangBiru(Graphics2D g2) {
        double[][] posisi = {{265, 125}, {625, 425}};
        g2.setColor(new Color(29, 78, 216));
        g2.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (double[] p : posisi) {
            int x = (int) p[0];
            int y = (int) p[1];
            g2.drawLine(x - 8, y - 8, x + 8, y + 8);
            g2.drawLine(x - 8, y + 8, x + 8, y - 8);
        }
    }

    private boolean adaJalurMacetAtauPadat() {
        for (Jalur j : peta.getSemuaJalur().values()) {
            KondisiJalan k = j.cekKondisi();
            if (k == KondisiJalan.PADAT || k == KondisiJalan.MACET) {
                return true;
            }
        }
        return false;
    }

    private void gambarPapanRambuDinamis(Graphics2D g2) {
        double[][] lokasi = {
            {144, 212},
            {320, 212},
            {504, 212},
            {490, 94}
        };
        boolean berdenyut = adaJalurMacetAtauPadat();
        double skalaDenyut = berdenyut ? (1.0 + 0.35 * Math.abs(Math.sin(faseDenyut))) : 1.0;

        for (double[] lok : lokasi) {
            double x = lok[0];
            double y = lok[1];
            if (berdenyut) {
                int alpha = (int) (70 + 130 * Math.abs(Math.sin(faseDenyut)));
                double rGlow = 10.0 * skalaDenyut;
                g2.setColor(new Color(239, 68, 68, alpha));
                g2.fill(new Ellipse2D.Double(x - rGlow, y - rGlow, rGlow * 2, rGlow * 2));
            }
            double s = 10.0 * (berdenyut ? (0.95 + 0.2 * Math.abs(Math.sin(faseDenyut))) : 1.0);
            g2.setColor(new Color(220, 38, 38));
            g2.fillRect((int) (x - s / 2.0), (int) (y - s / 2.0), (int) s, (int) s);
            g2.setColor(new Color(127, 29, 29));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRect((int) (x - s / 2.0), (int) (y - s / 2.0), (int) s, (int) s);
        }
    }

    private void gambarPanahArus(Graphics2D g2) {
        Color merahPanah = new Color(215, 30, 30);
        gambarPanahKecil(g2, 265, 255, 0.0, merahPanah);
        gambarPanahKecil(g2, 445, 255, 0.0, merahPanah);
        gambarPanahKecil(g2, 612, 255, 0.0, merahPanah);
        gambarPanahKecil(g2, 435, 125, 0.0, merahPanah);
        gambarPanahKecil(g2, 685, 185, Math.PI / 2.0, merahPanah);
        gambarPanahKecil(g2, 355, 425, 0.0, merahPanah);
    }

    private void gambarPanahKecil(Graphics2D g2, double x, double y, double sudut, Color warna) {
        AffineTransform lama = g2.getTransform();
        g2.translate(x, y);
        g2.rotate(sudut);
        Polygon panah = new Polygon(new int[]{-7, 5, 5, 12, 5, 5, -7}, new int[]{-2, -2, -6, 0, 6, 2, 2}, 7);
        g2.setColor(warna);
        g2.fill(panah);
        g2.setTransform(lama);
    }

    private void gambarLabelJalur(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(30, 41, 59));

        gambarTeksBerlatar(g2, "Jalur A-B", 224, 234);
        gambarTeksBerlatar(g2, "Jalur B-C", 406, 234);
        gambarTeksBerlatar(g2, "Jalur C-D", 574, 234);
        gambarTeksBerlatar(g2, "Jalur B-E", 392, 105);
        gambarTeksBerlatar(g2, "Jalur E-D", 568, 105);
        gambarTeksBerlatar(g2, "Jalur A-C (Alternatif)", 265, 456);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        gambarLabelBobot(g2, "A-B", 232, 286);
        gambarLabelBobot(g2, "B-C", 410, 286);
        gambarLabelBobot(g2, "C-D", 578, 286);
        gambarLabelBobot(g2, "B-E", 396, 152);
        gambarLabelBobot(g2, "E-D", 572, 152);
        gambarLabelBobot(g2, "A-C", 316, 412);
    }

    private void gambarLabelBobot(Graphics2D g2, String kode, int x, int y) {
        try {
            Jalur j = peta.cariJalur(kode);
            KondisiJalan k = j.cekKondisi();
            double normal = j.getWaktuNormal();
            double aktif = modeTercepat ? normal * k.getPengaliMacet() : normal;
            String teks;
            if (modeTercepat && k.getPengaliMacet() > 1.0) {
                teks = String.format("%s: %.0f -> %.1f", kode, normal, aktif);
            } else {
                teks = String.format("%s: %.0f", kode, aktif);
            }

            Color warnaTeks;
            if (k == KondisiJalan.MACET) {
                warnaTeks = new Color(185, 28, 28);
            } else if (k == KondisiJalan.PADAT) {
                warnaTeks = new Color(194, 65, 12);
            } else if (k == KondisiJalan.RAMAI) {
                warnaTeks = new Color(161, 98, 7);
            } else {
                warnaTeks = new Color(30, 64, 175);
            }

            FontMetrics fm = g2.getFontMetrics();
            int w = fm.stringWidth(teks) + 14;
            int h = fm.getHeight() + 4;
            g2.setColor(new Color(255, 255, 255, 240));
            g2.fillRoundRect(x - 7, y - h + 5, w, h, 6, 6);
            g2.setColor(warnaTeks);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(x - 7, y - h + 5, w, h, 6, 6);
            g2.drawString(teks, x, y);
        } catch (Exception ignored) {
        }
    }

    private void gambarTeksBerlatar(Graphics2D g2, String teks, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(teks) + 8;
        int h = fm.getHeight();
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(x - 4, y - h + 3, w, h, 4, 4);
        g2.setColor(new Color(24, 34, 52));
        g2.drawString(teks, x, y);
    }

    private void gambarPerempatan(Graphics2D g2) {
        Color biruDongker = new Color(17, 42, 85);
        g2.setFont(new Font("SansSerif", Font.BOLD, 15));

        for (Map.Entry<String, Point2D.Double> entry : koordinatPerempatan.entrySet()) {
            String kode = entry.getKey();
            Point2D.Double p = entry.getValue();
            double r = 17.0;

            g2.setColor(biruDongker);
            g2.fill(new Ellipse2D.Double(p.x - r, p.y - r, r * 2, r * 2));
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.0f));
            g2.draw(new Ellipse2D.Double(p.x - r, p.y - r, r * 2, r * 2));

            FontMetrics fm = g2.getFontMetrics();
            int tx = (int) (p.x - fm.stringWidth(kode) / 2.0);
            int ty = (int) (p.y + fm.getAscent() / 2.0 - 2);
            g2.drawString(kode, tx, ty);
        }
    }

    private void gambarPinTujuanD(Graphics2D g2) {
        Point2D.Double d = koordinatPerempatan.get("D");
        if (d == null) {
            return;
        }
        double px = d.x + 18;
        double py = d.y - 24;

        GeneralPath pin = new GeneralPath();
        pin.moveTo(px, py + 16);
        pin.curveTo(px - 12, py + 2, px - 12, py - 12, px, py - 12);
        pin.curveTo(px + 12, py - 12, px + 12, py + 2, px, py + 16);
        pin.closePath();

        g2.setColor(new Color(220, 38, 38));
        g2.fill(pin);
        g2.setColor(new Color(127, 29, 29));
        g2.setStroke(new BasicStroke(1.3f));
        g2.draw(pin);

        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(px - 3.5, py - 5.5, 7, 7));
    }

    private void gambarMobil(Graphics2D g2) {
        AffineTransform lama = g2.getTransform();
        g2.translate(posisiMobilX, posisiMobilY);
        g2.rotate(sudutMobilRad);

        Polygon sorotKiri = new Polygon(new int[]{12, 58, 58}, new int[]{-5, -20, -2}, 3);
        Polygon sorotKanan = new Polygon(new int[]{12, 58, 58}, new int[]{5, 2, 20}, 3);
        GradientPaint gp = new GradientPaint(
            12, 0, new Color(254, 240, 138, 175),
            58, 0, new Color(254, 240, 138, 0)
        );
        g2.setPaint(gp);
        g2.fill(sorotKiri);
        g2.fill(sorotKanan);

        g2.setColor(new Color(30, 41, 59));
        g2.fillRect(-11, -10, 6, 3);
        g2.fillRect(5, -10, 6, 3);
        g2.fillRect(-11, 7, 6, 3);
        g2.fillRect(5, 7, 6, 3);

        RoundRectangle2D.Double badan = new RoundRectangle2D.Double(-14, -8, 28, 16, 6, 6);
        g2.setColor(new Color(37, 99, 235));
        g2.fill(badan);
        g2.setColor(new Color(15, 23, 42));
        g2.setStroke(new BasicStroke(1.4f));
        g2.draw(badan);

        g2.setColor(new Color(191, 219, 254));
        g2.fillRoundRect(1, -6, 7, 12, 3, 3);
        g2.fillRoundRect(-9, -6, 5, 12, 2, 2);

        g2.setColor(new Color(254, 240, 138));
        g2.fillRect(12, -6, 2, 3);
        g2.fillRect(12, 3, 2, 3);

        g2.setTransform(lama);
    }

    private void gambarLegenda(Graphics2D g2) {
        RoundRectangle2D.Double kotak = new RoundRectangle2D.Double(766, 48, 286, 510, 10, 10);
        g2.setColor(new Color(240, 242, 245));
        g2.fill(kotak);
        g2.setColor(new Color(130, 136, 148));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(kotak);

        g2.setColor(new Color(24, 34, 52));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 19));
        g2.drawString("Keterangan Pemetaan", 786, 82);
        g2.drawString("Peta Kota Malang", 786, 106);

        g2.setColor(new Color(185, 192, 204));
        g2.drawLine(784, 120, 1034, 120);

        int xSimbol = 804;
        int xTeks = 834;
        int y = 158;
        int spasi = 62;

        g2.setColor(new Color(17, 42, 85));
        g2.fill(new Ellipse2D.Double(xSimbol - 12, y - 12, 24, 24));
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.drawString("A", xSimbol - 4, y + 4);
        g2.setColor(new Color(30, 41, 59));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString("Titik Perempatan (A-E)", xTeks, y + 5);

        y += spasi;
        GeneralPath pin = new GeneralPath();
        pin.moveTo(xSimbol, y + 10);
        pin.curveTo(xSimbol - 9, y, xSimbol - 9, y - 10, xSimbol, y - 10);
        pin.curveTo(xSimbol + 9, y - 10, xSimbol + 9, y, xSimbol, y + 10);
        pin.closePath();
        g2.setColor(new Color(220, 38, 38));
        g2.fill(pin);
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(xSimbol - 2.5, y - 5, 5, 5));
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Titik Tujuan (D)", xTeks, y + 5);

        y += spasi;
        for (int i = -12; i <= 4; i += 8) {
            g2.setColor(new Color(153, 27, 27));
            g2.fillRect(xSimbol + i, y - 4, 7, 8);
        }
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Jalur Padat / Macet", xTeks, y + 5);

        y += spasi;
        for (int i = -12; i <= 6; i += 14) {
            g2.setColor(Color.WHITE);
            g2.fillRect(xSimbol + i, y - 4, 8, 8);
            g2.setColor(new Color(55, 90, 140));
            g2.drawRect(xSimbol + i, y - 4, 8, 8);
        }
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Jalur Lancar", xTeks, y + 5);

        y += spasi;
        g2.setColor(new Color(29, 78, 216));
        g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(xSimbol - 7, y - 7, xSimbol + 7, y + 7);
        g2.drawLine(xSimbol - 7, y + 7, xSimbol + 7, y - 7);
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Akses Tertutup (X)", xTeks, y + 5);

        y += spasi;
        g2.setColor(new Color(220, 38, 38));
        g2.fillRect(xSimbol - 6, y - 6, 12, 12);
        g2.setColor(new Color(127, 29, 29));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRect(xSimbol - 6, y - 6, 12, 12);
        g2.setColor(new Color(30, 41, 59));
        g2.drawString("Papan Rambu Dinamis", xTeks, y + 5);
    }
}
