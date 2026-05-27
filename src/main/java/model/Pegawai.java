package model;

public abstract class Pegawai extends Person {
    protected int id;
    protected String nama;

    public Pegawai(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }
}
