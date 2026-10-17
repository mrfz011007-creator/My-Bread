# Roadmap My Bread

Tugas tidak dianggap selesai hanya karena source ditulis. Verifikasi dengan CI, tes yang sesuai, dan pemeriksaan APK.

## Milestone 00 — Fondasi dan CI
- [x] Spesifikasi, keputusan, dan roadmap terdokumentasi.
- [x] Fondasi Android Java/XML dan workflow Android CI.
- [x] Build/tes lama terverifikasi pada run #6 dan run #7.
- [x] Wrapper script dan konfigurasi versi 8.9 ditambahkan; CI membangkitkan wrapper JAR dari Gradle yang dipin.
- [ ] Wrapper JAR diperiksa sebagai bagian permanen repository.
- [ ] CI untuk perubahan integrasi terbaru lulus.
**Selesai jika:** workflow bersih menjalankan `./gradlew`, semua tes relevan lulus, dan artifact bisa diunduh.

## Milestone 01 — Perhitungan bisnis dan FIFO
- [x] Perhitungan modal, omzet, margin kotor, expenses, selisih kas.
- [x] FIFO untuk roti terjual dan roti rusak.
- [x] Penyesuaian stok terpisah dari penjualan/kerusakan.
- [x] Tes unit awal dan kasus batas ditambahkan.
- [ ] Verifikasi ulang melalui CI setelah integrasi database.
**Selesai jika:** seluruh tes bisnis/FIFO lulus.

## Milestone 02 — Database dan integritas data
- [x] Tabel SQLite untuk catatan harian, pengeluaran, dan penyesuaian.
- [x] Penyimpanan atomik dan upsert berdasarkan tanggal.
- [x] Edit tanggal lama memperbarui opening stock catatan berikutnya di dalam transaksi yang sama.
- [x] Tes Robolectric untuk integritas data, edit berantai, rollback, dan backup.
- [ ] CI terbaru terverifikasi.
**Selesai jika:** tes database lulus dan operasi gagal tidak merusak data lama.

## Milestone 03 — Formulir dan dashboard
- [x] Dashboard ringkasan keuangan, stok, dan grafik omzet tujuh hari.
- [x] Formulir harian dengan pengeluaran berulang, penyesuaian stok, pratinjau langsung, validasi, dan penyimpanan.
- [ ] Verifikasi UX dan input pada HP.

## Milestone 04 — Riwayat dan laporan
- [x] Riwayat catatan dan alur edit tanggal lama.
- [x] Ringkasan bulanan dan grafik omzet.
- [ ] Uji manual pada HP, termasuk edit historis dan perubahan stok berantai.

## Milestone 05 — Backup dan pemulihan
- [x] Ekspor JSON.
- [x] Validasi backup, pratinjau, konfirmasi eksplisit, dan transaksi pemulihan.
- [x] Tes backup/restore dan penolakan backup tidak valid.
- [ ] Uji impor/ekspor melalui pemilih berkas Android pada HP.

## Milestone 06 — Audit akhir
- [ ] Jalankan ulang tes lengkap dan build bersih pada branch final.
- [ ] Periksa artifact APK dan catat commit.
- [ ] Uji APK di perangkat Android.
- [ ] Perbarui checkpoint dengan hasil aktual.
- [ ] Gabungkan ke main hanya setelah CI lulus dan perubahan diperiksa.

## Batasan
- Offline-first; data disimpan lokal.
- Jangan menaruh rahasia di repository.
- Jangan mengubah proyek RotiKu.
- Harga default: modal Rp1.400, jual Rp2.500, margin kotor awal Rp1.100.
- Selisih kas bukan laba bersih selama masih ada stok yang belum terjual.
