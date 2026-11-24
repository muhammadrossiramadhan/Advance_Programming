package app;

import algoritma.PemilihJalur;
import algoritma.PemilihJalurTercepat;
import algoritma.PemilihJalurTerpendek;
import exception.DataTidakValidException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import model.Jalur;
import model.KondisiJalan;
import model.Perempatan;
import model.PetaKota;

public class SimulasiRambuKota extends JFrame {
    private final PetaKota peta;
    private PemilihJalur pencariJalur;
    private final LinkedList<String> riwayatPesan;

    private final JComboBox<String> cbJalur;
    private final JTextField tfJumlahKendaraan;
    private final JRadioButton rbTercepat;
    private final JRadioButton rbTerpendek;

    private final JLabel lblStatusJalur;
    private final JLabel lblTeksRambu;
    private final JTextArea taRiwayat;

    public SimulasiRambuKota() {
        peta = new PetaKota();
        pencariJalur = new PemilihJalurTercepat();
        riwayatPesan = new LinkedList<>();

        inisialisasiDataPeta();

        setTitle("Sistem Rambu Informasi Dinamis - Kota Malang");
        setSize(820, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlAtas = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlAtas.setBorder(BorderFactory.createTitledBorder("Panel Kontrol Operator"));

        JPanel pnlInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlInput.add(new JLabel("Pilih Jalur:"));
        cbJalur = new JComboBox<>(new String[]{"A-B", "B-C", "C-D", "B-E", "E-D"});
        cbJalur.setSelectedItem("B-C");
        pnlInput.add(cbJalur);

        pnlInput.add(new JLabel("Jumlah Kendaraan:"));
        tfJumlahKendaraan = new JTextField("120", 6);
        pnlInput.add(tfJumlahKendaraan);

        JButton btnUpdate = new JButton("Update Kepadatan");
        pnlInput.add(btnUpdate);

        JPanel pnlMode = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlMode.add(new JLabel("Mode Algoritma:"));
        rbTercepat = new JRadioButton("Jalur Tercepat (Menghindari Macet)", true);
        rbTerpendek = new JRadioButton("Jalur Terpendek (Waktu Normal)");
        ButtonGroup bgMode = new ButtonGroup();
        bgMode.add(rbTercepat);
        bgMode.add(rbTerpendek);
        pnlMode.add(rbTercepat);
        pnlMode.add(rbTerpendek);

        pnlAtas.add(pnlInput);
        pnlAtas.add(pnlMode);

        JPanel pnlTengah = new JPanel(new BorderLayout(5, 5));
        pnlTengah.setBorder(BorderFactory.createTitledBorder("Tampilan Papan Rambu Elektronik"));

        lblStatusJalur = new JLabel("Status Jalur Saat Ini: BELUM DIUPDATE", SwingConstants.CENTER);
        lblStatusJalur.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblTeksRambu = new JLabel("SILAKAN MASUKKAN DATA KEPADATAN", SwingConstants.CENTER);
        lblTeksRambu.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblTeksRambu.setOpaque(true);
        lblTeksRambu.setBackground(Color.DARK_GRAY);
        lblTeksRambu.setForeground(Color.WHITE);
        lblTeksRambu.setPreferredSize(new Dimension(0, 110));

        pnlTengah.add(lblStatusJalur, BorderLayout.NORTH);
        pnlTengah.add(lblTeksRambu, BorderLayout.CENTER);

        JPanel pnlBawah = new JPanel(new BorderLayout());
        pnlBawah.setBorder(BorderFactory.createTitledBorder("Riwayat Perubahan Status Rambu (LinkedList)"));
        taRiwayat = new JTextArea(9, 0);
        taRiwayat.setEditable(false);
        taRiwayat.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pnlBawah.add(new JScrollPane(taRiwayat), BorderLayout.CENTER);

        add(pnlAtas, BorderLayout.NORTH);
        add(pnlTengah, BorderLayout.CENTER);
        add(pnlBawah, BorderLayout.SOUTH);

        btnUpdate.addActionListener(e -> eksekusiSimulasi());
        rbTercepat.addActionListener(e -> {
            pencariJalur = new PemilihJalurTercepat();
            perbaruiTampilanRuteSaja();
        });
        rbTerpendek.addActionListener(e -> {
            pencariJalur = new PemilihJalurTerpendek();
            perbaruiTampilanRuteSaja();
        });

        perbaruiTampilanRuteSaja();
    }

    private void inisialisasiDataPeta() {
        try {
            Perempatan a = new Perempatan("A", "Jl. Merdeka");
            Perempatan b = new Perempatan("B", "Jl. Braga");
            Perempatan c = new Perempatan("C", "Jl. Asia Afrika");
            Perempatan d = new Perempatan("D", "Jl. Dago");
            Perempatan e = new Perempatan("E", "Jl. Riau");

            peta.tambahPerempatan(a);
            peta.tambahPerempatan(b);
            peta.tambahPerempatan(c);
            peta.tambahPerempatan(d);
            peta.tambahPerempatan(e);

            peta.tambahJalur(new Jalur("A-B", a, b, 3, 100));
            peta.tambahJalur(new Jalur("B-C", b, c, 4, 100));
            peta.tambahJalur(new Jalur("C-D", c, d, 2, 100));
            peta.tambahJalur(new Jalur("B-E", b, e, 5, 100));
            peta.tambahJalur(new Jalur("E-D", e, d, 4, 100));
        } catch (DataTidakValidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Inisialisasi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eksekusiSimulasi() {
        try {
            String kodeJalur = (String) cbJalur.getSelectedItem();
            int jumlah = Integer.parseInt(tfJumlahKendaraan.getText().trim());

            Jalur jalurDipilih = peta.cariJalur(kodeJalur);
            jalurDipilih.setJumlahKendaraan(jumlah);

            KondisiJalan kondisi = jalurDipilih.cekKondisi();
            lblStatusJalur.setText(
                "Status Jalur " + kodeJalur + ": " + kondisi
                + " (" + jalurDipilih.getJumlahKendaraan() + "/" + jalurDipilih.getKapasitasMaksimal()
                + " kendaraan | Pengali: " + kondisi.getPengaliMacet() + "x)"
            );

            LinkedList<Perempatan> rute = pencariJalur.cariRute(peta, "A", "D");
            perbaruiVisualRambu(kondisi, kodeJalur, rute);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Masukkan angka jumlah kendaraan yang valid!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (DataTidakValidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Data", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void perbaruiTampilanRuteSaja() {
        try {
            LinkedList<Perempatan> rute = pencariJalur.cariRute(peta, "A", "D");
            String mode = rbTercepat.isSelected() ? "TERCEPAT" : "TERPENDEK";
            lblTeksRambu.setText("<html><center>MODE " + mode + "<br>RUTE TERPILIH: " + formatRute(rute) + "</center></html>");
            lblTeksRambu.setBackground(Color.DARK_GRAY);
            lblTeksRambu.setForeground(Color.WHITE);
        } catch (DataTidakValidException ex) {
            lblTeksRambu.setText("RUTE TIDAK TERSEDIA");
        }
    }

    private void perbaruiVisualRambu(KondisiJalan kondisi, String kodeJalur, LinkedList<Perempatan> rute) {
        String ruteTeks = formatRute(rute);
        String mode = rbTercepat.isSelected() ? "TERCEPAT" : "TERPENDEK";
        String teksTampil;
        Color warnaBackground;

        switch (kondisi) {
            case LANCAR:
                teksTampil = "<html><center>KONDISI LANCAR (" + mode + ")<br>RUTE: " + ruteTeks + "</center></html>";
                warnaBackground = new Color(46, 125, 50);
                break;
            case RAMAI:
                teksTampil = "<html><center>KONDISI RAMAI (" + mode + ")<br>RUTE: " + ruteTeks + "</center></html>";
                warnaBackground = new Color(202, 138, 4);
                break;
            case PADAT:
                teksTampil = "<html><center>JALUR " + kodeJalur + " PADAT (" + mode + ")<br>GUNAKAN ALTERNATIF: " + ruteTeks + "</center></html>";
                warnaBackground = new Color(230, 81, 0);
                break;
            case MACET:
            default:
                teksTampil = "<html><center>JALUR " + kodeJalur + " MACET! (" + mode + ")<br>GUNAKAN ALTERNATIF: " + ruteTeks + "</center></html>";
                warnaBackground = new Color(198, 40, 40);
                break;
        }

        lblTeksRambu.setText(teksTampil);
        lblTeksRambu.setBackground(warnaBackground);
        lblTeksRambu.setForeground(Color.WHITE);

        String catatan = String.format("[%s | %s] Jalur %s diperbarui -> Rute: %s", kondisi, mode, kodeJalur, ruteTeks);
        riwayatPesan.addFirst(catatan);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < riwayatPesan.size(); i++) {
            sb.append(i + 1).append(". ").append(riwayatPesan.get(i)).append("\n");
        }
        taRiwayat.setText(sb.toString());
    }

    private String formatRute(LinkedList<Perempatan> rute) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rute.size(); i++) {
            sb.append(rute.get(i).getKode());
            if (i < rute.size() - 1) {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulasiRambuKota().setVisible(true));
    }
}
