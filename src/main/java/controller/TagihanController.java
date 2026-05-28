package controller;

import database.DBConnection;
import model.*;
import view.TagihanView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagihanController {
    private final TagihanView view;
    private final Connection connection;

    public TagihanController(TagihanView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnRefresh().addActionListener(e -> loadData());
        view.getBtnTambah().addActionListener(e -> tambahData());
        view.getBtnUbah().addActionListener(e -> ubahData());
        view.getBtnHapus().addActionListener(e -> hapusData());
        
        view.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }

    private void loadData() {
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> dataList = new ArrayList<>();
                if (connection == null) return dataList;

                String sql = "SELECT id, id_kunjungan, total_biaya, tanggal_pembayaran, jenis_pembayaran, status_pembayaran FROM tagihan ORDER BY id DESC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        dataList.add(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("id_kunjungan"),
                            rs.getDouble("total_biaya"),
                            rs.getTimestamp("tanggal_pembayaran") != null ? rs.getTimestamp("tanggal_pembayaran").toString() : "",
                            rs.getString("jenis_pembayaran"),
                            rs.getString("status_pembayaran")
                        });
                    }
                }
                return dataList;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> dataList = get();
                    String[] columnNames = {"ID", "Kunjungan ID", "Total Biaya", "Tanggal", "Jenis Pembayaran", "Status"};
                    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    for (Object[] row : dataList) {
                        model.addRow(row);
                    }
                    view.getTable().setModel(model);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal memuat data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void tambahData() {
        String kunjunganIdStr = view.getTxtKunjunganId().getText().trim();
        String totalBiayaStr = view.getTxtTotalBiaya().getText().trim();
        String tanggalStr = view.getTxtTanggal().getText().trim();
        String jenisPembayaran = view.getTxtJenisPembayaran().getText().trim();

        if (kunjunganIdStr.isEmpty() || tanggalStr.isEmpty() || jenisPembayaran.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Kunjungan ID, Tanggal, dan Jenis Pembayaran wajib diisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int kunjunganId = Integer.parseInt(kunjunganIdStr);
            double totalAwal = 0;
            if (totalBiayaStr.isEmpty()) {
                String sqlDokter = "SELECT d.id, d.nama, d.spesialisasi FROM kunjungan k JOIN dokter d ON k.id_dokter = d.id WHERE k.id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDokter)) {
                    pstmt.setInt(1, kunjunganId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int docId = rs.getInt("id");
                            String docNama = rs.getString("nama");
                            String docSpec = rs.getString("spesialisasi");
                            Dokter dokter;
                            if (docSpec.equalsIgnoreCase("Umum")) {
                                dokter = new DokterUmum(docId, docNama);
                            } else {
                                dokter = new DokterSpesialis(docId, docNama, docSpec);
                            }
                            totalAwal = dokter.hitungTarifKonsultasi();
                            JOptionPane.showMessageDialog(view, "Biaya Awal otomatis menggunakan tarif " + dokter.getRole() + " " + docSpec + " (" + docNama + "): Rp " + totalAwal);
                        } else {
                            JOptionPane.showMessageDialog(view, "Kunjungan ID tidak ditemukan atau tidak memiliki Dokter Pemeriksa!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            } else {
                totalAwal = Double.parseDouble(totalBiayaStr);
            }

            String formattedTanggalStr = tanggalStr;
            if (formattedTanggalStr.length() == 10) {
                formattedTanggalStr += " 00:00:00";
            }
            Timestamp tanggal = Timestamp.valueOf(formattedTanggalStr);

            // Implementasi Polimorfisme Pembayaran
            Pembayaran pembayaran;
            double diskon = totalAwal * 0.05; // 5% diskon
            double subsidi = 120000.0; // 120rb subsidi
            double coverPersen = 80.0; // 80% dicover

            if (jenisPembayaran.equalsIgnoreCase("Tunai")) {
                pembayaran = new PembayaranTunai(totalAwal, diskon);
                JOptionPane.showMessageDialog(view, "Pembayaran Tunai Terpilih.\nDiskon 5%: Rp " + diskon + "\nTotal Akhir: Rp " + pembayaran.hitungTotal());
            } else if (jenisPembayaran.equalsIgnoreCase("BPJS")) {
                pembayaran = new PembayaranBPJS(totalAwal, subsidi);
                JOptionPane.showMessageDialog(view, "Pembayaran BPJS Terpilih.\nSubsidi BPJS: Rp " + subsidi + "\nTotal Akhir: Rp " + pembayaran.hitungTotal());
            } else if (jenisPembayaran.equalsIgnoreCase("Asuransi")) {
                pembayaran = new PembayaranAsuransi(totalAwal, coverPersen);
                JOptionPane.showMessageDialog(view, "Pembayaran Asuransi Terpilih.\nDitanggung 80% oleh Asuransi.\nTotal Akhir: Rp " + pembayaran.hitungTotal());
            } else {
                JOptionPane.showMessageDialog(view, "Jenis pembayaran tidak valid! Gunakan: Tunai, BPJS, atau Asuransi.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double totalBiayaAkhir = pembayaran.hitungTotal();

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (connection == null) return false;

                    String sql = "INSERT INTO tagihan (id_kunjungan, total_biaya, tanggal_pembayaran, jenis_pembayaran, status_pembayaran) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, kunjunganId);
                        pstmt.setDouble(2, totalBiayaAkhir);
                        pstmt.setTimestamp(3, tanggal);
                        pstmt.setString(4, pembayaran.getJenisPembayaran());
                        pstmt.setString(5, "Lunas");
                        pstmt.executeUpdate();
                    }
                    return true;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(view, "Data Tagihan berhasil ditambahkan dengan kalkulasi polimorfis!");
                            clearForm();
                            loadData();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(view, "Gagal menambah data: " + e.getMessage());
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Kunjungan ID dan Total Biaya harus berupa angka!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(view, "Format Waktu salah! Gunakan YYYY-MM-DD HH:mm:ss", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void ubahData() {
        String idStr = view.getTxtId().getText().trim();
        String kunjunganIdStr = view.getTxtKunjunganId().getText().trim();
        String totalBiayaStr = view.getTxtTotalBiaya().getText().trim();
        String tanggalStr = view.getTxtTanggal().getText().trim();
        String jenisPembayaran = view.getTxtJenisPembayaran().getText().trim();

        if (idStr.isEmpty() || kunjunganIdStr.isEmpty() || tanggalStr.isEmpty() || jenisPembayaran.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pilih data dari tabel dan pastikan Kunjungan ID, Tanggal, dan Jenis Pembayaran terisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            int kunjunganId = Integer.parseInt(kunjunganIdStr);
            double totalAwal = 0;
            if (totalBiayaStr.isEmpty()) {
                String sqlDokter = "SELECT d.id, d.nama, d.spesialisasi FROM kunjungan k JOIN dokter d ON k.id_dokter = d.id WHERE k.id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sqlDokter)) {
                    pstmt.setInt(1, kunjunganId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int docId = rs.getInt("id");
                            String docNama = rs.getString("nama");
                            String docSpec = rs.getString("spesialisasi");
                            Dokter dokter;
                            if (docSpec.equalsIgnoreCase("Umum")) {
                                dokter = new DokterUmum(docId, docNama);
                            } else {
                                dokter = new DokterSpesialis(docId, docNama, docSpec);
                            }
                            totalAwal = dokter.hitungTarifKonsultasi();
                        } else {
                            JOptionPane.showMessageDialog(view, "Kunjungan ID tidak ditemukan atau tidak memiliki Dokter Pemeriksa!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }
            } else {
                totalAwal = Double.parseDouble(totalBiayaStr);
            }

            String formattedTanggalStr = tanggalStr;
            if (formattedTanggalStr.length() == 10) {
                formattedTanggalStr += " 00:00:00";
            }
            Timestamp tanggal = Timestamp.valueOf(formattedTanggalStr);

            // Re-kalkulasi polimorfis
            Pembayaran pembayaran;
            if (jenisPembayaran.equalsIgnoreCase("Tunai")) {
                pembayaran = new PembayaranTunai(totalAwal, totalAwal * 0.05);
            } else if (jenisPembayaran.equalsIgnoreCase("BPJS")) {
                pembayaran = new PembayaranBPJS(totalAwal, 120000.0);
            } else if (jenisPembayaran.equalsIgnoreCase("Asuransi")) {
                pembayaran = new PembayaranAsuransi(totalAwal, 80.0);
            } else {
                JOptionPane.showMessageDialog(view, "Jenis pembayaran tidak valid! Gunakan: Tunai, BPJS, atau Asuransi.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double totalBiayaAkhir = pembayaran.hitungTotal();

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (connection == null) return false;

                    String sql = "UPDATE tagihan SET id_kunjungan=?, total_biaya=?, tanggal_pembayaran=?, jenis_pembayaran=? WHERE id=?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setInt(1, kunjunganId);
                        pstmt.setDouble(2, totalBiayaAkhir);
                        pstmt.setTimestamp(3, tanggal);
                        pstmt.setString(4, pembayaran.getJenisPembayaran());
                        pstmt.setInt(5, id);
                        pstmt.executeUpdate();
                    }
                    return true;
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(view, "Data Tagihan berhasil diubah!");
                            clearForm();
                            loadData();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(view, "Gagal mengubah data: " + e.getMessage());
                    }
                }
            };
            worker.execute();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void hapusData() {
        String idStr = view.getTxtId().getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pilih data yang ingin dihapus dari tabel!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Hapus data tagihan ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (connection == null) return false;

                    String sql = "DELETE FROM tagihan WHERE id=?";
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
                            JOptionPane.showMessageDialog(view, "Data Tagihan berhasil dihapus!");
                            clearForm();
                            loadData();
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(view, "Gagal menghapus data: " + e.getMessage());
                    }
                }
            };
            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Error: " + e.getMessage());
        }
    }

    private void handleTableClick() {
        int row = view.getTable().getSelectedRow();
        if (row != -1) {
            view.getTxtId().setText(view.getTable().getValueAt(row, 0).toString());
            view.getTxtKunjunganId().setText(view.getTable().getValueAt(row, 1).toString());
            view.getTxtTotalBiaya().setText(view.getTable().getValueAt(row, 2).toString());
            view.getTxtTanggal().setText(view.getTable().getValueAt(row, 3).toString());
            view.getTxtJenisPembayaran().setText(view.getTable().getValueAt(row, 4).toString());
        }
    }

    private void clearForm() {
        view.getTxtId().setText("");
        view.getTxtKunjunganId().setText("");
        view.getTxtTotalBiaya().setText("");
        view.getTxtTanggal().setText("");
        view.getTxtJenisPembayaran().setText("");
    }
}
