package controller;

import database.DBConnection;
import model.Dokter;
import model.DokterSpesialis;
import model.DokterUmum;
import view.DokterView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DokterController {
    private final DokterView view;
    private final Connection connection;

    public DokterController(DokterView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initController();
        loadData();
        
        if (SessionManager.isLoggedIn() && SessionManager.hasRole("resepsionis")) {
            view.setReadOnlyMode();
        }
    }

    private void initController() {
        view.getBtnRefresh().addActionListener(e -> loadData());
        view.getBtnTambah().addActionListener(e -> tambahData());
        view.getBtnUbah().addActionListener(e -> ubahData());
        view.getBtnHapus().addActionListener(e -> hapusData());
        view.addSearchListener(e -> searchData(view.getTxtSearch().getText().trim()));
        
        // Pindaan teks otomatis saat baris tabel dipilih
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = view.getTable().getSelectedRow();
                if (row != -1) {
                    view.getTxtId().setText(view.getTable().getModel().getValueAt(row, 0).toString());
                    view.getTxtNama().setText(view.getTable().getModel().getValueAt(row, 1).toString());
                    view.getTxtSpesialisasi().setText(view.getTable().getModel().getValueAt(row, 2).toString());
                }
            }
        });
    }

    private void loadData() {
        SwingWorker<List<Dokter>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Dokter> doInBackground() throws Exception {
                List<Dokter> list = new ArrayList<>();
                if (connection == null) return list;
                
                String sql = "SELECT id, nama, spesialisasi FROM dokter WHERE is_active = 1 ORDER BY id DESC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String nama = rs.getString("nama");
                        String spesialisasi = rs.getString("spesialisasi");
                        
                        // Polimorfisme instansiasi objek turunan Dokter
                        Dokter d;
                        if ("Umum".equalsIgnoreCase(spesialisasi)) {
                            d = new DokterUmum(id, nama);
                        } else {
                            d = new DokterSpesialis(id, nama, spesialisasi);
                        }
                        list.add(d);
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<Dokter> list = get();
                    DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
                    model.setRowCount(0);
                    for (Dokter d : list) {
                        model.addRow(new Object[]{
                            d.getId(),
                            d.getNama(),
                            d.getSpesialisasi()
                        });
                    }
                    // Reset form fields
                    view.getTxtId().setText("");
                    view.getTxtNama().setText("");
                    view.getTxtSpesialisasi().setText("");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void tambahData() {
        String nama = view.getTxtNama().getText().trim();
        String spesialisasi = view.getTxtSpesialisasi().getText().trim();

        if (nama.isEmpty() || spesialisasi.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nama dan Spesialisasi wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                String sql = "INSERT INTO dokter (nama, spesialisasi) VALUES (?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, nama);
                    pstmt.setString(2, spesialisasi);
                    pstmt.executeUpdate();
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Data Dokter berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal menambahkan data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void ubahData() {
        String idStr = view.getTxtId().getText().trim();
        String nama = view.getTxtNama().getText().trim();
        String spesialisasi = view.getTxtSpesialisasi().getText().trim();

        if (idStr.isEmpty() || nama.isEmpty() || spesialisasi.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pilih data dokter yang ingin diubah terlebih dahulu!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                String sql = "UPDATE dokter SET nama = ?, spesialisasi = ? WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, nama);
                    pstmt.setString(2, spesialisasi);
                    pstmt.setInt(3, id);
                    pstmt.executeUpdate();
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Data Dokter berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal mengubah data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void searchData(String keyword) {
        SwingWorker<List<Dokter>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Dokter> doInBackground() throws Exception {
                List<Dokter> list = new ArrayList<>();
                if (connection == null) return list;
                String sql = "SELECT id, nama, spesialisasi FROM dokter WHERE is_active=1 " +
                             "AND (nama LIKE ? OR spesialisasi LIKE ?) ORDER BY id DESC";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    String kw = "%" + keyword + "%";
                    ps.setString(1, kw);
                    ps.setString(2, kw);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int id = rs.getInt("id");
                            String nama = rs.getString("nama");
                            String spesialisasi = rs.getString("spesialisasi");
                            Dokter d;
                            if ("Umum".equalsIgnoreCase(spesialisasi)) {
                                d = new DokterUmum(id, nama);
                            } else {
                                d = new DokterSpesialis(id, nama, spesialisasi);
                            }
                            list.add(d);
                        }
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<Dokter> list = get();
                    DefaultTableModel model = (DefaultTableModel) view.getTable().getModel();
                    model.setRowCount(0);
                    for (Dokter d : list) {
                        model.addRow(new Object[]{d.getId(), d.getNama(), d.getSpesialisasi()});
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error searching: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void hapusData() {
        String idStr = view.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pilih data dokter yang ingin dihapus terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);

        int confirm = JOptionPane.showConfirmDialog(view, "Apakah Anda yakin ingin menghapus dokter ini?\nTindakan ini dapat memengaruhi riwayat rekam medis terkait.", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                String sql = "UPDATE dokter SET is_active = 0 WHERE id = ?";
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
                        JOptionPane.showMessageDialog(view, "Data Dokter berhasil dinonaktifkan (Soft Delete)!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
