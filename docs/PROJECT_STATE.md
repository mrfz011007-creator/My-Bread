# Status Proyek My Bread

Terakhir diperbarui: 2026-10-16

## Tujuan
Membangun aplikasi Android **My Bread** untuk pencatatan usaha roti yang bekerja offline dan menyimpan data secara lokal. Repository ini terpisah dari proyek RotiKu; jangan mengubah repository atau file RotiKu.

## Status saat checkpoint
- [x] Repository GitHub tersedia: `mrfz011007-creator/My-Bread`.
- [x] Repository diperiksa sebelum dokumentasi dibuat.
- [x] Spesifikasi awal dan roadmap dicatat dalam dokumentasi.
- [ ] Struktur aplikasi Android belum dibuat.
- [ ] Gradle build belum disiapkan atau diverifikasi.
- [ ] GitHub Actions belum disiapkan atau dijalankan.
- [ ] APK belum berhasil dibangun dan belum diuji.
- [ ] Fitur aplikasi belum diimplementasikan.
- [ ] Tes otomatis belum dibuat atau dijalankan.

**Penting:** status di atas adalah catatan kondisi yang diketahui. Jangan menyebut build, tes, APK, atau fitur berhasil sebelum ada bukti hasil yang dapat diperiksa.

## Keputusan teknis awal
- Bahasa: Java.
- Antarmuka: layout XML Android.
- Penyimpanan: SQLite lokal.
- Build: Gradle dan Gradle Wrapper.
- CI: GitHub Actions untuk build dan tes, lalu unggah APK sebagai artifact.
- Target awal: offline-first, tanpa akun, backend, Firebase, Kotlin, Jetpack Compose, atau API AI.

Keputusan teknis dapat direvisi bila ada alasan yang jelas, tetapi revisi harus dicatat di `DECISIONS.md`.

## Cara melanjutkan di sesi baru
1. Baca `PROJECT_STATE.md`, `ROADMAP.md`, `SPECIFICATION.md`, `TESTING.md`, dan `DECISIONS.md`.
2. Periksa branch, commit terbaru, struktur file, workflow, serta hasil tes/build yang benar-benar tersedia di GitHub.
3. Pilih tugas paling awal yang belum selesai menurut roadmap.
4. Kerjakan dalam perubahan kecil yang bisa diperiksa; jangan mengulang pekerjaan yang sudah terbukti selesai.
5. Jalankan pemeriksaan yang relevan dan catat hasil aktualnya.
6. Perbarui dokumen checkpoint ini setelah perubahan diverifikasi.

## Langkah berikutnya
Mulai Milestone 00 pada `ROADMAP.md`: buat fondasi proyek Android minimal yang dapat dibangun, beserta konfigurasi tes dan workflow CI. Sebelum membuat struktur, periksa kembali kondisi repository agar tidak menimpa file yang sudah ada.
