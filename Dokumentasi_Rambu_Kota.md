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

---

## FAQ Sidang & Tanya Jawab (Ringkasan & Lokasi Kode)

1. **Kenapa `B-C` berstatus `RAMAI` belum pindah ke jalur alternatif?**
   - Status ditentukan rasio `jumlahKendaraan / kapasitasMaksimal`, bukan oleh `pengaliMacet`. Saat `RAMAI` (`1.5x`), bobot `B-C` menjadi `4 x 1.5 = 6` sehingga total `A -> B -> C -> D = 11`, masih lebih kecil dari `A -> B -> E -> D = 12`.
   - **Lokasi Kode:** `src/model/Jalur.java` (`hitungRasioKepadatan()`, `cekKondisi()`), `src/model/KondisiJalan.java` (`getPengaliMacet()`).

2. **Peran `HashMap`, `LinkedList`, dan `PriorityQueue`?**
   - `HashMap`: pencarian simpul/jalur `O(1)`.
   - `LinkedList`: daftar ketetanggaan graf, penyusunan rute mundur (`addFirst()`), dan riwayat log rambu terbaru.
   - `PriorityQueue`: *min-heap* untuk mengambil biaya terkecil `O(log V)`. Jika diganti `LinkedList` biasa, algoritma menjadi FIFO/BFS (bisa salah pilih rute atau melambat ke `O(V^2)`).
   - **Lokasi Kode:** `src/model/PetaKota.java` (`daftarPerempatan`, `daftarJalur`, `petaSambungan`), `src/algoritma/PemilihDijkstra.java` (`AntreanCek`, `cariRute()`), `src/app/SimulasiRambuKota.java` (`riwayatPesan`).

3. **Kenapa `PemilihDijkstra` dibuat `abstract class` padahal sudah ada interface `PemilihJalur`?**
   - Untuk menghindari duplikasi logika inti Dijkstra di subclass. Interface `PemilihJalur` menjadi kontrak untuk GUI, sedangkan `PemilihDijkstra` menyimpan kerangka algoritma dan mewajibkan subclass meng-override `protected abstract double hitungBiaya(Jalur)` (*Runtime Polymorphism*).
   - **Lokasi Kode:** `src/algoritma/PemilihJalur.java`, `src/algoritma/PemilihDijkstra.java`, `src/algoritma/PemilihJalurTercepat.java`, `src/algoritma/PemilihJalurTerpendek.java`.

4. **Alur *Encapsulation* & *Exception* saat input `-50` atau `"abc"`?**
   - `jumlahKendaraan` bersifat `private` dan hanya diubah lewat `setJumlahKendaraan()`. Input `"abc"` melempar `NumberFormatException` di `Integer.parseInt()`, sedangkan `-50` ditolak validasi `jumlahBaru < 0` yang melempar `DataTidakValidException` ke `SimulasiRambuKota` untuk ditampilkan lewat `JOptionPane`.
   - **Lokasi Kode:** `src/model/Jalur.java` (`setJumlahKendaraan()`), `src/exception/DataTidakValidException.java`, `src/app/SimulasiRambuKota.java` (`eksekusiSimulasi()`).

5. **Simulasi hitung Dijkstra: `B-C` Padat (85) vs `A-B` Macet (120)?**
   - `B-C = 85` (`PADAT`, `2.0x`) -> `B-C = 8`, `A -> B -> C -> D = 13 > 12` -> pindah ke `A -> B -> E -> D` (`12`).
   - `A-B = 120` (`MACET`, `3.0x`) -> `A-B = 9`, rute utama (`15`) dan bawah (`18`) ikut terdampak -> pindah ke jalur atas `A -> C -> D` (`13`).
   - **Lokasi Kode:** `src/algoritma/PemilihJalurTercepat.java` (`hitungBiaya()`), `src/app/PanelInfoBobot.java` (`perbaruiData()`).

6. **Bagaimana GUI Swing terhubung ke backend tanpa mencampuradukkan kode?**
   - Package `model`, `algoritma`, dan `exception` tidak meng-import `javax.swing` atau `java.awt` (*Separation of Concerns*). `SimulasiRambuKota` hanya menjadi penghubung input-output ke panel visual.
   - **Lokasi Kode:** `src/app/SimulasiRambuKota.java`, `src/app/PanelPetaLive.java`.

7. **Fungsi `if (sekarang.totalBiaya > biayaTerbaik.get(...)) continue;` di Dijkstra?**
   - `PriorityQueue` Java tidak memiliki *decrease-key*, sehingga entri baru yang lebih murah ditambahkan tanpa menghapus entri lama. Baris ini membuang entri basi agar tidak diproses ulang.
   - **Lokasi Kode:** `src/algoritma/PemilihDijkstra.java` (`cariRute()`).

8. **Tipe graf: *Directed* atau *Undirected*? Apa jadinya `cariRute(peta, "D", "A")`?**
   - *Directed Graph* karena `tambahJalur()` hanya mendaftarkan sisi dari `kodeAsal` ke `kodeTujuan`. Mencari `D -> A` membuat `biayaTerbaik` tetap `Double.POSITIVE_INFINITY` dan melempar `DataTidakValidException`.
   - **Lokasi Kode:** `src/model/PetaKota.java` (`tambahJalur()`), `src/algoritma/PemilihDijkstra.java` (`cariRute()`).

9. **Kenapa animasi memakai `javax.swing.Timer`, bukan `while (true)` + `Thread.sleep()`?**
   - Loop `while (true)` akan memblokir **EDT (Event Dispatch Thread)** sehingga GUI menjadi *Not Responding*. `javax.swing.Timer(33, ...)` berjalan non-blocking dan memicu update secara aman di atas EDT.
   - **Lokasi Kode:** `src/app/SimulasiRambuKota.java` (`timerAnimasi`, `main()`).

10. **Kenapa `KondisiJalan` memakai `enum` ber-parameter, bukan `String`/`int`?**
    - Menjamin *Type Safety* pada saat kompilasi dan melekatkan nilai `pengaliMacet` langsung pada setiap status tanpa `if-else` berulang.
    - **Lokasi Kode:** `src/model/KondisiJalan.java`.
