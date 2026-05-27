package controller;

import view.ObatView;
import model.Obat;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ObatController {
    private ObatView view;
    
    private static List<Obat> obatList = new ArrayList<>();

    static {
        obatList.add(new Obat(1, "Paracetamol", 50, 5000));
        obatList.add(new Obat(2, "Amoxicillin", 5, 15000)); 
    }

    public ObatController(ObatView view) {
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
                String[] columnNames = {"ID", "Nama", "Stok", "Harga"};
                Object[][] data = new Object[obatList.size()][4];
                for (int i = 0; i < obatList.size(); i++) {
                    Obat o = obatList.get(i);
                    data[i][0] = o.getId();
                    data[i][1] = o.getNama();
                    data[i][2] = o.getStok();
                    data[i][3] = o.getHarga();
                }
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
        JOptionPane.showMessageDialog(view, "Data Obat Ditambahkan");
        loadData();
    }

    private void ubahData() {
        JOptionPane.showMessageDialog(view, "Data Obat Diubah");
        loadData();
    }

    private void hapusData() {
        JOptionPane.showMessageDialog(view, "Data Obat Dihapus");
        loadData();
    }
    
    
    public List<Obat> getSemuaObat() {
        return obatList;
    }
}
