package model;

public class PembayaranTunai extends Pembayaran {
    private double tarifDokter;
    private double tarifObat;

    public PembayaranTunai(double tarifDokter, double tarifObat) {
        this.tarifDokter = tarifDokter;
        this.tarifObat = tarifObat;
    }

    @Override
    public double hitungTotal() {
        return tarifDokter + tarifObat;
    }

    @Override
    public String getJenisPembayaran() {
        return "Tunai";
    }
}
