# Roadmap My Bread

Roadmap ini adalah urutan kerja dan kriteria penerimaan. Tugas tidak dianggap selesai hanya karena kode ditulis; hasil harus diverifikasi.

## Milestone 00 — Spesifikasi dan fondasi
- [x] Catat spesifikasi awal dan aturan bisnis.
- [x] Tetapkan checkpoint dan prosedur melanjutkan pekerjaan.
- [x] Buat struktur proyek Android minimal menggunakan Java dan XML.
- [x] Tambahkan konfigurasi Gradle awal.
- [x] Tambahkan workflow GitHub Actions untuk tes, build APK, dan unggah artifact.
- [ ] Tambahkan Gradle Wrapper; saat ini workflow memasang Gradle 8.9 secara langsung.
- [ ] Buktikan tes dan build berhasil di CI.
**Status:** sebagian besar fondasi source sudah dibuat, tetapi milestone belum selesai karena wrapper dan bukti CI belum ada.
**Selesai jika:** source project tersedia, wrapper terkonfigurasi, workflow berjalan, tes lulus, dan log/artifact menunjukkan hasil aktual.

## Milestone 01 — Build APK berulang
- [ ] Workflow dapat membangun APK dari keadaan repository yang bersih.
- [ ] APK debug diunggah sebagai artifact GitHub Actions.
- [ ] Nama artifact dan petunjuk pengambilan APK didokumentasikan.
- [ ] Build dapat diulang tanpa bergantung pada file lokal yang tidak dilacak Git.
**Selesai jika:** workflow sukses dan APK artifact benar-benar tersedia serta dapat diunduh.

## Milestone 02 — Database dan aturan usaha
- [ ] Rancang skema SQLite dan migrasi versi database.
- [ ] Simpan catatan harian, pengeluaran, lot stok, dan penyesuaian stok.
- [ ] Terapkan validasi kuantitas serta aturan FIFO.
- [ ] Terapkan transaksi atomik untuk penyimpanan dan perubahan data terkait.
- [ ] Tambahkan tes unit/database untuk hitungan, stok, FIFO, dan kegagalan transaksi.
**Selesai jika:** tes otomatis mencakup aturan bisnis penting dan seluruh tes relevan lulus.

## Milestone 03 — Pencatatan harian dan dashboard
- [ ] Buat dashboard ringkasan.
- [ ] Buat formulir pencatatan harian dengan pratinjau perhitungan.
- [ ] Validasi input dan cegah penyimpanan catatan yang tidak valid.
- [ ] Tampilkan stok sisa dan rincian hitungan.
**Selesai jika:** alur input, validasi, simpan, buka kembali, dan ringkasan diuji pada perangkat/emulator atau melalui tes yang sesuai.

## Milestone 04 — Riwayat dan laporan
- [ ] Tampilkan daftar catatan berdasarkan tanggal.
- [ ] Izinkan pengeditan catatan lama dan pengeluaran.
- [ ] Saat catatan lama diubah, hitung ulang dampak pada tanggal sesudahnya.
- [ ] Tolak atau tangani dengan aman perubahan yang membuat stok berikutnya tidak valid.
- [ ] Tampilkan laporan dan grafik ringkas.
**Selesai jika:** tes membuktikan data tanggal lain tidak tertimpa, tidak ada duplikasi tanggal, dan perubahan berantai ditangani benar.

## Milestone 05 — Backup dan pemulihan
- [ ] Ekspor data ke berkas backup.
- [ ] Validasi format dan isi backup sebelum pemulihan.
- [ ] Tampilkan pratinjau dan minta konfirmasi sebelum mengganti data lokal.
- [ ] Pulihkan data secara atomik; bila berkas tidak valid atau pemulihan gagal, data lama tetap utuh.
- [ ] Tambahkan tes untuk backup valid, backup rusak, dan kegagalan pemulihan.
**Selesai jika:** seluruh skenario pemulihan lulus dan kegagalan tidak merusak data yang ada.

## Milestone 06 — Audit dan rilis
- [ ] Jalankan seluruh tes otomatis.
- [ ] Periksa validasi, transaksi database, migrasi, penanganan error, dan privasi data.
- [ ] Verifikasi build bersih dan APK artifact.
- [ ] Uji alur utama di perangkat Android bila memungkinkan.
- [ ] Dokumentasikan batasan yang belum terselesaikan.
**Selesai jika:** hasil audit dan build punya bukti, masalah kritis ditangani, dan cara instalasi APK jelas.

## Aturan pengerjaan
- Kerjakan milestone secara berurutan kecuali ada dependensi yang terdokumentasi.
- Hindari refactor besar yang tidak dibutuhkan.
- Jangan menambahkan layanan online atau dependensi berat tanpa alasan.
- Jangan pernah menyimpan token, kata sandi, atau rahasia di source code.
- Jangan mengubah proyek RotiKu.
- Setiap perubahan harus punya pemeriksaan yang sesuai dan checkpoint terbaru.
