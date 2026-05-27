package model;

import java.util.Date;
import java.util.List;

public class Resep {
    private int id;
    private Kunjungan kunjungan;
    private Dokter dokter;
    private List<Obat> daftarObat;
    private String keterangan;
    private Date tanggal;

    public Resep(int id, Kunjungan kunjungan, Dokter dokter, List<Obat> daftarObat, String keterangan, Date tanggal) {
        this.id = id;
        this.kunjungan = kunjungan;
        this.dokter = dokter;
        this.daftarObat = daftarObat;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Kunjungan getKunjungan() { return kunjungan; }
    public void setKunjungan(Kunjungan kunjungan) { this.kunjungan = kunjungan; }
    
    public Dokter getDokter() { return dokter; }
    public void setDokter(Dokter dokter) { this.dokter = dokter; }
    
    public List<Obat> getDaftarObat() { return daftarObat; }
    public void setDaftarObat(List<Obat> daftarObat) { this.daftarObat = daftarObat; }
    
    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }
    
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
}
