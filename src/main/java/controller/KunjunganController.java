package controller;

import database.DBConnection;
import view.KunjunganView;
import view.ResepDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KunjunganController {
    private final KunjunganView view;
    private final Connection connection;

    
    private int activeIdPasien   = -1;
    private int activeIdDokter   = -1;
    private String activeNoRM    = "";
    private String namaPasienAktif = "";
    private int idKunjunganAktif = -1;  

    private Runnable onSelesaiListener;

    public void setOnSelesaiListener(Runnable listener) {
        this.onSelesaiListener = listener;
    }

    public KunjunganController(KunjunganView view) {
        this.view       = view;
        this.connection = DBConnection.getInstance().getConnection();

        if (SessionManager.hasRole("resepsionis")) {
            view.setReadOnlyMode();
        }

        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addCariListener(e -> handleCari());
        view.addSortListener(e -> loadData());
        view.addInputResepListener(e -> handleInputResep());
        view.addSelesaikanListener(e -> handleSelesaikan());
        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    
    public void autoFillFromAntrian(int idPasien, String namaPasien,
                                     String noRM, String namaDokter, int idDokter) {
        this.activeIdPasien   = idPasien;
        this.activeIdDokter   = idDokter;
        this.activeNoRM       = noRM;
        this.namaPasienAktif  = namaPasien;
        this.idKunjunganAktif = -1;  

        view.setFormAutoFilled(namaPasien, noRM, namaDokter);
        view.setFormEnabled(true);
        view.setSelesaikanEnabled(false);  
    }

    
    private void handleInputResep() {
        if (activeIdPasien == -1) {
            JOptionPane.showMessageDialog(view,
                "Belum ada pasien aktif. Panggil pasien dari menu Antrian terlebih dahulu.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (idKunjunganAktif == -1) {
            
            String keluhan  = view.getKeluhanInput();
            String diagnosa = view.getDiagnosaInput();
            SwingWorker<Integer, Void> worker = new SwingWorker<>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    String sql =
                        "INSERT INTO kunjungan " +
                        "(id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa, status) " +
                        "VALUES (?, ?, NOW(), ?, ?, 'sedang_diperiksa')";
                    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, activeIdPasien);
                        ps.setInt(2, activeIdDokter);
                        ps.setString(3, keluhan);
                        ps.setString(4, diagnosa);
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) return rs.getInt(1);
                        }
                    }
                    return -1;
                }

                @Override
                protected void done() {
                    try {
                        int id = get();
                        if (id != -1) {
                            idKunjunganAktif = id;
                            openResepDialog();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(view,
                            "Gagal menyimpan kunjungan sementara: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        } else {
            openResepDialog();
        }
    }

    private void openResepDialog() {
        Window parent = SwingUtilities.getWindowAncestor(view);
        ResepDialog resepDialog = new ResepDialog(parent, idKunjunganAktif, namaPasienAktif);
        new ResepDialogController(resepDialog, idKunjunganAktif);
        resepDialog.setVisible(true);
    }

    
    private void handleSelesaikan() {
        if (activeIdPasien == -1) {
            JOptionPane.showMessageDialog(view, "Tidak ada kunjungan aktif!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String keluhan  = view.getKeluhanInput();
        String diagnosa = view.getDiagnosaInput();

        if (keluhan.isEmpty() || diagnosa.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Keluhan dan diagnosa wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        final int finalActiveIdPasien   = activeIdPasien;
        final int finalActiveIdDokter   = activeIdDokter;
        final int finalIdKunjunganAktif = idKunjunganAktif;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                connection.setAutoCommit(false);
                try {
                    if (finalIdKunjunganAktif == -1) {
                        
                        String sqlK =
                            "INSERT INTO kunjungan " +
                            "(id_pasien, id_dokter, tanggal_kunjungan, keluhan, diagnosa, status) " +
                            "VALUES (?, ?, NOW(), ?, ?, 'selesai')";
                        try (PreparedStatement ps = connection.prepareStatement(sqlK, Statement.RETURN_GENERATED_KEYS)) {
                            ps.setInt(1, finalActiveIdPasien);
                            ps.setInt(2, finalActiveIdDokter);
                            ps.setString(3, keluhan);
                            ps.setString(4, diagnosa);
                            ps.executeUpdate();
                        }
                    } else {
                        
                        String sqlK = "UPDATE kunjungan SET keluhan=?, diagnosa=?, status='selesai' WHERE id=?";
                        try (PreparedStatement ps = connection.prepareStatement(sqlK)) {
                            ps.setString(1, keluhan);
                            ps.setString(2, diagnosa);
                            ps.setInt(3, finalIdKunjunganAktif);
                            ps.executeUpdate();
                        }
                    }

                    
                    String sqlA =
                        "UPDATE antrian SET status='Selesai' " +
                        "WHERE id_pasien=? AND id_dokter=? AND DATE(tanggal)=CURRENT_DATE " +
                        "AND status IN ('Dipanggil','Diperiksa')";
                    try (PreparedStatement ps = connection.prepareStatement(sqlA)) {
                        ps.setInt(1, finalActiveIdPasien);
                        ps.setInt(2, finalActiveIdDokter);
                        ps.executeUpdate();
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
                        JOptionPane.showMessageDialog(view,
                            "Kunjungan selesai! Tagihan bisa dibuat oleh resepsionis.",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        
                        activeIdPasien   = -1;
                        activeIdDokter   = -1;
                        activeNoRM       = "";
                        namaPasienAktif  = "";
                        idKunjunganAktif = -1;
                        
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setSelesaikanEnabled(false);
                        loadData();

                        if (onSelesaiListener != null) {
                            onSelesaiListener.run();
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    
    private String getSortSql(String sortOption) {
        String filter = "";
        String order  = " GROUP BY k.id ORDER BY k.tanggal_kunjungan DESC, k.id DESC";
        if ("Paling Lama".equals(sortOption)) {
            order = " GROUP BY k.id ORDER BY k.tanggal_kunjungan ASC, k.id ASC";
        } else if ("24 Jam Terakhir".equals(sortOption)) {
            filter = " AND k.tanggal_kunjungan >= NOW() - INTERVAL 1 DAY";
        } else if ("1 Bulan Terakhir".equals(sortOption)) {
            filter = " AND k.tanggal_kunjungan >= NOW() - INTERVAL 30 DAY";
        }
        return filter + order;
    }

    private int resolveDokterId() {
        if (!SessionManager.isLoggedIn()) return -1;
        int idUser = SessionManager.getUser().getId();
        String sql = "SELECT id FROM dokter WHERE id_user = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void loadData() {
        view.setStatusText("Memuat riwayat kunjungan...");
        checkActiveKunjungan();

        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> rows = new ArrayList<>();
                if (connection == null) return rows;

                boolean isDokter = SessionManager.hasRole("dokter");
                int docId = isDokter ? resolveDokterId() : -1;

                String base =
                    "SELECT k.id, p.nama AS nama_pasien, d.nama AS nama_dokter, " +
                    "k.tanggal_kunjungan, k.keluhan, k.diagnosa, " +
                    "GROUP_CONCAT(o.nama SEPARATOR ', ') AS daftar_obat, k.status " +
                    "FROM kunjungan k " +
                    "JOIN pasien p ON k.id_pasien = p.id " +
                    "JOIN dokter  d ON k.id_dokter  = d.id " +
                    "LEFT JOIN resep r ON k.id = r.id_kunjungan " +
                    "LEFT JOIN detail_resep dr ON r.id = dr.id_resep " +
                    "LEFT JOIN obat o ON dr.id_obat = o.id ";

                String where = isDokter ? "WHERE k.id_dokter = ? " : "WHERE 1=1 ";
                String sql = base + where + getSortSql(view.getSortOption());

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    if (isDokter) ps.setInt(1, docId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getInt("id"),
                                rs.getString("nama_pasien"),
                                rs.getString("nama_dokter"),
                                rs.getTimestamp("tanggal_kunjungan") != null
                                    ? rs.getTimestamp("tanggal_kunjungan").toString() : "",
                                rs.getString("keluhan"),
                                rs.getString("diagnosa"),
                                rs.getString("daftar_obat") != null ? rs.getString("daftar_obat") : "-",
                                rs.getString("status")
                            });
                        }
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> rows = get();
                    DefaultTableModel m = view.getTableModel();
                    m.setRowCount(0);
                    for (Object[] row : rows) m.addRow(row);
                    view.setStatusText("Riwayat kunjungan berhasil dimuat (" + rows.size() + " catatan)");
                } catch (Exception e) {
                    view.setStatusText("Gagal memuat data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleCari() {
        String keyword = view.getCariInput();
        if (keyword.isEmpty()) { loadData(); return; }

        view.setStatusText("Mencari '" + keyword + "'...");
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> rows = new ArrayList<>();
                if (connection == null) return rows;

                boolean isDokter = SessionManager.hasRole("dokter");
                int docId = isDokter ? resolveDokterId() : -1;

                String base =
                    "SELECT k.id, p.nama AS nama_pasien, d.nama AS nama_dokter, " +
                    "k.tanggal_kunjungan, k.keluhan, k.diagnosa, " +
                    "GROUP_CONCAT(o.nama SEPARATOR ', ') AS daftar_obat, k.status " +
                    "FROM kunjungan k " +
                    "JOIN pasien p ON k.id_pasien = p.id " +
                    "JOIN dokter  d ON k.id_dokter  = d.id " +
                    "LEFT JOIN resep r ON k.id = r.id_kunjungan " +
                    "LEFT JOIN detail_resep dr ON r.id = dr.id_resep " +
                    "LEFT JOIN obat o ON dr.id_obat = o.id ";

                String where = isDokter
                    ? "WHERE k.id_dokter = ? AND (p.nama LIKE ? OR k.diagnosa LIKE ?) "
                    : "WHERE (p.nama LIKE ? OR k.diagnosa LIKE ?) ";

                String sql = base + where + getSortSql(view.getSortOption());

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    String kw = "%" + keyword + "%";
                    if (isDokter) { ps.setInt(1, docId); ps.setString(2, kw); ps.setString(3, kw); }
                    else          { ps.setString(1, kw); ps.setString(2, kw); }
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getInt("id"),
                                rs.getString("nama_pasien"),
                                rs.getString("nama_dokter"),
                                rs.getTimestamp("tanggal_kunjungan") != null
                                    ? rs.getTimestamp("tanggal_kunjungan").toString() : "",
                                rs.getString("keluhan"),
                                rs.getString("diagnosa"),
                                rs.getString("daftar_obat") != null ? rs.getString("daftar_obat") : "-",
                                rs.getString("status")
                            });
                        }
                    }
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> rows = get();
                    DefaultTableModel m = view.getTableModel();
                    m.setRowCount(0);
                    for (Object[] row : rows) m.addRow(row);
                    view.setStatusText("Ditemukan " + rows.size() + " catatan");
                } catch (Exception e) {
                    view.setStatusText("Gagal mencari: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void handleTableClick() {
        int row = view.getTable().getSelectedRow();
        if (row != -1) {
            String pasien = view.getTable().getModel().getValueAt(row, 1).toString();
            String dokter = view.getTable().getModel().getValueAt(row, 2).toString();
            String waktu  = view.getTable().getModel().getValueAt(row, 3).toString();
            String status = view.getTable().getModel().getValueAt(row, 7).toString();
            view.setStatusText("Dipilih: " + pasien + " | " + dokter + " | " + waktu + " | Status: " + status);
        }
    }

    public void checkActiveKunjungan() {
        if (!SessionManager.hasRole("dokter")) return;
        
        SwingWorker<ActiveKunjunganInfo, Void> worker = new SwingWorker<>() {
            @Override
            protected ActiveKunjunganInfo doInBackground() throws Exception {
                int docId = resolveDokterId();
                if (docId == -1) return null;
                
                String sqlAntrian = 
                    "SELECT a.id_pasien, p.nama AS nama_pasien, p.no_rm, d.nama AS nama_dokter " +
                    "FROM antrian a " +
                    "JOIN pasien p ON a.id_pasien = p.id " +
                    "JOIN dokter d ON a.id_dokter = d.id " +
                    "WHERE a.id_dokter = ? AND a.status IN ('Dipanggil', 'Diperiksa') " +
                    "AND a.tanggal = CURRENT_DATE LIMIT 1";
                
                try (PreparedStatement ps = connection.prepareStatement(sqlAntrian)) {
                    ps.setInt(1, docId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int idPasien = rs.getInt("id_pasien");
                            String namaPasien = rs.getString("nama_pasien");
                            String noRM = rs.getString("no_rm");
                            String namaDokter = rs.getString("nama_dokter");
                            
                            String sqlKunjungan = 
                                "SELECT id, keluhan, diagnosa " +
                                "FROM kunjungan " +
                                "WHERE id_pasien = ? AND id_dokter = ? AND status = 'sedang_diperiksa' " +
                                "AND DATE(tanggal_kunjungan) = CURRENT_DATE LIMIT 1";
                            
                            try (PreparedStatement psK = connection.prepareStatement(sqlKunjungan)) {
                                psK.setInt(1, idPasien);
                                psK.setInt(2, docId);
                                try (ResultSet rsK = psK.executeQuery()) {
                                    if (rsK.next()) {
                                        return new ActiveKunjunganInfo(
                                            rsK.getInt("id"), idPasien, docId, namaPasien, noRM, namaDokter,
                                            rsK.getString("keluhan"), rsK.getString("diagnosa")
                                        );
                                    } else {
                                        return new ActiveKunjunganInfo(
                                            -1, idPasien, docId, namaPasien, noRM, namaDokter,
                                            "", ""
                                        );
                                    }
                                }
                            }
                        }
                    }
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    ActiveKunjunganInfo info = get();
                    if (info != null) {
                        boolean isSamePatient = (activeIdPasien == info.idPasien && activeIdDokter == info.idDokter);
                        
                        activeIdPasien = info.idPasien;
                        activeIdDokter = info.idDokter;
                        activeNoRM = info.noRM;
                        namaPasienAktif = info.namaPasien;
                        idKunjunganAktif = info.idKunjungan;
                        
                        if (!isSamePatient) {
                            view.setFormAutoFilled(info.namaPasien, info.noRM, info.namaDokter);
                            view.setFormEnabled(true);
                            view.setKeluhanAndDiagnosa(info.keluhan, info.diagnosa);
                        } else {
                            view.setFormEnabled(true);
                        }
                        
                        boolean ready = !view.getKeluhanInput().isEmpty() && !view.getDiagnosaInput().isEmpty();
                        view.setSelesaikanEnabled(ready);
                    } else {
                        activeIdPasien = -1;
                        activeIdDokter = -1;
                        activeNoRM = "";
                        namaPasienAktif = "";
                        idKunjunganAktif = -1;
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setSelesaikanEnabled(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private static class ActiveKunjunganInfo {
        int idKunjungan;
        int idPasien;
        int idDokter;
        String namaPasien;
        String noRM;
        String namaDokter;
        String keluhan;
        String diagnosa;
        
        ActiveKunjunganInfo(int idKunjungan, int idPasien, int idDokter, String namaPasien, String noRM, String namaDokter, String keluhan, String diagnosa) {
            this.idKunjungan = idKunjungan;
            this.idPasien = idPasien;
            this.idDokter = idDokter;
            this.namaPasien = namaPasien;
            this.noRM = noRM;
            this.namaDokter = namaDokter;
            this.keluhan = keluhan;
            this.diagnosa = diagnosa;
        }
    }
}
