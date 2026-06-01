package controller;

import database.DBConnection;
import model.*;
import view.ComboItem;
import view.TagihanView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagihanController {
    private final TagihanView view;
    private final Connection connection;

    private double currentTarifDokter = 0;
    private double currentTarifObat = 0;
    private double currentTotalAwal = 0;
    private double currentTotalAkhir = 0;

    public TagihanController(TagihanView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initController();
        loadKunjunganPending();
        loadRiwayatTagihan();
    }

    private void initController() {
        view.addKunjunganSelectListener(e -> hitungSubtotal());
        view.addJenisPembayaranSelectListener(e -> hitungDiskonPembayaran());
        view.addBuatTagihanListener(e -> hitungSubtotal());
        view.addProsesBayarListener(e -> prosesPembayaran());
        view.addBatalListener(e -> {
            view.getCbKunjungan().setSelectedIndex(-1);
            resetForm();
        });
    }

    private void loadKunjunganPending() {
        view.getCbKunjungan().removeAllItems();
        // Ambil kunjungan yang sudah selesai dan belum memiliki tagihan
        String sql = "SELECT k.id, p.nama AS nama_pasien, d.nama AS nama_dokter, k.tanggal_kunjungan " +
                     "FROM kunjungan k " +
                     "JOIN pasien p ON k.id_pasien = p.id " +
                     "JOIN dokter d ON k.id_dokter = d.id " +
                     "LEFT JOIN tagihan t ON k.id = t.id_kunjungan " +
                     "WHERE t.id IS NULL " +
                     "  AND k.status = 'selesai' " +
                     "ORDER BY k.tanggal_kunjungan DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String label = "KJ-" + rs.getInt("id") + " : " + rs.getString("nama_pasien");
                view.getCbKunjungan().addItem(new ComboItem(rs.getInt("id"), label));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hitungSubtotal() {
        ComboItem terpilih = (ComboItem) view.getCbKunjungan().getSelectedItem();
        if (terpilih == null) {
            resetForm();
            return;
        }

        int idKunjungan = terpilih.getId();
        currentTarifDokter = 0;
        currentTarifObat = 0;
        
        try {
            // 1. Dapatkan detail Pasien & Dokter
            String sqlKunjungan = "SELECT p.nama AS nama_pasien, d.id AS doc_id, d.nama AS doc_nama, d.spesialisasi, k.tanggal_kunjungan " +
                                  "FROM kunjungan k JOIN pasien p ON k.id_pasien = p.id JOIN dokter d ON k.id_dokter = d.id " +
                                  "WHERE k.id = ?";
            try (PreparedStatement pst = connection.prepareStatement(sqlKunjungan)) {
                pst.setInt(1, idKunjungan);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        view.setAutoFillData(rs.getString("nama_pasien"), rs.getString("doc_nama"), rs.getString("tanggal_kunjungan"));
                        
                        // Kalkulasi Polimorfisme Dokter
                        String spec = rs.getString("spesialisasi");
                        Dokter dokter = spec.equalsIgnoreCase("Umum") ? 
                                        new DokterUmum(rs.getInt("doc_id"), rs.getString("doc_nama")) : 
                                        new DokterSpesialis(rs.getInt("doc_id"), rs.getString("doc_nama"), spec);
                        currentTarifDokter = dokter.hitungTarifKonsultasi();
                    }
                }
            }

            // 2. Dapatkan total Obat
            String sqlObat = "SELECT SUM(dr.jumlah * o.harga) AS total_obat FROM detail_resep dr " +
                             "JOIN resep r ON dr.id_resep = r.id JOIN obat o ON dr.id_obat = o.id " +
                             "WHERE r.id_kunjungan = ?";
            try (PreparedStatement pst = connection.prepareStatement(sqlObat)) {
                pst.setInt(1, idKunjungan);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        currentTarifObat = rs.getDouble("total_obat");
                    }
                }
            }

            currentTotalAwal = currentTarifDokter + currentTarifObat;
            hitungDiskonPembayaran();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void hitungDiskonPembayaran() {
        if (currentTotalAwal == 0) return;

        String jenis = (String) view.getCbJenisPembayaran().getSelectedItem();
        Pembayaran bayar;

        // Aturan sesuai implementasi OOP model
        if (jenis.equalsIgnoreCase("BPJS")) {
            bayar = new PembayaranBPJS(currentTarifDokter, currentTarifObat);
        } else if (jenis.equalsIgnoreCase("Asuransi Swasta")) {
            bayar = new PembayaranAsuransi(currentTarifDokter, currentTarifObat);
        } else {
            bayar = new PembayaranTunai(currentTarifDokter, currentTarifObat); 
        }

        currentTotalAkhir = bayar.hitungTotal();
        view.setSubtotals(currentTarifDokter, currentTarifObat, currentTotalAkhir);
    }

    private void prosesPembayaran() {
        ComboItem terpilih = (ComboItem) view.getCbKunjungan().getSelectedItem();
        if (terpilih == null || currentTotalAkhir == 0) {
            JOptionPane.showMessageDialog(view, "Pilih kunjungan dan hitung tagihan terlebih dahulu!");
            return;
        }

        String jenis = (String) view.getCbJenisPembayaran().getSelectedItem();
        String sql = "INSERT INTO tagihan (id_kunjungan, total_biaya, tanggal_pembayaran, jenis_pembayaran, status_pembayaran) VALUES (?, ?, CURRENT_TIMESTAMP, ?, 'Lunas')";
        
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, terpilih.getId());
            pst.setDouble(2, currentTotalAkhir);
            pst.setString(3, jenis);
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(view, "Pembayaran Lunas!\nTotal: Rp " + String.format("%,.2f", currentTotalAkhir));
            resetForm();
            loadKunjunganPending();
            loadRiwayatTagihan();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(view, "Gagal memproses pembayaran: " + e.getMessage());
        }
    }

    private void loadRiwayatTagihan() {
        String sql = "SELECT t.id, t.total_biaya, t.jenis_pembayaran, t.status_pembayaran, t.tanggal_pembayaran, " +
                     "p.nama AS pasien, d.nama AS dokter, d.spesialisasi, k.id AS kunjungan_id " +
                     "FROM tagihan t JOIN kunjungan k ON t.id_kunjungan = k.id " +
                     "JOIN pasien p ON k.id_pasien = p.id " +
                     "JOIN dokter d ON k.id_dokter = d.id " +
                     "ORDER BY t.id DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            DefaultTableModel model = view.getTableModel();
            model.setRowCount(0);
            List<model.Tagihan> tagihanList = new ArrayList<>();
            while (rs.next()) {
                model.Pasien p = new model.Pasien();
                p.setNama(rs.getString("pasien"));
                
                String spec = rs.getString("spesialisasi");
                model.Dokter d = "Umum".equalsIgnoreCase(spec) ?
                                 new model.DokterUmum(-1, rs.getString("dokter")) :
                                 new model.DokterSpesialis(-1, rs.getString("dokter"), spec);
                                 
                model.Kunjungan k = new model.Kunjungan();
                k.setId(rs.getInt("kunjungan_id"));
                k.setPasien(p);
                k.setDokter(d);
                
                model.Tagihan t = new model.Tagihan(
                    rs.getInt("id"),
                    k,
                    rs.getDouble("total_biaya"),
                    rs.getTimestamp("tanggal_pembayaran")
                );
                
                String jenis = rs.getString("jenis_pembayaran");
                model.Pembayaran pem;
                if ("BPJS".equalsIgnoreCase(jenis)) {
                    pem = new model.PembayaranBPJS(t.getTotalBiaya(), 0);
                } else if ("Asuransi Swasta".equalsIgnoreCase(jenis)) {
                    pem = new model.PembayaranAsuransi(t.getTotalBiaya(), 0);
                } else {
                    pem = new model.PembayaranTunai(t.getTotalBiaya(), 0);
                }
                t.setPembayaran(pem);
                tagihanList.add(t);
            }
            
            for (model.Tagihan t : tagihanList) {
                String jenisPembayaran = "";
                if (t.getPembayaran() instanceof model.PembayaranBPJS) {
                    jenisPembayaran = "BPJS";
                } else if (t.getPembayaran() instanceof model.PembayaranAsuransi) {
                    jenisPembayaran = "Asuransi Swasta";
                } else {
                    jenisPembayaran = "Tunai";
                }
                
                model.addRow(new Object[]{
                    "INV-" + t.getId(), 
                    t.getKunjungan().getPasien().getNama(), 
                    t.getKunjungan().getDokter().getNama(),
                    String.format("Rp %,.2f", t.getTotalBiaya()), 
                    jenisPembayaran, 
                    "Lunas"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        view.setAutoFillData("-", "-", "-");
        view.setSubtotals(0, 0, 0);
        currentTotalAwal = 0;
        currentTotalAkhir = 0;
    }
}