package model;

public class DokterUmum extends Dokter {
    public DokterUmum(int id, String nama) {
        super(id, nama, "Umum");
    }

    @Override
    public double hitungTarifKonsultasi() {
        return 50000.0;
    }
}
