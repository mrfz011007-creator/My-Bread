# Roadmap My Bread

Tugas tidak dianggap selesai hanya karena source ditulis. Verifikasi dengan CI, tes yang sesuai, dan pemeriksaan APK.

## Milestone 00 — Fondasi dan CI
- [x] Spesifikasi, keputusan, dan roadmap terdokumentasi.
- [x] Fondasi Android Java dan workflow Android CI.
- [x] Build/tes awal terverifikasi pada run #6, #7, dan #8.
- [x] Gradle Wrapper 8.9 masuk repository dan CI berjalan melalui `./gradlew`.
- [ ] CI untuk integrasi database/UI terbaru lulus.
**Selesai jika:** workflow checkout bersih menjalankan wrapper, semua tes lulus, APK tervalidasi, dan artifact tersedia.

## Milestone 01 — Perhitungan usaha dan FIFO
- [x] Modal, omzet, margin kotor, pengeluaran, selisih kas.
- [x] FIFO untuk roti terjual dan rusak.
- [x] Penyesuaian stok dicatat terpisah dari penjualan/kerusakan.
- [x] Tes unit termasuk kondisi batas.
- [ ] Verifikasi ulang di CI setelah integrasi database.

## Milestone 02 — Database dan integritas
- [x] Tabel SQLite untuk catatan harian, pengeluaran, dan penyesuaian stok.
- [x] Upsert dan penyimpanan atomik.
- [x] Edit catatan lama menghitung ulang stok awal tanggal berikutnya.
- [x] Rollback jika perubahan membuat catatan berikutnya tidak valid.
- [x] Tes Robolectric untuk integritas, cascading stock, rollback, dan backup.
- [ ] Semua tes lulus di CI untuk commit terbaru.

## Milestone 03 — Pencatatan dan dashboard
- [x] Dashboard dengan ringkasan uang, stok, dan grafik omzet 7 hari.
- [x] Formulir harian dengan pratinjau langsung dan pengeluaran terpisah.
- [x] Beberapa penyesuaian stok beserta alasan.
- [ ] Uji input dan pengalaman penggunaan di HP.

## Milestone 04 — Riwayat dan laporan
- [x] Riwayat harian dan alur edit berdasarkan tanggal.
- [x] Ringkasan bulanan serta grafik omzet.
- [ ] Uji edit tanggal lama dan stok berantai di HP.

## Milestone 05 — Backup dan pemulihan
- [x] Ekspor JSON.
- [x] Validasi backup, pratinjau dan konfirmasi sebelum penggantian data.
- [x] Pemulihan dalam transaksi dan tes backup valid/rusak.
- [ ] Uji impor/ekspor melalui pemilih berkas Android.

## Milestone 06 — Audit akhir
- [ ] CI untuk seluruh perubahan terbaru sukses.
- [ ] Artifact APK ada dan isinya tervalidasi.
- [ ] APK berhasil dipasang dan diuji di perangkat Android.
- [ ] Catat batasan dan hasil aktual di checkpoint.
- [ ] Gabungkan integrasi database/UI ke `main` setelah CI sukses dan perubahan diperiksa.

## Batasan proyek
- Offline-first dengan data lokal.
- Jangan menyimpan rahasia ke repo.
- Jangan mengubah proyek RotiKu.
- Harga default: modal Rp1.400, jual Rp2.500, margin kotor awal Rp1.100.
- Selisih kas bukan laba bersih jika stok belum terjual masih ada.
