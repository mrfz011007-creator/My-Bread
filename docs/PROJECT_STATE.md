# Status Proyek My Bread

Terakhir diperbarui: 2026-10-17

## Tujuan dan batasan
My Bread adalah aplikasi Android offline untuk pencatatan usaha roti. Data bisnis disimpan lokal. Proyek ini berdiri sendiri dari RotiKu; jangan mengubah repository atau file RotiKu.

## Bukti pada main
- [x] PR #1 kalkulator bisnis/FIFO digabung setelah CI sukses.
- [x] PR #2 Gradle Wrapper 8.9 dan pemeriksaan hasil tes di CI digabung.
- [x] PR #3 database, formulir, dashboard, riwayat, laporan dan backup digabung setelah CI sukses.
- [x] PR #4 validasi tombol simpan, konfirmasi perubahan penting, tes UI, dan privasi backup digabung setelah CI sukses.
- [x] Main commit `eb22946770ba1563aac9a5490ff037e9178e360e` diverifikasi oleh [GitHub Actions run #15](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39045025354).
- [x] Run #15: **26 tes, 0 failure, 0 error, 0 skipped**; APK debug dibangun, isi APK diperiksa, dan artifact berhasil diunggah.
- [x] Artifact `My-Bread-debug-apk` tersedia di run #15. Masa berlaku artifact sampai 31 Oktober 2026.
- [ ] APK belum dipasang dan diuji manual pada HP Android. Keberhasilan CI bukan pengganti uji perangkat.

## Teknologi
- Java 17 dan Android Views.
- SQLite lokal melalui `SQLiteOpenHelper`.
- Android Gradle Plugin 8.7.3, compileSdk 35, minSdk 23, targetSdk 28.
- Gradle Wrapper 8.9.
- JUnit dan Robolectric untuk tes otomatis, termasuk database dan validasi formulir.
- Tidak membutuhkan backend, login, Firebase, Kotlin, Jetpack Compose, atau API AI.
- Android system backup dimatikan; cadangan dikelola melalui ekspor/impor JSON di aplikasi.

## Implementasi saat ini
- Kalkulator modal, omzet, margin kotor, pengeluaran, dan selisih kas.
- FIFO untuk roti lama dan baru, termasuk roti rusak; penyesuaian stok tetap item terpisah dengan alasan.
- SQLite untuk catatan harian per tanggal, daftar pengeluaran, dan daftar penyesuaian.
- Penyimpanan atomik/upsert per tanggal dan hitung ulang stok awal pada tanggal berikutnya saat catatan historis diedit.
- Bila edit historis membuat stok berikutnya tidak cukup, penyimpanan dibatalkan dan transaksi di-rollback.
- Dashboard keuangan/stok dengan grafik omzet tujuh hari.
- Formulir harian dengan pratinjau langsung, input multi-pengeluaran, penyesuaian stok, dan tombol simpan yang nonaktif jika invalid atau tidak berubah.
- Riwayat, edit, laporan bulanan, ekspor JSON, serta pemulihan backup setelah validasi, pratinjau, dan konfirmasi.
- Pengeditan historis dengan dampak hari berikutnya memerlukan konfirmasi sebelum penyimpanan.

## Langkah selanjutnya
1. Unduh artifact dari run #15 atau file APK yang disediakan pada sesi ini.
2. Pasang APK di HP Android dan uji input/simpan, edit tanggal lama, FIFO/roti rusak, pengeluaran terpisah, laporan, serta ekspor dan impor backup.
3. Jika menemukan masalah, catat layar, input, hasil yang diharapkan, dan hasil aktual; lalu perbaiki dan jalankan ulang CI.
4. Jangan menyebut aplikasi siap rilis sampai pengujian manual pada HP selesai.

## Referensi
- [Repository](https://github.com/mrfz011007-creator/My-Bread)
- [Workflow dan artifact GitHub Actions](https://github.com/mrfz011007-creator/My-Bread/actions)
- [Run #15 pada main](https://github.com/mrfz011007-creator/My-Bread/actions/runs/39045025354)
- [Roadmap](ROADMAP.md), [spesifikasi](SPECIFICATION.md), dan [pedoman tes](TESTING.md)
