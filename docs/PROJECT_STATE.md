# Status Proyek My Bread

Terakhir diperbarui: 2026-10-17

## Tujuan
Aplikasi Android offline untuk pencatatan usaha roti. Data disimpan lokal. Repository ini terpisah dari RotiKu; jangan mengubah repository atau file RotiKu.

## Bukti build sebelumnya
- [x] GitHub Actions run #6 pada `main` sukses; tes, build APK debug, dan unggah artifact berhasil.
- [x] GitHub Actions run #7 pada `feature/business-calculations` sukses; kalkulator/FIFO, build APK debug, dan unggah artifact berhasil.
- [x] PR #1 (kalkulator bisnis + FIFO) telah digabung ke `main` setelah CI sukses.
- [x] PR #2 menambahkan Gradle Wrapper 8.9 dan pemeriksaan laporan hasil unit test.
- [ ] Build/tes untuk integrasi database dan UI masih menunggu run CI terbaru.
- [ ] APK belum diuji manual pada HP.

## Teknologi
- Java dan Android Views; sumber daya XML untuk konfigurasi/layout.
- SQLite lokal melalui `SQLiteOpenHelper`.
- Android Gradle Plugin 8.7.3, compileSdk 35, minSdk 23, targetSdk 28, Java 17.
- Gradle Wrapper 8.9 telah ditambahkan ke repo. Versi 8.9 sesuai dengan AGP 8.7.3.
- JUnit dan Robolectric untuk pengujian bisnis serta database tanpa emulator fisik.
- Tidak ada backend, login, Firebase, Kotlin, Jetpack Compose, atau API AI.

## Implementasi pada branch kerja integrasi
- Kalkulasi modal, omzet, margin kotor, pengeluaran, selisih kas, FIFO, dan penyesuaian stok.
- Database SQLite untuk catatan tanggal, beberapa pengeluaran, dan beberapa penyesuaian stok.
- Penyimpanan atomik/upsert per tanggal; perubahan tanggal lama mengalirkan ulang stok awal hari berikutnya di dalam transaksi.
- Bila edit historis membuat data stok sesudahnya tidak valid, transaksi dibatalkan agar data sebelumnya tetap utuh.
- Dashboard, formulir harian dengan pratinjau, riwayat, laporan bulanan, grafik omzet 7 hari, ekspor JSON, dan pemulihan setelah validasi/pratinjau/konfirmasi.
- Tes unit untuk kalkulator/FIFO dan tes Robolectric untuk simpan-baca, perubahan stok berantai, rollback, serta backup/restore.
- Implementasi baru belum boleh dianggap terverifikasi sampai CI untuk commit baru sukses dan artifact tersedia.

## Lanjutkan dari sini
1. Periksa workflow Actions terbaru dan log per langkah.
2. Pastikan semua unit test lulus, APK bisa dibangun, isi APK valid, dan artifact tersedia.
3. Perbaiki error CI bila ada, lalu jalankan ulang.
4. Setelah CI lulus, uji input, edit historis, FIFO, pengeluaran, laporan, dan backup/restore pada HP.
5. Jangan menyebut build, fitur, atau hasil tes berhasil tanpa bukti aktual.
