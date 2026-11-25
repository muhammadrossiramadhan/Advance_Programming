package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.Perempatan;
import model.PesanRambu;
import model.RiwayatPesan;

/**
 * Panel terintegrasi untuk menampilkan catatan riwayat perubahan status rambu
 * yang dikelola oleh model.RiwayatPesan (LinkedList<PesanRambu>).
 */
public class PanelRiwayatPesan extends JPanel {
    private final DefaultTableModel modelTabel;
    private final JLabel lblRingkasan;
    private final JButton btnBersihkan;

    public PanelRiwayatPesan() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(248, 250, 252));

        JPanel pnlHeader = new JPanel(new BorderLayout(8, 8));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        lblRingkasan = new JLabel("Total Catatan Riwayat (LinkedList): 0");
        lblRingkasan.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRingkasan.setForeground(new Color(15, 23, 42));

        JPanel pnlAksi = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlAksi.setOpaque(false);
        btnBersihkan = new JButton("Bersihkan Riwayat");
        btnBersihkan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlAksi.add(btnBersihkan);

        pnlHeader.add(lblRingkasan, BorderLayout.WEST);
        pnlHeader.add(pnlAksi, BorderLayout.EAST);

        String[] kolom = {"No", "Waktu", "Status", "Mode", "Jalur Dipantau", "Rute Terpilih", "Rekomendasi Tindakan"};
        modelTabel = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabel = new JTable(modelTabel);
        tabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabel.setRowHeight(30);
        tabel.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabel.getColumnModel().getColumn(0).setMaxWidth(52);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(85);
        tabel.getColumnModel().getColumn(1).setMaxWidth(105);
        tabel.getColumnModel().getColumn(2).setPreferredWidth(95);
        tabel.getColumnModel().getColumn(2).setMaxWidth(115);
        tabel.getColumnModel().getColumn(3).setPreferredWidth(105);
        tabel.getColumnModel().getColumn(3).setMaxWidth(130);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(110);
        tabel.getColumnModel().getColumn(4).setMaxWidth(130);
        tabel.getColumnModel().getColumn(5).setPreferredWidth(175);
        tabel.getColumnModel().getColumn(6).setPreferredWidth(420);

        tabel.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column
                );
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                if (!isSelected && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "LANCAR":
                            lbl.setBackground(new Color(220, 252, 231));
                            lbl.setForeground(new Color(22, 101, 52));
                            break;
                        case "RAMAI":
                            lbl.setBackground(new Color(254, 249, 195));
                            lbl.setForeground(new Color(133, 77, 14));
                            break;
                        case "PADAT":
                            lbl.setBackground(new Color(255, 237, 213));
                            lbl.setForeground(new Color(154, 52, 18));
                            break;
                        case "MACET":
                            lbl.setBackground(new Color(254, 226, 226));
                            lbl.setForeground(new Color(153, 27, 27));
                            break;
                        default:
                            lbl.setBackground(Color.WHITE);
                            lbl.setForeground(new Color(15, 23, 42));
                            break;
                    }
                }
                return lbl;
            }
        });

        JScrollPane spTabel = new JScrollPane(tabel);
        spTabel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            "Log Kronologis Pesan Rambu Dinamis (Terbaru di Atas)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(15, 23, 42)
        ));

        add(pnlHeader, BorderLayout.NORTH);
        add(spTabel, BorderLayout.CENTER);
    }

    public JButton getBtnBersihkan() {
        return btnBersihkan;
    }

    public void perbaruiTampilan(RiwayatPesan riwayat) {
        modelTabel.setRowCount(0);
        if (riwayat == null) {
            lblRingkasan.setText("Total Catatan Riwayat (LinkedList): 0");
            return;
        }

        LinkedList<PesanRambu> daftar = riwayat.getDaftarRiwayat();
        lblRingkasan.setText("Total Catatan Riwayat (LinkedList): " + daftar.size());

        for (int i = 0; i < daftar.size(); i++) {
            PesanRambu pesan = daftar.get(i);
            modelTabel.addRow(new Object[]{
                i + 1,
                pesan.getWaktuDibuatFormatted(),
                pesan.getTingkatUrgensi(),
                pesan.getModeAlgoritma(),
                pesan.getKodeJalur(),
                formatRute(pesan.getRute()),
                pesan.getRekomendasiTindakan()
            });
        }
    }

    private String formatRute(LinkedList<Perempatan> rute) {
        if (rute == null || rute.isEmpty()) {
            return "-";
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
