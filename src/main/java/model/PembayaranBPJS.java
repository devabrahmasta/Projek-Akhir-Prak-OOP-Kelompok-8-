package model;

public class PembayaranBPJS extends Pembayaran {
    private double tarifDokter;
    private double tarifObat;

    public PembayaranBPJS(double tarifDokter, double tarifObat) {
        this.tarifDokter = tarifDokter;
        this.tarifObat = tarifObat;
    }

    @Override
    public double hitungTotal() {
        return tarifObat * 0.2;
    }

    @Override
    public String getJenisPembayaran() {
        return "BPJS";
    }
}
