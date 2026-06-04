package model;

import java.sql.Timestamp;
import model.interfaces.Reportable;

public class Kunjungan implements Reportable {
    private int id;
    private int idPasien;
    private int idDokter;
    private Timestamp tanggalKunjungan;
    private String keluhan;
    private String diagnosa;
    
    private Pasien pasien;
    private Dokter dokter;

    public Kunjungan() {
    }

    public Kunjungan(int id, int idPasien, int idDokter, Timestamp tanggalKunjungan, String keluhan, String diagnosa) {
        this.id = id;
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.tanggalKunjungan = tanggalKunjungan;
        this.keluhan = keluhan;
        this.diagnosa = diagnosa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPasien() {
        return idPasien;
    }

    public void setIdPasien(int idPasien) {
        this.idPasien = idPasien;
    }

    public int getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(int idDokter) {
        this.idDokter = idDokter;
    }

    public Timestamp getTanggalKunjungan() {
        return tanggalKunjungan;
    }

    public void setTanggalKunjungan(Timestamp tanggalKunjungan) {
        this.tanggalKunjungan = tanggalKunjungan;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public String getDiagnosa() {
        return diagnosa;
    }

    public void setDiagnosa(String diagnosa) {
        this.diagnosa = diagnosa;
    }

    @Override
    public void cetakLaporan() {
        System.out.println("====== LAPORAN KUNJUNGAN ======");
        System.out.println("ID Kunjungan     : " + id);
        System.out.println("ID Pasien        : " + idPasien);
        System.out.println("ID Dokter        : " + idDokter);
        System.out.println("Waktu Kunjungan  : " + tanggalKunjungan);
        System.out.println("Keluhan          : " + keluhan);
        System.out.println("Diagnosa         : " + diagnosa);
        System.out.println("===============================");
    }

    public Pasien getPasien() {
        return pasien;
    }

    public void setPasien(Pasien pasien) {
        this.pasien = pasien;
    }

    public Dokter getDokter() {
        return dokter;
    }

    public void setDokter(Dokter dokter) {
        this.dokter = dokter;
    }

    @Override
    public String toDisplayString() {
        return String.format("Kunjungan #%d - Pasien ID: %d, Dokter ID: %d, Diagnosa: %s", id, idPasien, idDokter, diagnosa);
    }
}
