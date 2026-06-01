package controller;

import database.DBConnection;
import view.KunjunganView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KunjunganController {
    private final KunjunganView view;
    private final Connection connection;

    public KunjunganController(KunjunganView view) {
        this.view       = view;
        this.connection = DBConnection.getInstance().getConnection();

        initListeners();
        loadData();
    }

    private void initListeners() {
        view.addCariListener(e -> handleCari());
        view.addSortListener(e -> loadData());
        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    private String getSortSql(String sortOption) {
        String filter = "";
        String order  = " ORDER BY k.tanggal_kunjungan DESC, k.id DESC";
        if ("Paling Lama".equals(sortOption)) {
            order = " ORDER BY k.tanggal_kunjungan ASC, k.id ASC";
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

        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> rows = new ArrayList<>();
                if (connection == null) return rows;

                boolean isDokter = SessionManager.hasRole("dokter");
                int docId = isDokter ? resolveDokterId() : -1;

                String base =
                    "SELECT k.id, p.nama AS nama_pasien, d.nama AS nama_dokter, " +
                    "k.tanggal_kunjungan, k.keluhan, k.diagnosa, k.status " +
                    "FROM kunjungan k " +
                    "JOIN pasien p ON k.id_pasien = p.id " +
                    "JOIN dokter  d ON k.id_dokter  = d.id ";

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
                    "k.tanggal_kunjungan, k.keluhan, k.diagnosa, k.status " +
                    "FROM kunjungan k " +
                    "JOIN pasien p ON k.id_pasien = p.id " +
                    "JOIN dokter  d ON k.id_dokter  = d.id ";

                String where = isDokter
                    ? "WHERE k.id_dokter = ? AND (p.nama LIKE ? OR k.diagnosa LIKE ?) "
                    : "WHERE (p.nama LIKE ? OR k.diagnosa LIKE ?) ";

                String sql = base + where + getSortSql(view.getSortOption());

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    String kw = "%" + keyword + "%";
                    if (isDokter) {
                        ps.setInt(1, docId);
                        ps.setString(2, kw);
                        ps.setString(3, kw);
                    } else {
                        ps.setString(1, kw);
                        ps.setString(2, kw);
                    }
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
            String pasien  = view.getTable().getModel().getValueAt(row, 1).toString();
            String dokter  = view.getTable().getModel().getValueAt(row, 2).toString();
            String waktu   = view.getTable().getModel().getValueAt(row, 3).toString();
            String status  = view.getTable().getModel().getValueAt(row, 6).toString();
            view.setStatusText("Dipilih: " + pasien + " | Dr. " + dokter + " | " + waktu + " | Status: " + status);
        }
    }
}
