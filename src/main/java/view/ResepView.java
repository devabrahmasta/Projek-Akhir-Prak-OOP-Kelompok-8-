package view;

import javax.swing.*;
import java.awt.*;

public class ResepView extends JPanel {
    private JTable table;
    private JTextField txtId, txtKunjunganId, txtDokterId, txtObatIds, txtKeterangan, txtTanggal;
    private JButton btnTambah, btnUbah, btnHapus, btnRefresh;

    public ResepView() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        formPanel.add(new JLabel("ID Resep:"));
        txtId = new JTextField();
        formPanel.add(txtId);
        
        formPanel.add(new JLabel("ID Kunjungan:"));
        txtKunjunganId = new JTextField();
        formPanel.add(txtKunjunganId);
        
        formPanel.add(new JLabel("ID Dokter:"));
        txtDokterId = new JTextField();
        formPanel.add(txtDokterId);
        
        formPanel.add(new JLabel("IDs Obat (comma separated):"));
        txtObatIds = new JTextField();
        formPanel.add(txtObatIds);
        
        formPanel.add(new JLabel("Keterangan:"));
        txtKeterangan = new JTextField();
        formPanel.add(txtKeterangan);
        
        formPanel.add(new JLabel("Tanggal (YYYY-MM-DD):"));
        txtTanggal = new JTextField();
        formPanel.add(txtTanggal);
        
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
    public JTextField getTxtKunjunganId() { return txtKunjunganId; }
    public JTextField getTxtDokterId() { return txtDokterId; }
    public JTextField getTxtObatIds() { return txtObatIds; }
    public JTextField getTxtKeterangan() { return txtKeterangan; }
    public JTextField getTxtTanggal() { return txtTanggal; }
    public JButton getBtnTambah() { return btnTambah; }
    public JButton getBtnUbah() { return btnUbah; }
    public JButton getBtnHapus() { return btnHapus; }
    public JButton getBtnRefresh() { return btnRefresh; }
}
