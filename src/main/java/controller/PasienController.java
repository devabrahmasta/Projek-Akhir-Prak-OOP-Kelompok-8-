package controller;

import database.DBConnection;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import view.PasienView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PasienController {
    private final PasienView view;
    private final Connection connection;
    private boolean isEditingMode = false;
    private int selectedPasienId = -1;

    public PasienController(PasienView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addTambahListener(e -> handleTambah());
        view.addSimpanListener(e -> handleSimpan());
        view.addHapusListener(e -> handleHapus());
        view.addBatalListener(e -> handleBatal());
        
        // Cari dan Sort dipusatkan di loadData
        view.addCariListener(e -> loadData());
        view.addSortListener(e -> loadData());
        view.getBtnDaftarAntrian().addActionListener(e -> tampilkanDialogAntrian());
        
        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }
    
    private void tampilkanDialogAntrian() {
        int idPasien = view.getSelectedId();
        if (idPasien == -1) {
            JOptionPane.showMessageDialog(view, "Pilih pasien dari tabel terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(view), "Daftarkan Antrian", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new GridBagLayout());
        dialog.setLocationRelativeTo(view);
        dialog.getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Pilih Dokter / Poli:"), gbc);

        gbc.gridx = 1;
        JComboBox<view.ComboItem> cbDokter = new JComboBox<>();
        dialog.add(cbDokter, gbc);

        // Load dokter aktif
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nama, spesialisasi FROM dokter")) {
            while (rs.next()) {
                cbDokter.addItem(new view.ComboItem(rs.getInt("id"), rs.getString("nama") + " (" + rs.getString("spesialisasi") + ")"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton btnSimpan = new JButton("Simpan Antrian");
        btnSimpan.setBackground(new Color(55, 194, 174));
        btnSimpan.setForeground(Color.WHITE);
        dialog.add(btnSimpan, gbc);

        btnSimpan.addActionListener(e -> {
            view.ComboItem dokterDipilih = (view.ComboItem) cbDokter.getSelectedItem();
            if (dokterDipilih == null) return;

            try {
                // Generate Nomor Antrian
                String sqlMax = "SELECT MAX(nomor_antrian) FROM antrian WHERE tanggal = CURRENT_DATE";
                int noUrut = 1;
                try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sqlMax)) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        noUrut = rs.getInt(1) + 1;
                    }
                }

                String sqlInsert = "INSERT INTO antrian (id_pasien, id_dokter, tanggal, nomor_antrian, status) VALUES (?, ?, CURRENT_DATE, ?, 'Menunggu')";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlInsert)) {
                    pstmt.setInt(1, idPasien);
                    pstmt.setInt(2, dokterDipilih.getId());
                    pstmt.setInt(3, noUrut);
                    pstmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(dialog, "Berhasil masuk antrian! Nomor Urut: " + noUrut);
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan antrian: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    public void loadData() {
        view.setStatusText("Memuat data pasien...");
        String keyword = view.getCariInput();
        String sortOption = view.getSortOption();
        
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> dataList = new ArrayList<>();
                if (connection == null) return dataList;

                StringBuilder sql = new StringBuilder(
                    "SELECT id, no_rm, nama, no_telp, tanggal_lahir, golongan_darah, alergi, alamat FROM pasien"
                );
                
                List<String> conditions = new ArrayList<>();
                List<Object> params = new ArrayList<>();

                // Logika Pencarian
                if (keyword != null && !keyword.trim().isEmpty()) {
                    conditions.add("(nama LIKE ? OR no_rm LIKE ?)");
                    params.add("%" + keyword.trim() + "%");
                    params.add("%" + keyword.trim() + "%");
                }

                if (!conditions.isEmpty()) {
                    sql.append(" WHERE ").append(String.join(" AND ", conditions));
                }

                // Logika Sorting
                if ("Paling Lama".equals(sortOption)) {
                    sql.append(" ORDER BY id ASC");
                } else {
                    sql.append(" ORDER BY id DESC"); // Paling Baru (Default)
                }

                try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            dataList.add(new Object[]{
                                rs.getInt("id"),
                                rs.getString("no_rm"),
                                rs.getString("nama"),
                                rs.getString("no_telp"),
                                rs.getDate("tanggal_lahir"),
                                rs.getString("golongan_darah"),
                                rs.getString("alergi"),
                                rs.getString("alamat")
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
                    view.setStatusText("Data pasien berhasil dimuat (" + dataList.size() + " pasien)");
                } catch (Exception e) {
                    view.setStatusText("Gagal memuat data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleTambah() {
        isEditingMode = true;
        selectedPasienId = -1;
        view.clearForm();
        view.setFormEnabled(false);
        view.setButtonsState(true);
        view.setStatusText("Menghitung No. Rekam Medis baru...");
        
        generateNextNoRM();
    }

    private void generateNextNoRM() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                String nextRM = "RM-00001";
                if (connection == null) return nextRM;

                String sql = "SELECT no_rm FROM pasien WHERE no_rm LIKE 'RM-%'";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    int maxNum = 0;
                    while (rs.next()) {
                        String rm = rs.getString(1);
                        if (rm.length() > 3) {
                            try {
                                int num = Integer.parseInt(rm.substring(3));
                                if (num > maxNum) {
                                    maxNum = num;
                                }
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                    nextRM = String.format("RM-%05d", maxNum + 1);
                }
                return nextRM;
            }

            @Override
            protected void done() {
                try {
                    String nextRM = get();
                    view.fillForm("", nextRM, "", "", "", "", "", "");
                    view.setFormEnabled(true);
                    view.setStatusText("Mengisi data pasien baru");
                } catch (Exception e) {
                    view.setFormEnabled(true);
                    view.setStatusText("Error: Gagal memuat No. RM otomatis");
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleSimpan() {
        String nama = view.getNamaInput();
        String noRM = view.getNoRMInput();
        String alamat = view.getAlamatInput();
        String noTelp = view.getNoTelpInput();
        String tglLahirStr = view.getTanggalLahirInput();
        String golDarah = view.getGolonganDarahInput();
        String alergi = view.getAlergiInput();

        if (nama.isEmpty() || noRM.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nama dan No. RM wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date tglLahir = null;
        if (!tglLahirStr.isEmpty()) {
            try {
                tglLahir = Date.valueOf(tglLahirStr);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(view, "Format Tanggal Lahir salah! Gunakan YYYY-MM-DD", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        final Date finalTglLahir = tglLahir;
        view.setStatusText("Menyimpan data...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                if (selectedPasienId == -1) {
                    String sql = "INSERT INTO pasien (nama, no_rm, alamat, no_telp, tanggal_lahir, golongan_darah, alergi) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, nama);
                        pstmt.setString(2, noRM);
                        pstmt.setString(3, alamat);
                        pstmt.setString(4, noTelp);
                        pstmt.setDate(5, finalTglLahir);
                        pstmt.setString(6, golDarah);
                        pstmt.setString(7, alergi);
                        pstmt.executeUpdate();
                    }
                } else {
                    String sql = "UPDATE pasien SET nama=?, no_rm=?, alamat=?, no_telp=?, tanggal_lahir=?, golongan_darah=?, alergi=? WHERE id=?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, nama);
                        pstmt.setString(2, noRM);
                        pstmt.setString(3, alamat);
                        pstmt.setString(4, noTelp);
                        pstmt.setDate(5, finalTglLahir);
                        pstmt.setString(6, golDarah);
                        pstmt.setString(7, alergi);
                        pstmt.setInt(8, selectedPasienId);
                        pstmt.executeUpdate();
                    }
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Data pasien berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        isEditingMode = false;
                        selectedPasienId = -1;
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setButtonsState(false);
                        loadData();
                    } else {
                        view.setStatusText("Gagal menyimpan data.");
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
            JOptionPane.showMessageDialog(view, "Pilih pasien yang ingin dihapus terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Apakah Anda yakin ingin menghapus data pasien ini?\nTindakan ini akan menghapus semua riwayat kunjungan, resep, tagihan, dan antrian terkait pasien ini secara permanen.", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.setStatusText("Menghapus data...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                connection.setAutoCommit(false);
                try {
                    String sqlDelDetResep = "DELETE FROM detail_resep WHERE id_resep IN (" +
                                            "  SELECT id FROM resep WHERE id_kunjungan IN (" +
                                            "    SELECT id FROM kunjungan WHERE id_pasien = ?" +
                                            "  )" +
                                            ")";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelDetResep)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    String sqlDelResep = "DELETE FROM resep WHERE id_kunjungan IN (" +
                                         "  SELECT id FROM kunjungan WHERE id_pasien = ?" +
                                         ")";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelResep)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    String sqlDelTagihan = "DELETE FROM tagihan WHERE id_kunjungan IN (" +
                                           "  SELECT id FROM kunjungan WHERE id_pasien = ?" +
                                           ")";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelTagihan)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    String sqlDelKunjungan = "DELETE FROM kunjungan WHERE id_pasien = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelKunjungan)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    String sqlDelAntrian = "DELETE FROM antrian WHERE id_pasien = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelAntrian)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
    
                    String sql = "DELETE FROM pasien WHERE id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    connection.commit();
                    return true;
                } catch (Exception e) {
                    connection.rollback();
                    throw e;
                } finally {
                    connection.setAutoCommit(true);
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Data pasien berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        isEditingMode = false;
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
        selectedPasienId = -1;
        view.clearForm();
        view.setFormEnabled(false);
        view.setButtonsState(false);
        view.setStatusText("Siap");
    }

    private void handleTableClick() {
        int row = view.getTable().getSelectedRow();
        if (row != -1 && !isEditingMode) {
            selectedPasienId = Integer.parseInt(view.getTable().getValueAt(row, 0).toString());
            
            String noRM = String.valueOf(view.getTable().getValueAt(row, 1));
            String nama = String.valueOf(view.getTable().getValueAt(row, 2));
            String telp = view.getTable().getValueAt(row, 3) != null ? String.valueOf(view.getTable().getValueAt(row, 3)) : "";
            String tglLahir = view.getTable().getValueAt(row, 4) != null ? String.valueOf(view.getTable().getValueAt(row, 4)) : "";
            String golDarah = view.getTable().getValueAt(row, 5) != null ? String.valueOf(view.getTable().getValueAt(row, 5)) : "";
            String alergi = view.getTable().getValueAt(row, 6) != null ? String.valueOf(view.getTable().getValueAt(row, 6)) : "";
            String alamat = view.getTable().getValueAt(row, 7) != null ? String.valueOf(view.getTable().getValueAt(row, 7)) : "";

            view.fillForm(String.valueOf(selectedPasienId), noRM, nama, telp, tglLahir, golDarah, alergi, alamat);
            view.setFormEnabled(true);
            view.setButtonsState(true);
            isEditingMode = true;
            view.setStatusText("Mengedit pasien: " + nama);
        }
    }
}