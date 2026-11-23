# Dokumentasi Proyek: Sistem Rambu Informasi Dinamis Perkotaan

Dokumen ini menjelaskan struktur package, arsitektur OOP, alasan penggunaan algoritma Dijkstra dengan faktor `pengaliMacet`, serta cara menjalankan aplikasi GUI berbasis Java Swing (`javax.swing.*`).

---

## 1. Struktur Folder & Package

Seluruh file `.java` telah dipisahkan berdasarkan tanggung jawabnya agar bersih, tanpa komentar yang mengganggu, dan saling terhubung lewat `import`:

```text
Proyek_Rambu_Kota_Malang/
├── Dokumentasi_Rambu_Kota.md
└── src/
    ├── exception/
    │   └── DataTidakValidException.java
    ├── model/
    │   ├── KondisiJalan.java
    │   ├── Perempatan.java
    │   ├── Jalur.java
    │   └── PetaKota.java
    ├── algoritma/
    │   ├── PemilihJalur.java
    │   ├── PemilihDijkstra.java
    │   ├── PemilihJalurTerpendek.java
    │   └── PemilihJalurTercepat.java
    └── app/
        └── SimulasiRambuKota.java
```

---

## 2. Penjelasan Tiap Class & Konsep OOP

### A. Package `exception`
- **`DataTidakValidException`**: Custom exception yang dilempar jika operator menginput angka kendaraan negatif, nilai kapasitas/waktu normal `<= 0`, kode perempatan/jalur tidak terdaftar, atau tidak ditemukan rute penghubung.

### B. Package `model`
- **`KondisiJalan` (Enum)**: Menyimpan 4 kondisi tetap (`LANCAR`, `RAMAI`, `PADAT`, `MACET`) beserta atribut `private final double pengaliMacet` (`1.0`, `1.5`, `2.0`, `3.0`). Atribut dibuat `final` tanpa setter agar bobot penalti tidak bisa diubah sembarangan.
- **`Perempatan`**: Representasi titik lokasi/persimpangan (`A`, `B`, `C`, `D`, `E`) dengan atribut `private` (`kode`, `nama`) sebagai penerapan **Encapsulation**.
- **`Jalur`**: Penghubung antara dua `Perempatan`. Menyimpan `waktuNormal`, `kapasitasMaksimal`, dan `jumlahKendaraan`.
  - `hitungRasioKepadatan()` menghitung `jumlahKendaraan / kapasitasMaksimal`.
  - `cekKondisi()` memetakan rasio ke `KondisiJalan`: `< 0.50` (`LANCAR`), `< 0.75` (`RAMAI`), `< 1.00` (`PADAT`), `>= 1.00` (`MACET`).
- **`PetaKota`**: Menyimpan struktur graf menggunakan Collection Framework:
  - `HashMap<String, Perempatan> daftarPerempatan`: akses cepat perempatan berdasarkan kode.
  - `HashMap<String, Jalur> daftarJalur`: akses cepat jalur berdasarkan kode (misal `"B-C"`).
  - `HashMap<String, LinkedList<Jalur>> petaSambungan`: adjacency list yang menyimpan daftar jalur keluar dari setiap perempatan.

### C. Package `algoritma`
- **`PemilihJalur` (Interface)**: Kontrak utama pencarian rute (`cariRute`).
- **`PemilihDijkstra` (Abstract Class)**: Berisi mesin utama algoritma Dijkstra menggunakan `PriorityQueue` dan `HashMap`. Menyediakan method `protected abstract double hitungBiaya(Jalur jalur)` yang wajib diisi oleh kelas turunannya (**Abstraction & Inheritance**).
- **`PemilihJalurTerpendek`**: Meng-override `hitungBiaya()` untuk hanya mengembalikan `waktuNormal` tanpa memperhitungkan kemacetan.
- **`PemilihJalurTercepat`**: Meng-override `hitungBiaya()` dengan rumus `waktuNormal * cekKondisi().getPengaliMacet()` (**Polymorphism**).

### D. Package `app`
- **`SimulasiRambuKota`**: Aplikasi GUI desktop berbasis `javax.swing.*` (`JFrame`, `JPanel`, `JComboBox`, `JTextField`, `JRadioButton`, `JButton`, `JLabel`, `JTextArea`, `JOptionPane`). Mengimpor seluruh package (`model`, `algoritma`, `exception`) dan mencatat riwayat perubahan papan rambu menggunakan `LinkedList<String>`.

---

## 3. Mengapa Menggunakan `pengaliMacet`?

Algoritma Dijkstra selalu memilih rute dengan total bobot terkecil. Tanpa `pengaliMacet`, bobot jalur bersifat statis sehingga jalur yang macet tetap dipilih jika jarak fisiknya pendek.

Contoh pada peta `A -> D`:
- **Kondisi Normal (`B-C` Lancar, pengali `1.0x`):**
  - `A -> B -> C -> D` = `3 + (4 x 1.0) + 2 = 9` **(Terpilih)**
  - `A -> B -> E -> D` = `3 + 5 + 4 = 12`
- **Kondisi Macet (`B-C` Macet, pengali `3.0x`):**
  - `A -> B -> C -> D` = `3 + (4 x 3.0) + 2 = 17`
  - `A -> B -> E -> D` = `3 + 5 + 4 = 12` **(Terpilih sebagai rute alternatif)**

---

## 4. Cara Mengompilasi & Menjalankan Program

### A. Lewat Terminal / Command Prompt
1. Ekstrak file `Proyek_Rambu_Kota_Malang.zip`.
2. Buka terminal di dalam folder `src`:
   ```bash
   cd src
   ```
3. Kompilasi seluruh file `.java` lintas package:
   ```bash
   javac exception/*.java model/*.java algoritma/*.java app/*.java
   ```
4. Jalankan aplikasi GUI Java Swing:
   ```bash
   java app.SimulasiRambuKota
   ```

### B. Lewat NetBeans / IntelliJ IDEA
1. Buat proyek Java baru (Java Application).
2. Salin keempat folder package (`exception`, `model`, `algoritma`, `app`) dari folder `src/` ke dalam folder `Source Packages` (`src`) proyekmu.
3. Klik kanan `app/SimulasiRambuKota.java` lalu pilih **Run File**.

### C. Skenario Pengujian Cepat di GUI
1. Pilih jalur **`B-C`**, isi jumlah kendaraan **`20`**, mode **Jalur Tercepat** -> Papan rambu hijau (`LANCAR`), rute `A -> B -> C -> D`.
2. Ubah jumlah kendaraan jalur **`B-C`** menjadi **`120`** -> Papan rambu merah (`MACET`), rute otomatis berpindah ke `A -> B -> E -> D`.
3. Ubah mode ke **Jalur Terpendek** -> Rute kembali ke `A -> B -> C -> D` (membuktikan *Polymorphism*).
4. Isi jumlah kendaraan **`-50`** -> Muncul pop-up validasi dari `DataTidakValidException` (membuktikan *Exception Handling*).
