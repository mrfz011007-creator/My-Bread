# Pengujian dan Verifikasi My Bread

## Bukti dasar sebelum integrasi
- GitHub Actions run #6 pada `main`: success; tes unit, build APK debug, unggah artifact lulus. [Run #6](https://github.com/mrfz011007-creator/My-Bread/actions/runs/38818735292)
- GitHub Actions run #7 pada `feature/business-calculations`: success; tes kalkulator/FIFO, build APK debug, unggah artifact lulus. [Run #7](https://github.com/mrfz011007-creator/My-Bread/actions/runs/38994628719)
- Artifact bernama `My-Bread-debug-apk` tersedia saat audit dilakukan.
- Bukti tersebut berlaku untuk commit lama, bukan untuk perubahan database/UI setelahnya.

## CI saat ini
- Java 17.
- Gradle 8.9 dipin lewat setup-gradle.
- Workflow membangkitkan Gradle Wrapper pada runner bersih, lalu menjalankan `./gradlew testDebugUnitTest` dan `./gradlew assembleDebug`.
- Artifact debug APK diunggah selama 14 hari. Tidak ada klaim sukses untuk commit terbaru sampai run baru selesai.

## Cakupan tes yang diharapkan
1. Perhitungan modal, omzet, margin, expenses, nilai batas, dan overflow.
2. FIFO untuk stok lama, stok baru, dan penyesuaian bertanda.
3. SQLite: simpan/baca banyak item pengeluaran dan penyesuaian.
4. Edit catatan lama menghitung ulang opening stock berikutnya.
5. Jika edit lama membuat hari berikutnya tidak valid, transaksi harus rollback seluruh perubahan.
6. Ekspor backup, pemulihan, dan penolakan backup yang tidak valid tanpa mengganti data.
7. CI membangun APK dari checkout bersih dan mengunggah artifact.

## Periksa klaim
Catat commit yang diuji, workflow URL, hasil unit test, hasil build, dan artifact. APK berhasil dibuat hanya bila langkah build sukses dan artifact berkas tersedia. APK belum dianggap teruji pada perangkat sampai alur utama dicoba pada Android.
