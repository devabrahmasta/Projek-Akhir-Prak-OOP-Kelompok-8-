package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private static final String PORT = "8111";
    private static final String URL = "jdbc:mysql://localhost:"+PORT+"/klinik_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            initializeDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("DB Connection Error (Driver/SQL): " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        if (connection == null) return;
        try {
            DatabaseMetaData meta = connection.getMetaData();
            
            // Check & create pasien table if not exists
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pasien (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nama VARCHAR(100) NOT NULL, " +
                    "alamat TEXT, " +
                    "no_telp VARCHAR(15), " +
                    "tanggal_lahir DATE)");
                
                // Table dokter (so we can insert mock doctors)
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS dokter (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nama VARCHAR(100) NOT NULL, " +
                    "spesialisasi VARCHAR(100), " +
                    "no_telp VARCHAR(15), " +
                    "jadwal VARCHAR(100))");
                
                // Table kunjungan
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS kunjungan (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_pasien INT, " +
                    "id_dokter INT, " +
                    "tanggal_kunjungan DATETIME, " +
                    "keluhan TEXT, " +
                    "diagnosa TEXT, " +
                    "FOREIGN KEY (id_pasien) REFERENCES pasien(id), " +
                    "FOREIGN KEY (id_dokter) REFERENCES dokter(id))");
                
                // Table antrian
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS antrian (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_pasien INT, " +
                    "id_dokter INT, " +
                    "tanggal DATE, " +
                    "nomor_antrian INT, " +
                    "status VARCHAR(20), " +
                    "FOREIGN KEY (id_pasien) REFERENCES pasien(id), " +
                    "FOREIGN KEY (id_dokter) REFERENCES dokter(id))");
            }
            
            // Force AUTO_INCREMENT on existing tables if they exist but don't have it
            try (Statement stmt = connection.createStatement()) {
                try { stmt.executeUpdate("ALTER TABLE pasien MODIFY COLUMN id INT AUTO_INCREMENT"); } catch (SQLException e) {}
                try { stmt.executeUpdate("ALTER TABLE dokter MODIFY COLUMN id INT AUTO_INCREMENT"); } catch (SQLException e) {}
                try { stmt.executeUpdate("ALTER TABLE kunjungan MODIFY COLUMN id INT AUTO_INCREMENT"); } catch (SQLException e) {}
                try { stmt.executeUpdate("ALTER TABLE antrian MODIFY COLUMN id INT AUTO_INCREMENT"); } catch (SQLException e) {}
            }
            
            // Add columns to pasien if not exist
            boolean hasNoRM = false;
            boolean hasGolDarah = false;
            boolean hasAlergi = false;
            
            try (ResultSet rs = meta.getColumns(null, null, "pasien", null)) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if ("no_rm".equalsIgnoreCase(columnName)) hasNoRM = true;
                    if ("golongan_darah".equalsIgnoreCase(columnName)) hasGolDarah = true;
                    if ("alergi".equalsIgnoreCase(columnName)) hasAlergi = true;
                }
            }
            
            try (Statement stmt = connection.createStatement()) {
                if (!hasNoRM) {
                    stmt.executeUpdate("ALTER TABLE pasien ADD COLUMN no_rm VARCHAR(50)");
                }
                if (!hasGolDarah) {
                    stmt.executeUpdate("ALTER TABLE pasien ADD COLUMN golongan_darah VARCHAR(10)");
                }
                if (!hasAlergi) {
                    stmt.executeUpdate("ALTER TABLE pasien ADD COLUMN alergi TEXT");
                }
            }
            
            // Check if table dokter is empty, insert mock data
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM dokter")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.executeUpdate("INSERT INTO dokter (nama, spesialisasi, no_telp, jadwal) VALUES " +
                        "('dr. Andi Pratama', 'Umum', '081234567890', 'Senin - Jumat'), " +
                        "('dr. Budi Santoso', 'Spesialis Anak', '081234567891', 'Senin - Rabu'), " +
                        "('dr. Citra Lestari', 'Spesialis Gigi', '081234567892', 'Kamis - Sabtu')");
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Initialization Error: " + e.getMessage());
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
