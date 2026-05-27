package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class PasienView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtNama;
    private JTextField txtNoRM;
    private JTextArea txtAlamat;
    private JTextField txtNoTelp;
    private JTextField txtTanggalLahir; // Format: YYYY-MM-DD
    private JComboBox<String> cbGolDarah;
    private JTextField txtAlergi;
    
    private JTextField txtCari;
    private JButton btnCari;
    
    private JButton btnTambah;
    private JButton btnSimpan;
    private JButton btnHapus;
    private JButton btnBatal;
    
    private JLabel lblStatus;
    
    // Style constants
    private final Color COLOR_BG = new Color(0xEE, 0xEE, 0xEE);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(0x2F, 0xA0, 0x84);
    private final Color COLOR_PRIMARY_HOVER = new Color(0x1F, 0x6F, 0x5F);
    private final Color COLOR_TEXT = new Color(0x1F, 0x6F, 0x5F);
    private final Color COLOR_TEXT_MUTED = new Color(0x66, 0x80, 0x7A);
    private final Color COLOR_BORDER = new Color(0xD6, 0xDC, 0xDA);
    private final Color COLOR_INPUT_BG = new Color(0xF5, 0xF7, 0xF6);
    
    public PasienView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Manajemen Data Pasien");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(COLOR_BG);
        
        JLabel lblCari = new JLabel("Cari Nama/RM:");
        lblCari.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCari.setForeground(COLOR_TEXT_MUTED);
        searchPanel.add(lblCari);
        
        txtCari = new JTextField(15);
        styleTextField(txtCari);
        searchPanel.add(txtCari);
        
        btnCari = new JButton("Cari");
        styleButton(btnCari, COLOR_PRIMARY);
        searchPanel.add(btnCari);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        
        // Left Panel - Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Title Form
        JLabel formTitle = new JLabel("Form Data Pasien");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // No RM
        formPanel.add(createFormLabel("No. Rekam Medis (RM):"), gbc);
        gbc.gridy++;
        txtNoRM = new JTextField();
        styleTextField(txtNoRM);
        formPanel.add(txtNoRM, gbc);
        
        gbc.gridy++;
        // Nama
        formPanel.add(createFormLabel("Nama Lengkap:"), gbc);
        gbc.gridy++;
        txtNama = new JTextField();
        styleTextField(txtNama);
        formPanel.add(txtNama, gbc);
        
        gbc.gridy++;
        // Alamat
        formPanel.add(createFormLabel("Alamat:"), gbc);
        gbc.gridy++;
        txtAlamat = new JTextArea(3, 15);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBackground(COLOR_INPUT_BG);
        txtAlamat.setForeground(COLOR_TEXT);
        txtAlamat.setCaretColor(COLOR_TEXT);
        txtAlamat.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        formPanel.add(new JScrollPane(txtAlamat), gbc);
        
        gbc.gridy++;
        // No Telp
        formPanel.add(createFormLabel("No. Telepon:"), gbc);
        gbc.gridy++;
        txtNoTelp = new JTextField();
        styleTextField(txtNoTelp);
        formPanel.add(txtNoTelp, gbc);
        
        gbc.gridy++;
        // Tanggal Lahir
        formPanel.add(createFormLabel("Tanggal Lahir (YYYY-MM-DD):"), gbc);
        gbc.gridy++;
        txtTanggalLahir = new JTextField();
        styleTextField(txtTanggalLahir);
        formPanel.add(txtTanggalLahir, gbc);
        
        gbc.gridy++;
        // Golongan Darah
        formPanel.add(createFormLabel("Golongan Darah:"), gbc);
        gbc.gridy++;
        cbGolDarah = new JComboBox<>(new String[]{"-", "A", "B", "AB", "O"});
        cbGolDarah.setBackground(COLOR_INPUT_BG);
        cbGolDarah.setForeground(COLOR_TEXT);
        cbGolDarah.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(cbGolDarah, gbc);
        
        gbc.gridy++;
        // Alergi
        formPanel.add(createFormLabel("Alergi:"), gbc);
        gbc.gridy++;
        txtAlergi = new JTextField();
        styleTextField(txtAlergi);
        formPanel.add(txtAlergi, gbc);
        
        // Buttons Panel
        gbc.gridy++;
        gbc.insets = new Insets(15, 8, 8, 8);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBackground(COLOR_CARD);
        
        btnTambah = new JButton("Tambah");
        styleButton(btnTambah, COLOR_PRIMARY);
        btnSimpan = new JButton("Simpan");
        styleButton(btnSimpan, new Color(46, 125, 50));
        btnHapus = new JButton("Hapus");
        styleButton(btnHapus, new Color(198, 40, 40));
        btnBatal = new JButton("Batal");
        styleButton(btnBatal, new Color(97, 97, 97));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        formPanel.add(buttonPanel, gbc);
        
        gbc.gridy++;
        lblStatus = new JLabel("Status: Siap");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(COLOR_TEXT_MUTED);
        formPanel.add(lblStatus, gbc);
        
        // Right Panel - Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(COLOR_CARD);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tableTitle = new JLabel("Daftar Pasien Terdaftar");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        // Setup JTable
        String[] columns = {"ID", "No. RM", "Nama", "No. Telp", "Tgl Lahir", "Gol. Darah", "Alergi", "Alamat"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getViewport().setBackground(COLOR_CARD);
        
        splitPane.setLeftComponent(formScrollPane);
        splitPane.setRightComponent(tablePanel);
        add(splitPane, BorderLayout.CENTER);
        
        // Form Initial State
        setFormEnabled(false);
        btnSimpan.setEnabled(false);
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MUTED);
        return label;
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(COLOR_INPUT_BG);
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_TEXT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.brighter());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });
    }
    
    private void styleTable(JTable table) {
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_BORDER);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(28);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BORDER);
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        // Align headers and cells
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    // UI Helpers & Controllers bridge
    
    public void setFormEnabled(boolean enabled) {
        txtNoRM.setEnabled(enabled);
        txtNama.setEnabled(enabled);
        txtAlamat.setEnabled(enabled);
        txtNoTelp.setEnabled(enabled);
        txtTanggalLahir.setEnabled(enabled);
        cbGolDarah.setEnabled(enabled);
        txtAlergi.setEnabled(enabled);
    }
    
    public void clearForm() {
        txtNoRM.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtNoTelp.setText("");
        txtTanggalLahir.setText("");
        cbGolDarah.setSelectedIndex(0);
        txtAlergi.setText("");
        table.clearSelection();
    }
    
    public void fillForm(String id, String noRM, String nama, String telp, String tglLahir, String golDarah, String alergi, String alamat) {
        txtNoRM.setText(noRM);
        txtNama.setText(nama);
        txtNoTelp.setText(telp);
        txtTanggalLahir.setText(tglLahir);
        cbGolDarah.setSelectedItem(golDarah == null || golDarah.trim().isEmpty() ? "-" : golDarah);
        txtAlergi.setText(alergi);
        txtAlamat.setText(alamat);
    }
    
    // Listeners
    public void addTambahListener(ActionListener l) { btnTambah.addActionListener(l); }
    public void addSimpanListener(ActionListener l) { btnSimpan.addActionListener(l); }
    public void addHapusListener(ActionListener l) { btnHapus.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    public void addCariListener(ActionListener l) { btnCari.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter l) { table.addMouseListener(l); }
    
    // Getters for form inputs
    public String getNoRMInput() { return txtNoRM.getText().trim(); }
    public String getNamaInput() { return txtNama.getText().trim(); }
    public String getAlamatInput() { return txtAlamat.getText().trim(); }
    public String getNoTelpInput() { return txtNoTelp.getText().trim(); }
    public String getTanggalLahirInput() { return txtTanggalLahir.getText().trim(); }
    public String getGolonganDarahInput() { 
        String val = (String) cbGolDarah.getSelectedItem();
        return "-".equals(val) ? "" : val;
    }
    public String getAlergiInput() { return txtAlergi.getText().trim(); }
    
    public String getCariInput() { return txtCari.getText().trim(); }
    
    // Table model management
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return table; }
    
    public int getSelectedId() {
        int row = table.getSelectedRow();
        if (row != -1) {
            return Integer.parseInt(table.getValueAt(row, 0).toString());
        }
        return -1;
    }
    
    public void setStatusText(String text) {
        lblStatus.setText("Status: " + text);
    }
    
    public void setButtonsState(boolean isEditing) {
        btnTambah.setEnabled(!isEditing);
        btnSimpan.setEnabled(isEditing);
        btnHapus.setEnabled(!isEditing && table.getSelectedRow() != -1);
        btnBatal.setEnabled(isEditing);
    }
}
