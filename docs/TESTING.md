# Pengujian dan Verifikasi My Bread

## Hasil yang sudah diverifikasi
- [Run #7 — calculator/FIFO branch](https://github.com/mrfz011007-creator/My-Bread/actions/runs/38994628719): sukses.
- [Run #8 — main setelah merge calculator/FIFO](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39043933306): sukses.
- [Run #9 — Gradle Wrapper dan CI](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39044127980): sukses.
- [Run #12 — integrasi database dan UI pada PR](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39044479692): 22 tes, 0 failure/error, build, pemeriksaan APK, dan artifact berhasil.
- [Run #13 — main setelah merge fitur offline MVP](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39044589655): 22 tes, 0 failure/error, build dan artifact berhasil.
- [Run #14 — validasi form dan privasi backup](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39044930209): **26 tes, 0 failure, 0 error, 0 skipped**, build dan artifact berhasil.
- [Run #15 — main setelah merge final](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39045025354): **26 tes, 0 failure, 0 error, 0 skipped**; build APK, pemeriksaan APK, dan unggah artifact berhasil.

## Prosedur CI
- Java 17.
- Gradle Wrapper 8.9.
- `./gradlew --no-daemon --console=plain testDebugUnitTest`.
- Memastikan laporan JUnit XML ada, jumlah tes tidak nol, dan tidak ada failure/error.
- `./gradlew --no-daemon --console=plain assembleDebug`.
- Memastikan APK tidak kosong, arsip valid, dan `classes.dex` ada.
- Mengunggah artifact bernama `My-Bread-debug-apk` selama 14 hari.

## Cakupan tes
1. Perhitungan modal, omzet, margin, pengeluaran, input batas, dan overflow.
2. FIFO, stok lama/baru, dan penyesuaian bertanda.
3. SQLite: penyimpanan catatan, banyak pengeluaran, dan penyesuaian.
4. Edit historis menghitung ulang stok awal hari berikutnya secara atomik.
5. Edit historis yang membuat stok berikutnya tidak cukup membatalkan transaksi.
6. Backup valid, pemulihan, serta penolakan backup tidak valid tanpa mengganti data lama.
7. UI: simpan dinonaktifkan untuk catatan baru yang belum berubah, record edit tanpa perubahan, kuantitas invalid, dan pengeluaran tidak lengkap.

## Status yang belum bisa dibuktikan otomatis
APK belum diuji manual pada perangkat Android. Pemeriksaan CI hanya membuktikan unit test, build, struktur APK, dan unggah artifact; bukan jaminan semua interaksi UI sesuai kebutuhan di HP.

## Catatan artifact
Artifact run #15: `My-Bread-debug-apk`, tersedia hingga 31 Oktober 2026. Buka run di atas dan unduh artifact pada bagian Artifacts.
