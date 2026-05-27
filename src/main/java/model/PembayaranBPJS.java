package model;

public class PembayaranBPJS extends Pembayaran {
    private double jumlahBayar;
    private double subsidiBPJS;

    public PembayaranBPJS(double jumlahBayar, double subsidiBPJS) {
        this.jumlahBayar = jumlahBayar;
        this.subsidiBPJS = subsidiBPJS;
    }

    @Override
    public double hitungTotal() {
        return Math.max(0, jumlahBayar - subsidiBPJS);
    }

    @Override
    public String getJenisPembayaran() {
        return "BPJS";
    }
}
