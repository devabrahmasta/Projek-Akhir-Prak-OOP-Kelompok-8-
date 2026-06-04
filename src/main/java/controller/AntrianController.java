package controller;

import database.DBConnection;
import view.AntrianView;
import view.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AntrianController {
    private final AntrianView view;
    private final Connection connection;

    private Thread refreshThread;
    private volatile boolean refreshThreadRunning = true;

    private Consumer<PasienInfo> onPanggilListener;

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
                        SwingUtilities.invokeLater(() -> loadData(false));
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
        if (refreshThread != null) refreshThread.interrupt();
    }

    public void setOnPanggilListener(Consumer<PasienInfo> listener) {
        this.onPanggilListener = listener;
    }

    public void loadDropdowns() {
        SwingWorker<List<ComboItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ComboItem> doInBackground() throws Exception {
                List<ComboItem> dokterList = new ArrayList<>();
                if (connection == null) return dokterList;
                String sql = "SELECT id, nama, spesialisasi FROM dokter WHERE is_active=1 ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        dokterList.add(new ComboItem(rs.getInt("id"),
                            rs.getString("nama") + " - " + rs.getString("spesialisasi")));
                    }
                }
                return dokterList;
            }

            @Override
            protected void done() {
                try { view.setDokterList(get().toArray(new ComboItem[0])); }
                catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    public void loadData(boolean isManualRefresh) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            int menunggu = 0, diperiksa = 0, selesai = 0;
            List<ActiveServingInfo> activeList = new ArrayList<>();
            List<Object[]> waitingList  = new ArrayList<>();
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
                    for (int i = 0; i < params.size(); i++) pstmt.setObject(i + 1, params.get(i));
                    try (ResultSet rs = pstmt.executeQuery()) {
                        int noSelesai = 1;
                        while (rs.next()) {
                            String status    = rs.getString("status");
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
                                activeList.add(new ActiveServingInfo(
                                    rs.getInt("nomor_antrian"),
                                    namaPasien,
                                    namaDokter
                                ));
                            } else if (status.equalsIgnoreCase("Selesai")) {
                                selesai++;
                                finishedList.add(new Object[]{noSelesai++, namaPasien, rs.getString("no_rm"), namaDokter, status});
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
                    view.setNowServing(activeList);

                    boolean isDokter  = SessionManager.hasRole("dokter");
                    boolean isAdmin   = SessionManager.hasRole("admin");
                    boolean canBatal  = SessionManager.hasRole("resepsionis") || isAdmin;

                    view.clearAntrianCards();
                    for (Object[] row : waitingList) {
                        int antrianId = (int) row[0];
                        ActionListener onPanggil = (isDokter || isAdmin)
                            ? e -> panggilPasien(antrianId) : null;
                        ActionListener onBatal = canBatal
                            ? e -> batalkanAntrian(antrianId) : null;
                        view.addAntrianCard(
                            (int) row[1], (String) row[2], (String) row[3], (String) row[4],
                            onPanggil, onBatal
                        );
                    }
                    if (waitingList.isEmpty()) view.showEmptyState();

                    DefaultTableModel model = view.getTableModelSelesai();
                    if (model != null) {
                        model.setRowCount(0);
                        for (Object[] row : finishedList) model.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void batalkanAntrian(int idAntrian) {
        int confirm = JOptionPane.showConfirmDialog(view,
            "Batalkan antrian ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                String sql = "UPDATE antrian SET status='Batal' WHERE id=?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, idAntrian);
                    ps.executeUpdate();
                }
                return true;
            }
            @Override
            protected void done() {
                try { if (get()) loadData(true); }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void panggilPasien(int idAntrian) {
        SwingWorker<PasienInfo, Void> worker = new SwingWorker<>() {
            private String activePatientName = null;
            private String targetDoctorName = null;

            @Override
            protected PasienInfo doInBackground() throws Exception {
                if (connection == null) return null;
                
                // 1. Get the doctor details for this queue item
                int targetDocId = -1;
                String sqlDoc = "SELECT d.id, d.nama FROM antrian a JOIN dokter d ON a.id_dokter = d.id WHERE a.id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlDoc)) {
                    ps.setInt(1, idAntrian);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            targetDocId = rs.getInt("id");
                            targetDoctorName = rs.getString("nama");
                        }
                    }
                }
                
                if (targetDocId == -1) return null;
                
                // 2. Check if this doctor already has a patient with status 'Dipanggil' or 'Diperiksa' today
                String sqlCheck = 
                    "SELECT p.nama FROM antrian a " +
                    "JOIN pasien p ON a.id_pasien = p.id " +
                    "WHERE a.id_dokter = ? AND a.status IN ('Dipanggil', 'Diperiksa') " +
                    "AND a.tanggal = CURRENT_DATE LIMIT 1";
                try (PreparedStatement ps = connection.prepareStatement(sqlCheck)) {
                    ps.setInt(1, targetDocId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            activePatientName = rs.getString("nama");
                            return null;
                        }
                    }
                }
                
                String sqlUpd = "UPDATE antrian SET status='Dipanggil' WHERE id=?";
                try (PreparedStatement ps = connection.prepareStatement(sqlUpd)) {
                    ps.setInt(1, idAntrian);
                    ps.executeUpdate();
                }
                
                String sqlPasien =
                    "SELECT p.id, p.nama, p.no_rm, d.nama AS nama_dokter, d.id AS id_dokter " +
                    "FROM antrian a " +
                    "JOIN pasien p ON a.id_pasien = p.id " +
                    "JOIN dokter d ON a.id_dokter = d.id " +
                    "WHERE a.id = ?";
                try (PreparedStatement ps = connection.prepareStatement(sqlPasien)) {
                    ps.setInt(1, idAntrian);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new PasienInfo(
                                rs.getInt("id"), rs.getString("nama"),
                                rs.getString("no_rm"), rs.getString("nama_dokter"),
                                rs.getInt("id_dokter")
                            );
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    if (activePatientName != null) {
                        JOptionPane.showMessageDialog(view, 
                            "Dokter " + targetDoctorName + " masih memiliki pasien yang sedang diperiksa: " + activePatientName + ".\n" +
                            "Selesaikan kunjungan pasien tersebut terlebih dahulu sebelum memanggil pasien baru.", 
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    PasienInfo info = get();
                    if (info == null) return;
                    loadData(false); 
                    if (onPanggilListener != null) {
                        onPanggilListener.accept(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    
    public static class PasienInfo {
        public int idPasien, idDokter;
        public String nama, noRM, namaDokter;

        public PasienInfo(int idPasien, String nama, String noRM, String namaDokter, int idDokter) {
            this.idPasien   = idPasien;
            this.nama       = nama;
            this.noRM       = noRM;
            this.namaDokter = namaDokter;
            this.idDokter   = idDokter;
        }
    }

    public static class ActiveServingInfo {
        public final int nomorAntrian;
        public final String namaPasien;
        public final String namaDokter;

        public ActiveServingInfo(int nomorAntrian, String namaPasien, String namaDokter) {
            this.nomorAntrian = nomorAntrian;
            this.namaPasien = namaPasien;
            this.namaDokter = namaDokter;
        }
    }
}
