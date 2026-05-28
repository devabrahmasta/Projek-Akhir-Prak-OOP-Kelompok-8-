package model;

public class DokterSpesialis extends Dokter {
    public DokterSpesialis(int id, String nama, String spesialisasi) {
        super(id, nama, spesialisasi);
    }

    @Override
    public double hitungTarifKonsultasi() {
        return 150000.0;
    }
}
