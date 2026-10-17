# Spesifikasi My Bread

Status: spesifikasi awal berdasarkan keputusan yang telah disepakati. Jika ada perubahan, catat alasan dan tanggalnya di `DECISIONS.md`.

## 1. Tujuan dan batasan
My Bread adalah aplikasi Android offline untuk membantu pencatatan usaha roti. Data disimpan di perangkat. Pengguna dapat mencatat pembelian roti baru, penjualan, roti rusak/tidak layak, pengeluaran, stok, riwayat, dan laporan.

Di luar cakupan awal: login, server/backend, sinkronisasi cloud, Firebase, Kotlin, Jetpack Compose, dan integrasi AI.

## 2. Nilai awal
- Modal per roti baru: **Rp1.400**.
- Harga jual per roti: **Rp2.500**.
- Margin kotor per roti terjual: **Rp1.100**.
- Pengguna membayar modal untuk seluruh roti baru yang dibeli hari itu, termasuk yang belum terjual.
- Jumlah roti yang dibeli dapat berubah setiap hari dan dimasukkan manual.

Nilai harga sebaiknya disimpan/diatur secara konsisten agar perubahan harga di masa depan tidak mengubah hasil hitung historis secara diam-diam. Desain detail harga historis perlu ditetapkan saat implementasi domain/database.

## 3. Input catatan harian
Setiap catatan harian dapat mencakup:
- Tanggal.
- Stok sisa dari hari sebelumnya yang dibawa hari ini. Untuk catatan baru, aplikasi memberi nilai awal dari hasil hari sebelumnya; pengguna dapat mencatat koreksi fisik lewat penyesuaian stok yang memiliki alasan.
- Jumlah roti baru yang dibeli hari ini.
- Jumlah roti terjual.
- Jumlah roti rusak/tidak layak.
- Daftar pengeluaran dengan keterangan dan nominal; satu hari dapat memiliki beberapa pengeluaran.

Saat catatan tanggal lama diedit, stok awal pada catatan berikutnya dihitung ulang berantai dari stok akhir terbaru. Jika jumlah fisik berbeda, catat selisih sebagai penyesuaian stok dengan alasan; jangan mengubah penyesuaian menjadi penjualan atau roti rusak.

## 4. Aturan stok dan FIFO
- Stok lama dan stok baru harus dapat dilacak sebagai lot terpisah.
- FIFO: stok yang paling lama masuk dianggap keluar terlebih dahulu.
- Roti rusak/tidak layak juga mengurangi stok tertua terlebih dahulu (FIFO).
- Penyesuaian stok dicatat terpisah dengan alasan; jangan menganggapnya sebagai penjualan atau roti rusak secara otomatis.
- Input tidak boleh negatif.
- Jumlah terjual ditambah jumlah rusak tidak boleh melebihi total stok tersedia untuk catatan yang bersangkutan.
- Perubahan catatan tanggal lama dapat memengaruhi stok pada tanggal berikutnya. Dampak harus dihitung ulang dan ditampilkan sebelum penyimpanan. Jika stok lanjutan menjadi tidak valid, jangan menyimpan keadaan yang tidak konsisten secara diam-diam.

## 5. Rumus ringkasan
- Omzet = jumlah terjual × Rp2.500.
- Margin kotor = jumlah terjual × Rp1.100.
- Modal yang dibayar hari ini = jumlah roti baru dibeli × Rp1.400.
- Selisih kas = omzet − modal yang dibayar hari ini − total pengeluaran.

Selisih kas bukan laba bersih karena roti yang belum terjual masih memiliki nilai modal. Aplikasi harus memberi label yang tidak menyesatkan dan membedakan metrik ini.

Contoh: beli 300 roti, jual 250, rusak 0, sisa 50:
- Modal dibayar: Rp420.000.
- Omzet: Rp625.000.
- Margin kotor: Rp275.000.
- Selisih kas sebelum pengeluaran: Rp205.000.

## 6. Pengeditan dan penyimpanan
- Catatan harian dapat diedit.
- Tanggal ditampilkan dan dikunci dalam alur edit biasa; perubahan tanggal, bila diperlukan, harus melalui tindakan khusus yang jelas.
- Perhitungan modal, omzet, margin, dan stok dilakukan otomatis, bukan diedit manual.
- Pengeluaran dikelola sebagai item terpisah dan dapat diedit.
- Form menyediakan pratinjau langsung.
- Batal membuang perubahan yang belum disimpan.
- Tombol Simpan nonaktif jika input tidak valid atau tidak ada perubahan.
- Perubahan penting memerlukan konfirmasi.
- Menyimpan ulang catatan pada tanggal yang sama memperbarui catatan tersebut, bukan membuat duplikat.
- Catatan harian dan pengeluaran terkait harus diperbarui dalam satu transaksi atomik. Jika simpan gagal, data sebelumnya tetap utuh.

## 7. Backup dan pemulihan
- Pengguna dapat mengekspor data.
- Import backup akan mengganti seluruh data lokal setelah validasi berkas, pratinjau, dan konfirmasi eksplisit.
- Jika berkas tidak valid atau proses pemulihan gagal, seluruh data lokal yang lama harus tetap utuh.
- Pemulihan tidak boleh meninggalkan keadaan setengah terganti.

## 8. Layar utama
- Beranda/dashboard dengan kartu ringkasan dan grafik.
- Form pencatatan harian.
- Riwayat.
- Laporan.
- Backup/data.

Tampilan diharapkan modern, mudah dibaca, dengan input sederhana dan laporan visual. Prioritaskan responsif dan ringan untuk ponsel Android dengan sumber daya terbatas.

## 9. Teknologi awal
- Java.
- Layout XML Android.
- SQLite lokal, kemungkinan menggunakan `SQLiteOpenHelper`.
- Gradle dan Gradle Wrapper.
- GitHub Actions untuk build dan tes otomatis serta mengunggah APK sebagai artifact.
- Tes unit dan database untuk logika usaha dan integritas data.

Pilihan versi SDK, Gradle, Android Gradle Plugin, dan dependensi harus diperiksa kompatibilitasnya saat fondasi proyek dibuat; jangan menebak bahwa build sukses sebelum CI membuktikannya.

## 10. Privasi dan ketahanan data
- Aplikasi harus berfungsi offline.
- Jangan menaruh rahasia dalam repository.
- Validasi berkas backup sebelum mengubah data.
- Gunakan transaksi database untuk perubahan yang saling berkaitan.
- Kegagalan operasi tidak boleh merusak data lama.
