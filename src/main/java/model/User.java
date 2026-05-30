package model;

public class User {
    private int id;
    private String username;
    private String nama;
    private String role;
    private boolean aktif;

    public User(int id, String username, String nama, String role, boolean aktif) {
        this.id = id;
        this.username = username;
        this.nama = nama;
        this.role = role;
        this.aktif = aktif;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getNama() { return nama; }
    public String getRole() { return role; }
    public boolean isAktif() { return aktif; }

    @Override
    public String toString() {
        return nama;
    }
}