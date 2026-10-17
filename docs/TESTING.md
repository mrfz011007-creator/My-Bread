# Pengujian dan Verifikasi My Bread

## Bukti CI dasar
- [Run #6 pada main](https://github.com/mrfz011007-creator/My-Bread/actions/runs/38818735292): sukses; unit test, build APK debug, dan unggah artifact.
- [Run #7 pada feature/business-calculations](https://github.com/mrfz011007-creator/My-Bread/actions/runs/38994628719): sukses; kalkulator/FIFO, build APK debug, dan unggah artifact.
- [Run #8 pada main](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39043933306): sukses; branch main setelah merge kalkulator/FIFO.
- [Run #9 pada build/gradle-wrapper-8.9](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39044127980): sukses untuk perubahan wrapper/CI saja. Bukti ini bukan validasi kode database/UI setelah perubahan berikutnya.

## Workflow sekarang
- Java 17.
- Gradle Wrapper 8.9 di-commit ke repository.
- Menjalankan `./gradlew --no-daemon --console=plain testDebugUnitTest`.
- Memeriksa laporan XML agar jumlah tes tidak nol dan tidak ada failure/error.
- Menjalankan `./gradlew --no-daemon --console=plain assembleDebug`.
- Memastikan APK tidak kosong, arsip ZIP APK valid, dan `classes.dex` tersedia.
- Mengunggah `app-debug.apk` sebagai artifact selama 14 hari.

## Cakupan tes pada branch integrasi
1. Hitungan modal, omzet, margin, expense total, batas input, dan overflow.
2. FIFO untuk stok lama, stok baru, serta penyesuaian stok bertanda.
3. SQLite: menyimpan beberapa item pengeluaran dan penyesuaian stok.
4. Edit catatan lama mengubah stok awal hari-hari berikutnya secara atomik.
5. Edit yang membuat stok lanjutan tidak cukup harus rollback tanpa mengubah data sebelumnya.
6. Backup/restore berhasil dan backup tidak valid ditolak sebelum data lama diganti.
7. Build bersih menghasilkan APK yang valid dan artifact yang dapat diunduh.

## Kriteria penerimaan
Catat SHA commit, status workflow, hasil unit test, hasil build, dan tautan artifact. Build hanya dinyatakan berhasil bila langkah build dan pemeriksaan APK lulus serta artifact tersedia. APK belum dianggap teruji pada perangkat sampai alur utama diuji di HP Android.
