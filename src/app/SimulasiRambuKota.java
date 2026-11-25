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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
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
    private final JCheckBox chkKameraIkuti;

    private final PanelPetaLive panelPetaLive;
    private final PanelPovPengendara panelPovPengendara;
    private final PanelInfoBobot panelInfoBobot;
    private final JTextArea taRiwayat;
    private final Timer timerAnimasi;

    private String pesanRambuAktif = "KONDISI LANCAR";
    private Color warnaRambuAktif = new Color(46, 125, 50);

    public SimulasiRambuKota() {
        peta = new PetaKota();
        pencariJalur = new PemilihJalurTercepat();
        riwayatPesan = new LinkedList<>();

        inisialisasiDataPeta();

        setTitle("Sistem Rambu Informasi Dinamis - Kota Malang (Live Motion, POV, Bobot & Dokumentasi)");
        setSize(1420, 900);
        setMinimumSize(new Dimension(1120, 740));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Font fontLabel = new Font("Segoe UI", Font.BOLD, 15);
        Font fontKontrol = new Font("Segoe UI", Font.BOLD, 14);

        JPanel pnlAtas = new JPanel(new GridLayout(3, 1, 3, 3));
        pnlAtas.setBorder(buatJudulBorder("Panel Kontrol Operator, Kamera & Preset Uji"));

        JPanel pnlInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        JLabel lblPilihJalur = new JLabel("Pilih Jalur:");
        lblPilihJalur.setFont(fontLabel);
        pnlInput.add(lblPilihJalur);

        cbJalur = new JComboBox<>(new String[]{"B-C", "A-B", "B-E", "E-D", "C-D", "A-C"});
        cbJalur.setSelectedItem("B-C");
        cbJalur.setFont(fontKontrol);
        pnlInput.add(cbJalur);

        JLabel lblJumlah = new JLabel("Jumlah Kendaraan:");
        lblJumlah.setFont(fontLabel);
        pnlInput.add(lblJumlah);

        tfJumlahKendaraan = new JTextField("120", 6);
        tfJumlahKendaraan.setFont(fontKontrol);
        pnlInput.add(tfJumlahKendaraan);

        JButton btnUpdate = new JButton("Update Kepadatan");
        btnUpdate.setFont(fontKontrol);
        pnlInput.add(btnUpdate);

        JButton btnResetMobil = new JButton("Reset Mobil");
        btnResetMobil.setFont(fontKontrol);
        pnlInput.add(btnResetMobil);

        chkKameraIkuti = new JCheckBox("Kamera Ikuti Mobil (Zoom)");
        chkKameraIkuti.setFont(fontKontrol);
        pnlInput.add(chkKameraIkuti);

        JPanel pnlMode = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        JLabel lblMode = new JLabel("Mode Algoritma:");
        lblMode.setFont(fontLabel);
        pnlMode.add(lblMode);

        rbTercepat = new JRadioButton("Jalur Tercepat", true);
        rbTercepat.setFont(fontKontrol);
        rbTerpendek = new JRadioButton("Jalur Terpendek");
        rbTerpendek.setFont(fontKontrol);
        ButtonGroup bgMode = new ButtonGroup();
        bgMode.add(rbTercepat);
        bgMode.add(rbTerpendek);
        pnlMode.add(rbTercepat);
        pnlMode.add(rbTerpendek);

        JPanel pnlPreset = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        JLabel lblPreset = new JLabel("Preset Skenario:");
        lblPreset.setFont(fontLabel);
        pnlPreset.add(lblPreset);

        JButton btnTesLancar = buatTombolPreset("Tes Lancar", fontKontrol);
        JButton btnTesBcRamai = buatTombolPreset("Tes B-C Ramai (60)", fontKontrol);
        JButton btnTesBcPadat = buatTombolPreset("Tes B-C Padat (85)", fontKontrol);
        JButton btnTesBcMacet = buatTombolPreset("Tes B-C Macet (120)", fontKontrol);
        JButton btnTesCdMacet = buatTombolPreset("Tes C-D Macet (120)", fontKontrol);
        JButton btnTesAbMacet = buatTombolPreset("Tes A-B Macet (120)", fontKontrol);
        pnlPreset.add(btnTesLancar);
        pnlPreset.add(btnTesBcRamai);
        pnlPreset.add(btnTesBcPadat);
        pnlPreset.add(btnTesBcMacet);
        pnlPreset.add(btnTesCdMacet);
        pnlPreset.add(btnTesAbMacet);

        pnlAtas.add(pnlInput);
        pnlAtas.add(pnlMode);
        pnlAtas.add(pnlPreset);

        panelPetaLive = new PanelPetaLive(peta);
        panelPovPengendara = new PanelPovPengendara();
        panelInfoBobot = new PanelInfoBobot();

        JSplitPane splitKanan = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelPovPengendara, panelInfoBobot);
        splitKanan.setResizeWeight(0.45);
        splitKanan.setContinuousLayout(true);

        JSplitPane splitUtama = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelPetaLive, splitKanan);
        splitUtama.setResizeWeight(0.62);
        splitUtama.setContinuousLayout(true);

        JPanel pnlBawah = new JPanel(new BorderLayout());
        pnlBawah.setBorder(buatJudulBorder("Riwayat Perubahan Status Rambu (LinkedList)"));
        taRiwayat = new JTextArea(4, 0);
        taRiwayat.setEditable(false);
        taRiwayat.setFont(new Font("Monospaced", Font.PLAIN, 15));
        pnlBawah.add(new JScrollPane(taRiwayat), BorderLayout.CENTER);

        JPanel pnlSimulasi = new JPanel(new BorderLayout(6, 6));
        pnlSimulasi.add(pnlAtas, BorderLayout.NORTH);
        pnlSimulasi.add(splitUtama, BorderLayout.CENTER);
        pnlSimulasi.add(pnlBawah, BorderLayout.SOUTH);

        JTabbedPane tabUtama = new JTabbedPane();
        tabUtama.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabUtama.addTab("Simulasi Live (Peta, POV & Bobot)", pnlSimulasi);
        tabUtama.addTab("Dokumentasi & Arsitektur Kode", new PanelDokumentasi());
        add(tabUtama, BorderLayout.CENTER);

        btnUpdate.addActionListener(e -> eksekusiSimulasi());
        btnResetMobil.addActionListener(e -> panelPetaLive.resetMobil());
        chkKameraIkuti.addActionListener(e -> panelPetaLive.setKameraIkutiMobil(chkKameraIkuti.isSelected()));
        rbTercepat.addActionListener(e -> {
            pencariJalur = new PemilihJalurTercepat();
            panelPetaLive.setModeTercepat(true);
            perbaruiTampilanRuteSaja();
        });
        rbTerpendek.addActionListener(e -> {
            pencariJalur = new PemilihJalurTerpendek();
            panelPetaLive.setModeTercepat(false);
            perbaruiTampilanRuteSaja();
        });

        btnTesLancar.addActionListener(e -> terapkanPreset("B-C", 20, true));
        btnTesBcRamai.addActionListener(e -> terapkanPreset("B-C", 60, false));
        btnTesBcPadat.addActionListener(e -> terapkanPreset("B-C", 85, false));
        btnTesBcMacet.addActionListener(e -> terapkanPreset("B-C", 120, false));
        btnTesCdMacet.addActionListener(e -> terapkanPreset("C-D", 120, false));
        btnTesAbMacet.addActionListener(e -> terapkanPreset("A-B", 120, false));

        perbaruiTampilanRuteSaja();
        panelPetaLive.resetMobil();

        timerAnimasi = new Timer(33, e -> {
            double dt = 0.033;
            panelPetaLive.updateAnimasi(dt);
            panelPovPengendara.perbaruiStatus(
                panelPetaLive.getKodeJalurSaatIni(),
                panelPetaLive.getKondisiSaatIni(),
                panelPetaLive.getKecepatanKmh(),
                panelPetaLive.getArahBerikutnya(),
                pesanRambuAktif,
                warnaRambuAktif,
                panelPetaLive.getSudutMobilRad(),
                dt
            );
        });
        timerAnimasi.start();

        SwingUtilities.invokeLater(() -> {
            splitUtama.setDividerLocation(0.62);
            splitKanan.setDividerLocation(0.45);
        });
    }

    private TitledBorder buatJudulBorder(String judul) {
        return BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            judul,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 15),
            new Color(15, 23, 42)
        );
    }

    private JButton buatTombolPreset(String teks, Font font) {
        JButton btn = new JButton(teks);
        btn.setFont(font);
        return btn;
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
            peta.tambahJalur(new Jalur("A-C", a, c, 11, 100));
        } catch (DataTidakValidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Inisialisasi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void terapkanPreset(String kodeTarget, int jumlahTarget, boolean semuaLancar) {
        try {
            for (Jalur j : peta.getSemuaJalur().values()) {
                j.setJumlahKendaraan(20);
            }
            if (!semuaLancar) {
                peta.cariJalur(kodeTarget).setJumlahKendaraan(jumlahTarget);
            }
            cbJalur.setSelectedItem(kodeTarget);
            tfJumlahKendaraan.setText(String.valueOf(jumlahTarget));
            eksekusiSimulasi();
        } catch (DataTidakValidException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Data", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eksekusiSimulasi() {
        try {
            String kodeJalur = (String) cbJalur.getSelectedItem();
            int jumlah = Integer.parseInt(tfJumlahKendaraan.getText().trim());

            Jalur jalurDipilih = peta.cariJalur(kodeJalur);
            jalurDipilih.setJumlahKendaraan(jumlah);

            KondisiJalan kondisi = jalurDipilih.cekKondisi();
            LinkedList<Perempatan> rute = pencariJalur.cariRute(peta, "A", "D");
            panelPetaLive.setRuteAktif(rute);
            panelInfoBobot.perbaruiData(peta, rbTercepat.isSelected(), rute);
            perbaruiVisualRambu(kondisi, kodeJalur, rute);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Masukkan angka jumlah kendaraan yang valid!",
                "Input Error",
                JOptionPane.ERROR_MESSAGE
            );
        } catch (DataTidakValidException ex) {
            JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Validasi Data",
                JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void perbaruiTampilanRuteSaja() {
        try {
            LinkedList<Perempatan> rute = pencariJalur.cariRute(peta, "A", "D");
            panelPetaLive.setRuteAktif(rute);
            panelInfoBobot.perbaruiData(peta, rbTercepat.isSelected(), rute);
            String mode = rbTercepat.isSelected() ? "TERCEPAT" : "TERPENDEK";
            pesanRambuAktif = "MODE " + mode + " | RUTE: " + formatRute(rute);
            warnaRambuAktif = new Color(37, 99, 235);
        } catch (DataTidakValidException ex) {
            pesanRambuAktif = "RUTE TIDAK TERSEDIA";
            warnaRambuAktif = new Color(185, 28, 28);
        }
    }

    private void perbaruiVisualRambu(KondisiJalan kondisi, String kodeJalur, LinkedList<Perempatan> rute) {
        String ruteTeks = formatRute(rute);
        String mode = rbTercepat.isSelected() ? "TERCEPAT" : "TERPENDEK";

        switch (kondisi) {
            case LANCAR:
                pesanRambuAktif = "LANCAR (" + mode + "): " + ruteTeks;
                warnaRambuAktif = new Color(46, 125, 50);
                break;
            case RAMAI:
                pesanRambuAktif = "RAMAI (" + mode + "): " + ruteTeks;
                warnaRambuAktif = new Color(202, 138, 4);
                break;
            case PADAT:
                pesanRambuAktif = kodeJalur + " PADAT! RUTE: " + ruteTeks;
                warnaRambuAktif = new Color(230, 81, 0);
                break;
            case MACET:
            default:
                pesanRambuAktif = kodeJalur + " MACET! RUTE: " + ruteTeks;
                warnaRambuAktif = new Color(198, 40, 40);
                break;
        }

        String catatan = String.format("[%s | %s] Jalur %s diperbarui -> Rute: %s", kondisi, mode, kodeJalur, ruteTeks);
        riwayatPesan.addFirst(catatan);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < riwayatPesan.size(); i++) {
            sb.append(i + 1).append(". ").append(riwayatPesan.get(i)).append(System.lineSeparator());
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
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new SimulasiRambuKota().setVisible(true));
    }
}
