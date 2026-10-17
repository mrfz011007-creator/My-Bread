# Catatan Keputusan My Bread

Dokumen ini menyimpan keputusan yang memengaruhi rancangan agar sesi berikutnya tidak mengubahnya tanpa alasan. Tambahkan keputusan baru dengan tanggal, konteks, pilihan, dan konsekuensi.

## 2026-10-16 — Dokumentasi sebagai checkpoint lintas sesi
- **Keputusan:** gunakan dokumen proyek di GitHub sebagai sumber status proyek yang dapat diperiksa dari sesi baru.
- **Alasan:** konteks percakapan tidak boleh menjadi satu-satunya tempat menyimpan roadmap dan keputusan.
- **Konsekuensi:** setiap milestone harus memperbarui checkpoint dan menyertakan bukti aktual.

## Teknologi awal
- **Keputusan:** Java + XML untuk UI Android, SQLite lokal untuk data, Gradle/Gradle Wrapper untuk build, GitHub Actions untuk CI.
- **Alasan:** pendekatan Android native yang relatif langsung untuk aplikasi offline.
- **Konsekuensi:** pilih versi toolchain yang kompatibel dan verifikasi melalui CI; jangan menambahkan stack alternatif tanpa alasan.

## Model usaha
- **Keputusan:** modal awal Rp1.400 per roti baru; harga jual Rp2.500; margin kotor awal Rp1.100 per roti terjual.
- **Konsekuensi:** modal yang dibayar dihitung dari seluruh pembelian baru hari itu, bukan hanya jumlah yang terjual.

## Sisa stok dan FIFO
- **Keputusan:** sisa stok hari sebelumnya dimasukkan manual oleh pengguna; aplikasi dapat menampilkan angka sebelumnya sebagai referensi.
- **Keputusan:** stok lama dan stok baru harus dapat dilacak sebagai lot terpisah.
- **Keputusan:** FIFO berlaku untuk roti terjual dan roti rusak/tidak layak.
- **Keputusan:** penyesuaian stok dicatat terpisah dengan alasan, bukan otomatis dianggap sebagai penjualan atau roti rusak.

## Edit catatan dan integritas data
- **Keputusan:** mengedit catatan tanggal lama memerlukan penghitungan ulang dampak pada tanggal setelahnya.
- **Keputusan:** catatan harian dan pengeluaran disimpan secara atomik.
- **Konsekuensi:** kegagalan simpan tidak boleh meninggalkan data sebagian atau duplikat; perubahan yang membuat stok lanjutan tidak valid harus ditolak atau diselesaikan secara eksplisit.

## Backup dan restore
- **Keputusan:** import backup mengganti semua data lokal hanya setelah validasi, pratinjau, dan konfirmasi pengguna.
- **Konsekuensi:** jika restore gagal, data lokal sebelumnya harus tetap utuh.

## Batasan cakupan awal
- **Keputusan:** aplikasi offline-first, tanpa login, server/backend, Firebase, Kotlin, Jetpack Compose, atau API AI pada versi awal.
- **Konsekuensi:** fokus pada pencatatan lokal, konsistensi data, dan APK yang dapat dibangun berulang melalui CI.


## 2026-10-17 — Perubahan catatan lama dan stok berantai
- **Keputusan:** bila catatan pada tanggal lama diubah, perhitungan stok awal pada tanggal berikutnya dihitung ulang secara berantai dalam satu transaksi database.
- **Alasan:** riwayat tidak boleh mempertahankan saldo stok yang menjadi tidak konsisten setelah perubahan data sebelumnya.
- **Perlindungan:** pengguna mendapat peringatan sebelum menyimpan perubahan historis. Jika penjualan/kerusakan pada hari berikutnya melebihi stok yang dihitung ulang, seluruh transaksi dibatalkan. Koreksi jumlah fisik harus dicatat sebagai penyesuaian stok dengan alasan, bukan penjualan/kerusakan.
