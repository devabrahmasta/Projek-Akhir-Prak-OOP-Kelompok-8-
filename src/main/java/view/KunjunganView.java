package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class KunjunganView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JComboBox<ComboItem> cbPasien;
    private JComboBox<ComboItem> cbDokter;
    private JTextField txtTanggal; // Format: YYYY-MM-DD HH:mm:ss
    private JTextArea txtKeluhan;
    private JTextArea txtDiagnosa;
    
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
    private final Color COLOR_PRIMARY = new Color(0x2F, 0xA0, 0x84); // Sea Green
    private final Color COLOR_PRIMARY_HOVER = new Color(0x1F, 0x6F, 0x5F); // Dark Teal
    private final Color COLOR_TEXT = new Color(0x1F, 0x6F, 0x5F); // Dark Teal Text
    private final Color COLOR_TEXT_MUTED = new Color(0x66, 0x80, 0x7A);
    private final Color COLOR_BORDER = new Color(0xD6, 0xDC, 0xDA);
    private final Color COLOR_INPUT_BG = new Color(0xF5, 0xF7, 0xF6);
    
    public KunjunganView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Rekam Medis & Kunjungan Pasien");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(COLOR_BG);
        
        JLabel lblCari = new JLabel("Cari Nama Pasien:");
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
        splitPane.setDividerLocation(340);
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
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        // Title Form
        JLabel formTitle = new JLabel("Form Catatan Kunjungan");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Pasien
        formPanel.add(createFormLabel("Pasien:"), gbc);
        gbc.gridy++;
        cbPasien = new JComboBox<>();
        styleComboBox(cbPasien);
        formPanel.add(cbPasien, gbc);
        
        gbc.gridy++;
        // Dokter
        formPanel.add(createFormLabel("Dokter Pemeriksa:"), gbc);
        gbc.gridy++;
        cbDokter = new JComboBox<>();
        styleComboBox(cbDokter);
        formPanel.add(cbDokter, gbc);
        
        gbc.gridy++;
        // Tanggal Kunjungan
        formPanel.add(createFormLabel("Waktu (YYYY-MM-DD HH:mm:ss):"), gbc);
        gbc.gridy++;
        txtTanggal = new JTextField();
        styleTextField(txtTanggal);
        formPanel.add(txtTanggal, gbc);
        
        gbc.gridy++;
        // Keluhan
        formPanel.add(createFormLabel("Keluhan Pasien:"), gbc);
        gbc.gridy++;
        txtKeluhan = new JTextArea(3, 15);
        styleTextArea(txtKeluhan);
        formPanel.add(new JScrollPane(txtKeluhan), gbc);
        
        gbc.gridy++;
        // Diagnosa
        formPanel.add(createFormLabel("Diagnosa Medis:"), gbc);
        gbc.gridy++;
        txtDiagnosa = new JTextArea(3, 15);
        styleTextArea(txtDiagnosa);
        formPanel.add(new JScrollPane(txtDiagnosa), gbc);
        
        // Buttons
        gbc.gridy++;
        gbc.insets = new Insets(15, 6, 6, 6);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBackground(COLOR_CARD);
        
        btnTambah = new JButton("Catat Kunjungan");
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
        
        JLabel tableTitle = new JLabel("Riwayat Kunjungan Pasien");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        // Setup Table
        String[] columns = {"ID", "Pasien", "Dokter Pemeriksa", "Waktu Kunjungan", "Keluhan", "Diagnosa"};
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
        
        // Initial form state
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
    
    private void styleTextArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(COLOR_INPUT_BG);
        area.setForeground(COLOR_TEXT);
        area.setCaretColor(COLOR_TEXT);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    private void styleComboBox(JComboBox<ComboItem> combo) {
        combo.setBackground(COLOR_INPUT_BG);
        combo.setForeground(COLOR_TEXT);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
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
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    // UI Helpers & controllers bridging
    
    public void setFormEnabled(boolean enabled) {
        cbPasien.setEnabled(enabled);
        cbDokter.setEnabled(enabled);
        txtTanggal.setEnabled(enabled);
        txtKeluhan.setEnabled(enabled);
        txtDiagnosa.setEnabled(enabled);
    }
    
    public void clearForm() {
        if (cbPasien.getItemCount() > 0) cbPasien.setSelectedIndex(0);
        if (cbDokter.getItemCount() > 0) cbDokter.setSelectedIndex(0);
        txtTanggal.setText("");
        txtKeluhan.setText("");
        txtDiagnosa.setText("");
        table.clearSelection();
    }
    
    public void fillForm(ComboItem pasienItem, ComboItem dokterItem, String tgl, String keluhan, String diagnosa) {
        cbPasien.setSelectedItem(pasienItem);
        cbDokter.setSelectedItem(dokterItem);
        txtTanggal.setText(tgl);
        txtKeluhan.setText(keluhan);
        txtDiagnosa.setText(diagnosa);
    }
    
    public void setPasienList(ComboItem[] items) {
        cbPasien.removeAllItems();
        for (ComboItem item : items) {
            cbPasien.addItem(item);
        }
    }
    
    public void setDokterList(ComboItem[] items) {
        cbDokter.removeAllItems();
        for (ComboItem item : items) {
            cbDokter.addItem(item);
        }
    }
    
    // Getters for form inputs
    public ComboItem getSelectedPasien() { return (ComboItem) cbPasien.getSelectedItem(); }
    public ComboItem getSelectedDokter() { return (ComboItem) cbDokter.getSelectedItem(); }
    public String getTanggalInput() { return txtTanggal.getText().trim(); }
    public String getKeluhanInput() { return txtKeluhan.getText().trim(); }
    public String getDiagnosaInput() { return txtDiagnosa.getText().trim(); }
    
    public String getCariInput() { return txtCari.getText().trim(); }
    
    // Setters
    public void setTanggalInput(String val) { txtTanggal.setText(val); }
    
    // Listeners
    public void addTambahListener(ActionListener l) { btnTambah.addActionListener(l); }
    public void addSimpanListener(ActionListener l) { btnSimpan.addActionListener(l); }
    public void addHapusListener(ActionListener l) { btnHapus.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    public void addCariListener(ActionListener l) { btnCari.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter l) { table.addMouseListener(l); }
    
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
