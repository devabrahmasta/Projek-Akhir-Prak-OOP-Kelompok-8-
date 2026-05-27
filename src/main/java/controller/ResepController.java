package controller;

import view.ResepView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ResepController {
    private ResepView view;

    public ResepController(ResepView view) {
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
        
        SwingWorker<DefaultTableModel, Void> worker = new SwingWorker<DefaultTableModel, Void>() {
            @Override
            protected DefaultTableModel doInBackground() throws Exception {
                
                Thread.sleep(500); 
                String[] columnNames = {"ID", "Kunjungan ID", "Dokter ID", "Keterangan", "Tanggal"};
                Object[][] data = {
                    {1, 1, 1, "Minum 3x sehari", "2026-05-27"}
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
        JOptionPane.showMessageDialog(view, "Data Resep Ditambahkan");
        loadData();
    }

    private void ubahData() {
        JOptionPane.showMessageDialog(view, "Data Resep Diubah");
        loadData();
    }

    private void hapusData() {
        JOptionPane.showMessageDialog(view, "Data Resep Dihapus");
        loadData();
    }
}
