package view;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class KunjunganView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JComboBox<ComboItem> cbPasien;
    private JComboBox<ComboItem> cbDokter;
    private JTextField txtTanggal; 
    private JTextArea txtKeluhan;
    private JTextArea txtDiagnosa;
    
    private JTextField txtCari;
    private JButton btnCari;
    
    private JComboBox<String> cbSortWaktu;
    
    private JButton btnTambah;
    private JButton btnSimpan;
    private JButton btnHapus;
    private JButton btnBatal;
    
    private JLabel lblStatus;
    
    // --- COLOR PALETTE MODERN ---
    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); 
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);
    
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
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(COLOR_BG);
        
        JLabel lblCari = new JLabel("Cari Pasien/Diagnosa:");
        lblCari.setFont(new Font("Poppins", Font.PLAIN, 14));
        lblCari.setForeground(COLOR_TEXT_MUTED);
        searchPanel.add(lblCari);
        
        txtCari = new JTextField(15);
        styleTextField(txtCari);
        searchPanel.add(txtCari);
        
        btnCari = new JButton("Cari");
        styleRoundedButton(btnCari, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        searchPanel.add(btnCari);
        
        headerPanel.add(searchPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380); 
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);
        
        // --- PANEL KIRI (FORM) ---
        JPanel formWrapper = createRoundedWrapper();
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel formTitle = new JLabel("Form Catatan Kunjungan");
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
        formPanel.add(createFormLabel("Dokter Pemeriksa:"), gbc);
        gbc.gridy++;
        cbDokter = new JComboBox<>();
        styleComboBox(cbDokter);
        formPanel.add(cbDokter, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Waktu (YYYY-MM-DD HH:mm:ss):"), gbc);
        gbc.gridy++;
        txtTanggal = new JTextField();
        styleTextField(txtTanggal);
        formPanel.add(txtTanggal, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Keluhan Pasien:"), gbc);
        gbc.gridy++;
        txtKeluhan = new JTextArea(3, 15);
        txtKeluhan.setLineWrap(true);
        txtKeluhan.setWrapStyleWord(true);
        txtKeluhan.setBackground(COLOR_INPUT_BG);
        txtKeluhan.setForeground(COLOR_TEXT);
        txtKeluhan.setCaretColor(COLOR_TEXT);
        txtKeluhan.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtKeluhan.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        JScrollPane scrollKeluhan = new JScrollPane(txtKeluhan);
        scrollKeluhan.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(scrollKeluhan, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Diagnosa Medis:"), gbc);
        gbc.gridy++;
        txtDiagnosa = new JTextArea(3, 15);
        txtDiagnosa.setLineWrap(true);
        txtDiagnosa.setWrapStyleWord(true);
        txtDiagnosa.setBackground(COLOR_INPUT_BG);
        txtDiagnosa.setForeground(COLOR_TEXT);
        txtDiagnosa.setCaretColor(COLOR_TEXT);
        txtDiagnosa.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtDiagnosa.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        
        JScrollPane scrollDiagnosa = new JScrollPane(txtDiagnosa);
        scrollDiagnosa.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(scrollDiagnosa, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 6, 6, 6);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBackground(COLOR_CARD);
        
        btnTambah = new JButton("Catat");
        styleRoundedButton(btnTambah, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        
        btnSimpan = new JButton("Simpan");
        styleRoundedButton(btnSimpan, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        
        btnHapus = new JButton("Hapus");
        styleRoundedButton(btnHapus, new Color(231, 76, 60), new Color(200, 60, 50));
        
        btnBatal = new JButton("Batal");
        styleRoundedButton(btnBatal, new Color(149, 165, 166), new Color(120, 140, 140));
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnBatal);
        formPanel.add(buttonPanel, gbc);
        
        gbc.gridy++;
        lblStatus = new JLabel("Status: Siap");
        lblStatus.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblStatus.setForeground(COLOR_TEXT_MUTED);
        formPanel.add(lblStatus, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // REVISI: Scrollbar disembunyikan (NEVER), namun fungsi scroll mouse wheel tetap aktif
        formScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); 
        
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formScrollPane.getViewport().setBackground(COLOR_CARD);
        
        formWrapper.add(formScrollPane, BorderLayout.CENTER);
        
        // --- PANEL KANAN (TABEL & FILTER) ---
        JPanel tableWrapper = createRoundedWrapper();
        
        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitle = new JLabel("Riwayat Kunjungan");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        topTablePanel.add(tableTitle, BorderLayout.WEST);
        
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        sortPanel.setOpaque(false);
        JLabel lblSort = new JLabel("Filter Waktu:");
        lblSort.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSort.setForeground(COLOR_TEXT_MUTED);
        sortPanel.add(lblSort);
        
        cbSortWaktu = new JComboBox<>(new String[]{"Paling Baru", "Paling Lama", "24 Jam Terakhir", "1 Bulan Terakhir"});
        styleComboBox(cbSortWaktu);
        cbSortWaktu.setPreferredSize(new Dimension(140, 30));
        sortPanel.add(cbSortWaktu);
        
        topTablePanel.add(sortPanel, BorderLayout.EAST);
        tableWrapper.add(topTablePanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Pasien", "Dokter Pemeriksa", "Waktu Kunjungan", "Keluhan", "Diagnosa"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isEditing = btnSimpan.isEnabled(); 
                boolean hasSelection = table.getSelectedRow() != -1;
                
                btnHapus.setEnabled(hasSelection && !isEditing);
                btnBatal.setEnabled(isEditing || hasSelection);
            }
        });
        
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(formWrapper);
        splitPane.setRightComponent(tableWrapper);
        add(splitPane, BorderLayout.CENTER);
        
        setFormEnabled(false);
        setButtonsState(false);
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
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
    
    private void styleComboBox(JComboBox combo) {
        combo.setBackground(COLOR_INPUT_BG);
        combo.setForeground(COLOR_TEXT);
        combo.setFont(new Font("Poppins", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
    }
    
    private void styleRoundedButton(JButton button, Color bgColor, Color hoverColor) {
        final Color COLOR_DISABLED_BG = new Color(235, 235, 235);
        final Color COLOR_DISABLED_TEXT = new Color(160, 160, 160);

        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installDefaults(AbstractButton b) {
                super.installDefaults(b);
                b.setOpaque(false);
                b.setBorderPainted(false);
                b.setFocusPainted(false);
            }

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
        table.setRowHeight(30);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("Poppins", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    // --- METHOD BAWAAN CONTROLLER ---
    
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
    
    public ComboItem getSelectedPasien() { return (ComboItem) cbPasien.getSelectedItem(); }
    public ComboItem getSelectedDokter() { return (ComboItem) cbDokter.getSelectedItem(); }
    public String getTanggalInput() { return txtTanggal.getText().trim(); }
    public String getKeluhanInput() { return txtKeluhan.getText().trim(); }
    public String getDiagnosaInput() { return txtDiagnosa.getText().trim(); }
    public String getCariInput() { return txtCari.getText().trim(); }
    
    public String getSortOption() { return (String) cbSortWaktu.getSelectedItem(); }
    public void addSortListener(ActionListener l) { cbSortWaktu.addActionListener(l); }
    
    public void setTanggalInput(String val) { txtTanggal.setText(val); }
    
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
        
        boolean hasSelection = table.getSelectedRow() != -1;
        btnHapus.setEnabled(hasSelection && !isEditing);
        btnBatal.setEnabled(isEditing || hasSelection);
    }
}