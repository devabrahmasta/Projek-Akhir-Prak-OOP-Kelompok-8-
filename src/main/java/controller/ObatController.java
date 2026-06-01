package controller;

import database.DBConnection;
import view.ComboItem;
import view.ObatView;
import model.Obat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObatController {
    private final ObatView view;
    private final Connection connection;

    private boolean isEditingMode = false;
    private String selectedKode = null;

    public ObatController(ObatView view) {
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

        view.addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableClick();
            }
        });
    }
    
    public List<Obat> getSemuaObat() {
        List<Obat> listObat = new ArrayList<>();
        if (connection == null) return listObat;

        // Query cukup mengambil kolom yang dibutuhkan oleh StokMonitorThread
        String sql = "SELECT id, kode_obat, nama, stok, harga FROM obat";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Pastikan class model.Obat Anda memiliki default constructor 
                // dan setter/getter untuk Nama dan Stok.
                Obat obat = new Obat(
                        rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getInt("stok"),
                    rs.getDouble("harga")
                );
                // Jika DB menggunakan "kode_obat" alih-alih "id"
                // obat.setId(rs.getString("kode_obat")); 
                obat.setNama(rs.getString("nama"));
                obat.setStok(rs.getInt("stok"));
                
                listObat.add(obat);
            }
        } catch (SQLException e) {
            System.err.println("Error getSemuaObat: " + e.getMessage());
        }
        
        return listObat;
    }

    // =============================================
    // LOAD: Semua data obat dari DB
    // =============================================
    public void loadData() {
        view.setStatusText("Memuat data obat...");

        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> list = new ArrayList<>();
                if (connection == null) return list;

                String sql = "SELECT kode_obat, nama, kategori, stok, satuan, harga, stok_minimum " +
                             "FROM obat ORDER BY nama ASC";

                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int stok    = rs.getInt("stok");
                        int stokMin = rs.getInt("stok_minimum");
                        String status = (stok <= stokMin) ? "Kritis" : "Aman";

                        list.add(new Object[]{
                            rs.getString("kode_obat"),
                            rs.getString("nama"),
                            rs.getString("kategori"),
                            stok,
                            rs.getString("satuan"),
                            rs.getDouble("harga"),
                            stokMin,
                            status
                        });
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    for (Object[] row : list) {
                        model.addRow(row);
                    }
                    view.setStatusText("Data obat dimuat (" + list.size() + " item)");
                } catch (Exception e) {
                    view.setStatusText("Error memuat data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // =============================================
    // TAMBAH: Buka form kosong
    // =============================================
    private void handleTambah() {
        isEditingMode = true;
        selectedKode  = null;
        view.clearForm();
        view.setFormEnabled(true);
        view.setButtonsState(true);
        view.setKodeObatInput(generateKode());
        view.setStatusText("Mengisi data obat baru");
    }

    // =============================================
    // SIMPAN: Insert atau Update ke DB
    // =============================================
    private void handleSimpan() {
        String kode     = view.getKodeObatInput();
        String nama     = view.getNamaObatInput();
        String kategori = view.getKategoriInput();
        String stokStr  = view.getStokInput();
        String satuan   = view.getSatuanInput();
        String hargaStr = view.getHargaInput();
        String stokMinStr = view.getStokMinimumInput();

        if (nama.isEmpty() || stokStr.isEmpty() || hargaStr.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Nama, Stok, dan Harga wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stok, stokMin;
        double harga;
        try {
            stok    = Integer.parseInt(stokStr);
            stokMin = Integer.parseInt(stokMinStr);
            harga   = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Stok, Stok Minimum, dan Harga harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        view.setStatusText("Menyimpan data obat...");

        final int    fStok    = stok;
        final int    fStokMin = stokMin;
        final double fHarga   = harga;

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;

                if (selectedKode == null) {
                    // INSERT
                    String sql = "INSERT INTO obat (kode_obat, nama, kategori, stok, satuan, harga, stok_minimum) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement pst = connection.prepareStatement(sql)) {
                        pst.setString(1, kode);
                        pst.setString(2, nama);
                        pst.setString(3, kategori);
                        pst.setInt(4, fStok);
                        pst.setString(5, satuan);
                        pst.setDouble(6, fHarga);
                        pst.setInt(7, fStokMin);
                        pst.executeUpdate();
                    }
                } else {
                    // UPDATE
                    String sql = "UPDATE obat SET nama=?, kategori=?, stok=?, satuan=?, harga=?, stok_minimum=? " +
                                 "WHERE kode_obat=?";
                    try (PreparedStatement pst = connection.prepareStatement(sql)) {
                        pst.setString(1, nama);
                        pst.setString(2, kategori);
                        pst.setInt(3, fStok);
                        pst.setString(4, satuan);
                        pst.setDouble(5, fHarga);
                        pst.setInt(6, fStokMin);
                        pst.setString(7, selectedKode);
                        pst.executeUpdate();
                    }
                }
                return true;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        String msg = (selectedKode == null) ? "Data obat berhasil ditambahkan!" : "Data obat berhasil diperbarui!";
                        JOptionPane.showMessageDialog(view, msg, "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        isEditingMode = false;
                        selectedKode  = null;
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setButtonsState(false);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    view.setStatusText("Gagal menyimpan: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    // =============================================
    // HAPUS: Delete dari DB berdasarkan kode
    // =============================================
    private void handleHapus() {
        String kode = view.getSelectedKode();
        if (kode == null) {
            JOptionPane.showMessageDialog(view, "Pilih obat yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
            "Hapus obat dengan kode '" + kode + "'? Tindakan ini tidak dapat dibatalkan.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        view.setStatusText("Menghapus data obat...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                String sql = "DELETE FROM obat WHERE kode_obat = ?";
                try (PreparedStatement pst = connection.prepareStatement(sql)) {
                    pst.setString(1, kode);
                    return pst.executeUpdate() > 0;
                }
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(view, "Data obat berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        view.clearForm();
                        view.setFormEnabled(false);
                        view.setButtonsState(false);
                        loadData();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal menghapus: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // =============================================
    // BATAL: Reset form
    // =============================================
    private void handleBatal() {
        isEditingMode = false;
        selectedKode  = null;
        view.clearForm();
        view.setFormEnabled(false);
        view.setButtonsState(false);
        view.setStatusText("Siap");
    }

    // =============================================
    // KLIK TABEL: Isi form dengan data baris terpilih
    // =============================================
    private void handleTableClick() {
        int row = view.getTable().getSelectedRow();
        if (row == -1 || isEditingMode) return;

        selectedKode = view.getTable().getValueAt(row, 0).toString();

        view.setKodeObatInput(selectedKode);
        view.setNamaObatInput(view.getTable().getValueAt(row, 1).toString());
        view.setKategoriInput(view.getTable().getValueAt(row, 2).toString());
        view.setStokInput(view.getTable().getValueAt(row, 3).toString());
        view.setSatuanInput(view.getTable().getValueAt(row, 4).toString());
        view.setHargaInput(view.getTable().getValueAt(row, 5).toString());
        view.setStokMinimumInput(view.getTable().getValueAt(row, 6).toString());

        isEditingMode = true;
        view.setFormEnabled(true);
        view.setButtonsState(true);
        view.setStatusText("Mengedit obat: " + selectedKode);
    }

    // =============================================
    // UTIL: Generate kode obat otomatis
    // =============================================
    private String generateKode() {
        try {
            String sql = "SELECT MAX(CAST(SUBSTRING(kode_obat, 4) AS UNSIGNED)) AS max_no FROM obat WHERE kode_obat LIKE 'OBT%'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    int next = rs.getInt("max_no") + 1;
                    return String.format("OBT%03d", next);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OBT001";
    }

    // =============================================
    // UTIL: Ambil ComboItem obat untuk dipakai module lain (ResepController dll)
    // =============================================
    public void loadObatComboItems(java.util.function.Consumer<ComboItem[]> callback) {
        SwingWorker<ComboItem[], Void> worker = new SwingWorker<>() {
            @Override
            protected ComboItem[] doInBackground() throws Exception {
                List<ComboItem> list = new ArrayList<>();
                if (connection == null) return new ComboItem[0];
                String sql = "SELECT id, nama, stok FROM obat ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        list.add(new ComboItem(rs.getInt("id"),
                            rs.getString("nama") + " (Stok: " + rs.getInt("stok") + ")"));
                    }
                }
                return list.toArray(new ComboItem[0]);
            }

            @Override
            protected void done() {
                try { callback.accept(get()); } catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }
}
