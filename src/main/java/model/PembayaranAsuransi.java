package model;

public class PembayaranAsuransi extends Pembayaran {
    private double jumlahBayar;
    private double persentaseCover;

    public PembayaranAsuransi(double jumlahBayar, double persentaseCover) {
        this.jumlahBayar = jumlahBayar;
        this.persentaseCover = persentaseCover;
    }

    @Override
    public double hitungTotal() {
        return jumlahBayar - (jumlahBayar * (persentaseCover / 100));
    }

    @Override
    public String getJenisPembayaran() {
        return "Asuransi";
    }
}
