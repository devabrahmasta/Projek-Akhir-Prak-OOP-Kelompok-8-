package model;

public abstract class Dokter extends Pegawai {
    protected String spesialisasi;

    public Dokter(int id, String nama, String spesialisasi) {
        super(id, nama);
        this.spesialisasi = spesialisasi;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
    }

    @Override
    public String getRole() {
        return "Dokter";
    }
}
