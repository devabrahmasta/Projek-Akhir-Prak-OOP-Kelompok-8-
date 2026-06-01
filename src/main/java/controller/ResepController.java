package controller;

import database.DBConnection;
import view.ComboItem;
import view.ResepView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResepController {
    private final ResepView view;
    private final Connection connection;
    
    private int activeKunjunganId = -1;
    private int activePasienId = -1;
    private int activeResepId = -1;

    public ResepController(ResepView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        
        initListeners();
        loadObat();
    }

    private void initListeners() {
        view.addBtnTambahItemListener(e -> handleTambahItem());
        view.addBtnSimpanResepListener(e -> handleSimpanResep());
        view.addBtnBatalListener(e -> handleBatal());
        
        view.addDetailResepMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = view.getTabelDetailResep().columnAtPoint(e.getPoint());
                int row = view.getTabelDetailResep().rowAtPoint(e.getPoint());
                if (row != -1 && col == 4) { // Kolom Aksi (Hapus)
                    view.getTabelDetailModel().removeRow(row);
                }
            }
        });
        
        view.addRiwayatResepMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleRiwayatClick();
                }
            }
        });
    }
    
    public void setActiveKunjungan(int idKunjungan, int idPasien) {
        this.activeKunjunganId = idKunjungan;
        this.activePasienId = idPasien;
        this.activeResepId = -1;
        view.setLblKunjungan(String.valueOf(idKunjungan));
        view.getTabelDetailModel().setRowCount(0);
        view.clearFormObat();
        
        loadRiwayat();
        loadExistingResepForKunjungan();
    }

    private void loadObat() {
        SwingWorker<List<ComboItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ComboItem> doInBackground() throws Exception {
                List<ComboItem> list = new ArrayList<>();
                if (connection == null) return list;
                
                String sql = "SELECT id, nama, stok FROM obat ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String label = rs.getString("nama") + " (Stok: " + rs.getInt("stok") + ")";
                        list.add(new ComboItem(id, label));
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try {
                    List<ComboItem> list = get();
                    view.setObatList(list.toArray(new ComboItem[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void loadExistingResepForKunjungan() {
        if (activeKunjunganId == -1) return;
        
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> items = new ArrayList<>();
                if (connection == null) return items;
                
                String sqlResep = "SELECT id FROM resep WHERE id_kunjungan = ?";
                try (PreparedStatement pst = connection.prepareStatement(sqlResep)) {
                    pst.setInt(1, activeKunjunganId);
                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            activeResepId = rs.getInt("id");
                        }
                    }
                }
                
                if (activeResepId != -1) {
                    String sqlDetail = "SELECT dr.id_obat, o.nama, dr.jumlah, dr.dosis " +
                                       "FROM detail_resep dr JOIN obat o ON dr.id_obat = o.id " +
                                       "WHERE dr.id_resep = ?";
                    try (PreparedStatement pst = connection.prepareStatement(sqlDetail)) {
                        pst.setInt(1, activeResepId);
                        try (ResultSet rs = pst.executeQuery()) {
                            while (rs.next()) {
                                items.add(new Object[]{
                                    rs.getInt("id_obat"),
                                    rs.getString("nama"),
                                    rs.getInt("jumlah"),
                                    rs.getString("dosis"),
                                    "Hapus"
                                });
                            }
                        }
                    }
                }
                return items;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> items = get();
                    DefaultTableModel model = view.getTabelDetailModel();
                    model.setRowCount(0);
                    for (Object[] row : items) {
                        model.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void loadRiwayat() {
        if (activePasienId == -1) return;
        
        view.setStatusText("Memuat riwayat resep...");
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                List<Object[]> data = new ArrayList<>();
                if (connection == null) return data;
                
                int idUser = SessionManager.getUser().getId();
                int idDokter = -1;
                String sqlDoc = "SELECT id FROM dokter WHERE id_user = ?";
                try (PreparedStatement pstDoc = connection.prepareStatement(sqlDoc)) {
                    pstDoc.setInt(1, idUser);
                    try (ResultSet rsDoc = pstDoc.executeQuery()) {
                        if (rsDoc.next()) {
                            idDokter = rsDoc.getInt("id");
                        }
                    }
                }
                
                String sql = "SELECT r.id, k.id AS id_kunjungan, k.tanggal_kunjungan, " +
                             "(SELECT COUNT(*) FROM detail_resep dr WHERE dr.id_resep = r.id) as jml_item " +
                             "FROM resep r " +
                             "JOIN kunjungan k ON r.id_kunjungan = k.id " +
                             "WHERE k.id_pasien = ? AND k.id_dokter = ? " +
                             "ORDER BY k.tanggal_kunjungan DESC";
                             
                try (PreparedStatement pst = connection.prepareStatement(sql)) {
                    pst.setInt(1, activePasienId);
                    pst.setInt(2, idDokter);
                    try (ResultSet rs = pst.executeQuery()) {
                        while (rs.next()) {
                            data.add(new Object[]{
                                rs.getInt("id"),
                                rs.getDate("tanggal_kunjungan").toString(),
                                rs.getInt("jml_item")
                            });
                        }
                    }
                }
                return data;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> data = get();
                    DefaultTableModel model = view.getTabelRiwayatModel();
                    model.setRowCount(0);
                    for (Object[] row : data) {
                        model.addRow(row);
                    }
                    view.setStatusText("Riwayat resep dimuat (" + data.size() + " data)");
                } catch (Exception e) {
                    view.setStatusText("Error muat riwayat: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void handleTambahItem() {
        ComboItem obat = view.getSelectedObat();
        String jumlahStr = view.getJumlahInput();
        String dosis = view.getDosisInput();

        if (obat == null || jumlahStr.isEmpty() || dosis.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Semua field harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Jumlah harus berupa angka positif!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cek duplikasi di tabel
        DefaultTableModel model = view.getTabelDetailModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int idExisting = (int) model.getValueAt(i, 0);
            if (idExisting == obat.getId()) {
                JOptionPane.showMessageDialog(view, "Obat sudah ada di daftar resep!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        model.addRow(new Object[]{obat.getId(), obat.toString(), jumlah, dosis, "Hapus"});
        view.clearFormObat();
    }

    private void handleSimpanResep() {
        if (activeKunjunganId == -1) {
            JOptionPane.showMessageDialog(view, "Tidak ada kunjungan yang aktif dipilih!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = view.getTabelDetailModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Tambahkan minimal satu obat ke dalam resep!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil data item dari tabel
        List<Object[]> itemsToSave = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            itemsToSave.add(new Object[]{
                model.getValueAt(i, 0), // id obat
                model.getValueAt(i, 2), // jumlah
                model.getValueAt(i, 3)  // dosis
            });
        }

        view.setStatusText("Menyimpan resep...");
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                
                int idDokter = SessionManager.getUser().getId();
                
                connection.setAutoCommit(false);
                try {
                    // Jika resep belum ada, buat baru
                    if (activeResepId == -1) {
                        String sqlInsertResep = "INSERT INTO resep (id_kunjungan, tanggal, status) VALUES (?, ?, ?)";
                        try (PreparedStatement pst = connection.prepareStatement(sqlInsertResep, Statement.RETURN_GENERATED_KEYS)) {
                            pst.setInt(1, activeKunjunganId);
                            pst.setDate(2, new java.sql.Date(System.currentTimeMillis()));
                            pst.setString(3, "belum_disiapkan");
                            pst.executeUpdate();
                            
                            try (ResultSet rs = pst.getGeneratedKeys()) {
                                if (rs.next()) {
                                    activeResepId = rs.getInt(1);
                                }
                            }
                        }
                    } else {
                        // Jika sudah ada, hapus detail lama
                        String sqlDeleteDetail = "DELETE FROM detail_resep WHERE id_resep = ?";
                        try (PreparedStatement pst = connection.prepareStatement(sqlDeleteDetail)) {
                            pst.setInt(1, activeResepId);
                            pst.executeUpdate();
                        }
                    }

                    // Insert detail resep baru
                    String sqlInsertDetail = "INSERT INTO detail_resep (id_resep, id_obat, jumlah, dosis) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement pst = connection.prepareStatement(sqlInsertDetail)) {
                        for (Object[] item : itemsToSave) {
                            pst.setInt(1, activeResepId);
                            pst.setInt(2, (int) item[0]);
                            pst.setInt(3, (int) item[1]);
                            pst.setString(4, (String) item[2]);
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
                        JOptionPane.showMessageDialog(view, "Resep berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        loadRiwayat();
                        view.setStatusText("Resep berhasil disimpan.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal menyimpan resep: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    view.setStatusText("Error simpan: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void handleBatal() {
        view.clearFormObat();
        view.getTabelDetailModel().setRowCount(0);
        if (activeKunjunganId != -1) {
            loadExistingResepForKunjungan();
        }
    }

    private void handleRiwayatClick() {
        int row = view.getSelectedRiwayatResepRow();
        if (row != -1) {
            int idResep = (int) view.getTabelRiwayatResep().getValueAt(row, 0);
            JOptionPane.showMessageDialog(view, "Menampilkan detail riwayat resep ID " + idResep + " tidak didukung pada view ini. Gunakan fitur cetak.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
