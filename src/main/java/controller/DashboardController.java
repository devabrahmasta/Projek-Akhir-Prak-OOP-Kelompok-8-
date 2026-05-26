package controller;

import database.DBConnection;
import view.DashboardView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    private final DashboardView view;
    private final Connection connection;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public DashboardController(DashboardView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addRefreshListener(e -> loadData());
    }

    public void loadData() {
        view.setLastUpdatedText("Memperbarui dashboard...");
        
        SwingWorker<DashboardMetrics, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardMetrics doInBackground() throws Exception {
                int totalPasien = 0;
                int antrianMenunggu = 0;
                int kunjunganHariIni = 0;
                int dokterAktif = 0;
                
                List<Object[]> antrianRows = new ArrayList<>();
                List<Object[]> kunjunganRows = new ArrayList<>();

                if (connection == null) {
                    return new DashboardMetrics(0, 0, 0, 0, antrianRows, kunjunganRows);
                }

                try (Statement stmt = connection.createStatement()) {
                    // 1. Total Pasien
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pasien")) {
                        if (rs.next()) totalPasien = rs.getInt(1);
                    }

                    // 2. Antrian Menunggu
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM antrian WHERE status = 'Menunggu' AND tanggal = CURRENT_DATE()")) {
                        if (rs.next()) {
                            antrianMenunggu = rs.getInt(1);
                        } else {
                            // Fallback if no dates match today
                            try (ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM antrian WHERE status = 'Menunggu'")) {
                                if (rs2.next()) antrianMenunggu = rs2.getInt(1);
                            }
                        }
                    }

                    // 3. Kunjungan Hari Ini
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM kunjungan WHERE DATE(tanggal_kunjungan) = CURRENT_DATE()")) {
                        if (rs.next()) {
                            kunjunganHariIni = rs.getInt(1);
                        } else {
                            // Fallback count of total visits if today has 0
                            try (ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM kunjungan")) {
                                if (rs2.next()) kunjunganHariIni = rs2.getInt(1);
                            }
                        }
                    }

                    // 4. Dokter Aktif
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM dokter")) {
                        if (rs.next()) dokterAktif = rs.getInt(1);
                    }

                    // 5. Antrian List (Today's queues or latest 10 queues)
                    String sqlAntrian = "SELECT a.nomor_antrian, p.nama AS nama_pasien, d.nama AS nama_dokter, a.status " +
                                        "FROM antrian a " +
                                        "JOIN pasien p ON a.id_pasien = p.id " +
                                        "JOIN dokter d ON a.id_dokter = d.id " +
                                        "WHERE a.tanggal = CURRENT_DATE() " +
                                        "ORDER BY a.nomor_antrian ASC LIMIT 10";
                    try (ResultSet rs = stmt.executeQuery(sqlAntrian)) {
                        while (rs.next()) {
                            antrianRows.add(new Object[]{
                                rs.getInt("nomor_antrian"),
                                rs.getString("nama_pasien"),
                                rs.getString("nama_dokter"),
                                rs.getString("status")
                            });
                        }
                    }
                    
                    // Fallback to latest 10 queues if today is empty
                    if (antrianRows.isEmpty()) {
                        String sqlLatestAntrian = "SELECT a.nomor_antrian, p.nama AS nama_pasien, d.nama AS nama_dokter, a.status " +
                                                  "FROM antrian a " +
                                                  "JOIN pasien p ON a.id_pasien = p.id " +
                                                  "JOIN dokter d ON a.id_dokter = d.id " +
                                                  "ORDER BY a.tanggal DESC, a.nomor_antrian ASC LIMIT 10";
                        try (ResultSet rs = stmt.executeQuery(sqlLatestAntrian)) {
                            while (rs.next()) {
                                antrianRows.add(new Object[]{
                                    rs.getInt("nomor_antrian"),
                                    rs.getString("nama_pasien"),
                                    rs.getString("nama_dokter"),
                                    rs.getString("status")
                                });
                            }
                        }
                    }

                    // 6. Kunjungan List (Latest 10 visits)
                    String sqlKunjungan = "SELECT k.tanggal_kunjungan, p.nama AS nama_pasien, k.keluhan, k.diagnosa " +
                                          "FROM kunjungan k " +
                                          "JOIN pasien p ON k.id_pasien = p.id " +
                                          "ORDER BY k.tanggal_kunjungan DESC LIMIT 10";
                    try (ResultSet rs = stmt.executeQuery(sqlKunjungan)) {
                        while (rs.next()) {
                            antrianRows.size(); // dummy trigger
                            kunjunganRows.add(new Object[]{
                                rs.getTimestamp("tanggal_kunjungan").toString(),
                                rs.getString("nama_pasien"),
                                rs.getString("keluhan"),
                                rs.getString("diagnosa")
                            });
                        }
                    }
                }
                return new DashboardMetrics(totalPasien, antrianMenunggu, kunjunganHariIni, dokterAktif, antrianRows, kunjunganRows);
            }

            @Override
            protected void done() {
                try {
                    DashboardMetrics metrics = get();
                    view.setTotalPasien(String.valueOf(metrics.totalPasien));
                    view.setAntrianMenunggu(String.valueOf(metrics.antrianMenunggu));
                    view.setKunjunganHariIni(String.valueOf(metrics.kunjunganHariIni));
                    view.setDokterAktif(String.valueOf(metrics.dokterAktif));

                    // Populate tables
                    DefaultTableModel antrianModel = view.getTableModelAntrian();
                    antrianModel.setRowCount(0);
                    for (Object[] row : metrics.antrianRows) {
                        antrianModel.addRow(row);
                    }

                    DefaultTableModel kunjunganModel = view.getTableModelKunjungan();
                    kunjunganModel.setRowCount(0);
                    for (Object[] row : metrics.kunjunganRows) {
                        kunjunganModel.addRow(row);
                    }

                    view.setLastUpdatedText(LocalTime.now().format(timeFormatter));
                } catch (Exception e) {
                    view.setLastUpdatedText("Gagal memperbarui: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private static class DashboardMetrics {
        final int totalPasien;
        final int antrianMenunggu;
        final int kunjunganHariIni;
        final int dokterAktif;
        final List<Object[]> antrianRows;
        final List<Object[]> kunjunganRows;

        DashboardMetrics(int totalPasien, int antrianMenunggu, int kunjunganHariIni, int dokterAktif, 
                         List<Object[]> antrianRows, List<Object[]> kunjunganRows) {
            this.totalPasien = totalPasien;
            this.antrianMenunggu = antrianMenunggu;
            this.kunjunganHariIni = kunjunganHariIni;
            this.dokterAktif = dokterAktif;
            this.antrianRows = antrianRows;
            this.kunjunganRows = kunjunganRows;
        }
    }
}
