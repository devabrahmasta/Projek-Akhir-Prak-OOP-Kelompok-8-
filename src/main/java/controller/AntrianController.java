package controller;

import database.DBConnection;
import view.ComboItem;
import view.AntrianView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AntrianController {
    private final AntrianView view;
    private final Connection connection;
    private int selectedAntrianId = -1;
    
    private Thread refreshThread;
    private volatile boolean refreshThreadRunning = true;

    public AntrianController(AntrianView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        initDefaultValues();
        loadDropdowns();
        loadData(true);
        startRefreshThread();
    }

    private void initListeners() {
        view.addTambahListener(e -> handleTambah());
        view.addPanggilListener(e -> handleUpdateStatus("Dipanggil"));
        view.addSelesaiListener(e -> handleUpdateStatus("Selesai"));
        view.addBatalListener(e -> handleUpdateStatus("Batal"));
        view.addHapusListener(e -> handleHapus());
        view.addFilterListener(e -> loadData(true));
        
        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    private void initDefaultValues() {
        LocalDate today = LocalDate.now();
        view.setTanggalInput(today.toString());
        view.setFilterTanggal(today.toString());
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
        SwingWorker<DropdownData, Void> worker = new SwingWorker<>() {
            @Override
            protected DropdownData doInBackground() throws Exception {
                List<ComboItem> pasienList = new ArrayList<>();
                List<ComboItem> dokterList = new ArrayList<>();
                
                if (connection == null) return new DropdownData(pasienList, dokterList);

                
                String sqlPasien = "SELECT id, nama, no_rm FROM pasien ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlPasien)) {
                    while (rs.next()) {
                        pasienList.add(new ComboItem(
                            rs.getInt("id"),
                            rs.getString("nama") + " (" + rs.getString("no_rm") + ")"
                        ));
                    }
                }

                
                String sqlDokter = "SELECT id, nama, spesialisasi FROM dokter ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlDokter)) {
                    while (rs.next()) {
                        dokterList.add(new ComboItem(
                            rs.getInt("id"),
                            rs.getString("nama") + " - " + rs.getString("spesialisasi")
                        ));
                    }
                }

                return new DropdownData(pasienList, dokterList);
            }

            @Override
            protected void done() {
                try {
                    DropdownData data = get();
                    view.setPasienList(data.pasienList.toArray(new ComboItem[0]));
                    view.setDokterList(data.dokterList.toArray(new ComboItem[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void loadData(boolean showMessageInStatus) {
        if (showMessageInStatus) {
            view.setStatusText("Memuat data antrian...");
        }

        
        ComboItem filterDokter = view.getFilterDokter();
        String filterTglStr = view.getFilterTanggal();

        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> dataList = new ArrayList<>();
                if (connection == null) return dataList;

                StringBuilder sql = new StringBuilder(
                    "SELECT a.id, a.nomor_antrian, p.nama AS nama_pasien, p.no_rm, " +
                    "d.nama AS nama_dokter, d.spesialisasi, a.tanggal, a.status " +
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

                if (!filterTglStr.isEmpty()) {
                    sql.append(hasWhere ? " AND" : " WHERE").append(" a.tanggal = ?");
                    params.add(Date.valueOf(filterTglStr));
                }

                sql.append(" ORDER BY a.tanggal DESC, a.nomor_antrian ASC");

                try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            dataList.add(new Object[]{
                                rs.getInt("id"),
                                rs.getInt("nomor_antrian"),
                                rs.getString("nama_pasien") + " (" + rs.getString("no_rm") + ")",
                                rs.getString("nama_dokter") + " - " + rs.getString("spesialisasi"),
                                rs.getDate("tanggal").toString(),
                                rs.getString("status")
                            });
                        }
                    }
                }
                return dataList;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> dataList = get();
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    for (Object[] row : dataList) {
                        model.addRow(row);
                    }
                    if (showMessageInStatus) {
                        view.setStatusText("Antrian dimuat (" + dataList.size() + " antrian)");
                    }
                } catch (Exception e) {
                    if (showMessageInStatus) {
                        view.setStatusText("Gagal memuat antrian: " + e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleTambah() {
        ComboItem pasien = view.getSelectedPasien();
        ComboItem dokter = view.getSelectedDokter();
        String tglStr = view.getTanggalInput();
        String status = view.getStatusInput();

        if (pasien == null || dokter == null || tglStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pasien, Dokter, dan Tanggal wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date tgl;
        try {
            tgl = Date.valueOf(tglStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Format Tanggal salah! Gunakan YYYY-MM-DD", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.setStatusText("Menambahkan antrian baru...");
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                if (connection == null) return -1;

                
                int nextQueueNum = 1;
                String sqlMax = "SELECT MAX(nomor_antrian) FROM antrian WHERE id_dokter = ? AND tanggal = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlMax)) {
                    pstmt.setInt(1, dokter.getId());
                    pstmt.setDate(2, tgl);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            nextQueueNum = rs.getInt(1) + 1;
                        }
                    }
                }

                
                String sqlInsert = "INSERT INTO antrian (id_pasien, id_dokter, tanggal, nomor_antrian, status) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                    pstmt.setInt(1, pasien.getId());
                    pstmt.setInt(2, dokter.getId());
                    pstmt.setDate(3, tgl);
                    pstmt.setInt(4, nextQueueNum);
                    pstmt.setString(5, status);
                    pstmt.executeUpdate();
                }
                return nextQueueNum;
            }

            @Override
            protected void done() {
                try {
                    int queueNum = get();
                    if (queueNum != -1) {
                        JOptionPane.showMessageDialog(view, "Berhasil menambahkan antrian!\nNomor Antrian Anda: " + queueNum, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        view.clearForm();
                        selectedAntrianId = -1;
                        view.setSelectionButtonsEnabled(false);
                        loadData(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    view.setStatusText("Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleUpdateStatus(String newStatus) {
        if (selectedAntrianId == -1) {
            JOptionPane.showMessageDialog(view, "Pilih antrian dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.setStatusText("Memperbarui status antrian ke '" + newStatus + "'...");
        SwingWorker<NotificationResult, Void> worker = new SwingWorker<>() {
            @Override
            protected NotificationResult doInBackground() throws Exception {
                if (connection == null) return new NotificationResult(false, null, "", 0);

                model.Pasien pasien = null;
                String namaDokter = "";
                int nomorAntrian = 0;

                String sqlDetails = "SELECT p.id, p.nama, p.no_rm, p.alamat, p.no_telp, p.tanggal_lahir, p.golongan_darah, p.alergi, " +
                                     "d.nama AS nama_dokter, a.nomor_antrian " +
                                     "FROM antrian a " +
                                     "JOIN pasien p ON a.id_pasien = p.id " +
                                     "JOIN dokter d ON a.id_dokter = d.id " +
                                     "WHERE a.id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDetails)) {
                    pstmt.setInt(1, selectedAntrianId);
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
                            namaDokter = rs.getString("nama_dokter");
                            nomorAntrian = rs.getInt("nomor_antrian");
                        }
                    }
                }

                String sql = "UPDATE antrian SET status = ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, newStatus);
                    pstmt.setInt(2, selectedAntrianId);
                    pstmt.executeUpdate();
                }

                return new NotificationResult(true, pasien, namaDokter, nomorAntrian);
            }

            @Override
            protected void done() {
                try {
                    NotificationResult result = get();
                    if (result.success) {
                        view.setStatusText("Status antrian diperbarui!");
                        view.clearForm();
                        selectedAntrianId = -1;
                        view.setSelectionButtonsEnabled(false);
                        loadData(true);

                        if (result.pasien != null) {
                            if (newStatus.equals("Dipanggil")) {
                                result.pasien.kirimNotifikasi(
                                    "Panggilan Antrian!\n\nHalo " + result.pasien.getNama() + ",\nNomor antrian Anda (" + 
                                    result.nomorAntrian + ") saat ini sedang DIPANGGIL.\nSilakan segera menuju ke ruangan periksa dr. " + result.namaDokter + "."
                                );
                            } else if (newStatus.equals("Selesai")) {
                                result.pasien.kirimNotifikasi(
                                    "Pemeriksaan Selesai!\n\nHalo " + result.pasien.getNama() + ",\nPemeriksaan medis Anda dengan dr. " + 
                                    result.namaDokter + " telah SELESAI.\nTerima kasih telah berkunjung ke Medika Center."
                                );
                            } else if (newStatus.equals("Batal")) {
                                result.pasien.kirimNotifikasi(
                                    "Pembatalan Antrian!\n\nHalo " + result.pasien.getNama() + ",\nNomor antrian Anda (" + 
                                    result.nomorAntrian + ") untuk dr. " + result.namaDokter + " telah kami BATALKAN sesuai permintaan."
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void handleHapus() {
        if (selectedAntrianId == -1) {
            JOptionPane.showMessageDialog(view, "Pilih antrian dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Hapus antrian ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.setStatusText("Menghapus antrian...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;

                String sql = "DELETE FROM antrian WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, selectedAntrianId);
                    pstmt.executeUpdate();
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        view.setStatusText("Antrian berhasil dihapus!");
                        view.clearForm();
                        selectedAntrianId = -1;
                        view.setSelectionButtonsEnabled(false);
                        loadData(true);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void handleTableClick() {
        int row = view.getTable().getSelectedRow();
        if (row != -1) {
            selectedAntrianId = Integer.parseInt(view.getTable().getValueAt(row, 0).toString());
            view.setSelectionButtonsEnabled(true);
            view.setStatusText("Terpilih Antrian ID: " + selectedAntrianId);
        }
    }

    private static class DropdownData {
        final List<ComboItem> pasienList;
        final List<ComboItem> dokterList;

        DropdownData(List<ComboItem> pasienList, List<ComboItem> dokterList) {
            this.pasienList = pasienList;
            this.dokterList = dokterList;
        }
    }

    private static class NotificationResult {
        final boolean success;
        final model.Pasien pasien;
        final String namaDokter;
        final int nomorAntrian;

        NotificationResult(boolean success, model.Pasien pasien, String namaDokter, int nomorAntrian) {
            this.success = success;
            this.pasien = pasien;
            this.namaDokter = namaDokter;
            this.nomorAntrian = nomorAntrian;
        }
    }
}
