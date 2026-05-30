package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class AntrianView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JComboBox<ComboItem> cbPasien;
    private JComboBox<ComboItem> cbDokter;
    private JTextField txtTanggal; 
    private JComboBox<String> cbStatus;
    
    private JComboBox<ComboItem> cbFilterDokter;
    private JTextField txtFilterTanggal;
    private JButton btnFilter;
    
    private JButton btnTambah;
    private JButton btnPanggil;
    private JButton btnSelesai;
    private JButton btnBatal;
    private JButton btnHapus;
    
    private JLabel lblStatus;
    private JLabel lblSelectedInfo; // UX Baru: Banner Info Pasien Aktif
    
    // --- COLOR PALETTE MODERN ---
    private final Color COLOR_BG = new Color(240, 246, 246); // Surface
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); // Tosca
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);
    
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
        
        JLabel titleLabel = new JLabel("Manajemen Antrian Klinik");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(COLOR_BG);
        
        JLabel lblFilDokter = new JLabel("Dokter:");
        lblFilDokter.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblFilDokter.setForeground(COLOR_TEXT_MUTED);
        filterPanel.add(lblFilDokter);
        
        cbFilterDokter = new JComboBox<>();
        cbFilterDokter.setPreferredSize(new Dimension(180, 32));
        styleComboBox(cbFilterDokter);
        filterPanel.add(cbFilterDokter);
        
        JLabel lblFilTgl = new JLabel("Tgl (YYYY-MM-DD):");
        lblFilTgl.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblFilTgl.setForeground(COLOR_TEXT_MUTED);
        filterPanel.add(lblFilTgl);
        
        txtFilterTanggal = new JTextField(10);
        styleTextField(txtFilterTanggal);
        filterPanel.add(txtFilterTanggal);
        
        btnFilter = new JButton("Filter");
        styleRoundedButton(btnFilter, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        filterPanel.add(btnFilter);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);
        
        // --- PANEL KIRI (FORM TAMBAH ANTRIAN) ---
        JPanel formWrapper = createRoundedWrapper();
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel formTitle = new JLabel("Registrasi Antrian Baru");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        formPanel.add(createFormLabel("Pasien:"), gbc);
        gbc.gridy++;
        cbPasien = new JComboBox<>();
        styleComboBox(cbPasien);
        formPanel.add(cbPasien, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Pilih Dokter / Poli:"), gbc);
        gbc.gridy++;
        cbDokter = new JComboBox<>();
        styleComboBox(cbDokter);
        formPanel.add(cbDokter, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Tanggal Antrian (YYYY-MM-DD):"), gbc);
        gbc.gridy++;
        txtTanggal = new JTextField();
        styleTextField(txtTanggal);
        formPanel.add(txtTanggal, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Status Awal:"), gbc);
        gbc.gridy++;
        cbStatus = new JComboBox<>(new String[]{"Menunggu", "Dipanggil", "Selesai", "Batal"});
        styleComboBox(cbStatus);
        formPanel.add(cbStatus, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 8, 8, 8);
        
        btnTambah = new JButton("Daftarkan ke Antrian");
        styleRoundedButton(btnTambah, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        formPanel.add(btnTambah, gbc);
        
        gbc.gridy++;
        lblStatus = new JLabel("Status: Siap");
        lblStatus.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblStatus.setForeground(COLOR_TEXT_MUTED);
        formPanel.add(lblStatus, gbc);
        
        // Mencegah form overflow
        gbc.gridy++;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        formScrollPane.getViewport().setBackground(COLOR_CARD);
        
        formWrapper.add(formScrollPane, BorderLayout.CENTER);
        
        // --- PANEL KANAN (KONTROL & TABEL ANTRIAN) ---
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);
        
        // UX BARU: Banner Kontrol Antrian Aktif
        JPanel controlBanner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 245, 243)); // Latar belakang tosca super pucat
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Garis aksen kiri
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, 8, getHeight(), 20, 20);
                g2.fillRect(5, 0, 5, getHeight());
                g2.dispose();
            }
        };
        controlBanner.setOpaque(false);
        controlBanner.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 20));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        infoPanel.setOpaque(false);
        JLabel lblControlTitle = new JLabel("KONTROL ANTRIAN TERPILIH");
        lblControlTitle.setFont(new Font("Poppins", Font.BOLD, 12));
        lblControlTitle.setForeground(COLOR_PRIMARY);
        
        lblSelectedInfo = new JLabel("Pilih antrian dari tabel...");
        lblSelectedInfo.setFont(new Font("Poppins", Font.BOLD, 18));
        lblSelectedInfo.setForeground(COLOR_TEXT);
        
        infoPanel.add(lblControlTitle);
        infoPanel.add(lblSelectedInfo);
        controlBanner.add(infoPanel, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setOpaque(false);
        
        btnPanggil = new JButton("Panggil Pasien");
        styleRoundedButton(btnPanggil, new Color(41, 121, 255), new Color(31, 100, 220)); 
        
        btnSelesai = new JButton("Selesai Diperiksa");
        styleRoundedButton(btnSelesai, COLOR_PRIMARY, COLOR_PRIMARY_HOVER); 
        
        btnBatal = new JButton("Batal");
        styleRoundedButton(btnBatal, new Color(149, 165, 166), new Color(120, 140, 140)); 
        
        btnHapus = new JButton("Hapus");
        styleRoundedButton(btnHapus, new Color(231, 76, 60), new Color(200, 60, 50)); 
        
        actionPanel.add(btnPanggil);
        actionPanel.add(btnSelesai);
        actionPanel.add(btnBatal);
        actionPanel.add(btnHapus);
        controlBanner.add(actionPanel, BorderLayout.EAST);
        
        rightPanel.add(controlBanner, BorderLayout.NORTH);
        
        // Tabel Antrian
        JPanel tableWrapper = createRoundedWrapper();
        JLabel tableTitle = new JLabel("Daftar Antrian Hari Ini");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"ID", "No. Antrian", "Nama Pasien", "Dokter Pemeriksa", "Tanggal", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        
        // Sembunyikan kolom ID secara visual
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        
        rightPanel.add(tableWrapper, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(formWrapper);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
        
        setSelectionButtonsEnabled(false);
    }
    
    // --- HELPER METODE UI MODERN ---
    
    private JPanel createRoundedWrapper() {
        JPanel wrapper = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return wrapper;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Poppins", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MUTED);
        return label;
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(COLOR_INPUT_BG);
        field.setForeground(COLOR_TEXT);
        field.setCaretColor(COLOR_TEXT);
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
    
    private void styleComboBox(JComboBox combo) {
        combo.setBackground(COLOR_INPUT_BG);
        combo.setForeground(COLOR_TEXT);
        combo.setFont(new Font("Poppins", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
    }
    
    private void styleRoundedButton(JButton button, Color bgColor, Color hoverColor) {
        final Color COLOR_DISABLED_BG = new Color(220, 220, 220);
        final Color COLOR_DISABLED_TEXT = new Color(160, 160, 160);

        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                Color bg;
                if (!b.isEnabled()) {
                    bg = COLOR_DISABLED_BG;
                } else if (model.isPressed()) {
                    bg = hoverColor.darker();
                } else if (model.isRollover()) {
                    bg = hoverColor;
                } else {
                    bg = bgColor;
                }

                g2.setColor(bg);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                g2.dispose();
                super.paint(g, c);
            }

            @Override
            protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(b.getFont());
                g2.setColor(b.isEnabled() ? Color.WHITE : COLOR_DISABLED_TEXT);

                FontMetrics fm = g2.getFontMetrics();
                int x = textRect.x;
                int y = textRect.y + fm.getAscent();
                g2.drawString(text, x, y);
                g2.dispose();
            }
        });
    }
    
    private void styleTable(JTable table) {
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_BORDER);
        table.setShowGrid(true);
        table.setFont(new Font("Poppins", Font.PLAIN, 12));
        table.setRowHeight(35); // Baris sedikit lebih lega
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("Poppins", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    // --- METHOD BAWAAN CONTROLLER ---
    
    public void setPasienList(ComboItem[] items) {
        cbPasien.removeAllItems();
        for (ComboItem item : items) { cbPasien.addItem(item); }
    }
    
    public void setDokterList(ComboItem[] items) {
        cbDokter.removeAllItems();
        cbFilterDokter.removeAllItems();
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
        setSelectionButtonsEnabled(false);
    }
    
    public void fillForm(ComboItem pasienItem, ComboItem dokterItem, String tgl, String status) {
        cbPasien.setSelectedItem(pasienItem);
        cbDokter.setSelectedItem(dokterItem);
        txtTanggal.setText(tgl);
        cbStatus.setSelectedItem(status);
    }
    
    public ComboItem getSelectedPasien() { return (ComboItem) cbPasien.getSelectedItem(); }
    public ComboItem getSelectedDokter() { return (ComboItem) cbDokter.getSelectedItem(); }
    public String getTanggalInput() { return txtTanggal.getText().trim(); }
    public String getStatusInput() { return (String) cbStatus.getSelectedItem(); }
    
    public ComboItem getFilterDokter() { return (ComboItem) cbFilterDokter.getSelectedItem(); }
    public String getFilterTanggal() { return txtFilterTanggal.getText().trim(); }
    
    public void setTanggalInput(String val) { txtTanggal.setText(val); }
    public void setFilterTanggal(String val) { txtFilterTanggal.setText(val); }
    
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
    
    // UX PINTAR: Tombol menyesuaikan status dari tabel
    public void setSelectionButtonsEnabled(boolean enabled) {
        if (!enabled) {
            lblSelectedInfo.setText("Pilih antrian dari tabel...");
            btnPanggil.setEnabled(false);
            btnSelesai.setEnabled(false);
            btnBatal.setEnabled(false);
            btnHapus.setEnabled(false);
        } else {
            int row = table.getSelectedRow();
            if (row != -1) {
                String nama = table.getValueAt(row, 2).toString();
                String status = table.getValueAt(row, 5).toString();
                
                lblSelectedInfo.setText(nama + " (" + status + ")");
                
                // Logika Aktifasi Tombol
                btnPanggil.setEnabled(status.equals("Menunggu"));
                btnSelesai.setEnabled(status.equals("Dipanggil"));
                btnBatal.setEnabled(status.equals("Menunggu") || status.equals("Dipanggil"));
                btnHapus.setEnabled(true);
            }
        }
    }
}