package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanelDokumentasi extends JPanel {
    private final Map<String, String> kontenTopik = new LinkedHashMap<>();
    private final Map<String, String> teksPolosTopik = new LinkedHashMap<>();
    private final DefaultListModel<String> modelList = new DefaultListModel<>();
    private final JList<String> listTopik;
    private final JEditorPane editorKonten;
    private final JTextField tfCari;
    private final JLabel lblStatusCari;

    public PanelDokumentasi() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        inisialisasiKonten();

        for (String judul : kontenTopik.keySet()) {
            modelList.addElement(judul);
            teksPolosTopik.put(judul, bersihkanHtml(kontenTopik.get(judul)));
        }

        listTopik = new JList<>(modelList);
        listTopik.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTopik.setFont(new Font("Segoe UI", Font.BOLD, 16));
        listTopik.setBackground(new Color(241, 245, 249));
        listTopik.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
                lbl.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
                return lbl;
            }
        });
        listTopik.setSelectedIndex(0);

        JPanel pnlCari = new JPanel(new BorderLayout(4, 4));
        pnlCari.setBorder(BorderFactory.createEmptyBorder(4, 4, 6, 4));

        tfCari = new JTextField();
        tfCari.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfCari.setToolTipText("Cari topik atau kata kunci (Ctrl+F)...");

        JButton btnHapusCari = new JButton("X");
        btnHapusCari.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHapusCari.setMargin(new Insets(2, 8, 2, 8));
        btnHapusCari.setToolTipText("Hapus pencarian (Esc)");

        lblStatusCari = new JLabel("Menampilkan " + kontenTopik.size() + " dari " + kontenTopik.size() + " topik (Ctrl+F)");
        lblStatusCari.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblStatusCari.setForeground(new Color(71, 85, 105));

        JPanel pnlInputCari = new JPanel(new BorderLayout(4, 0));
        pnlInputCari.add(tfCari, BorderLayout.CENTER);
        pnlInputCari.add(btnHapusCari, BorderLayout.EAST);

        pnlCari.add(pnlInputCari, BorderLayout.CENTER);
        pnlCari.add(lblStatusCari, BorderLayout.SOUTH);

        JScrollPane spList = new JScrollPane(listTopik);
        spList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spList.setBorder(null);

        JPanel pnlKiri = new JPanel(new BorderLayout());
        pnlKiri.setPreferredSize(new Dimension(360, 0));
        pnlKiri.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            "Daftar Topik Dokumentasi",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(15, 23, 42)
        ));
        pnlKiri.add(pnlCari, BorderLayout.NORTH);
        pnlKiri.add(spList, BorderLayout.CENTER);

        editorKonten = new JEditorPane();
        editorKonten.setContentType("text/html");
        editorKonten.setEditable(false);
        editorKonten.setBackground(Color.WHITE);

        JScrollPane spKanan = new JScrollPane(editorKonten);
        spKanan.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            "Detail Penjelasan & Arsitektur",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            new Color(15, 23, 42)
        ));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlKiri, spKanan);
        splitPane.setDividerLocation(360);
        splitPane.setContinuousLayout(true);

        listTopik.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                tampilkanTopikTerpilih();
            }
        });

        tfCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                saringTopik();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                saringTopik();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                saringTopik();
            }
        });

        btnHapusCari.addActionListener(e -> {
            tfCari.setText("");
            tfCari.requestFocusInWindow();
        });

        pasangPintasanKeyboard();

        tampilkanTopikTerpilih();
        add(splitPane, BorderLayout.CENTER);
    }

    private void pasangPintasanKeyboard() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK),
            "fokusCari"
        );
        getActionMap().put("fokusCari", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfCari.requestFocusInWindow();
                tfCari.selectAll();
            }
        });

        tfCari.getInputMap(JComponent.WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            "bersihkanCari"
        );
        tfCari.getActionMap().put("bersihkanCari", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfCari.setText("");
            }
        });

        tfCari.getInputMap(JComponent.WHEN_FOCUSED).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
            "pindahKeList"
        );
        tfCari.getActionMap().put("pindahKeList", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!modelList.isEmpty()) {
                    listTopik.requestFocusInWindow();
                }
            }
        });
    }

    private String bersihkanHtml(String html) {
        String tanpaStyle = html.replaceAll("(?s)<style.*?>.*?</style>", " ");
        String tanpaTag = tanpaStyle.replaceAll("<[^>]+>", " ");
        return tanpaTag
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replaceAll("\\s+", " ")
            .trim();
    }

    private void saringTopik() {
        String kataKunci = tfCari.getText().trim();
        String kataKunciLower = kataKunci.toLowerCase();
        modelList.clear();

        for (String judul : kontenTopik.keySet()) {
            if (kataKunciLower.isEmpty()) {
                modelList.addElement(judul);
            } else {
                String teksJudul = judul.toLowerCase();
                String teksIsi = teksPolosTopik.get(judul).toLowerCase();
                if (teksJudul.contains(kataKunciLower) || teksIsi.contains(kataKunciLower)) {
                    modelList.addElement(judul);
                }
            }
        }

        if (kataKunci.isEmpty()) {
            lblStatusCari.setText("Menampilkan " + modelList.size() + " dari " + kontenTopik.size() + " topik (Ctrl+F)");
        } else {
            lblStatusCari.setText("Menampilkan " + modelList.size() + " dari " + kontenTopik.size() + " topik");
        }

        if (modelList.isEmpty()) {
            editorKonten.setText(buatHtmlTidakDitemukan(kataKunci));
            editorKonten.setCaretPosition(0);
        } else {
            listTopik.setSelectedIndex(0);
            tampilkanTopikTerpilih();
        }
    }

    private void tampilkanTopikTerpilih() {
        String dipilih = listTopik.getSelectedValue();
        if (dipilih == null) {
            return;
        }
        String htmlAsli = kontenTopik.get(dipilih);
        String kataKunci = tfCari.getText().trim();
        if (kataKunci.isEmpty()) {
            editorKonten.setText(htmlAsli);
        } else {
            editorKonten.setText(sorotKataKunciDiHtml(htmlAsli, kataKunci));
        }
        editorKonten.setCaretPosition(0);
    }

    private String sorotKataKunciDiHtml(String html, String kataKunci) {
        int idxBodyStart = html.indexOf("<body>");
        int idxBodyEnd = html.lastIndexOf("</body>");
        if (idxBodyStart < 0 || idxBodyEnd <= idxBodyStart) {
            return html;
        }

        String kepala = html.substring(0, idxBodyStart + 6);
        String badan = html.substring(idxBodyStart + 6, idxBodyEnd);
        String ekor = html.substring(idxBodyEnd);

        Pattern polaTag = Pattern.compile("<[^>]+>");
        Pattern polaCari = Pattern.compile(Pattern.quote(kataKunci), Pattern.CASE_INSENSITIVE);
        Matcher mTag = polaTag.matcher(badan);

        StringBuilder hasil = new StringBuilder();
        int posisiTerakhir = 0;

        while (mTag.find()) {
            String teksSebelumTag = badan.substring(posisiTerakhir, mTag.start());
            hasil.append(sorotTeksPolos(teksSebelumTag, polaCari));
            hasil.append(mTag.group());
            posisiTerakhir = mTag.end();
        }

        if (posisiTerakhir < badan.length()) {
            hasil.append(sorotTeksPolos(badan.substring(posisiTerakhir), polaCari));
        }

        return kepala + hasil.toString() + ekor;
    }

    private String sorotTeksPolos(String teks, Pattern polaCari) {
        if (teks.isEmpty()) {
            return teks;
        }
        Matcher m = polaCari.matcher(teks);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String sisipan = "<span style=\"background-color:#fef08a; color:#000000; font-weight:bold;\">"
                + Matcher.quoteReplacement(m.group())
                + "</span>";
            m.appendReplacement(sb, sisipan);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private String buatHtmlTidakDitemukan(String kataKunci) {
        String aman = kataKunci.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Tidak ditemukan topik untuk kata kunci <b>'").append(aman).append("'</b>.</p>");
        sb.append("<h2>Saran Kata Kunci Populer</h2>");
        sb.append("<div style='background:#fef9c3; border-left:4px solid #ca8a04; padding:12px 16px;'>");
        sb.append("<ul>");
        sb.append("<li><code>LANCAR</code>, <code>RAMAI</code>, <code>PADAT</code>, <code>MACET</code></li>");
        sb.append("<li><code>Dijkstra</code>, <code>pengali</code></li>");
        sb.append("<li><code>HashMap</code>, <code>PriorityQueue</code>, <code>LinkedList</code></li>");
        sb.append("<li><code>OOP</code>, <code>Exception</code></li>");
        sb.append("</ul></div>");
        return bungkusHtml("Hasil Pencarian Tidak Ditemukan", sb.toString());
    }

    private void inisialisasiKonten() {
        kontenTopik.put("1. Gambaran Sistem & Alur Kerja", buatHtmlGambaranSistem());
        kontenTopik.put("2. Beda LANCAR, RAMAI, PADAT, MACET", buatHtmlKondisiJalan());
        kontenTopik.put("3. Peta Package & File Kode", buatHtmlPetaPackage());
        kontenTopik.put("4. Penerapan 6 Konsep OOP", buatHtmlKonsepOop());
        kontenTopik.put("5. Cara Kerja Dijkstra & Pengali", buatHtmlDijkstra());
        kontenTopik.put("6. Panduan GUI & Skenario Uji", buatHtmlPanduanGui());
    }

    private String bungkusHtml(String judul, String isi) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><style>");
        sb.append("body { font-family: 'Segoe UI', Arial, sans-serif; font-size: 18px; line-height: 1.6; color: #111827; padding: 16px 24px; }");
        sb.append("h1 { font-size: 24px; font-weight: bold; color: #0f172a; border-bottom: 2px solid #2563eb; padding-bottom: 8px; margin-top: 0; }");
        sb.append("h2 { font-size: 20px; font-weight: bold; color: #1e293b; margin-top: 16px; }");
        sb.append("table { border-collapse: collapse; width: 100%; margin-top: 12px; }");
        sb.append("th, td { font-size: 17px; padding: 10px 12px; border: 1px solid #94a3b8; text-align: left; }");
        sb.append("code { font-family: Consolas, monospace; font-size: 17px; background-color: #f1f5f9; padding: 2px 6px; }");
        sb.append("li { margin-bottom: 8px; }");
        sb.append("</style></head><body>");
        sb.append("<h1>").append(judul).append("</h1>");
        sb.append(isi);
        sb.append("</body></html>");
        return sb.toString();
    }

    private String buatHtmlGambaranSistem() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p><b>Sistem Rambu Informasi Dinamis Kota Malang</b> memantau kepadatan lalu lintas ");
        sb.append("secara real-time pada graf jalan A sampai D untuk mencegah penumpukan kendaraan di jalur utama.</p>");
        sb.append("<h2>4 Langkah Alur Kerja Sistem</h2>");
        sb.append("<div style='background:#eff6ff; border-left:4px solid #2563eb; padding:12px 16px; margin:10px 0;'>");
        sb.append("<ol>");
        sb.append("<li><b>Hitung Kendaraan:</b> Operator atau sensor memasukkan <code>jumlahKendaraan</code> ke objek <code>Jalur</code>.</li>");
        sb.append("<li><b>Nilai Kondisi:</b> Method <code>cekKondisi()</code> membandingkan rasio kepadatan terhadap kapasitas (100) untuk menentukan status <code>LANCAR</code>, <code>RAMAI</code>, <code>PADAT</code>, atau <code>MACET</code>.</li>");
        sb.append("<li><b>Cari Rute Alternatif (Dijkstra):</b> <code>PemilihJalur</code> menghitung biaya terendah dari <code>A</code> ke <code>D</code>.</li>");
        sb.append("<li><b>Tampil di Rambu &amp; Catat Riwayat:</b> Peta live, gantry POV pengendara, tabel bobot, dan log <code>LinkedList</code> diperbarui otomatis.</li>");
        sb.append("</ol></div>");
        return bungkusHtml("1. Gambaran Sistem &amp; Alur Kerja", sb.toString());
    }

    private String buatHtmlKondisiJalan() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Ambang batas ditentukan oleh <code>Jalur.cekKondisi()</code> melalui rasio ");
        sb.append("<code>jumlahKendaraan / kapasitasMaksimal</code> dan bobot pengali pada enum <code>KondisiJalan</code>:</p>");
        sb.append("<table>");
        sb.append("<tr style='background:#1e293b; color:white;'>");
        sb.append("<th>Status</th><th>Rasio</th><th>Mobil (Kap. 100)</th><th>Pengali</th><th>Perilaku Sistem</th></tr>");
        sb.append("<tr style='background:#dcfce7;'><td><b>LANCAR</b></td><td>&lt; 0.50</td><td>0 - 49</td><td>1.0x</td><td>Arus bebas, gunakan jalur utama A-B-C-D.</td></tr>");
        sb.append("<tr style='background:#fef9c3;'><td><b>RAMAI</b></td><td>0.50 - 0.74</td><td>50 - 74</td><td>1.5x</td><td>Arus masih mengalir; tetap di jalur utama jika total bobot masih lebih kecil.</td></tr>");
        sb.append("<tr style='background:#ffedd5;'><td><b>PADAT</b></td><td>0.75 - 0.99</td><td>75 - 99</td><td>2.0x</td><td>Batas kritis! Mulai pecah arus ke rute alternatif sebagai pencegahan.</td></tr>");
        sb.append("<tr style='background:#fee2e2;'><td><b>MACET</b></td><td>&gt;= 1.00</td><td>&gt;= 100</td><td>3.0x</td><td>Kapasitas penuh/meluber! Wajib alihkan rute ke jalur alternatif.</td></tr>");
        sb.append("</table>");
        sb.append("<h2>Kapan Jalur Alternatif Dipakai?</h2>");
        sb.append("<div style='background:#f8fafc; border:1px solid #cbd5e1; padding:12px 16px;'>");
        sb.append("<ul>");
        sb.append("<li><b>Jalur B-E &amp; E-D (Alternatif Bawah):</b> Dipakai saat <code>B-C</code> berstatus PADAT/MACET atau <code>C-D</code> MACET.</li>");
        sb.append("<li><b>Jalur A-C (Alternatif Atas):</b> Dipakai saat <code>A-B</code> MACET atau jalur tengah dan bawah sama-sama terhambat.</li>");
        sb.append("</ul></div>");
        return bungkusHtml("2. Perbedaan LANCAR, RAMAI, PADAT, MACET", sb.toString());
    }

    private String buatHtmlPetaPackage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Proyek terbagi menjadi 4 package utama dengan pemisahan tanggung jawab yang jelas:</p>");
        sb.append("<h2>Package exception &amp; model</h2><ul>");
        sb.append("<li><code>DataTidakValidException.java</code>: Custom exception untuk input negatif, kapasitas &lt;= 0, atau kode tidak ditemukan.</li>");
        sb.append("<li><code>KondisiJalan.java</code>: Enum status jalan (<code>LANCAR</code> 1.0x, <code>RAMAI</code> 1.5x, <code>PADAT</code> 2.0x, <code>MACET</code> 3.0x).</li>");
        sb.append("<li><code>Perempatan.java</code>: Representasi simpul (node) <code>A</code>, <code>B</code>, <code>C</code>, <code>D</code>, <code>E</code>.</li>");
        sb.append("<li><code>Jalur.java</code>: Representasi sisi (edge) berarah dengan <code>waktuNormal</code>, <code>kapasitasMaksimal</code>, dan <code>cekKondisi()</code>.</li>");
        sb.append("<li><code>PetaKota.java</code>: Struktur graf memakai <code>HashMap</code> dan <code>LinkedList</code>.</li></ul>");
        sb.append("<h2>Package algoritma &amp; app</h2><ul>");
        sb.append("<li><code>PemilihJalur.java</code>: Interface kontrak pencarian rute <code>cariRute(peta, asal, tujuan)</code>.</li>");
        sb.append("<li><code>PemilihDijkstra.java</code>: Abstract class Dijkstra berbasis <code>PriorityQueue</code>.</li>");
        sb.append("<li><code>PemilihJalurTerpendek.java</code> &amp; <code>PemilihJalurTercepat.java</code>: Subclass perhitungan biaya.</li>");
        sb.append("<li><code>PanelPetaLive.java</code>, <code>PanelPovPengendara.java</code>, <code>PanelInfoBobot.java</code>, <code>PanelDokumentasi.java</code>, <code>SimulasiRambuKota.java</code>: Antarmuka GUI Swing.</li>");
        sb.append("</ul>");
        return bungkusHtml("3. Peta Package &amp; Seluruh File Kode", sb.toString());
    }

    private String buatHtmlKonsepOop() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ol>");
        sb.append("<li><b>Encapsulation:</b> Atribut pada <code>Jalur</code> dan <code>Perempatan</code> bersifat <code>private</code>, dengan validasi pada <code>setJumlahKendaraan()</code>.</li>");
        sb.append("<li><b>Abstraction &amp; Inheritance:</b> <code>PemilihDijkstra</code> mewariskan kerangka algoritma ke <code>PemilihJalurTercepat</code> dan <code>PemilihJalurTerpendek</code>.</li>");
        sb.append("<li><b>Polymorphism:</b> Method abstrak <code>hitungBiaya(Jalur)</code> di-override berbeda oleh mode Tercepat dan Terpendek.</li>");
        sb.append("<li><b>Interface:</b> <code>PemilihJalur</code> menjadi kontrak seragam untuk seluruh strategi rute.</li>");
        sb.append("<li><b>Exception Handling:</b> <code>DataTidakValidException</code> dan <code>NumberFormatException</code> ditangkap GUI lewat <code>JOptionPane</code>.</li>");
        sb.append("<li><b>Collection Framework:</b> <code>HashMap</code>, <code>PriorityQueue</code>, dan <code>LinkedList</code>.</li>");
        sb.append("</ol>");
        return bungkusHtml("4. Penerapan 6 Konsep OOP di Kode", sb.toString());
    }

    private String buatHtmlDijkstra() {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>Pada mode <b>Jalur Tercepat</b>, biaya tiap jalur dihitung dengan rumus: ");
        sb.append("<code>biaya = waktuNormal * kondisi.getPengaliMacet()</code>.</p>");
        sb.append("<p>Bobot normal: <code>A-B=3</code>, <code>B-C=4</code>, <code>C-D=2</code>, <code>B-E=5</code>, <code>E-D=4</code>, <code>A-C=11</code>. ");
        sb.append("Rute Bawah <code>A-&gt;B-&gt;E-&gt;D = 12</code> dan Rute Atas <code>A-&gt;C-&gt;D = 13</code>.</p>");
        sb.append("<table>");
        sb.append("<tr style='background:#0f172a; color:white;'><th>Skenario B-C</th><th>Bobot B-C</th><th>Total A-B-C-D</th><th>Banding A-B-E-D (12)</th><th>Keputusan Dijkstra</th></tr>");
        sb.append("<tr><td>Lancar (20)</td><td>4 x 1.0 = 4</td><td><b>9</b></td><td>9 &lt; 12</td><td>Tetap A -&gt; B -&gt; C -&gt; D</td></tr>");
        sb.append("<tr><td>Ramai (60)</td><td>4 x 1.5 = 6</td><td><b>11</b></td><td>11 &lt; 12</td><td>Tetap A -&gt; B -&gt; C -&gt; D</td></tr>");
        sb.append("<tr><td>Padat (85)</td><td>4 x 2.0 = 8</td><td><b>13</b></td><td>13 &gt; 12</td><td>Pindah ke A -&gt; B -&gt; E -&gt; D</td></tr>");
        sb.append("<tr><td>Macet (120)</td><td>4 x 3.0 = 12</td><td><b>17</b></td><td>17 &gt; 12</td><td>Wajib ke A -&gt; B -&gt; E -&gt; D</td></tr>");
        sb.append("</table>");
        return bungkusHtml("5. Cara Kerja Dijkstra &amp; Rumus Pengali", sb.toString());
    }

    private String buatHtmlPanduanGui() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ol>");
        sb.append("<li><b>Kontrol Operator:</b> Pilih jalur di combo box, isi jumlah kendaraan, lalu klik <i>Update Kepadatan</i>.</li>");
        sb.append("<li><b>Tombol Preset Uji:</b> Gunakan tombol cepat untuk menguji perpindahan rute seketika.</li>");
        sb.append("<li><b>Panel Peta Live:</b> Memperlihatkan kondisi jalur, angka bobot real-time, sorotan rute aktif, dan mobil yang melambat di jalur padat/macet.</li>");
        sb.append("<li><b>Panel POV &amp; Info Bobot:</b> Sisi kanan menampilkan sudut pandang pengemudi, tabel bobot seluruh jalur, dan perbandingan 3 rute kandidat.</li>");
        sb.append("<li><b>Uji Validasi Error:</b> Masukkan angka negatif seperti <code>-50</code> atau teks bukan angka untuk melihat dialog <code>JOptionPane</code>.</li>");
        sb.append("</ol>");
        return bungkusHtml("6. Panduan GUI &amp; Skenario Uji", sb.toString());
    }
}
