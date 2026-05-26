package model;

import java.sql.Date;

public class Antrian {
    private int id;
    private int idPasien;
    private int idDokter;
    private Date tanggal;
    private int nomorAntrian;
    private String status;

    public Antrian() {
    }

    public Antrian(int id, int idPasien, int idDokter, Date tanggal, int nomorAntrian, String status) {
        this.id = id;
        this.idPasien = idPasien;
        this.idDokter = idDokter;
        this.tanggal = tanggal;
        this.nomorAntrian = nomorAntrian;
        this.status = status;
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

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getNomorAntrian() {
        return nomorAntrian;
    }

    public void setNomorAntrian(int nomorAntrian) {
        this.nomorAntrian = nomorAntrian;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
