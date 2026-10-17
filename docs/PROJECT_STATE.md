# Status Proyek My Bread

Terakhir diperbarui: 2026-10-17

## Tujuan
Aplikasi Android offline untuk pencatatan usaha roti. Data disimpan lokal. Repository ini terpisah dari RotiKu; jangan mengubah repository atau file RotiKu.

## Status verifikasi sebelumnya
- [x] GitHub Actions run #6 pada `main` sukses; artifact debug APK tersedia.
- [x] GitHub Actions run #7 pada `feature/business-calculations` sukses; tes unit, build APK debug, dan unggah artifact berhasil.
- [x] Branch fitur berisi kalkulator bisnis, kalkulator FIFO, dan 11 tes unit awal.
- [ ] APK yang telah dibuat belum diuji secara manual pada HP.
- [ ] Gradle Wrapper binary belum disimpan permanen di repository. Workflow membangkitkan wrapper dari Gradle 8.9 yang dipin sebelum menjalankan `./gradlew`.
- [ ] Integrasi database dan antarmuka baru menunggu bukti CI setelah perubahan terbaru.

## Teknologi
- Java, layout XML tersedia sebagai sumber daya; layar aktif dirender menggunakan Android Views.
- SQLite lokal via `SQLiteOpenHelper`.
- Android Gradle Plugin 8.7.3, compileSdk 35, minSdk 23, targetSdk 28, Java 17.
- Gradle 8.9: kompatibel dengan AGP 8.7.3; dijalankan lewat wrapper yang dibangkitkan pada runner CI.
- Robolectric untuk pengujian database lokal tanpa emulator fisik.
- Tidak ada backend, login, Firebase, Kotlin, Jetpack Compose, atau API AI.

## Fitur yang dibuat pada cabang kerja
- Kalkulasi modal, omzet, margin kotor, pengeluaran, selisih kas, stok FIFO, dan penyesuaian stok.
- Database harian dengan transaksi, beberapa pengeluaran, beberapa penyesuaian stok, dan upsert per tanggal.
- Pengeditan catatan tanggal lama menghitung ulang stok awal catatan berikutnya dalam satu transaksi. Bila data berikutnya menjadi tidak valid, semua perubahan dibatalkan.
- Dashboard, formulir harian dengan pratinjau, riwayat, laporan bulanan, grafik omzet 7 hari, ekspor JSON, dan pemulihan backup dengan pratinjau + konfirmasi.
- Tes database: transaksi, cascade stok, rollback saat edit tak valid, dan backup/restore.

## Lanjutkan dari sini
1. Periksa run GitHub Actions terbaru dan log setiap langkah, bukan hanya status PR.
2. Pastikan artifact debug APK diunggah dan belum kedaluwarsa.
3. Perbaiki setiap error CI sebelum merge.
4. Jika build lulus, uji alur input, edit tanggal lama, pengeluaran, FIFO, laporan, ekspor/impor backup di HP.
5. Jangan menyebut fitur atau hasil berhasil tanpa bukti aktual.
