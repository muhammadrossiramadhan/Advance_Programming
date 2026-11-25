# Dokumentasi Revisi GUI Sistem Rambu Kota Malang

## Ringkasan Perubahan (`package app`)
Seluruh perubahan hanya dilakukan pada package `app` tanpa mengubah nama package, class, atau variabel pada package `exception`, `model`, dan `algoritma`.

1. **`src/app/PanelInfoBobot.java`**
   - Menampilkan **Panduan Ambang Batas (Kapasitas 100)** dengan badge warna untuk `LANCAR` (0-49, `<50%`, `1.0x`), `RAMAI` (50-74, `50-74%`, `1.5x`), `PADAT` (75-99, `75-99%`, `2.0x`), dan `MACET` (`>=100`, `>=100%`, `3.0x`).
   - Menyediakan **Tabel Bobot Real-Time Semua Jalur** (`JTable` non-editable) dengan kolom `Jalur`, `Kendaraan`, `Status`, `Pengali`, dan `Bobot Aktif`.
   - Menyediakan **Perbandingan 3 Rute Kandidat (`A -> D`)** (`A -> B -> C -> D`, `A -> B -> E -> D`, `A -> C -> D`) beserta penanda `[TERPILIH]` dan alasan keputusan Dijkstra.

2. **`src/app/PanelDokumentasi.java`**
   - Laman dokumentasi interaktif menggunakan `JSplitPane` horizontal (`JList<String>` di kiri dan `JEditorPane` `text/html` read-only di kanan).
   - Berisi 6 topik lengkap: Gambaran Sistem & Alur Kerja, Perbedaan `LANCAR`/`RAMAI`/`PADAT`/`MACET`, Peta Package & Seluruh File Kode, Penerapan 6 Konsep OOP di Kode, Cara Kerja Dijkstra & Rumus Pengali, serta Panduan GUI & Skenario Uji.

3. **`src/app/PanelPetaLive.java`**
   - Menambahkan label bobot aktif real-time di dekat setiap jalur (`A-B`, `B-C`, `C-D`, `B-E`, `E-D`, `A-C`).
   - Memberikan indikator warna oranye (`PADAT`) dan merah (`MACET`) untuk jalur mana pun yang mengalami kepadatan/kemacetan, serta sinkronisasi mode `Tercepat` vs `Terpendek`.

4. **`src/app/SimulasiRambuKota.java`**
   - Membungkus antarmuka utama menggunakan `JTabbedPane` dengan Tab 1 (*Simulasi Live (Peta, POV & Bobot)*) dan Tab 2 (*Dokumentasi & Arsitektur Kode*).
   - Menggabungkan `PanelPovPengendara` dan `PanelInfoBobot` dalam `JSplitPane` vertikal di sisi kanan Tab 1.
   - Menambahkan 6 tombol preset skenario uji (`Tes Lancar`, `Tes B-C Ramai (60)`, `Tes B-C Padat (85)`, `Tes B-C Macet (120)`, `Tes C-D Macet (120)`, `Tes A-B Macet (120)`) yang menyegarkan seluruh panel dan riwayat `LinkedList` secara otomatis.
