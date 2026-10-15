# Pengujian dan Verifikasi My Bread

## Status awal
Fondasi source Android dan workflow CI sudah ditambahkan. Pemeriksaan daftar GitHub Actions runs saat checkpoint mengembalikan nol workflow run; karena itu **build dan tes belum terverifikasi**. Ini belum membuktikan build gagal; hanya berarti belum ada hasil run yang dapat diperiksa saat pengecekan.

## Prinsip bukti
Untuk setiap klaim build atau tes, catat:
- commit yang diuji;
- perintah atau workflow yang dijalankan;
- hasil aktual (lulus/gagal);
- tautan log workflow atau artifact jika tersedia;
- kegagalan dan tindak lanjutnya.

## Lapisan tes yang direncanakan
1. **Unit test logika bisnis:** rumus uang, validasi kuantitas, dan kalkulasi ringkasan.
2. **Database test:** pembuatan skema, pembacaan/penulisan, foreign key, migrasi, transaksi, dan rollback.
3. **Tes FIFO:** roti terjual mengurangi lot tertua lebih dahulu; roti rusak juga mengurangi lot tertua.
4. **Tes riwayat:** tanggal yang sama memperbarui catatan yang ada; edit tanggal lama menghitung ulang dampak tanggal selanjutnya.
5. **Tes pengeluaran:** banyak item per hari, edit item, total pengeluaran, dan penyimpanan atomik.
6. **Tes backup/restore:** backup valid, berkas rusak/tidak kompatibel, konfirmasi, dan pemulihan gagal tanpa menghilangkan data lama.
7. **Build CI:** build dari keadaan repository yang bersih di GitHub Actions dan unggah APK artifact.
8. **Pemeriksaan manual:** alur utama di perangkat Android bila perangkat/emulator tersedia.

## Kasus penerimaan minimum
- Nilai negatif ditolak.
- Penjualan + roti rusak tidak melebihi stok yang tersedia.
- FIFO berlaku untuk penjualan dan roti rusak.
- Kegagalan penyimpanan membatalkan seluruh perubahan terkait.
- Catatan tanggal yang sama tidak terduplikasi.
- Edit catatan lama tidak meninggalkan stok tanggal berikutnya dalam keadaan tidak valid.
- Restore backup gagal tidak mengubah data lokal sebelumnya.
- Build CI sukses dan artifact APK benar-benar tersedia sebelum menyatakan APK berhasil dibangun.

## Status workflow awal
Workflow: `.github/workflows/android-ci.yml`
- Menyiapkan Java 17.
- Memasang Gradle 8.9 menggunakan `gradle/actions/setup-gradle@v4` karena wrapper belum ada.
- Menjalankan `gradle testDebugUnitTest`.
- Menjalankan `gradle assembleDebug`.
- Mengunggah `app/build/outputs/apk/debug/app-debug.apk` sebagai artifact.

Status run: belum tersedia pada saat pemeriksaan checkpoint. Periksa halaman Actions lagi sebelum melanjutkan.

## Format catatan hasil
Tambahkan catatan ringkas per commit atau milestone:
- Tanggal:
- Commit:
- Cakupan:
- Perintah/workflow:
- Hasil aktual:
- Bukti:
- Masalah tersisa:

Jangan mengarang hasil yang tidak tersedia. Jika tes tidak bisa dijalankan, tulis dengan jelas bahwa tes belum dijalankan dan alasannya.
