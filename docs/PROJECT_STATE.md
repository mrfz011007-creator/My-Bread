# Status Proyek My Bread

Terakhir diperbarui: 2026-10-16

## Tujuan
Membangun aplikasi Android **My Bread** untuk pencatatan usaha roti yang bekerja offline dan menyimpan data secara lokal. Repository ini terpisah dari proyek RotiKu; jangan mengubah repository atau file RotiKu.

## Status saat checkpoint
- [x] Repository GitHub tersedia: `mrfz011007-creator/My-Bread`.
- [x] Spesifikasi awal, roadmap, kebijakan pengujian, dan keputusan dicatat dalam dokumentasi.
- [x] Fondasi source Android awal ditambahkan: konfigurasi Gradle, manifest, Activity, dan layout XML.
- [x] Workflow GitHub Actions awal ditambahkan untuk tes unit, build APK debug, dan unggah artifact.
- [ ] Gradle Wrapper belum ditambahkan; workflow sementara memasang Gradle 8.9 melalui action resmi.
- [ ] Workflow CI sudah terpicu, tetapi run yang terlihat masih berstatus berjalan; hasil akhir belum diverifikasi.
- [ ] APK belum dikonfirmasi berhasil dibangun atau diuji pada perangkat.
- [ ] Database, aturan stok/FIFO, pengeluaran, riwayat, laporan, dan backup belum diimplementasikan.

**Penting:** status di atas adalah catatan kondisi yang diketahui. Jangan menyebut build, tes, APK, atau fitur berhasil sebelum ada bukti hasil yang dapat diperiksa.

## Struktur teknis awal
- Bahasa: Java.
- Antarmuka: layout XML Android.
- Penyimpanan yang direncanakan: SQLite lokal; belum diimplementasikan.
- Build: Gradle; saat ini workflow memakai Gradle 8.9 yang dipasang oleh action karena Gradle Wrapper belum tersedia.
- CI: `.github/workflows/android-ci.yml` menjalankan tes unit, build APK debug, dan mengunggah APK sebagai artifact.
- Package aplikasi: `com.fariz.mybread`.
- Konfigurasi awal: compile SDK 35, min SDK 23, target SDK 28, Java 17.
- Target awal: offline-first, tanpa akun, backend, Firebase, Kotlin, Jetpack Compose, atau API AI.

Keputusan teknis dapat direvisi bila ada alasan yang jelas, tetapi revisi harus dicatat di `DECISIONS.md`.

## Cara melanjutkan di sesi baru
1. Baca `PROJECT_STATE.md`, `ROADMAP.md`, `SPECIFICATION.md`, `TESTING.md`, dan `DECISIONS.md`.
2. Periksa branch, commit terbaru, struktur file, workflow, serta hasil tes/build yang benar-benar tersedia di GitHub.
3. Periksa status workflow GitHub Actions sebelum menyimpulkan CI berhasil atau gagal.
4. Pilih tugas paling awal yang belum selesai menurut roadmap.
5. Kerjakan dalam perubahan kecil yang bisa diperiksa; jangan mengulang pekerjaan yang sudah terbukti selesai.
6. Jalankan pemeriksaan yang relevan dan catat hasil aktualnya.
7. Perbarui dokumen checkpoint ini setelah perubahan diverifikasi.

## Langkah berikutnya
1. Periksa hasil akhir run CI yang sedang berjalan dan perbaiki kegagalan bila ada.
2. Tambahkan Gradle Wrapper setelah jalur bootstrap tersedia, lalu ubah CI untuk menggunakan `./gradlew`.
3. Lanjutkan ke skema database dan aturan usaha sesuai `ROADMAP.md`.
