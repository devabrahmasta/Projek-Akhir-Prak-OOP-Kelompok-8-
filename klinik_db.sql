-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:8111
-- Waktu pembuatan: 01 Jun 2026 pada 04.17
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `klinik_db`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `antrian`
--

CREATE TABLE `antrian` (
  `id` int(11) NOT NULL,
  `id_pasien` int(11) DEFAULT NULL,
  `id_dokter` int(11) DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `nomor_antrian` int(11) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `antrian`
--

INSERT INTO `antrian` (`id`, `id_pasien`, `id_dokter`, `tanggal`, `nomor_antrian`, `status`) VALUES
(1, 2, 1, '2026-06-01', 1, 'Selesai'),
(2, 3, 1, '2026-06-01', 2, 'Dipanggil'),
(3, 5, 2, '2026-06-01', 1, 'Dipanggil');

-- --------------------------------------------------------

--
-- Struktur dari tabel `apoteker`
--

CREATE TABLE `apoteker` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `no_telp` varchar(15) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `apoteker`
--

INSERT INTO `apoteker` (`id`, `nama`, `no_telp`, `id_user`) VALUES
(1, 'Dewi Farmasi', '085566778899', 6);

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_resep`
--

CREATE TABLE `detail_resep` (
  `id` int(11) NOT NULL,
  `id_resep` int(11) DEFAULT NULL,
  `id_obat` int(11) DEFAULT NULL,
  `jumlah` int(11) DEFAULT NULL,
  `dosis` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `detail_resep`
--

INSERT INTO `detail_resep` (`id`, `id_resep`, `id_obat`, `jumlah`, `dosis`) VALUES
(1, 1, 1, 10, '3 x 1 sesudah makan'),
(2, 1, 5, 10, '1 x 1 sesudah makan'),
(3, 2, 1, 10, '3 x 1 bila nyeri'),
(4, 2, 2, 15, '3 x 1 dihabiskan'),
(5, 3, 4, 1, '3 x 1 sendok makan'),
(6, 3, 1, 10, '3 x 1 bila demam');

-- --------------------------------------------------------

--
-- Struktur dari tabel `dokter`
--

CREATE TABLE `dokter` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `spesialisasi` varchar(100) DEFAULT NULL,
  `no_telp` varchar(15) DEFAULT NULL,
  `jadwal` varchar(100) DEFAULT NULL,
  `id_user` int(11) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `dokter`
--

INSERT INTO `dokter` (`id`, `nama`, `spesialisasi`, `no_telp`, `jadwal`, `id_user`, `is_active`) VALUES
(1, 'dr. Andi Pratama', 'Umum', '081234567890', 'Senin - Jumat', 3, 1),
(2, 'dr. Budi Santoso', 'Spesialis Anak', '081234567891', 'Senin - Rabu', 4, 1),
(3, 'dr. Citra Lestari', 'Spesialis Gigi', '081234567892', 'Kamis - Sabtu', 5, 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `kunjungan`
--

CREATE TABLE `kunjungan` (
  `id` int(11) NOT NULL,
  `id_pasien` int(11) DEFAULT NULL,
  `id_dokter` int(11) DEFAULT NULL,
  `tanggal_kunjungan` datetime DEFAULT NULL,
  `keluhan` text DEFAULT NULL,
  `diagnosa` text DEFAULT NULL,
  `status` enum('sedang_diperiksa','selesai') NOT NULL DEFAULT 'sedang_diperiksa'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `kunjungan`
--

INSERT INTO `kunjungan` (`id`, `id_pasien`, `id_dokter`, `tanggal_kunjungan`, `keluhan`, `diagnosa`, `status`) VALUES
(1, 1, 1, '2026-05-28 09:30:00', 'Demam tinggi dan pusing sejak 2 hari lalu', 'Gejala Tifus / Demam Berdarah ringan', 'selesai'),
(2, 5, 3, '2026-05-29 10:15:00', 'Gigi geraham belakang ngilu saat makan dingin', 'Pulpitis (Gigi Berlubang)', 'selesai'),
(3, 2, 1, '2026-06-01 08:15:00', 'Batuk berdahak dan pilek', 'ISPA (Inspeksi Saluran Pernapasan Akut)', 'selesai'),
(4, 3, 3, '2026-06-01 09:15:48', 'tidur tak tenang', 'kebanyakan nonton one piece', 'selesai');

-- --------------------------------------------------------

--
-- Struktur dari tabel `obat`
--

CREATE TABLE `obat` (
  `id` int(11) NOT NULL,
  `kode_obat` varchar(20) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `kategori` varchar(50) DEFAULT NULL,
  `stok` int(11) DEFAULT 0,
  `satuan` varchar(20) DEFAULT 'Tablet',
  `harga` decimal(10,2) DEFAULT NULL,
  `stok_minimum` int(11) DEFAULT 10
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `obat`
--

INSERT INTO `obat` (`id`, `kode_obat`, `nama`, `kategori`, `stok`, `satuan`, `harga`, `stok_minimum`) VALUES
(1, 'OBT001', 'Paracetamol 500mg', 'Tablet', 150, 'Tablet', 5000.00, 10),
(2, 'OBT002', 'Amoxicillin 500mg', 'Kapsul', 100, 'Tablet', 8500.00, 10),
(3, 'OBT003', 'Ibuprofen 400mg', 'Tablet', 80, 'Tablet', 7000.00, 10),
(4, 'OBT004', 'Sirup Obat Batuk Hitam (OBH)', 'Sirup', 45, 'Tablet', 15000.00, 10),
(5, 'OBT005', 'Vitamin C 1000mg', 'Tablet', 200, 'Tablet', 12000.00, 10);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pasien`
--

CREATE TABLE `pasien` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `alamat` text DEFAULT NULL,
  `no_telp` varchar(15) DEFAULT NULL,
  `tanggal_lahir` date DEFAULT NULL,
  `no_rm` varchar(50) DEFAULT NULL,
  `golongan_darah` varchar(10) DEFAULT NULL,
  `alergi` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pasien`
--

INSERT INTO `pasien` (`id`, `nama`, `alamat`, `no_telp`, `tanggal_lahir`, `no_rm`, `golongan_darah`, `alergi`, `is_active`) VALUES
(1, 'Atep Sudirohusodo', 'Janti, Sambilegi, Jatinangor, Jakarta', '083444332233', '2002-02-23', 'RM-00000', 'AB', 'kacang', 1),
(2, 'Salmanan', 'Jakarta', '081232323232', '2002-12-02', 'RM-00001', 'AB', 'kacang', 1),
(3, 'Andrea Sudrajat', 'Jatiluhur Jawa Barat', '08888888888', '2002-12-12', 'RM-00002', 'AB', 'wibu', 1),
(4, 'Diana Putri', 'Jl. Merdeka No. 10', '081122334455', '1995-08-15', 'RM-00003', 'O', 'Udang', 1),
(5, 'Eko Wahyudi', 'Jl. Sudirman No. 5', '082233445566', '1988-03-20', 'RM-00004', 'B', '-', 1);

-- --------------------------------------------------------

--
-- Struktur dari tabel `resep`
--

CREATE TABLE `resep` (
  `id` int(11) NOT NULL,
  `id_kunjungan` int(11) DEFAULT NULL,
  `id_apoteker` int(11) DEFAULT NULL,
  `tanggal` date DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `resep`
--

INSERT INTO `resep` (`id`, `id_kunjungan`, `id_apoteker`, `tanggal`, `status`) VALUES
(1, 1, 1, '2026-05-28', 'sudah_disiapkan'),
(2, 2, 1, '2026-05-29', 'sudah_disiapkan'),
(3, 3, NULL, '2026-06-01', 'belum_disiapkan');

-- --------------------------------------------------------

--
-- Struktur dari tabel `tagihan`
--

CREATE TABLE `tagihan` (
  `id` int(11) NOT NULL,
  `id_kunjungan` int(11) DEFAULT NULL,
  `total_biaya` decimal(10,2) DEFAULT NULL,
  `status_pembayaran` varchar(20) DEFAULT NULL,
  `tanggal_pembayaran` datetime DEFAULT NULL,
  `jenis_pembayaran` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `tagihan`
--

INSERT INTO `tagihan` (`id`, `id_kunjungan`, `total_biaya`, `status_pembayaran`, `tanggal_pembayaran`, `jenis_pembayaran`) VALUES
(1, 1, 67000.00, 'Lunas', '2026-05-28 10:05:00', 'Tunai'),
(2, 2, 227500.00, 'Lunas', '2026-05-29 10:45:00', 'Tunai'),
(3, 3, 70000.00, 'Belum Lunas', NULL, NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `role` enum('admin','resepsionis','dokter','apoteker') NOT NULL,
  `aktif` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `nama`, `role`, `aktif`, `created_at`) VALUES
(1, 'admin_medika', 'admin123', 'Super Admin', 'admin', 1, '2026-05-30 08:22:56'),
(2, 'resep_front', 'resep123', 'Resepsionis Utama', 'resepsionis', 1, '2026-05-30 08:22:56'),
(3, 'dr_andi', 'andi123', 'dr. Andi Pratama', 'dokter', 1, '2026-05-30 08:22:56'),
(4, 'dr_budi', 'budi123', 'dr. Budi Santoso', 'dokter', 1, '2026-06-01 01:39:11'),
(5, 'dr_citra', 'citra123', 'dr. Citra Lestari', 'dokter', 1, '2026-06-01 01:39:11'),
(6, 'apoteker_dewi', 'dewi123', 'Dewi Farmasi', 'apoteker', 1, '2026-06-01 01:39:11');

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `antrian`
--
ALTER TABLE `antrian`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pasien` (`id_pasien`),
  ADD KEY `id_dokter` (`id_dokter`);

--
-- Indeks untuk tabel `apoteker`
--
ALTER TABLE `apoteker`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_apoteker_user` (`id_user`);

--
-- Indeks untuk tabel `detail_resep`
--
ALTER TABLE `detail_resep`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_resep` (`id_resep`),
  ADD KEY `id_obat` (`id_obat`);

--
-- Indeks untuk tabel `dokter`
--
ALTER TABLE `dokter`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_dokter_user` (`id_user`);

--
-- Indeks untuk tabel `kunjungan`
--
ALTER TABLE `kunjungan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_pasien` (`id_pasien`),
  ADD KEY `id_dokter` (`id_dokter`);

--
-- Indeks untuk tabel `obat`
--
ALTER TABLE `obat`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `kode_obat` (`kode_obat`);

--
-- Indeks untuk tabel `pasien`
--
ALTER TABLE `pasien`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `resep`
--
ALTER TABLE `resep`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_kunjungan` (`id_kunjungan`),
  ADD KEY `id_apoteker` (`id_apoteker`);

--
-- Indeks untuk tabel `tagihan`
--
ALTER TABLE `tagihan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_kunjungan` (`id_kunjungan`);

--
-- Indeks untuk tabel `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `antrian`
--
ALTER TABLE `antrian`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `apoteker`
--
ALTER TABLE `apoteker`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `detail_resep`
--
ALTER TABLE `detail_resep`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT untuk tabel `dokter`
--
ALTER TABLE `dokter`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `kunjungan`
--
ALTER TABLE `kunjungan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT untuk tabel `obat`
--
ALTER TABLE `obat`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `pasien`
--
ALTER TABLE `pasien`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `resep`
--
ALTER TABLE `resep`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `tagihan`
--
ALTER TABLE `tagihan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `antrian`
--
ALTER TABLE `antrian`
  ADD CONSTRAINT `antrian_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id`),
  ADD CONSTRAINT `antrian_ibfk_2` FOREIGN KEY (`id_dokter`) REFERENCES `dokter` (`id`);

--
-- Ketidakleluasaan untuk tabel `apoteker`
--
ALTER TABLE `apoteker`
  ADD CONSTRAINT `fk_apoteker_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `detail_resep`
--
ALTER TABLE `detail_resep`
  ADD CONSTRAINT `detail_resep_ibfk_1` FOREIGN KEY (`id_resep`) REFERENCES `resep` (`id`),
  ADD CONSTRAINT `detail_resep_ibfk_2` FOREIGN KEY (`id_obat`) REFERENCES `obat` (`id`);

--
-- Ketidakleluasaan untuk tabel `dokter`
--
ALTER TABLE `dokter`
  ADD CONSTRAINT `fk_dokter_user` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`);

--
-- Ketidakleluasaan untuk tabel `kunjungan`
--
ALTER TABLE `kunjungan`
  ADD CONSTRAINT `kunjungan_ibfk_1` FOREIGN KEY (`id_pasien`) REFERENCES `pasien` (`id`),
  ADD CONSTRAINT `kunjungan_ibfk_2` FOREIGN KEY (`id_dokter`) REFERENCES `dokter` (`id`);

--
-- Ketidakleluasaan untuk tabel `resep`
--
ALTER TABLE `resep`
  ADD CONSTRAINT `resep_ibfk_1` FOREIGN KEY (`id_kunjungan`) REFERENCES `kunjungan` (`id`),
  ADD CONSTRAINT `resep_ibfk_2` FOREIGN KEY (`id_apoteker`) REFERENCES `apoteker` (`id`);

--
-- Ketidakleluasaan untuk tabel `tagihan`
--
ALTER TABLE `tagihan`
  ADD CONSTRAINT `tagihan_ibfk_1` FOREIGN KEY (`id_kunjungan`) REFERENCES `kunjungan` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
