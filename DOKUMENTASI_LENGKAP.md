# 📘 Buku Panduan & Dokumentasi Lengkap Aplikasi Bangun Ruang 3D (Edisi Pemula)

Dokumen ini ditulis secara **sangat rinci (langkah demi langkah)** agar siapapun, termasuk orang awam yang baru pertama kali menyentuh Android Studio atau aplikasi ini, dapat memahaminya tanpa kebingungan.

---

## 🚀 BAGIAN 1: Panduan Lengkap Menjalankan Aplikasi di Android Studio

Jika Anda baru pertama kali menggunakan Android Studio, ikuti langkah ini **satu per satu tanpa ada yang terlewat**:

### Tahap 1: Membuka Proyek
1. Buka aplikasi **Android Studio** di laptop/komputer Anda.
2. Saat jendela pertama muncul, klik tombol **"Open"** (biasanya berada di tengah atas atau di menu File -> Open).
3. Akan muncul jendela penelusuran file (seperti Windows Explorer). Carilah folder tempat Anda menyimpan proyek ini, yaitu: `E:\Faisal\skripsi-android`.
4. Klik **sekali** pada folder `skripsi-android` tersebut (pastikan ikon foldernya memiliki logo kecil Android hijau, menandakan itu adalah proyek Android yang valid).
5. Klik **"OK"**.
6. **[PENTING]** Tunggu proses sinkronisasi (*Gradle Sync*). Lihat di pojok kanan bawah layar, akan ada tulisan *Syncing...* atau bilah progres yang berjalan. Proses ini mendownload file-file penting dari internet. **Jangan mengklik apa pun** sampai tulisan tersebut hilang dan muncul pesan *Build: successful*.

### Tahap 2: Menyiapkan HP Android Anda (USB Debugging)
Agar aplikasi bisa ditransfer dari laptop ke HP, Anda harus mengaktifkan mode khusus di HP Anda:
1. Buka aplikasi **Pengaturan (Settings)** di HP Android Anda.
2. Geser ke paling bawah, cari menu **Tentang Ponsel (About Phone)**.
3. Cari tulisan **Nomor Bentukan (Build Number)**. Ketuk tulisan tersebut dengan jari Anda **7 kali berturut-turut dengan cepat** hingga muncul tulisan *"Anda sekarang adalah seorang pengembang!"*.
4. Kembali ke menu Pengaturan sebelumnya, cari menu baru bernama **Opsi Pengembang (Developer Options)**.
5. Masuk ke Opsi Pengembang, cari pengaturan bernama **USB Debugging** (Debugging USB). Aktifkan *switch/toggle* tersebut.

### Tahap 3: Memasang Aplikasi (Build & Run)
1. Colokkan HP Android Anda ke laptop menggunakan kabel data (kabel charger) yang berfungsi baik.
2. Jika di layar HP Anda muncul peringatan *"Izinkan USB Debugging dari komputer ini?"*, berikan centang dan tekan **Izinkan (Allow)**.
3. Kembali ke **Android Studio**. Lihat baris menu paling atas, akan ada kotak *dropdown* yang berisi nama HP Anda (misal: *Samsung SM-A525F* atau *Xiaomi M2010...*). Jika sudah muncul, artinya laptop Anda sudah mengenali HP Anda.
4. Klik tombol **▶️ Play berwarna Hijau** (Run 'app') yang berada persis di sebelah kanan nama HP Anda.
5. Tunggu proses instalasi. Jika berhasil, layar HP Anda akan otomatis membuka aplikasi Bangun Ruang secara ajaib!

---

## 💻 BAGIAN 2: Bedah Total Isi Kodingan (Arsitektur Aplikasi)

Bagaimana aplikasi ini bekerja di balik layar? Kodingan dipecah menjadi beberapa "Kelas" (Class). Berikut adalah penjelasan fungsi setiap kelas dalam bahasa manusia:

### 1. `Interactive3DView.java` (Sistem Visual 3D Utama)
*Berlokasi di: `app/src/main/java/com/faisal/bangunruang/renderer/Interactive3DView.java`*

Ini adalah kelas paling rumit sekaligus paling penting di seluruh aplikasi. Kelas ini tidak menggunakan *game engine* (seperti Unity), melainkan **murni menggambar garis dan bentuk matematika** secara manual di atas Kanvas Android.
- **Fungsi `project(...)`**: Bertugas menghitung matematika murni. Mengubah titik koordinat 3D (X, Y, Z) menjadi koordinat 2D (X, Y) agar bisa digambar di layar HP yang datar. Ini dibantu oleh efek *perspektif* dan *rotasi matriks*.
- **Fungsi `drawFaces()`, `drawEdges()`, `drawVertices()`**: Bertugas menggambar Sisi (wajah), Rusuk (garis), dan Titik Sudut.
- **Fungsi `draw2DShape(...)` [FITUR EDUKASI]**: Ini adalah kecerdasan edukasinya! Jika anak mengklik bagian selimut atau sisi dari bangun ruang yang berputar, fungsi ini dipanggil untuk menghapus bentuk 3D yang miring tersebut, dan menggantinya menjadi **Bangun Datar 2D (seperti Persegi, Juring Lingkaran, dll) yang lurus di tengah layar**.
- **Fungsi `ValueAnimator` (Animasi Nafas)**: Di bagian awal kelas ini, ada pengatur waktu (*timer*) yang bergerak naik turun tiada henti. Timer ini dihubungkan ke gambar agar objek yang dipilih membesar-mengecil (bernafas) dan berkedip, sehingga perhatian anak-anak terfokus ke sana.
- **Fungsi `onTouchEvent(...)`**: Mendengarkan usapan jari. Jika satu jari digeser, ia mengubah variabel *rotasi*. Jika dua jari merenggang (cubit), ia mengubah variabel *skala (zoom)*.

### 2. `ImageUtils.java` (Sistem Mata AI)
*Berlokasi di: `app/src/main/java/com/faisal/bangunruang/utils/ImageUtils.java`*

Ini bertugas sebagai "Mata" dari aplikasi sebelum diserahkan ke Kecerdasan Buatan (AI).
- Mengapa ada kelas ini? Kamera HP memotret gambar berbentuk memanjang (Persegi Panjang), padahal otak AI (ONNX Model) hanya mau menerima gambar berbentuk kotak sempurna (Persegi).
- **Fungsi Utama (`CenterCrop`)**: Memotong paksa gambar persis di bagian tengahnya agar menjadi kotak. Tanpa kode ini, gambar kamera akan *gepeng/peyang*, yang membuat AI salah menebak bangun ruangnya.

### 3. `LearnActivity.java` (Tampilan Layar Belajar)
*Berlokasi di: `app/src/main/java/com/faisal/bangunruang/LearnActivity.java`*

Jika `Interactive3DView` adalah penggambar gambarnya, maka `LearnActivity` adalah **bingkai foto dan ruangan kelasnya**.
- **Mengatur Tata Letak**: Kelas ini memuat gambar 3D di bagian atas, tombol-tombol Tab (Sifat, Rumus, Contoh) di bagian tengah, dan teks informasi di kotak kuning di bagian bawah.
- **Sistem Komunikasi (Listener)**: Ada fungsi `setOnInfoClickListener`. Ini adalah "Kabel Telepon" antara gambar 3D dan kotak teks kuning. Saat anak mengklik gambar Sisi di `Interactive3DView`, kelas ini akan menerima pesan (misal: "Sisi Kubus diklik!"), lalu kelas ini memerintahkan kotak kuning untuk menuliskan rumus luas kubus di layar.

---

## 📖 BAGIAN 3: Panduan Penggunaan Aplikasi (Buku Petunjuk Anak)

Berikut adalah panduan klik-demi-klik (step-by-step) untuk memandu anak SD atau pemula menggunakan aplikasi ini:

### Langkah 1: Memindai Bangun Ruang
1. Buka aplikasi di HP Anda.
2. Di layar awal, Anda akan melihat kamera menyala.
3. Arahkan kamera tepat ke sebuah gambar/benda bangun ruang (misalnya gambar kubus di buku cetak sekolah).
4. Tekan tombol ambil gambar (jika ada) atau tunggu aplikasi mendeteksi secara otomatis.
5. Layar akan memuat sekejap, dan Anda akan dibawa ke **Layar Belajar 3D**.

### Langkah 2: Berinteraksi dengan Objek 3D (Memutar & Zoom)
Di tengah layar, bentuk bangun ruang (misalnya Kubus) akan mulai berputar secara perlahan.
- **Ingin melihat bagian belakang?** Tempelkan satu jari Anda di gambar bangun ruang, lalu **geser jari (usap) ke kiri atau kanan**. Bangun ruang akan berputar mengikuti arah jari Anda.
- **Ingin melihat lebih dekat?** Tempelkan dua jari (jempol dan telunjuk) di layar, lalu rentangkan kedua jari tersebut saling menjauh (gerakan mencubit ke luar). Bangun ruang akan membesar (Zoom In).

### Langkah 3: Menggunakan Fitur Edukasi Jaring-Jaring (2D)
Aplikasi ini memungkinkan siswa "membedah" bangun ruang. Caranya:
1. **Klik salah satu "Titik" (Pojokan)**:
   - Ketuk titik biru di salah satu ujung bentuk.
   - Titik tersebut akan berubah menjadi aura cahaya yang berdetak-detak.
   - Lihat ke kotak kuning di bawah, akan ada tulisan penjelasan mengenai Titik Sudut.
2. **Klik salah satu "Garis" (Rusuk)**:
   - Ketuk garis tepi dari bangun ruang tersebut.
   - Garis tersebut akan berubah menjadi warna oranye terang dan terlihat "memompa" membesar. Kotak kuning akan menjelaskan tentang Rusuk.
3. **Melihat Sisi / Jaring-Jaring 2D (Super Fitur!)**:
   - Ketuk tepat di tengah-tengah ruang/sisi (misalnya area dinding kubus, atau bagian melengkung/selimut pada tabung).
   - **Apa yang terjadi?** Bentuk 3D yang rumit tadi akan lenyap! Sebagai gantinya, **bentuk datar 2D yang lurus** akan muncul di tengah layar sambil bernafas (membesar-mengecil pelan).
   - *Contoh:* Jika Anda mengklik sisi miring Kubus, Anda akan melihat sebuah **Persegi** murni yang rata. Jika Anda mengklik selimut Tabung, akan muncul **Persegi Panjang**.
   - Ini sangat berguna agar anak tahu bahwa bangun ruang 3D sebenarnya dibentuk dari lipatan-lipatan bentuk 2D dasar.
   - Di kotak kuning bawah, akan muncul rumus spesifik untuk bentuk 2D tersebut (misalnya `Luas = Panjang x Lebar`).
4. **Cara kembali ke bentuk 3D semula**:
   - Cukup ketuk *(tap)* dengan pelan jari Anda di bagian putih manapun (latar belakang) di luar gambar bentuk tersebut. Semuanya akan langsung kembali menjadi wujud 3D berputar.

---
*Dokumentasi ini ditulis untuk mempermudah pengerjaan dan pelaporan Skripsi tanpa perlu pusing menerjemahkan istilah teknis kodingan.*
