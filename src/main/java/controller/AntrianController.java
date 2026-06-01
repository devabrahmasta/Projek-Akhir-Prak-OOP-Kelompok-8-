package controller;

import database.DBConnection;
import view.ComboItem;
import view.AntrianView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntrianController {
    private final AntrianView view;
    private final Connection connection;
    
    private Thread refreshThread;
    private volatile boolean refreshThreadRunning = true;

    public AntrianController(AntrianView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        view.setFilterTanggal(LocalDate.now().toString());
        loadDropdowns();
        loadData(true);
        startRefreshThread();
    }

    private void initListeners() {
        view.addFilterListener(e -> loadData(true));
    }

    private void startRefreshThread() {
        refreshThreadRunning = true;
        refreshThread = new Thread(() -> {
            while (refreshThreadRunning) {
                try {
                    Thread.sleep(10000); 
                    if (refreshThreadRunning) {
                        loadData(false);
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "Antrian Refresh Thread");
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    public void stopRefreshThread() {
        refreshThreadRunning = false;
        if (refreshThread != null) {
            refreshThread.interrupt();
        }
    }

    public void loadDropdowns() {
        SwingWorker<List<ComboItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ComboItem> doInBackground() throws Exception {
                List<ComboItem> dokterList = new ArrayList<>();
                if (connection == null) return dokterList;

                String sqlDokter = "SELECT id, nama, spesialisasi FROM dokter WHERE is_active = 1 ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlDokter)) {
                    while (rs.next()) {
                        dokterList.add(new ComboItem(
                            rs.getInt("id"),
                            rs.getString("nama") + " - " + rs.getString("spesialisasi")
                        ));
                    }
                }
                return dokterList;
            }

            @Override
            protected void done() {
                try {
                    view.setDokterList(get().toArray(new ComboItem[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void loadData(boolean isManualRefresh) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int menunggu = 0, diperiksa = 0, selesai = 0;
            String nowServingNama = null;
            String nowServingDokter = null;
            List<Object[]> waitingList = new ArrayList<>();
            List<Object[]> finishedList = new ArrayList<>();

            @Override
            protected Void doInBackground() throws Exception {
                if (connection == null) return null;

                ComboItem filterDokter = view.getFilterDokter();
                String filterTgl = view.getFilterTanggal();

                StringBuilder sql = new StringBuilder(
                    "SELECT a.id, a.nomor_antrian, p.nama AS nama_pasien, p.no_rm, " +
                    "d.nama AS nama_dokter, a.status " +
                    "FROM antrian a " +
                    "JOIN pasien p ON a.id_pasien = p.id " +
                    "JOIN dokter d ON a.id_dokter = d.id "
                );

                List<Object> params = new ArrayList<>();
                boolean hasWhere = false;

                if (filterDokter != null && filterDokter.getId() > 0) {
                    sql.append(" WHERE a.id_dokter = ?");
                    params.add(filterDokter.getId());
                    hasWhere = true;
                }

                if (!filterTgl.isEmpty()) {
                    sql.append(hasWhere ? " AND" : " WHERE").append(" a.tanggal = ?");
                    params.add(Date.valueOf(filterTgl));
                }

                sql.append(" ORDER BY a.nomor_antrian ASC");

                try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    try (ResultSet rs = pstmt.executeQuery()) {
                        int noSelesai = 1;
                        while (rs.next()) {
                            String status = rs.getString("status");
                            String namaPasien = rs.getString("nama_pasien");
                            String namaDokter = rs.getString("nama_dokter");

                            if (status.equalsIgnoreCase("Menunggu")) {
                                menunggu++;
                                waitingList.add(new Object[]{
                                    rs.getInt("id"), rs.getInt("nomor_antrian"), 
                                    namaPasien, rs.getString("no_rm"), namaDokter
                                });
                            } else if (status.equalsIgnoreCase("Dipanggil") || status.equalsIgnoreCase("Diperiksa")) {
                                diperiksa++;
                                nowServingNama = namaPasien;
                                nowServingDokter = namaDokter;
                            } else if (status.equalsIgnoreCase("Selesai")) {
                                selesai++;
                                finishedList.add(new Object[]{
                                    noSelesai++, namaPasien, rs.getString("no_rm"), namaDokter, status
                                });
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    
                    view.setStatCount(menunggu, diperiksa, selesai);
                    view.setNowServing(nowServingNama, nowServingDokter);

                    view.clearAntrianCards();
                    for (Object[] row : waitingList) {
                        int antrianId = (int) row[0];
                        ActionListener onPanggil = e -> panggilPasien(antrianId);
                        view.addAntrianCard((int) row[1], (String) row[2], (String) row[3], (String) row[4], onPanggil);
                    }

                    DefaultTableModel model = view.getTableModelSelesai();
                    if (model != null) {
                        model.setRowCount(0);
                        for (Object[] row : finishedList) {
                            model.addRow(row);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void panggilPasien(int idAntrian) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                if (connection == null) return null;
                
                // 1. Update status antrian menjadi 'Dipanggil'
                String sqlUpdate = "UPDATE antrian SET status = 'Dipanggil' WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlUpdate)) {
                    pstmt.setInt(1, idAntrian);
                    pstmt.executeUpdate();
                }
                
                // 2. Cari detail pasien untuk direhidrasi
                String sqlPasien = "SELECT p.id, p.nama, p.no_rm, p.alamat, p.no_telp, p.tanggal_lahir, p.golongan_darah, p.alergi " +
                                   "FROM antrian a JOIN pasien p ON a.id_pasien = p.id " +
                                   "WHERE a.id = ?";
                model.Pasien pasien = null;
                try (PreparedStatement pstmt = connection.prepareStatement(sqlPasien)) {
                    pstmt.setInt(1, idAntrian);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            pasien = new model.Pasien(
                                rs.getInt("id"),
                                rs.getString("nama"),
                                rs.getString("no_rm"),
                                rs.getString("alamat"),
                                rs.getString("no_telp"),
                                rs.getDate("tanggal_lahir"),
                                rs.getString("golongan_darah"),
                                rs.getString("alergi")
                            );
                        }
                    }
                }
                
                // 3. Kirim notifikasi melalui model domain
                if (pasien != null) {
                    return pasien.kirimNotifikasi("Silakan menuju ke ruang periksa.");
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    String notification = get();
                    if (notification != null) {
                        JOptionPane.showMessageDialog(view, notification, "Panggilan Pasien", JOptionPane.INFORMATION_MESSAGE);
                    }
                    loadData(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    loadData(true);
                }
            }
        };
        worker.execute();
    }
}