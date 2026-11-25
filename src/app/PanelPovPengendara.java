package app;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;
import model.KondisiJalan;

public class PanelPovPengendara extends JPanel {
    private double offsetMarka = 0.0;
    private int kecepatanKmh = 62;
    private String jalurAktif = "A-B";
    private String arahBerikutnya = "B (Jl. Braga)";
    private KondisiJalan kondisiJalan = KondisiJalan.LANCAR;
    private String pesanRambu = "KONDISI LANCAR";
    private Color warnaRambu = new Color(34, 139, 64);
    private double sudutSetir = 0.0;

    public PanelPovPengendara() {
        setBackground(new Color(15, 23, 42));
        setMinimumSize(new Dimension(340, 260));
        setPreferredSize(new Dimension(440, 310));
    }

    public void perbaruiStatus(
            String jalurAktif,
            KondisiJalan kondisiJalan,
            int kecepatanKmh,
            String arahBerikutnya,
            String pesanRambu,
            Color warnaRambu,
            double sudutMobilRad,
            double dtDetik) {
        this.jalurAktif = jalurAktif;
        this.kondisiJalan = kondisiJalan;
        this.kecepatanKmh = kecepatanKmh;
        this.arahBerikutnya = arahBerikutnya;
        this.pesanRambu = pesanRambu;
        this.warnaRambu = warnaRambu;
        this.sudutSetir = Math.sin(sudutMobilRad) * 0.25;

        double lajuMarka;
        switch (kondisiJalan) {
            case LANCAR:
                lajuMarka = 1.65;
                break;
            case RAMAI:
                lajuMarka = 0.95;
                break;
            case PADAT:
                lajuMarka = 0.42;
                break;
            case MACET:
            default:
                lajuMarka = 0.14;
                break;
        }
        if (kecepatanKmh == 0) {
            lajuMarka = 0.0;
        }
        offsetMarka = (offsetMarka + lajuMarka * dtDetik) % 1.0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        int w = getWidth();
        int h = getHeight();
        int dashY = Math.min((int) (h * 0.67), Math.max(130, h - 108));
        int horizonY = Math.max(82, (int) (dashY * 0.56));
        int tengahX = w / 2;

        GradientPaint langit = new GradientPaint(
            0, 0, new Color(15, 23, 42),
            0, horizonY, new Color(51, 65, 85)
        );
        g2.setPaint(langit);
        g2.fillRect(0, 0, w, horizonY);

        g2.setColor(new Color(22, 101, 52));
        g2.fillRect(0, horizonY, w, dashY - horizonY);

        int lebarAtas = Math.max(44, (int) (w * 0.14));
        int lebarBawah = (int) (w * 0.90);

        Polygon jalan = new Polygon(
            new int[]{tengahX - lebarAtas / 2, tengahX + lebarAtas / 2, tengahX + lebarBawah / 2, tengahX - lebarBawah / 2},
            new int[]{horizonY, horizonY, dashY, dashY},
            4
        );
        g2.setColor(new Color(45, 52, 64));
        g2.fill(jalan);

        g2.setColor(new Color(226, 232, 240));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawLine(tengahX - lebarAtas / 2, horizonY, tengahX - lebarBawah / 2, dashY);
        g2.drawLine(tengahX + lebarAtas / 2, horizonY, tengahX + lebarBawah / 2, dashY);

        gambarMarkaKuning(g2, tengahX, horizonY, dashY);
        gambarKendaraanDiDepan(g2, tengahX, horizonY, dashY);
        gambarGantryRambu(g2, w, horizonY);
        gambarDashboardDanSetir(g2, w, h, dashY);
        gambarHud(g2, w, h, dashY);

        g2.dispose();
    }

    private void gambarMarkaKuning(Graphics2D g2, int tengahX, int horizonY, int dashY) {
        g2.setColor(new Color(250, 204, 21));
        int jumlahSegmen = 5;
        for (int i = 0; i < jumlahSegmen; i++) {
            double t1 = ((double) i / jumlahSegmen + offsetMarka / jumlahSegmen) % 1.0;
            double t2 = Math.min(1.0, t1 + 0.09);
            double p1 = t1 * t1;
            double p2 = t2 * t2;
            int y1 = (int) (horizonY + p1 * (dashY - horizonY));
            int y2 = (int) (horizonY + p2 * (dashY - horizonY));
            if (y2 <= y1) {
                continue;
            }
            int w1 = Math.max(2, (int) (2 + p1 * 10));
            int w2 = Math.max(3, (int) (2 + p2 * 12));
            Polygon marka = new Polygon(
                new int[]{tengahX - w1 / 2, tengahX + w1 / 2, tengahX + w2 / 2, tengahX - w2 / 2},
                new int[]{y1, y1, y2, y2},
                4
            );
            g2.fill(marka);
        }
    }

    private void gambarKendaraanDiDepan(Graphics2D g2, int tengahX, int horizonY, int dashY) {
        if (kondisiJalan == KondisiJalan.LANCAR) {
            return;
        }
        int jumlahMobil = kondisiJalan == KondisiJalan.RAMAI ? 1 : (kondisiJalan == KondisiJalan.PADAT ? 2 : 3);
        double[] faktorJarak = {0.32, 0.54, 0.78};
        int[] geserX = {-18, 22, -6};

        for (int i = 0; i < jumlahMobil; i++) {
            double p = faktorJarak[i];
            int y = (int) (horizonY + p * (dashY - horizonY));
            int mw = (int) (28 + p * 44);
            int mh = (int) (18 + p * 24);
            int x = tengahX + (int) (geserX[i] * p) - mw / 2;

            g2.setColor(i == 0 ? new Color(185, 28, 28) : (i == 1 ? new Color(217, 119, 6) : new Color(71, 85, 105)));
            g2.fillRoundRect(x, y - mh, mw, mh, 6, 6);
            g2.setColor(new Color(15, 23, 42));
            g2.fillRoundRect(x + 4, y - mh + 3, mw - 8, mh / 3, 4, 4);

            g2.setColor(new Color(239, 68, 68));
            g2.fillRect(x + 3, y - 6, 6, 3);
            g2.fillRect(x + mw - 9, y - 6, 6, 3);
        }
    }

    private void gambarGantryRambu(Graphics2D g2, int w, int horizonY) {
        int tiangKiri = (int) (w * 0.06);
        int tiangKanan = (int) (w * 0.94);
        int papanY = Math.max(10, (int) (horizonY * 0.12));
        int papanW = tiangKanan - tiangKiri - 12;
        int papanH = 66;

        g2.setColor(new Color(148, 163, 184));
        g2.setStroke(new BasicStroke(4f));
        g2.drawLine(tiangKiri, papanY + 14, tiangKiri, horizonY + 18);
        g2.drawLine(tiangKanan, papanY + 14, tiangKanan, horizonY + 18);
        g2.drawLine(tiangKiri, papanY + 14, tiangKanan, papanY + 14);

        RoundRectangle2D.Double papan = new RoundRectangle2D.Double(tiangKiri + 6, papanY, papanW, papanH, 8, 8);
        g2.setColor(new Color(15, 23, 42));
        g2.fill(papan);
        g2.setColor(warnaRambu);
        g2.setStroke(new BasicStroke(2.5f));
        g2.draw(papan);

        g2.setColor(warnaRambu);
        g2.fillRoundRect(tiangKiri + 12, papanY + 6, papanW - 12, papanH - 12, 5, 5);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.BOLD, 17));
        FontMetrics fm = g2.getFontMetrics();
        String tampil = pesanRambu;
        while (fm.stringWidth(tampil) > papanW - 22 && tampil.length() > 8) {
            tampil = tampil.substring(0, tampil.length() - 1);
        }
        int tx = tiangKiri + 6 + (papanW - fm.stringWidth(tampil)) / 2;
        g2.drawString(tampil, tx, papanY + 29);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        String sub = "Jalur " + jalurAktif + " | Status: " + kondisiJalan;
        FontMetrics fm2 = g2.getFontMetrics();
        int sx = tiangKiri + 6 + (papanW - fm2.stringWidth(sub)) / 2;
        g2.drawString(sub, sx, papanY + 51);
    }

    private void gambarDashboardDanSetir(Graphics2D g2, int w, int h, int dashY) {
        g2.setColor(new Color(17, 24, 39));
        g2.fillRect(0, dashY, w, h - dashY);
        g2.setColor(new Color(55, 65, 81));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(0, dashY, w, dashY);

        int tinggiDash = h - dashY;
        int radius = Math.min(40, Math.max(26, (int) (tinggiDash * 0.36)));
        int cx = w / 2 - 20;
        int cy = dashY + tinggiDash / 2 + 2;

        AffineTransform lama = g2.getTransform();
        g2.translate(cx, cy);
        g2.rotate(sudutSetir);

        g2.setColor(new Color(31, 41, 55));
        g2.setStroke(new BasicStroke(9f));
        g2.drawOval(-radius, -radius, radius * 2, radius * 2);

        g2.setColor(new Color(75, 85, 99));
        g2.setStroke(new BasicStroke(6f));
        g2.drawLine(-radius + 5, 0, radius - 5, 0);
        g2.drawLine(0, 0, 0, radius - 5);

        g2.setColor(new Color(15, 23, 42));
        g2.fillOval(-12, -12, 24, 24);
        g2.setColor(new Color(59, 130, 246));
        g2.drawOval(-12, -12, 24, 24);

        g2.setTransform(lama);
    }

    private void gambarHud(Graphics2D g2, int w, int h, int dashY) {
        int tinggiDash = h - dashY;
        int spR = Math.min(42, Math.max(30, (int) (tinggiDash * 0.40)));
        int spX = 16;
        int spY = dashY + Math.max(6, (tinggiDash - spR * 2) / 2);

        g2.setColor(new Color(30, 41, 59));
        g2.fillOval(spX, spY, spR * 2, spR * 2);
        g2.setColor(new Color(56, 189, 248));
        g2.setStroke(new BasicStroke(3.5f));
        double rasioKecepatan = Math.min(1.0, kecepatanKmh / 80.0);
        g2.draw(new Arc2D.Double(spX + 3, spY + 3, spR * 2 - 6, spR * 2 - 6, 210, -240 * rasioKecepatan, Arc2D.OPEN));

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Consolas", Font.BOLD, 22));
        String angka = String.valueOf(kecepatanKmh);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(angka, spX + spR - fm.stringWidth(angka) / 2, spY + spR + 4);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(new Color(148, 163, 184));
        FontMetrics fmUnit = g2.getFontMetrics();
        g2.drawString("km/h", spX + spR - fmUnit.stringWidth("km/h") / 2, spY + spR + 21);

        int infoX = w / 2 + 34;
        if (infoX < w - 120) {
            int teksY = dashY + Math.max(24, tinggiDash / 2 - 18);
            g2.setColor(new Color(226, 232, 240));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
            g2.drawString("Jalur: " + jalurAktif, infoX, teksY);
            g2.setColor(new Color(148, 163, 184));
            g2.drawString("Arah:", infoX, teksY + 22);
            g2.setColor(new Color(250, 204, 21));
            g2.drawString(arahBerikutnya, infoX, teksY + 42);
        }
    }
}
