package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AntrianView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JComboBox<ComboItem> cbPasien;
    private JComboBox<ComboItem> cbDokter;
    private JTextField txtTanggal; // Format: YYYY-MM-DD
    private JComboBox<String> cbStatus;
    
    // Filters
    private JComboBox<ComboItem> cbFilterDokter;
    private JTextField txtFilterTanggal;
    private JButton btnFilter;
    
    private JButton btnTambah;
    private JButton btnPanggil;
    private JButton btnSelesai;
    private JButton btnBatal;
    private JButton btnHapus;
    
    private JLabel lblStatus;
    private JLabel lblAutoRefresh;
    
    // Style constants
    private final Color COLOR_BG = new Color(18, 18, 20);
    private final Color COLOR_CARD = new Color(34, 34, 40);
    private final Color COLOR_PRIMARY = new Color(124, 77, 255); // Electric Violet
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_TEXT_MUTED = new Color(160, 160, 170);
    private final Color COLOR_BORDER = new Color(45, 45, 52);
    private final Color COLOR_INPUT_BG = new Color(26, 26, 30);
    
    public AntrianView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Manajemen Antrian Klinik");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        titlePanel.add(titleLabel);
        
        lblAutoRefresh = new JLabel("● Auto-refresh: Aktif (10s)");
        lblAutoRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAutoRefresh.setForeground(new Color(76, 175, 80)); // Green dot
        titlePanel.add(lblAutoRefresh);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Filters Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(COLOR_BG);
        
        JLabel lblFilDokter = new JLabel("Dokter:");
        lblFilDokter.setForeground(COLOR_TEXT_MUTED);
        filterPanel.add(lblFilDokter);
        
        cbFilterDokter = new JComboBox<>();
        cbFilterDokter.setPreferredSize(new Dimension(150, 26));
        styleComboBox(cbFilterDokter);
        filterPanel.add(cbFilterDokter);
        
        JLabel lblFilTgl = new JLabel("Tgl (YYYY-MM-DD):");
        lblFilTgl.setForeground(COLOR_TEXT_MUTED);
        filterPanel.add(lblFilTgl);
        
        txtFilterTanggal = new JTextField(8);
        styleTextField(txtFilterTanggal);
        filterPanel.add(txtFilterTanggal);
        
        btnFilter = new JButton("Filter");
        styleButton(btnFilter, COLOR_PRIMARY);
        filterPanel.add(btnFilter);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JLabel formTitle = new JLabel("Registrasi Antrian Baru");
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
        formPanel.add(createFormLabel("Pilih Dokter / Poli:"), gbc);
        gbc.gridy++;
        cbDokter = new JComboBox<>();
        styleComboBox(cbDokter);
        formPanel.add(cbDokter, gbc);
        
        gbc.gridy++;
        // Tanggal
        formPanel.add(createFormLabel("Tanggal Antrian (YYYY-MM-DD):"), gbc);
        gbc.gridy++;
        txtTanggal = new JTextField();
        styleTextField(txtTanggal);
        formPanel.add(txtTanggal, gbc);
        
        gbc.gridy++;
        // Status
        formPanel.add(createFormLabel("Status:"), gbc);
        gbc.gridy++;
        cbStatus = new JComboBox<>(new String[]{"Menunggu", "Dipanggil", "Selesai", "Batal"});
        cbStatus.setBackground(COLOR_INPUT_BG);
        cbStatus.setForeground(COLOR_TEXT);
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbStatus.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(cbStatus, gbc);
        
        // Action Buttons
        gbc.gridy++;
        gbc.insets = new Insets(15, 8, 8, 8);
        
        btnTambah = new JButton("Tambah Ke Antrian");
        styleButton(btnTambah, COLOR_PRIMARY);
        formPanel.add(btnTambah, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(4, 8, 8, 8);
        JPanel gridStatusPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        gridStatusPanel.setBackground(COLOR_CARD);
        
        btnPanggil = new JButton("Panggil");
        styleButton(btnPanggil, new Color(41, 121, 255)); // Bright Blue
        btnSelesai = new JButton("Selesai");
        styleButton(btnSelesai, new Color(46, 125, 50)); // Green
        btnBatal = new JButton("Batal");
        styleButton(btnBatal, new Color(117, 117, 117)); // Grey
        btnHapus = new JButton("Hapus");
        styleButton(btnHapus, new Color(198, 40, 40)); // Red
        
        gridStatusPanel.add(btnPanggil);
        gridStatusPanel.add(btnSelesai);
        gridStatusPanel.add(btnBatal);
        gridStatusPanel.add(btnHapus);
        formPanel.add(gridStatusPanel, gbc);
        
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
        
        JLabel tableTitle = new JLabel("Daftar Antrian Aktif");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        // Setup JTable
        String[] columns = {"ID", "No. Antrian", "Nama Pasien", "Dokter Pemeriksa", "Tanggal", "Status"};
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
        
        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(tablePanel);
        add(splitPane, BorderLayout.CENTER);
        
        // Initial states
        setSelectionButtonsEnabled(false);
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
    
    public void setPasienList(ComboItem[] items) {
        cbPasien.removeAllItems();
        for (ComboItem item : items) {
            cbPasien.addItem(item);
        }
    }
    
    public void setDokterList(ComboItem[] items) {
        cbDokter.removeAllItems();
        cbFilterDokter.removeAllItems();
        
        // Add "Semua Dokter" to filter
        cbFilterDokter.addItem(new ComboItem(0, "-- Semua Dokter --"));
        
        for (ComboItem item : items) {
            cbDokter.addItem(item);
            cbFilterDokter.addItem(item);
        }
    }
    
    public void clearForm() {
        if (cbPasien.getItemCount() > 0) cbPasien.setSelectedIndex(0);
        if (cbDokter.getItemCount() > 0) cbDokter.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        table.clearSelection();
    }
    
    public void fillForm(ComboItem pasienItem, ComboItem dokterItem, String tgl, String status) {
        cbPasien.setSelectedItem(pasienItem);
        cbDokter.setSelectedItem(dokterItem);
        txtTanggal.setText(tgl);
        cbStatus.setSelectedItem(status);
    }
    
    // Input Getters
    public ComboItem getSelectedPasien() { return (ComboItem) cbPasien.getSelectedItem(); }
    public ComboItem getSelectedDokter() { return (ComboItem) cbDokter.getSelectedItem(); }
    public String getTanggalInput() { return txtTanggal.getText().trim(); }
    public String getStatusInput() { return (String) cbStatus.getSelectedItem(); }
    
    // Filter Getters
    public ComboItem getFilterDokter() { return (ComboItem) cbFilterDokter.getSelectedItem(); }
    public String getFilterTanggal() { return txtFilterTanggal.getText().trim(); }
    
    // Setters
    public void setTanggalInput(String val) { txtTanggal.setText(val); }
    public void setFilterTanggal(String val) { txtFilterTanggal.setText(val); }
    
    // Action Listeners
    public void addTambahListener(ActionListener l) { btnTambah.addActionListener(l); }
    public void addPanggilListener(ActionListener l) { btnPanggil.addActionListener(l); }
    public void addSelesaiListener(ActionListener l) { btnSelesai.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    public void addHapusListener(ActionListener l) { btnHapus.addActionListener(l); }
    public void addFilterListener(ActionListener l) { btnFilter.addActionListener(l); }
    
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
    
    public void setSelectionButtonsEnabled(boolean enabled) {
        btnPanggil.setEnabled(enabled);
        btnSelesai.setEnabled(enabled);
        btnBatal.setEnabled(enabled);
        btnHapus.setEnabled(enabled);
    }
    
    public void setAutoRefreshStatus(boolean active) {
        if (active) {
            lblAutoRefresh.setText("● Auto-refresh: Aktif (10s)");
            lblAutoRefresh.setForeground(new Color(76, 175, 80));
        } else {
            lblAutoRefresh.setText("○ Auto-refresh: Mati");
            lblAutoRefresh.setForeground(new Color(198, 40, 40));
        }
    }
}
