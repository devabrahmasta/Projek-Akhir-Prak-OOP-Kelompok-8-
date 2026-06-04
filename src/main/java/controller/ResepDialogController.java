package controller;

import database.DBConnection;
import view.ComboItem;
import view.ResepDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResepDialogController {
    private final ResepDialog view;
    private final int idKunjungan;
    private final Connection connection;

    public ResepDialogController(ResepDialog view, int idKunjungan) {
        this.view        = view;
        this.idKunjungan = idKunjungan;
        this.connection  = DBConnection.getInstance().getConnection();

        view.addTambahkanListener(e -> handleTambahkan());
        view.addSimpanListener(e -> handleSimpan());
        view.addTutupListener(e -> view.dispose());

        loadObat();
    }

    private void loadObat() {
        SwingWorker<List<ComboItem>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ComboItem> doInBackground() throws Exception {
                List<ComboItem> list = new ArrayList<>();
                if (connection == null) return list;
                String sql = "SELECT id, nama, stok FROM obat ORDER BY nama ASC";
                try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        int id    = rs.getInt("id");
                        String nm = rs.getString("nama") + " (Stok: " + rs.getInt("stok") + ")";
                        list.add(new ComboItem(id, nm));
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                try { view.setObatList(get().toArray(new ComboItem[0])); }
                catch (Exception e) { e.printStackTrace(); }
            }
        };
        worker.execute();
    }

    private void handleTambahkan() {
        ComboItem selectedObat = view.getSelectedObat();
        String jumlahStr = view.getJumlahInput();
        String dosis     = view.getDosisInput();

        if (selectedObat == null || jumlahStr.isEmpty() || dosis.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Obat, jumlah, dan dosis wajib diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int jumlah;
        try {
            jumlah = Integer.parseInt(jumlahStr);
            if (jumlah <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Jumlah harus berupa angka positif!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        DefaultTableModel model = view.getDetailModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(selectedObat.getId())) {
                JOptionPane.showMessageDialog(view, "Obat ini sudah ada di daftar resep!", "Duplikasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        
        String namaObat = selectedObat.getLabel();
        if (namaObat.contains(" (Stok:")) {
            namaObat = namaObat.substring(0, namaObat.indexOf(" (Stok:"));
        }

        model.addRow(new Object[]{selectedObat.getId(), namaObat, jumlah, dosis, "×"});
        view.clearFormObat();
    }

    private void handleSimpan() {
        DefaultTableModel model = view.getDetailModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, "Tambahkan minimal satu obat terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (connection == null) return false;
                connection.setAutoCommit(false);
                try {
                    
                    String sqlResep = "INSERT INTO resep (id_kunjungan, tanggal, status) VALUES (?, CURRENT_DATE, 'belum_disiapkan')";
                    int idResep = -1;
                    try (PreparedStatement ps = connection.prepareStatement(sqlResep, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, idKunjungan);
                        ps.executeUpdate();
                        try (ResultSet rs = ps.getGeneratedKeys()) {
                            if (rs.next()) idResep = rs.getInt(1);
                        }
                    }

                    
                    String sqlDetail = "INSERT INTO detail_resep (id_resep, id_obat, jumlah, dosis) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = connection.prepareStatement(sqlDetail)) {
                        for (int i = 0; i < model.getRowCount(); i++) {
                            ps.setInt(1, idResep);
                            ps.setInt(2, (int) model.getValueAt(i, 0)); 
                            ps.setInt(3, (int) model.getValueAt(i, 2)); 
                            ps.setString(4, (String) model.getValueAt(i, 3)); 
                            ps.addBatch();
                        }
                        ps.executeBatch();
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
                        JOptionPane.showMessageDialog(view, "Resep berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        view.dispose();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Gagal menyimpan resep: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
