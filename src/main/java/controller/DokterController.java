package controller;

import view.DokterView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;

public class DokterController {
    private DokterView view;

    public DokterController(DokterView view) {
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
                String[] columnNames = {"ID", "Nama", "Spesialisasi"};
                Object[][] data = {
                    {1, "Dr. Andi", "Umum"},
                    {2, "Dr. Budi", "Jantung"}
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
        
        JOptionPane.showMessageDialog(view, "Data Dokter Ditambahkan");
        loadData();
    }

    private void ubahData() {
        
        JOptionPane.showMessageDialog(view, "Data Dokter Diubah");
        loadData();
    }

    private void hapusData() {
        
        JOptionPane.showMessageDialog(view, "Data Dokter Dihapus");
        loadData();
    }
}
