package controller;

import database.DBConnection;
import view.ComboItem;
import view.KunjunganView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KunjunganController {
    private final KunjunganView view;
    private final Connection connection;
    private boolean isEditingMode = false;
    private int selectedKunjunganId = -1;
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public KunjunganController(KunjunganView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        loadDropdowns();
        loadData();
    }

    private void initListeners() {
        view.addTambahListener(e -> handleTambah());
        view.addSimpanListener(e -> handleSimpan());
        view.addHapusListener(e -> handleHapus());
        view.addBatalListener(e -> handleBatal());
        view.addCariListener(e -> handleCari());
        
        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    public void loadDropdowns() {
        // SwingWorker to load dropdowns in background
        SwingWorker<DropdownData, Void> worker = new SwingWorker<>() {
            @Override
            protected DropdownData doInBackground() throws Exception {
                List<ComboItem> pasienList = new ArrayList<>();
                List<ComboItem> dokterList = new ArrayList<>();
                
                if (connection == null) return new DropdownData(pasienList, dokterList);

                // Load pasien
                String sqlPasien = "SELECT id, nama, no_rm FROM pasien ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlPasien)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String label = rs.getString("nama") + " (" + rs.getString("no_rm") + ")";
                        pasienList.add(new ComboItem(id, label));
                    }
                }

                // Load dokter
                String sqlDokter = "SELECT id, nama, spesialisasi FROM dokter ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlDokter)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String label = rs.getString("nama") + " - " + rs.getString("spesialisasi");
                        dokterList.add(new ComboItem(id, label));
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

    public void loadData() {
        view.setStatusText("Memuat riwayat kunjungan...");
        
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> dataList = new ArrayList<>();
                if (connection == null) return dataList;

                // Query with SQL JOINs to display Patient and Doctor Names
                String sql = "SELECT k.id, k.id_pasien, k.id_dokter, p.nama AS nama_pasien, p.no_rm, " +
                             "d.nama AS nama_dokter, d.spesialisasi, k.tanggal_kunjungan, k.keluhan, k.diagnosa " +
                             "FROM kunjungan k " +
                             "JOIN pasien p ON k.id_pasien = p.id " +
                             "JOIN dokter d ON k.id_dokter = d.id " +
                             "ORDER BY k.tanggal_kunjungan DESC, k.id DESC";
                             
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String pasienLabel = rs.getString("nama_pasien") + " (" + rs.getString("no_rm") + ")";
                        String dokterLabel = rs.getString("nama_dokter") + " - " + rs.getString("spesialisasi");
                        Timestamp tgl = rs.getTimestamp("tanggal_kunjungan");
                        String keluhan = rs.getString("keluhan");
                        String diagnosa = rs.getString("diagnosa");
                        
                        dataList.add(new Object[]{
                            id, 
                            pasienLabel, 
                            dokterLabel, 
                            tgl != null ? tgl.toString() : "", 
                            keluhan, 
                            diagnosa
                        });
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
                    view.setStatusText("Riwayat kunjungan berhasil dimuat (" + dataList.size() + " catatan)");
                } catch (Exception e) {
                    view.setStatusText("Gagal memuat data kunjungan: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleTambah() {
        if (view.getSelectedPasien() == null || view.getSelectedDokter() == null) {
            JOptionPane.showMessageDialog(view, "Pastikan data Pasien dan Dokter sudah terdaftar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        isEditingMode = true;
        selectedKunjunganId = -1;
        view.clearForm();
        view.setFormEnabled(true);
        view.setButtonsState(true);
        
        // Auto fill current timestamp
        view.setTanggalInput(LocalDateTime.now().format(formatter));
        view.setStatusText("Mencatat kunjungan medis baru");
    }

    private void handleSimpan() {
        ComboItem pasien = view.getSelectedPasien();
        ComboItem dokter = view.getSelectedDokter();
        String tglStr = view.getTanggalInput();
        String keluhan = view.getKeluhanInput();
        String diagnosa = view.getDiagnosaInput();

        if (pasien == null || dokter == null || tglStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pasien, Dokter, dan Waktu wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Timestamp tglKunjungan;
        try {
            // Support standard timestamp parse
            tglKunjungan = Timestamp.valueOf(tglStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Format Waktu salah! Gunakan YYYY-MM-DD HH:mm:ss", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.setStatusText("Menyimpan catatan kunjungan...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;

                if (selectedKunjunganId == -1) {
                    String sql = "INSERT INTO kunjungan (id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, pasien.getId());
                        pstmt.setInt(2, dokter.getId());
                        pstmt.setTimestamp(3, tglKunjungan);
                        pstmt.setString(4, keluhan);
                        pstmt.setString(5, diagnosa);
                        pstmt.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE kunjungan SET id_pasien=?, id_dokter=?, tanggal_kunjungan=?, keluhan=?, diagnosa=? WHERE id=?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, pasien.getId());
                        pstmt.setInt(2, dokter.getId());
                        pstmt.setTimestamp(3, tglKunjungan);
                        pstmt.setString(4, keluhan);
                        pstmt.setString(5, diagnosa);
                        pstmt.setInt(6, selectedKunjunganId);
                        pstmt.executeUpdate();
                    }
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Catatan kunjungan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        isEditingMode = false;
                        selectedKunjunganId = -1;
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setButtonsState(false);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    view.setStatusText("Error saat menyimpan: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleHapus() {
        int id = view.getSelectedId();
        if (id == -1) {
            JOptionPane.showMessageDialog(view, "Pilih catatan kunjungan yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Hapus catatan kunjungan ini? Tindakan ini juga akan menghapus resep dan tagihan terkait jika ada.", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.setStatusText("Menghapus catatan kunjungan...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                // Delete dependecies first (tagihan, resep/detail resep)
                // Delete detail resep
                String sqlDelDetResep = "DELETE FROM detail_resep WHERE id_resep IN (SELECT id FROM resep WHERE id_kunjungan = ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDelDetResep)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
                
                // Delete resep
                String sqlDelResep = "DELETE FROM resep WHERE id_kunjungan = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDelResep)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
                
                // Delete tagihan
                String sqlDelTagihan = "DELETE FROM tagihan WHERE id_kunjungan = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDelTagihan)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }

                String sql = "DELETE FROM kunjungan WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Catatan kunjungan berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setButtonsState(false);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error saat menghapus: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    view.setStatusText("Gagal menghapus: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleBatal() {
        isEditingMode = false;
        selectedKunjunganId = -1;
        view.clearForm();
        view.setFormEnabled(false);
        view.setButtonsState(false);
        view.setStatusText("Siap");
    }

    private void handleCari() {
        String keyword = view.getCariInput();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        view.setStatusText("Mencari riwayat kunjungan '" + keyword + "'...");
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> dataList = new ArrayList<>();
                if (connection == null) return dataList;

                String sql = "SELECT k.id, k.id_pasien, k.id_dokter, p.nama AS nama_pasien, p.no_rm, " +
                             "d.nama AS nama_dokter, d.spesialisasi, k.tanggal_kunjungan, k.keluhan, k.diagnosa " +
                             "FROM kunjungan k " +
                             "JOIN pasien p ON k.id_pasien = p.id " +
                             "JOIN dokter d ON k.id_dokter = d.id " +
                             "WHERE p.nama LIKE ? OR d.nama LIKE ? OR k.diagnosa LIKE ? " +
                             "ORDER BY k.tanggal_kunjungan DESC";
                             
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, "%" + keyword + "%");
                    pstmt.setString(2, "%" + keyword + "%");
                    pstmt.setString(3, "%" + keyword + "%");
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            String pasienLabel = rs.getString("nama_pasien") + " (" + rs.getString("no_rm") + ")";
                            String dokterLabel = rs.getString("nama_dokter") + " - " + rs.getString("spesialisasi");
                            Timestamp tgl = rs.getTimestamp("tanggal_kunjungan");
                            String keluhan = rs.getString("keluhan");
                            String diagnosa = rs.getString("diagnosa");
                            
                            dataList.add(new Object[]{
                                id, pasienLabel, dokterLabel, tgl != null ? tgl.toString() : "", keluhan, diagnosa
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
                    view.setStatusText("Ditemukan " + dataList.size() + " catatan kunjungan");
                } catch (Exception e) {
                    view.setStatusText("Gagal mencari kunjungan: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleTableClick() {
        int row = view.getTable().getSelectedRow();
        if (row != -1 && !isEditingMode) {
            selectedKunjunganId = Integer.parseInt(view.getTable().getValueAt(row, 0).toString());
            
            view.setStatusText("Mengambil rincian kunjungan...");
            
            // SwingWorker to fetch specific row data with precise ComboItem matching
            SwingWorker<KunjunganDetails, Void> worker = new SwingWorker<>() {
                @Override
                protected KunjunganDetails doInBackground() throws Exception {
                    if (connection == null) return null;
                    
                    String sql = "SELECT id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa FROM kunjungan WHERE id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, selectedKunjunganId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                return new KunjunganDetails(
                                    rs.getInt("id_pasien"),
                                    rs.getInt("id_dokter"),
                                    rs.getTimestamp("tanggal_kunjungan"),
                                    rs.getString("keluhan"),
                                    rs.getString("diagnosa")
                                );
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        KunjunganDetails details = get();
                        if (details != null) {
                            ComboItem pasienItem = new ComboItem(details.idPasien, "");
                            ComboItem dokterItem = new ComboItem(details.idDokter, "");
                            
                            view.fillForm(
                                pasienItem, 
                                dokterItem, 
                                details.tanggal != null ? details.tanggal.toString() : "", 
                                details.keluhan, 
                                details.diagnosa
                            );
                            
                            view.setFormEnabled(true);
                            view.setButtonsState(true);
                            isEditingMode = true;
                            view.setStatusText("Mengedit catatan kunjungan ID: " + selectedKunjunganId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    // Helper classes
    private static class DropdownData {
        final List<ComboItem> pasienList;
        final List<ComboItem> dokterList;

        DropdownData(List<ComboItem> pasienList, List<ComboItem> dokterList) {
            this.pasienList = pasienList;
            this.dokterList = dokterList;
        }
    }

    private static class KunjunganDetails {
        final int idPasien;
        final int idDokter;
        final Timestamp tanggal;
        final String keluhan;
        final String diagnosa;

        KunjunganDetails(int idPasien, int idDokter, Timestamp tanggal, String keluhan, String diagnosa) {
            this.idPasien = idPasien;
            this.idDokter = idDokter;
            this.tanggal = tanggal;
            this.keluhan = keluhan;
            this.diagnosa = diagnosa;
        }
    }
}
