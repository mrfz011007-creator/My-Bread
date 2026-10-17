# My Bread

Aplikasi Android offline untuk pencatatan usaha roti. Data disimpan di perangkat menggunakan SQLite; aplikasi tidak memerlukan akun, backend, atau internet untuk pencatatan harian.

## Fitur
- Dashboard ringkasan modal, omzet, margin kotor, pengeluaran, selisih kas, dan stok.
- Formulir harian dengan pratinjau otomatis.
- Beberapa item pengeluaran dalam satu hari.
- FIFO untuk roti lama dan baru; penyesuaian stok tersimpan terpisah dengan alasan.
- Riwayat yang dapat diedit, dengan hitung ulang stok awal hari-hari berikutnya.
- Laporan bulanan dan grafik omzet 7 hari.
- Ekspor backup JSON dan pemulihan dengan validasi, pratinjau, serta konfirmasi.

## Build
- Java 17, Android Gradle Plugin 8.7.3, compile SDK 35.
- Gradle Wrapper 8.9: `./gradlew testDebugUnitTest` dan `./gradlew assembleDebug`.
- GitHub Actions menjalankan tes, memeriksa APK, dan mengunggah artifact `My-Bread-debug-apk`.
- Lihat [GitHub Actions](https://github.com/mrfz011007-creator/My-Bread/actions) untuk status run terbaru.

## Nilai awal usaha
- Modal per roti baru: Rp1.400.
- Harga jual per roti: Rp2.500.
- Margin kotor awal per roti terjual: Rp1.100.
- Selisih kas bukan laba bersih selama masih ada roti yang belum terjual.

Proyek ini berdiri sendiri. Jangan mengubah repository RotiKu.
