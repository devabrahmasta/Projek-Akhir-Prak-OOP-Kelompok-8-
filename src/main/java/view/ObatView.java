package view;

import javax.swing.*;
import java.awt.*;

public class ObatView extends JPanel {
    private JTable table;
    private JTextField txtId, txtNama, txtStok, txtHarga;
    private JButton btnTambah, btnUbah, btnHapus, btnRefresh;

    public ObatView() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("ID Obat:"));
        txtId = new JTextField();
        formPanel.add(txtId);
        
        formPanel.add(new JLabel("Nama Obat:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);
        
        formPanel.add(new JLabel("Stok:"));
        txtStok = new JTextField();
        formPanel.add(txtStok);
        
        formPanel.add(new JLabel("Harga:"));
        txtHarga = new JTextField();
        formPanel.add(txtHarga);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnTambah = new JButton("Tambah");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnRefresh = new JButton("Refresh");
        
        btnPanel.add(btnTambah);
        btnPanel.add(btnUbah);
        btnPanel.add(btnHapus);
        btnPanel.add(btnRefresh);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public JTable getTable() { return table; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtNama() { return txtNama; }
    public JTextField getTxtStok() { return txtStok; }
    public JTextField getTxtHarga() { return txtHarga; }
    public JButton getBtnTambah() { return btnTambah; }
    public JButton getBtnUbah() { return btnUbah; }
    public JButton getBtnHapus() { return btnHapus; }
    public JButton getBtnRefresh() { return btnRefresh; }
}
