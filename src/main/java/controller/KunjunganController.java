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
    private java.util.function.BiConsumer<Integer, Integer> onKunjunganSelectedListener;
    
    public void setOnKunjunganSelectedListener(java.util.function.BiConsumer<Integer, Integer> listener) {
        this.onKunjunganSelectedListener = listener;
    }
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public KunjunganController(KunjunganView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        loadDropdowns();
        loadAntrian();
        loadData();
        
        if (SessionManager.isLoggedIn()) {
            view.setDokterName(SessionManager.getUser().getNama());
            view.showDokterCombo(SessionManager.hasRole("admin"));
            if (SessionManager.hasRole("resepsionis")) {
                view.setReadOnlyMode();
            }
        }
    }
    
    private int resolveDokterId() {
        if (!SessionManager.isLoggedIn()) return -1;
        int idUser = SessionManager.getUser().getId();
        String sql = "SELECT id FROM dokter WHERE id_user = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idUser);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static class DropdownsData {
        final List<ComboItem> pasienList;
        final List<ComboItem> dokterList;
        DropdownsData(List<ComboItem> p, List<ComboItem> d) {
            this.pasienList = p;
            this.dokterList = d;
        }
    }

    private String getSortOptionSql(String sortOption) {
        String filter = "";
        String order = " ORDER BY k.tanggal_kunjungan DESC, k.id DESC";
        
        if ("Paling Lama".equals(sortOption)) {
            order = " ORDER BY k.tanggal_kunjungan ASC, k.id ASC";
        } else if ("24 Jam Terakhir".equals(sortOption)) {
            filter = " AND k.tanggal_kunjungan >= NOW() - INTERVAL 1 DAY ";
        } else if ("1 Bulan Terakhir".equals(sortOption)) {
            filter = " AND k.tanggal_kunjungan >= NOW() - INTERVAL 30 DAY ";
        }
        return filter + order;
    }

    private void initListeners() {
        view.addTambahListener(e -> handleTambah());
        view.addSimpanListener(e -> handleSimpan());
        view.addHapusListener(e -> handleHapus());
        view.addBatalListener(e -> handleBatal());
        view.addCariListener(e -> handleCari());
        view.addSortListener(e -> loadData());
        
        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    public void loadDropdowns() {
        SwingWorker<DropdownsData, Void> worker = new SwingWorker<>() {
            @Override
            protected DropdownsData doInBackground() throws Exception {
                List<ComboItem> pasienList = new ArrayList<>();
                List<ComboItem> dokterList = new ArrayList<>();
                if (connection == null) return new DropdownsData(pasienList, dokterList);

                String sqlPasien = "SELECT id, nama, no_rm FROM pasien WHERE is_active = 1 ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlPasien)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String label = rs.getString("nama") + " (" + rs.getString("no_rm") + ")";
                        pasienList.add(new ComboItem(id, label));
                    }
                }

                String sqlDokter = "SELECT id, nama FROM dokter WHERE is_active = 1 ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sqlDokter)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String label = rs.getString("nama");
                        dokterList.add(new ComboItem(id, label));
                    }
                }
                return new DropdownsData(pasienList, dokterList);
            }

            @Override
            protected void done() {
                try {
                    DropdownsData data = get();
                    view.setPasienList(data.pasienList.toArray(new ComboItem[0]));
                    view.setDokterList(data.dokterList.toArray(new ComboItem[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    public void loadAntrian() {
        SwingWorker<List<AntrianItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<AntrianItem> doInBackground() throws Exception {
                List<AntrianItem> list = new ArrayList<>();
                if (connection == null) return list;
                
                boolean isDokter = SessionManager.hasRole("dokter");
                int docId = -1;
                String sql;
                if (isDokter) {
                    docId = resolveDokterId();
                    sql = "SELECT a.id, a.id_pasien, a.nomor_antrian, p.nama, p.no_rm " +
                          "FROM antrian a JOIN pasien p ON a.id_pasien = p.id " +
                          "WHERE a.id_dokter = ? AND DATE(a.tanggal) = CURRENT_DATE AND a.status != 'Selesai' " +
                          "ORDER BY a.nomor_antrian ASC";
                } else {
                    sql = "SELECT a.id, a.id_pasien, a.nomor_antrian, p.nama, p.no_rm " +
                          "FROM antrian a JOIN pasien p ON a.id_pasien = p.id " +
                          "WHERE DATE(a.tanggal) = CURRENT_DATE AND a.status != 'Selesai' " +
                          "ORDER BY a.nomor_antrian ASC";
                }
                
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    if (isDokter) {
                        pstmt.setInt(1, docId);
                    }
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            list.add(new AntrianItem(
                                rs.getInt("id"), 
                                rs.getInt("id_pasien"), 
                                rs.getInt("nomor_antrian"), 
                                rs.getString("nama"),
                                rs.getString("no_rm")
                            ));
                        }
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<AntrianItem> list = get();
                    view.setAntrianAktif(list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void loadData() {
        view.setStatusText("Memuat riwayat kunjungan...");
        
        SwingWorker<List<model.Kunjungan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<model.Kunjungan> doInBackground() throws Exception {
                List<model.Kunjungan> kunjunganList = new ArrayList<>();
                if (connection == null) return kunjunganList;

                boolean isDokter = SessionManager.hasRole("dokter");
                int docId = -1;
                String sql;
                if (isDokter) {
                    docId = resolveDokterId();
                    sql = "SELECT k.id, k.id_pasien, k.id_dokter, p.nama AS nama_pasien, p.no_rm, " +
                          "d.nama AS nama_dokter, d.spesialisasi, k.tanggal_kunjungan, k.keluhan, k.diagnosa " +
                          "FROM kunjungan k " +
                          "JOIN pasien p ON k.id_pasien = p.id " +
                          "JOIN dokter d ON k.id_dokter = d.id " +
                          "WHERE k.id_dokter = ? " + getSortOptionSql(view.getSortOption());
                } else {
                    sql = "SELECT k.id, k.id_pasien, k.id_dokter, p.nama AS nama_pasien, p.no_rm, " +
                          "d.nama AS nama_dokter, d.spesialisasi, k.tanggal_kunjungan, k.keluhan, k.diagnosa " +
                          "FROM kunjungan k " +
                          "JOIN pasien p ON k.id_pasien = p.id " +
                          "JOIN dokter d ON k.id_dokter = d.id " +
                          "WHERE 1=1 " + getSortOptionSql(view.getSortOption());
                }
                             
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    if (isDokter) {
                        pstmt.setInt(1, docId);
                    }
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            int idPasien = rs.getInt("id_pasien");
                            int idDokter = rs.getInt("id_dokter");
                            Timestamp tgl = rs.getTimestamp("tanggal_kunjungan");
                            String keluhan = rs.getString("keluhan");
                            String diagnosa = rs.getString("diagnosa");
                            
                            // Rehidrasi objek Kunjungan seutuhnya (OOP Murni)
                            model.Kunjungan k = new model.Kunjungan(id, idPasien, idDokter, tgl, keluhan, diagnosa);
                            
                            // Rehidrasi objek asosiasi domain Pasien
                            model.Pasien p = new model.Pasien();
                            p.setId(idPasien);
                            p.setNama(rs.getString("nama_pasien"));
                            p.setNoRM(rs.getString("no_rm"));
                            k.setPasien(p);
                            
                            // Rehidrasi objek asosiasi domain Dokter secara polimorfik
                            String spec = rs.getString("spesialisasi");
                            model.Dokter d = "Umum".equalsIgnoreCase(spec) ?
                                             new model.DokterUmum(idDokter, rs.getString("nama_dokter")) :
                                             new model.DokterSpesialis(idDokter, rs.getString("nama_dokter"), spec);
                            k.setDokter(d);
                            
                            kunjunganList.add(k);
                        }
                    }
                }
                return kunjunganList;
            }

            @Override
            protected void done() {
                try {
                    List<model.Kunjungan> kunjunganList = get();
                    DefaultTableModel modelTable = view.getTableModel();
                    modelTable.setRowCount(0);
                    for (model.Kunjungan k : kunjunganList) {
                        String pasienLabel = k.getPasien().getNama() + " (" + k.getPasien().getNoRM() + ")";
                        String dokterLabel = k.getDokter().getNama() + " - " + k.getDokter().getSpesialisasi();
                        modelTable.addRow(new Object[]{
                            k.getId(), 
                            pasienLabel, 
                            dokterLabel, 
                            k.getTanggalKunjungan() != null ? k.getTanggalKunjungan().toString() : "", 
                            k.getKeluhan(), 
                            k.getDiagnosa()
                        });
                    }
                    view.setStatusText("Riwayat kunjungan berhasil dimuat (" + kunjunganList.size() + " catatan)");
                } catch (Exception e) {
                    view.setStatusText("Gagal memuat data kunjungan: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleTambah() {
        if (view.getSelectedPasien() == null) {
            JOptionPane.showMessageDialog(view, "Pastikan data Pasien sudah terdaftar!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        isEditingMode = true;
        selectedKunjunganId = -1;
        view.clearForm();
        view.setFormEnabled(true);
        view.setButtonsState(true);
        
        view.setTanggalInput(LocalDateTime.now().format(formatter));
        view.setStatusText("Mencatat kunjungan medis baru");
    }

    private void handleSimpan() {
        ComboItem pasien = view.getSelectedPasien();
        String tglStr = view.getTanggalInput();
        String keluhan = view.getKeluhanInput();
        String diagnosa = view.getDiagnosaInput();
        int idDokter = -1;
        if (SessionManager.hasRole("dokter")) {
            idDokter = resolveDokterId();
        } else if (SessionManager.hasRole("admin")) {
            ComboItem selectedDoc = view.getSelectedDokter();
            if (selectedDoc != null) {
                idDokter = selectedDoc.getId();
            } else {
                idDokter = 1; // Fallback
            }
        } else {
            idDokter = 1; // Default fallback to first doctor
        }
        
        final int finalIdDokter = idDokter;

        if (pasien == null || tglStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pasien dan Waktu wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Timestamp tglKunjungan;
        try {
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

                connection.setAutoCommit(false);
                try {
                    if (selectedKunjunganId == -1) {
                        String sql = "INSERT INTO kunjungan (id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                            pstmt.setInt(1, pasien.getId());
                            pstmt.setInt(2, finalIdDokter);
                            pstmt.setTimestamp(3, tglKunjungan);
                            pstmt.setString(4, keluhan);
                            pstmt.setString(5, diagnosa);
                            pstmt.executeUpdate();
                            
                            int generatedId = -1;
                            try (ResultSet rsKeys = pstmt.getGeneratedKeys()) {
                                if (rsKeys.next()) {
                                    generatedId = rsKeys.getInt(1);
                                }
                            }
                            
                            // Rehidrasi objek Kunjungan seutuhnya untuk memenuhi OOP
                            model.Kunjungan kunjunganObj = new model.Kunjungan(
                                generatedId,
                                pasien.getId(),
                                finalIdDokter,
                                tglKunjungan,
                                keluhan,
                                diagnosa
                            );
                            
                            model.Pasien pObj = new model.Pasien();
                            pObj.setId(pasien.getId());
                            pObj.setNama(pasien.getLabel());
                            kunjunganObj.setPasien(pObj);
                            
                            model.Dokter dObj = new model.DokterUmum(finalIdDokter, "Dokter Pemeriksa");
                            kunjunganObj.setDokter(dObj);
                            
                            // Memanggil method cetakLaporan() dari antarmuka Reportable
                            kunjunganObj.cetakLaporan();
                        }
                        
                        // Opsional: Update status antrian menjadi 'Selesai' jika ada
                        String sqlAntrian = "UPDATE antrian SET status = 'Selesai' WHERE id_pasien = ? AND id_dokter = ? AND DATE(tanggal) = CURRENT_DATE";
                        try (PreparedStatement pstmt = connection.prepareStatement(sqlAntrian)) {
                            pstmt.setInt(1, pasien.getId());
                            pstmt.setInt(2, finalIdDokter);
                            pstmt.executeUpdate();
                        }
                    } else {
                        String sql = "UPDATE kunjungan SET id_pasien=?, id_dokter=?, tanggal_kunjungan=?, keluhan=?, diagnosa=? WHERE id=?";
                        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                            pstmt.setInt(1, pasien.getId());
                            pstmt.setInt(2, finalIdDokter);
                            pstmt.setTimestamp(3, tglKunjungan);
                            pstmt.setString(4, keluhan);
                            pstmt.setString(5, diagnosa);
                            pstmt.setInt(6, selectedKunjunganId);
                            pstmt.executeUpdate();
                        }
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
                        JOptionPane.showMessageDialog(view, "Catatan kunjungan berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        isEditingMode = false;
                        selectedKunjunganId = -1;
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setButtonsState(false);
                        loadData();
                        loadAntrian();
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
                
                connection.setAutoCommit(false);
                try {
                    String sqlDelDetResep = "DELETE FROM detail_resep WHERE id_resep IN (SELECT id FROM resep WHERE id_kunjungan = ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelDetResep)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
                    String sqlDelResep = "DELETE FROM resep WHERE id_kunjungan = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDelResep)) {
                        pstmt.setInt(1, id);
                        pstmt.executeUpdate();
                    }
                    
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
        SwingWorker<List<model.Kunjungan>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<model.Kunjungan> doInBackground() throws Exception {
                List<model.Kunjungan> kunjunganList = new ArrayList<>();
                if (connection == null) return kunjunganList;
                
                boolean isDokter = SessionManager.hasRole("dokter");
                int docId = -1;
                String sql;
                if (isDokter) {
                    docId = resolveDokterId();
                    sql = "SELECT k.id, k.id_pasien, k.id_dokter, p.nama AS nama_pasien, p.no_rm, " +
                          "d.nama AS nama_dokter, d.spesialisasi, k.tanggal_kunjungan, k.keluhan, k.diagnosa " +
                          "FROM kunjungan k " +
                          "JOIN pasien p ON k.id_pasien = p.id " +
                          "JOIN dokter d ON k.id_dokter = d.id " +
                          "WHERE k.id_dokter = ? AND (p.nama LIKE ? OR k.diagnosa LIKE ?) " + getSortOptionSql(view.getSortOption());
                } else {
                    sql = "SELECT k.id, k.id_pasien, k.id_dokter, p.nama AS nama_pasien, p.no_rm, " +
                          "d.nama AS nama_dokter, d.spesialisasi, k.tanggal_kunjungan, k.keluhan, k.diagnosa " +
                          "FROM kunjungan k " +
                          "JOIN pasien p ON k.id_pasien = p.id " +
                          "JOIN dokter d ON k.id_dokter = d.id " +
                          "WHERE (p.nama LIKE ? OR k.diagnosa LIKE ?) " + getSortOptionSql(view.getSortOption());
                }
                             
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    if (isDokter) {
                        pstmt.setInt(1, docId);
                        pstmt.setString(2, "%" + keyword + "%");
                        pstmt.setString(3, "%" + keyword + "%");
                    } else {
                        pstmt.setString(1, "%" + keyword + "%");
                        pstmt.setString(2, "%" + keyword + "%");
                    }
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            int idPasien = rs.getInt("id_pasien");
                            int idDokter = rs.getInt("id_dokter");
                            Timestamp tgl = rs.getTimestamp("tanggal_kunjungan");
                            String keluhan = rs.getString("keluhan");
                            String diagnosa = rs.getString("diagnosa");
                            
                            // Rehidrasi objek Kunjungan seutuhnya (OOP Murni)
                            model.Kunjungan k = new model.Kunjungan(id, idPasien, idDokter, tgl, keluhan, diagnosa);
                            
                            // Rehidrasi objek asosiasi domain Pasien
                            model.Pasien p = new model.Pasien();
                            p.setId(idPasien);
                            p.setNama(rs.getString("nama_pasien"));
                            p.setNoRM(rs.getString("no_rm"));
                            k.setPasien(p);
                            
                            // Rehidrasi objek asosiasi domain Dokter secara polimorfik
                            String spec = rs.getString("spesialisasi");
                            model.Dokter d = "Umum".equalsIgnoreCase(spec) ?
                                             new model.DokterUmum(idDokter, rs.getString("nama_dokter")) :
                                             new model.DokterSpesialis(idDokter, rs.getString("nama_dokter"), spec);
                            k.setDokter(d);
                            
                            kunjunganList.add(k);
                        }
                    }
                }
                return kunjunganList;
            }

            @Override
            protected void done() {
                try {
                    List<model.Kunjungan> kunjunganList = get();
                    DefaultTableModel modelTable = view.getTableModel();
                    modelTable.setRowCount(0);
                    for (model.Kunjungan k : kunjunganList) {
                        String pasienLabel = k.getPasien().getNama() + " (" + k.getPasien().getNoRM() + ")";
                        String dokterLabel = k.getDokter().getNama() + " - " + k.getDokter().getSpesialisasi();
                        modelTable.addRow(new Object[]{
                            k.getId(), 
                            pasienLabel, 
                            dokterLabel, 
                            k.getTanggalKunjungan() != null ? k.getTanggalKunjungan().toString() : "", 
                            k.getKeluhan(), 
                            k.getDiagnosa()
                        });
                    }
                    view.setStatusText("Ditemukan " + kunjunganList.size() + " catatan kunjungan");
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
            
            SwingWorker<KunjunganDetails, Void> worker = new SwingWorker<>() {
                @Override
                protected KunjunganDetails doInBackground() throws Exception {
                    if (connection == null) return null;
                    
                    boolean isDokter = SessionManager.hasRole("dokter");
                    int docId = -1;
                    String sql;
                    if (isDokter) {
                        docId = resolveDokterId();
                        sql = "SELECT id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa FROM kunjungan WHERE id = ? AND id_dokter = ?";
                    } else {
                        sql = "SELECT id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa FROM kunjungan WHERE id = ?";
                    }
                    
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, selectedKunjunganId);
                        if (isDokter) {
                            pstmt.setInt(2, docId);
                        }
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
                            
                            if (onKunjunganSelectedListener != null) {
                                onKunjunganSelectedListener.accept(selectedKunjunganId, details.idPasien);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        }
    }

    private static class AntrianItem {
        final int idAntrian;
        final int idPasien;
        final int nomorAntrian;
        final String namaPasien;
        final String noRm;
        
        AntrianItem(int idAntrian, int idPasien, int nomorAntrian, String namaPasien, String noRm) {
            this.idAntrian = idAntrian;
            this.idPasien = idPasien;
            this.nomorAntrian = nomorAntrian;
            this.namaPasien = namaPasien;
            this.noRm = noRm;
        }
        
        @Override
        public String toString() {
            // Include rm so view logic can match the string with cbPasien label easily
            return "Antrian " + nomorAntrian + " - " + namaPasien + " (" + noRm + ")";
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
