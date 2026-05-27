package model;

public class PembayaranTunai extends Pembayaran {
    private double jumlahBayar;
    private double diskon;

    public PembayaranTunai(double jumlahBayar, double diskon) {
        this.jumlahBayar = jumlahBayar;
        this.diskon = diskon;
    }

    @Override
    public double hitungTotal() {
        return jumlahBayar - diskon;
    }

    @Override
    public String getJenisPembayaran() {
        return "Tunai";
    }
}
