package model;

import java.sql.Date;

public class Pasien extends Person {
    private int id;
    private String nama;
    private String noRM;
    private String alamat;
    private String noTelp;
    private Date tanggalLahir;
    private String golonganDarah;
    private String alergi;

    public Pasien() {
    }

    public Pasien(int id, String nama, String noRM, String alamat, String noTelp, Date tanggalLahir, String golonganDarah, String alergi) {
        this.id = id;
        this.nama = nama;
        this.noRM = noRM;
        this.alamat = alamat;
        this.noTelp = noTelp;
        this.tanggalLahir = tanggalLahir;
        this.golonganDarah = golonganDarah;
        this.alergi = alergi;
    }

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getRole() {
        return "Pasien";
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoRM() {
        return noRM;
    }

    public void setNoRM(String noRM) {
        this.noRM = noRM;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public Date getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(Date tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getGolonganDarah() {
        return golonganDarah;
    }

    public void setGolonganDarah(String golonganDarah) {
        this.golonganDarah = golonganDarah;
    }

    public String getAlergi() {
        return alergi;
    }

    public void setAlergi(String alergi) {
        this.alergi = alergi;
    }
}
