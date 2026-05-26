CREATE DATABASE IF NOT EXISTS klinik_db;
USE klinik_db;

CREATE TABLE pasien (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    alamat TEXT,
    no_telp VARCHAR(15),
    tanggal_lahir DATE
);

CREATE TABLE pegawai (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    jabatan VARCHAR(50),
    no_telp VARCHAR(15)
);

CREATE TABLE dokter (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    spesialisasi VARCHAR(100),
    no_telp VARCHAR(15),
    jadwal VARCHAR(100)
);

CREATE TABLE apoteker (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    no_telp VARCHAR(15)
);

CREATE TABLE obat (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nama_obat VARCHAR(100) NOT NULL,
    jenis VARCHAR(50),
    stok INT DEFAULT 0,
    harga DECIMAL(10, 2)
);

CREATE TABLE kunjungan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pasien INT,
    id_dokter INT,
    tanggal_kunjungan DATETIME,
    keluhan TEXT,
    diagnosa TEXT,
    FOREIGN KEY (id_pasien) REFERENCES pasien(id),
    FOREIGN KEY (id_dokter) REFERENCES dokter(id)
);

CREATE TABLE antrian (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pasien INT,
    id_dokter INT,
    tanggal DATE,
    nomor_antrian INT,
    status VARCHAR(20),
    FOREIGN KEY (id_pasien) REFERENCES pasien(id),
    FOREIGN KEY (id_dokter) REFERENCES dokter(id)
);

CREATE TABLE resep (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_kunjungan INT,
    id_apoteker INT,
    tanggal DATE,
    status VARCHAR(20),
    FOREIGN KEY (id_kunjungan) REFERENCES kunjungan(id),
    FOREIGN KEY (id_apoteker) REFERENCES apoteker(id)
);

CREATE TABLE detail_resep (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_resep INT,
    id_obat INT,
    jumlah INT,
    dosis VARCHAR(50),
    FOREIGN KEY (id_resep) REFERENCES resep(id),
    FOREIGN KEY (id_obat) REFERENCES obat(id)
);

CREATE TABLE tagihan (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_kunjungan INT,
    total_biaya DECIMAL(10, 2),
    status_pembayaran VARCHAR(20),
    tanggal_pembayaran DATETIME,
    jenis_pembayaran VARCHAR(50),
    FOREIGN KEY (id_kunjungan) REFERENCES kunjungan(id)
);
