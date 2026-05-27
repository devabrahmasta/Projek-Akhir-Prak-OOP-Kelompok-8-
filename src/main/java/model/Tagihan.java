package model;

import java.util.Date;

public class Tagihan {
    private int id;
    private Kunjungan kunjungan;
    private double totalBiaya;
    private Date tanggal;
    private Pembayaran pembayaran;

    public Tagihan(int id, Kunjungan kunjungan, double totalBiaya, Date tanggal) {
        this.id = id;
        this.kunjungan = kunjungan;
        this.totalBiaya = totalBiaya;
        this.tanggal = tanggal;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Kunjungan getKunjungan() { return kunjungan; }
    public void setKunjungan(Kunjungan kunjungan) { this.kunjungan = kunjungan; }
    
    public double getTotalBiaya() { return totalBiaya; }
    public void setTotalBiaya(double totalBiaya) { this.totalBiaya = totalBiaya; }
    
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    
    public Pembayaran getPembayaran() { return pembayaran; }
    public void setPembayaran(Pembayaran pembayaran) { this.pembayaran = pembayaran; }
}
