package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import model.Jalur;
import model.KondisiJalan;
import model.Perempatan;
import model.PetaKota;

public class PanelInfoBobot extends JPanel {
    private static final String[] URUTAN_JALUR = {"A-B", "B-C", "C-D", "B-E", "E-D", "A-C"};

    private final DefaultTableModel modelTabel;
    private final JLabel lblRuteUtama;
    private final JLabel lblRuteBawah;
    private final JLabel lblRuteAtas;
    private final JLabel lblAlasanDijkstra;

    public PanelInfoBobot() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 250, 252));

        JPanel konten = new JPanel();
        konten.setLayout(new BoxLayout(konten, BoxLayout.Y_AXIS));
        konten.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        konten.setBackground(new Color(248, 250, 252));

        JPanel pnlAmbang = buatPanelAmbangBatas();

        String[] kolom = {"Jalur", "Kendaraan", "Status", "Pengali", "Bobot Aktif"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(modelTabel);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tabel.setRowHeight(28);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        JScrollPane spTabel = new JScrollPane(tabel);
        spTabel.setBorder(buatJudulBorder("Bobot Real-Time Semua Jalur"));
        spTabel.setPreferredSize(new Dimension(420, 212));

        JPanel pnlRute = new JPanel();
        pnlRute.setLayout(new BoxLayout(pnlRute, BoxLayout.Y_AXIS));
        pnlRute.setBorder(buatJudulBorder("Perbandingan 3 Rute Kandidat (A -> D)"));
        pnlRute.setBackground(Color.WHITE);

        lblRuteUtama = buatLabelRute();
        lblRuteBawah = buatLabelRute();
        lblRuteAtas = buatLabelRute();
        lblAlasanDijkstra = new JLabel("Mode: Jalur Tercepat");
        lblAlasanDijkstra.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblAlasanDijkstra.setForeground(new Color(51, 65, 85));
        lblAlasanDijkstra.setBorder(BorderFactory.createEmptyBorder(6, 6, 4, 6));

        pnlRute.add(lblRuteUtama);
        pnlRute.add(lblRuteBawah);
        pnlRute.add(lblRuteAtas);
        pnlRute.add(lblAlasanDijkstra);

        konten.add(pnlAmbang);
        konten.add(spTabel);
        konten.add(pnlRute);

        JScrollPane spUtama = new JScrollPane(konten);
        spUtama.setBorder(null);
        spUtama.getVerticalScrollBar().setUnitIncrement(16);
        add(spUtama, BorderLayout.CENTER);
    }

    private TitledBorder buatJudulBorder(String judul) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            judul,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(15, 23, 42)
        );
    }

    private JPanel buatPanelAmbangBatas() {
        JPanel pnl = new JPanel(new GridLayout(4, 1, 4, 4));
        pnl.setBorder(buatJudulBorder("Panduan Ambang Batas (Kapasitas 100)"));
        pnl.setBackground(Color.WHITE);

        pnl.add(buatBadge("LANCAR", new Color(22, 163, 74), "0-49 mobil (<50%, 1.0x)"));
        pnl.add(buatBadge("RAMAI", new Color(202, 138, 4), "50-74 mobil (50-74%, 1.5x, tetap jika lebih cepat)"));
        pnl.add(buatBadge("PADAT", new Color(234, 88, 12), "75-99 mobil (75-99%, 2.0x, mulai alihkan rute)"));
        pnl.add(buatBadge("MACET", new Color(220, 38, 38), ">=100 mobil (>=100%, 3.0x, wajib alihkan rute)"));
        return pnl;
    }

    private JPanel buatBadge(String judul, Color warna, String keterangan) {
        JPanel baris = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        baris.setBackground(Color.WHITE);

        JLabel badge = new JLabel(judul, SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(warna);
        badge.setForeground(Color.WHITE);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        badge.setPreferredSize(new Dimension(78, 22));

        JLabel ket = new JLabel(keterangan);
        ket.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ket.setForeground(new Color(30, 41, 59));

        baris.add(badge);
        baris.add(ket);
        return baris;
    }

    private JLabel buatLabelRute() {
        JLabel lbl = new JLabel();
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        return lbl;
    }

    public void perbaruiData(PetaKota peta, boolean modeTercepat, LinkedList<Perempatan> ruteTerpilih) {
        modelTabel.setRowCount(0);
        for (String kode : URUTAN_JALUR) {
            try {
                Jalur j = peta.cariJalur(kode);
                KondisiJalan k = j.cekKondisi();
                double bobot = modeTercepat ? j.getWaktuNormal() * k.getPengaliMacet() : j.getWaktuNormal();
                modelTabel.addRow(new Object[]{
                    kode,
                    j.getJumlahKendaraan() + "/" + j.getKapasitasMaksimal(),
                    k.name(),
                    k.getPengaliMacet() + "x",
                    String.format("%.1f", bobot)
                });
            } catch (Exception ignored) {
            }
        }

        double bobotUtama = hitungTotalRute(peta, modeTercepat, "A-B", "B-C", "C-D");
        double bobotBawah = hitungTotalRute(peta, modeTercepat, "A-B", "B-E", "E-D");
        double bobotAtas = hitungTotalRute(peta, modeTercepat, "A-C", "C-D");

        String ruteTeks = formatRute(ruteTerpilih);
        aturLabelKandidat(lblRuteUtama, "Utama (A -> B -> C -> D)", bobotUtama, "A -> B -> C -> D".equals(ruteTeks));
        aturLabelKandidat(lblRuteBawah, "Alt. Bawah (A -> B -> E -> D)", bobotBawah, "A -> B -> E -> D".equals(ruteTeks));
        aturLabelKandidat(lblRuteAtas, "Alt. Atas (A -> C -> D)", bobotAtas, "A -> C -> D".equals(ruteTeks));

        String mode = modeTercepat ? "Jalur Tercepat (Waktu x Pengali)" : "Jalur Terpendek (Waktu Normal)";
        lblAlasanDijkstra.setText("Dijkstra [" + mode + "] memilih bobot terkecil.");
    }

    private double hitungTotalRute(PetaKota peta, boolean modeTercepat, String... kodeJalur) {
        double total = 0.0;
        for (String kode : kodeJalur) {
            try {
                Jalur j = peta.cariJalur(kode);
                total += modeTercepat ? j.getWaktuNormal() * j.cekKondisi().getPengaliMacet() : j.getWaktuNormal();
            } catch (Exception ignored) {
            }
        }
        return total;
    }

    private void aturLabelKandidat(JLabel lbl, String namaRute, double total, boolean terpilih) {
        String teks = String.format("%s = %.1f%s", namaRute, total, terpilih ? "  [TERPILIH]" : "");
        lbl.setText(teks);
        if (terpilih) {
            lbl.setBackground(new Color(219, 234, 254));
            lbl.setForeground(new Color(30, 58, 138));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        } else {
            lbl.setBackground(Color.WHITE);
            lbl.setForeground(new Color(51, 65, 85));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        }
    }

    private String formatRute(LinkedList<Perempatan> rute) {
        if (rute == null || rute.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rute.size(); i++) {
            sb.append(rute.get(i).getKode());
            if (i < rute.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
