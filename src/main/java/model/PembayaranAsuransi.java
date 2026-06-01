package model;

public class PembayaranAsuransi extends Pembayaran {
    private double tarifDokter;
    private double tarifObat;

    public PembayaranAsuransi(double tarifDokter, double tarifObat) {
        this.tarifDokter = tarifDokter;
        this.tarifObat = tarifObat;
    }

    @Override
    public double hitungTotal() {
        return (tarifDokter + tarifObat) * 0.2;
    }

    @Override
    public String getJenisPembayaran() {
        return "Asuransi";
    }
}
