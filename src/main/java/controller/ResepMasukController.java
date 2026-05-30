package controller;

import database.DBConnection;
import view.ResepMasukView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResepMasukController {

    private final ResepMasukView view;
    private final Connection connection;

    // Inner class untuk menyimpan data resep dari DB
    private static class ResepItem {
        final int id;
        final String noResep;
        final String namaPasien;
        final String namaDokter;
        final String tanggal;
        final boolean sudahDisiapkan;

        ResepItem(int id, String noResep, String namaPasien, String namaDokter, String tanggal, boolean sudahDisiapkan) {
            this.id = id;
            this.noResep = noResep;
            this.namaPasien = namaPasien;
            this.namaDokter = namaDokter;
            this.tanggal = tanggal;
            this.sudahDisiapkan = sudahDisiapkan;
        }

        @Override
        public String toString() {
            String jam = tanggal.length() >= 16 ? tanggal.substring(11, 16) : "-";
            return "#" + id + " - " + namaPasien + " - " + jam;
        }
    }

    // Daftar item yang ditampilkan di JList, agar bisa ambil id saat klik
    private List<ResepItem> cachedResepList = new ArrayList<>();

    public ResepMasukController(ResepMasukView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();

        initListeners();
        loadDaftarResep();
    }

    private void initListeners() {
        view.addBtnRefreshListener(e -> loadDaftarResep());
        view.addBtnKonfirmasiListener(e -> handleKonfirmasi());

        view.addListResepSelectListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = view.getSelectedResepIndex();
                if (idx >= 0 && idx < cachedResepList.size()) {
                    loadDetailResep(cachedResepList.get(idx));
                }
            }
        });
    }

    // =============================================
    // LOAD: Daftar resep dengan status 'belum_disiapkan'
    // =============================================
    public void loadDaftarResep() {
        SwingWorker<List<ResepItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ResepItem> doInBackground() throws Exception {
                List<ResepItem> list = new ArrayList<>();
                if (connection == null) return list;

                String sql =
                    "SELECT r.id, p.nama AS nama_pasien, d.nama AS nama_dokter, " +
                    "       k.tanggal_kunjungan, r.status " +
                    "FROM resep r " +
                    "JOIN kunjungan k ON r.id_kunjungan = k.id " +
                    "JOIN pasien   p ON k.id_pasien  = p.id " +
                    "JOIN dokter   d ON r.id_dokter  = d.id " +
                    "WHERE r.status = 'belum_disiapkan' " +
                    "ORDER BY k.tanggal_kunjungan ASC";

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int    id            = rs.getInt("id");
                        String noResep       = "RES-" + String.format("%04d", id);
                        String namaPasien    = rs.getString("nama_pasien");
                        String namaDokter    = rs.getString("nama_dokter");
                        String tanggal       = rs.getString("tanggal_kunjungan");
                        boolean sudahDisiapkan = false; // filter: belum_disiapkan
                        list.add(new ResepItem(id, noResep, namaPasien, namaDokter, tanggal, sudahDisiapkan));
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    cachedResepList = get();
                    view.setDaftarResep(cachedResepList);
                    view.clearDetail();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view,
                        "Gagal memuat daftar resep: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // =============================================
    // LOAD: Detail obat dari satu resep terpilih
    // =============================================
    private void loadDetailResep(ResepItem item) {
        view.setDetail(
            item.noResep,
            item.namaPasien,
            item.namaDokter,
            item.tanggal,
            item.sudahDisiapkan
        );

        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> rows = new ArrayList<>();
                if (connection == null) return rows;

                // Join ke tabel obat untuk dapat nama & satuan
                String sql =
                    "SELECT o.nama, dr.jumlah, o.satuan, dr.dosis " +
                    "FROM detail_resep dr " +
                    "JOIN obat o ON dr.id_obat = o.id " +
                    "WHERE dr.id_resep = ?";

                try (PreparedStatement pst = connection.prepareStatement(sql)) {
                    pst.setInt(1, item.id);
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            rows.add(new Object[]{
                                rs.getString("nama"),
                                rs.getInt("jumlah"),
                                rs.getString("satuan"),
                                rs.getString("dosis")
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
                    DefaultTableModel model = view.getTabelDetailModel();
                    model.setRowCount(0);
                    for (Object[] row : rows) {
                        model.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view,
                        "Gagal memuat detail resep: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // =============================================
    // AKSI: Konfirmasi resep -> ubah status ke 'sudah_disiapkan'
    // =============================================
    private void handleKonfirmasi() {
        int idx = view.getSelectedResepIndex();
        if (idx < 0 || idx >= cachedResepList.size()) {
            JOptionPane.showMessageDialog(view,
                "Pilih resep yang ingin dikonfirmasi terlebih dahulu!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ResepItem item = cachedResepList.get(idx);

        int confirm = JOptionPane.showConfirmDialog(view,
            "Konfirmasi bahwa obat untuk resep " + item.noResep +
            " (" + item.namaPasien + ") sudah disiapkan?",
            "Konfirmasi Penyiapan Obat", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;

                connection.setAutoCommit(false);
                try {
                    // 1. UPDATE status resep
                    String sqlUpdateResep = "UPDATE resep SET status = 'sudah_disiapkan' WHERE id = ?";
                    try (PreparedStatement pst = connection.prepareStatement(sqlUpdateResep)) {
                        pst.setInt(1, item.id);
                        int updated = pst.executeUpdate();
                        if (updated == 0) {
                            connection.rollback();
                            return false;
                        }
                    }

                    // 2. Ambil detail resep untuk kurangi stok
                    String sqlSelectDetail = "SELECT id_obat, jumlah FROM detail_resep WHERE id_resep = ?";
                    List<int[]> obatList = new ArrayList<>();
                    try (PreparedStatement pst = connection.prepareStatement(sqlSelectDetail)) {
                        pst.setInt(1, item.id);
                        try (ResultSet rs = pst.executeQuery()) {
                            while (rs.next()) {
                                obatList.add(new int[]{rs.getInt("id_obat"), rs.getInt("jumlah")});
                            }
                        }
                    }

                    // 3. UPDATE stok obat
                    String sqlUpdateStok = "UPDATE obat SET stok = stok - ? WHERE id = ?";
                    try (PreparedStatement pst = connection.prepareStatement(sqlUpdateStok)) {
                        for (int[] obatItem : obatList) {
                            pst.setInt(1, obatItem[1]); // jumlah
                            pst.setInt(2, obatItem[0]); // id_obat
                            pst.addBatch();
                        }
                        pst.executeBatch();
                    }

                    connection.commit();
                    return true;
                } catch (SQLException e) {
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
                            "Resep " + item.noResep + " berhasil dikonfirmasi!",
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);

                        // Update tampilan detail jadi 'Sudah Disiapkan' (hijau)
                        view.setStatusResepLabel("Sudah Disiapkan", true);

                        // Refresh daftar (hilangkan dari pending list)
                        loadDaftarResep();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view,
                        "Gagal mengkonfirmasi resep: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
