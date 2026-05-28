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

        int kunjunganId;
        try {
            kunjunganId = Integer.parseInt(kunjunganIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Kunjungan ID harus berupa angka!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Exception> worker = new SwingWorker<>() {
            private String pesanKalkulasi = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) throw new Exception("Tidak ada koneksi database");

                Timestamp tanggal;
                try {
                    String formattedTanggalStr = tanggalStr;
                    if (formattedTanggalStr.length() == 10) {
                        formattedTanggalStr += " 00:00:00";
                    }
                    tanggal = Timestamp.valueOf(formattedTanggalStr);
                } catch (IllegalArgumentException ex) {
                    throw new Exception("Format Waktu salah! Gunakan YYYY-MM-DD");
                }

                double totalAwal = 0;

                if (totalBiayaStr.isEmpty()) {
                    double tarifDokter = 0;
                    double totalObat = 0;
                    String docNama = "";
                    String docSpec = "";
                    String role = "";

                    // Query Dokter
                    String sqlDokter = "SELECT d.id, d.nama, d.spesialisasi FROM kunjungan k JOIN dokter d ON k.id_dokter = d.id WHERE k.id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDokter)) {
                        pstmt.setInt(1, kunjunganId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                int docId = rs.getInt("id");
                                docNama = rs.getString("nama");
                                docSpec = rs.getString("spesialisasi");
                                Dokter dokter;
                                if (docSpec.equalsIgnoreCase("Umum")) {
                                    dokter = new DokterUmum(docId, docNama);
                                } else {
                                    dokter = new DokterSpesialis(docId, docNama, docSpec);
                                }
                                tarifDokter = dokter.hitungTarifKonsultasi();
                                role = dokter.getRole();
                            } else {
                                throw new Exception("Kunjungan ID tidak valid atau Dokter tidak ditemukan.");
                            }
                        }
                    }

                    // Query Obat dari Resep
                    String sqlObat = "SELECT dr.jumlah, o.harga FROM detail_resep dr " +
                                     "JOIN resep r ON dr.id_resep = r.id " +
                                     "JOIN obat o ON dr.id_obat = o.id " +
                                     "WHERE r.id_kunjungan = ?";
                    try (PreparedStatement pstmtObat = connection.prepareStatement(sqlObat)) {
                        pstmtObat.setInt(1, kunjunganId);
                        try (ResultSet rsObat = pstmtObat.executeQuery()) {
                            while (rsObat.next()) {
                                totalObat += (rsObat.getInt("jumlah") * rsObat.getDouble("harga"));
                            }
                        }
                    }

                    totalAwal = tarifDokter + totalObat;
                    pesanKalkulasi = String.format("Rincian Otomatis:\nTarif %s %s: Rp %,.2f\nBiaya Obat: Rp %,.2f\nTotal Awal: Rp %,.2f", 
                                                    role, docSpec, tarifDokter, totalObat, totalAwal);
                } else {
                    try {
                        totalAwal = Double.parseDouble(totalBiayaStr);
                    } catch (NumberFormatException ex) {
                        throw new Exception("Total Biaya harus berupa angka desimal!");
                    }
                }

                Pembayaran pembayaran;
                if (jenisPembayaran.equalsIgnoreCase("Tunai")) {
                    pembayaran = new PembayaranTunai(totalAwal, totalAwal * 0.05); 
                } else if (jenisPembayaran.equalsIgnoreCase("BPJS")) {
                    pembayaran = new PembayaranBPJS(totalAwal, 120000.0); 
                } else if (jenisPembayaran.equalsIgnoreCase("Asuransi")) {
                    pembayaran = new PembayaranAsuransi(totalAwal, 80.0); 
                } else {
                    throw new Exception("Jenis pembayaran tidak dikenali! Gunakan: Tunai, BPJS, atau Asuransi.");
                }

                double totalBiayaAkhir = pembayaran.hitungTotal();
                if (!pesanKalkulasi.isEmpty()) {
                    pesanKalkulasi += String.format("\n\nMetode %s diterapkan.\nTotal Akhir (Setelah Potongan/Subsidi): Rp %,.2f", pembayaran.getJenisPembayaran(), totalBiayaAkhir);
                }

                
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
                        if (!pesanKalkulasi.isEmpty()) {
                            JOptionPane.showMessageDialog(view, pesanKalkulasi, "Kalkulasi Tagihan", JOptionPane.INFORMATION_MESSAGE);
                        }
                        JOptionPane.showMessageDialog(view, "Data Tagihan berhasil ditambahkan!");
                        clearForm();
                        loadData();
                    }
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (e.getCause() != null) msg = e.getCause().getMessage();
                    JOptionPane.showMessageDialog(view, msg, "Error Validasi/Sistem", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void ubahData() {
        String idStr = view.getTxtId().getText().trim();
        String kunjunganIdStr = view.getTxtKunjunganId().getText().trim();
        String totalBiayaStr = view.getTxtTotalBiaya().getText().trim();
        String tanggalStr = view.getTxtTanggal().getText().trim();
        String jenisPembayaran = view.getTxtJenisPembayaran().getText().trim();

        if (idStr.isEmpty() || kunjunganIdStr.isEmpty() || tanggalStr.isEmpty() || jenisPembayaran.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Pilih data dari tabel dan pastikan form utama terisi!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int tagihanId, kunjunganId;
        try {
            tagihanId = Integer.parseInt(idStr);
            kunjunganId = Integer.parseInt(kunjunganIdStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "ID Tagihan dan Kunjungan ID harus berupa angka!", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Exception> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) throw new Exception("Tidak ada koneksi database");

                Timestamp tanggal;
                try {
                    String formattedTanggalStr = tanggalStr;
                    if (formattedTanggalStr.length() == 10) {
                        formattedTanggalStr += " 00:00:00";
                    }
                    tanggal = Timestamp.valueOf(formattedTanggalStr);
                } catch (IllegalArgumentException ex) {
                    throw new Exception("Format Waktu salah! Gunakan YYYY-MM-DD");
                }

                double totalAwal = 0;

                if (totalBiayaStr.isEmpty()) {
                    double tarifDokter = 0;
                    double totalObat = 0;

                    String sqlDokter = "SELECT d.id, d.nama, d.spesialisasi FROM kunjungan k JOIN dokter d ON k.id_dokter = d.id WHERE k.id = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sqlDokter)) {
                        pstmt.setInt(1, kunjunganId);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                int docId = rs.getInt("id");
                                String docSpec = rs.getString("spesialisasi");
                                Dokter dokter = docSpec.equalsIgnoreCase("Umum") ? 
                                                new DokterUmum(docId, rs.getString("nama")) : 
                                                new DokterSpesialis(docId, rs.getString("nama"), docSpec);
                                tarifDokter = dokter.hitungTarifKonsultasi();
                            } else {
                                throw new Exception("Kunjungan ID tidak valid.");
                            }
                        }
                    }

                    String sqlObat = "SELECT dr.jumlah, o.harga FROM detail_resep dr JOIN resep r ON dr.id_resep = r.id JOIN obat o ON dr.id_obat = o.id WHERE r.id_kunjungan = ?";
                    try (PreparedStatement pstmtObat = connection.prepareStatement(sqlObat)) {
                        pstmtObat.setInt(1, kunjunganId);
                        try (ResultSet rsObat = pstmtObat.executeQuery()) {
                            while (rsObat.next()) {
                                totalObat += (rsObat.getInt("jumlah") * rsObat.getDouble("harga"));
                            }
                        }
                    }
                    totalAwal = tarifDokter + totalObat;
                } else {
                    try {
                        totalAwal = Double.parseDouble(totalBiayaStr);
                    } catch (NumberFormatException ex) {
                        throw new Exception("Total Biaya harus berupa angka desimal!");
                    }
                }

                Pembayaran pembayaran;
                if (jenisPembayaran.equalsIgnoreCase("Tunai")) {
                    pembayaran = new PembayaranTunai(totalAwal, totalAwal * 0.05);
                } else if (jenisPembayaran.equalsIgnoreCase("BPJS")) {
                    pembayaran = new PembayaranBPJS(totalAwal, 120000.0);
                } else if (jenisPembayaran.equalsIgnoreCase("Asuransi")) {
                    pembayaran = new PembayaranAsuransi(totalAwal, 80.0);
                } else {
                    throw new Exception("Jenis pembayaran tidak dikenali! Gunakan: Tunai, BPJS, atau Asuransi.");
                }

                double totalBiayaAkhir = pembayaran.hitungTotal();

                String sql = "UPDATE tagihan SET id_kunjungan=?, total_biaya=?, tanggal_pembayaran=?, jenis_pembayaran=? WHERE id=?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, kunjunganId);
                    pstmt.setDouble(2, totalBiayaAkhir);
                    pstmt.setTimestamp(3, tanggal);
                    pstmt.setString(4, pembayaran.getJenisPembayaran());
                    pstmt.setInt(5, tagihanId);
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
                    String msg = e.getMessage();
                    if (e.getCause() != null) msg = e.getCause().getMessage();
                    JOptionPane.showMessageDialog(view, msg, "Error Validasi/Sistem", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "ID Tagihan tidak valid!");
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