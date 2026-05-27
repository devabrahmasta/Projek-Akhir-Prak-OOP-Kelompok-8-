package controller;

import view.TagihanView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TagihanController {
    private TagihanView view;

    public TagihanController(TagihanView view) {
        this.view = view;
        initController();
        loadData();
    }

    private void initController() {
        view.getBtnRefresh().addActionListener(e -> loadData());
        view.getBtnTambah().addActionListener(e -> tambahData());
        view.getBtnUbah().addActionListener(e -> ubahData());
        view.getBtnHapus().addActionListener(e -> hapusData());
    }

    private void loadData() {
        // SwingWorker for database read operations
        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {
            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                // Mock database operation
                Thread.sleep(500); 
                String[] columnNames = {"ID", "Kunjungan ID", "Total Biaya", "Tanggal"};
                Object[][] data = {
                    {1, 1, 150000.0, "2026-05-27"}
                };
                return new DefaultTableModel(data, columnNames);
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = get();
                    view.getTable().setModel(model);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(view, "Error loading data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void tambahData() {
        JOptionPane.showMessageDialog(view, "Data Tagihan Ditambahkan");
        loadData();
    }

    private void ubahData() {
        JOptionPane.showMessageDialog(view, "Data Tagihan Diubah");
        loadData();
    }

    private void hapusData() {
        JOptionPane.showMessageDialog(view, "Data Tagihan Dihapus");
        loadData();
    }
}
