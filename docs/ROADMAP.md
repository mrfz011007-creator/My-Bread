# Roadmap My Bread

Status diperbarui 17 Oktober 2026. Sebuah fitur dianggap lengkap dari sisi kode ketika tes yang sesuai lulus di CI; kesiapan rilis tetap memerlukan uji perangkat.

## Milestone 00 — Fondasi dan build berulang
- [x] Spesifikasi, keputusan, dan checkpoint.
- [x] Android Java/Views dan Gradle Wrapper 8.9.
- [x] GitHub Actions menjalankan unit test, memeriksa laporan JUnit, membangun APK, memvalidasi isi APK, dan mengunggah artifact.
- [x] Run #15 di `main` sukses untuk commit `eb22946770ba1563aac9a5490ff037e9178e360e`.

## Milestone 01 — Kalkulasi dan stok FIFO
- [x] Modal dihitung dari seluruh roti baru yang dibawa.
- [x] Omzet, margin kotor, pengeluaran, dan selisih kas dihitung.
- [x] FIFO berlaku untuk penjualan dan roti rusak.
- [x] Penyesuaian stok terpisah dari penjualan/kerusakan.
- [x] Tes unit dan kasus batas lulus di CI.

## Milestone 02 — Database dan integritas
- [x] SQLite untuk catatan harian, pengeluaran, dan penyesuaian.
- [x] Upsert per tanggal dan perubahan atomik.
- [x] Perhitungan ulang opening stock setelah edit historis.
- [x] Rollback ketika perubahan membuat stok hari berikutnya tidak cukup.
- [x] Tes database Robolectric lulus.

## Milestone 03 — Pencatatan dan dashboard
- [x] Dashboard ringkasan uang dan stok.
- [x] Grafik omzet tujuh hari.
- [x] Formulir harian dengan pratinjau langsung, pengeluaran berulang, dan penyesuaian stok.
- [x] Tombol simpan nonaktif bila input invalid atau data tidak berubah.
- [ ] Uji kegunaan dan input pada HP.

## Milestone 04 — Riwayat dan laporan
- [x] Riwayat catatan serta edit harian.
- [x] Peringatan saat edit historis memengaruhi hari berikutnya.
- [x] Ringkasan laporan bulanan dan grafik.
- [ ] Uji edit historis/stock chain pada HP.

## Milestone 05 — Backup dan pemulihan
- [x] Ekspor JSON.
- [x] Validasi file dan pratinjau sebelum impor.
- [x] Konfirmasi sebelum mengganti semua data lokal.
- [x] Pemulihan dengan transaksi dan tes untuk backup invalid.
- [x] Android system backup dinonaktifkan supaya alur cadangan dikelola eksplisit melalui aplikasi.
- [ ] Uji pemilih berkas, ekspor, dan impor di HP.

## Milestone 06 — Verifikasi akhir
- [x] PR kalkulasi/FIFO, wrapper/CI, offline MVP, dan validasi form telah digabung ke `main`.
- [x] Run #15 pada `main`: 26 tes lulus, APK debug dibangun, pemeriksaan isi APK lulus, artifact diunggah.
- [ ] Pasang dan uji APK pada perangkat Android.
- [ ] Selesaikan masalah UX atau integrasi yang hanya terlihat pada perangkat sebelum menyatakan siap rilis.

## Batasan tetap
- Offline-first dengan database lokal.
- Jangan menaruh rahasia di repository.
- Jangan mengubah proyek RotiKu.
- Harga awal: modal Rp1.400, jual Rp2.500, margin kotor awal Rp1.100.
- Selisih kas bukan laba bersih selama masih ada stok yang belum terjual.
