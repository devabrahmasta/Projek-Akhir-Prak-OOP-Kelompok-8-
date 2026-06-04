package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class ObatView extends JPanel {
    
    
    private JTextField txtKodeObat;
    private JTextField txtNamaObat;
    private JComboBox<String> cbKategori;
    private JTextField txtStok;
    private JComboBox<String> cbSatuan;
    private JTextField txtHarga;
    private JTextField txtStokMinimum;
    
    private JButton btnTambah;
    private JButton btnSimpan;
    private JButton btnHapus;
    private JButton btnBatal;
    
    private JLabel lblStatus;
    
    
    private JTable tabelObat;
    private DefaultTableModel tabelModel;
    
    
    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); 
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);
    
    public ObatView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Manajemen Data Obat & Stok");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380); 
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);
        
        
        
        
        JPanel formWrapper = createRoundedWrapper();
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel formTitle = new JLabel("Data Obat");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        formPanel.add(createFormLabel("Kode Obat:"), gbc);
        gbc.gridy++;
        txtKodeObat = new JTextField();
        styleTextField(txtKodeObat);
        txtKodeObat.setEditable(false); 
        txtKodeObat.setBackground(new Color(245, 245, 245));
        formPanel.add(txtKodeObat, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Nama Obat:"), gbc);
        gbc.gridy++;
        txtNamaObat = new JTextField();
        styleTextField(txtNamaObat);
        formPanel.add(txtNamaObat, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Kategori:"), gbc);
        gbc.gridy++;
        String[] kategoriArr = {"Analgesik", "Antibiotik", "Vitamin", "Antihipertensi", "Lainnya"};
        cbKategori = new JComboBox<>(kategoriArr);
        styleComboBox(cbKategori);
        formPanel.add(cbKategori, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Stok:"), gbc);
        gbc.gridy++;
        txtStok = new JTextField();
        styleTextField(txtStok);
        formPanel.add(txtStok, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Satuan:"), gbc);
        gbc.gridy++;
        String[] satuanArr = {"Tablet", "Kapsul", "Botol", "Sachet", "Ampul"};
        cbSatuan = new JComboBox<>(satuanArr);
        styleComboBox(cbSatuan);
        cbSatuan.setEditable(true); 
        formPanel.add(cbSatuan, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Harga (Rp):"), gbc);
        gbc.gridy++;
        txtHarga = new JTextField();
        styleTextField(txtHarga);
        formPanel.add(txtHarga, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Stok Minimum:"), gbc);
        gbc.gridy++;
        txtStokMinimum = new JTextField("10");
        styleTextField(txtStokMinimum);
        formPanel.add(txtStokMinimum, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 6, 6, 6);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBackground(COLOR_CARD);
        
        btnTambah = new JButton("Tambah");
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
        formScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); 
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formScrollPane.getViewport().setBackground(COLOR_CARD);
        
        formWrapper.add(formScrollPane, BorderLayout.CENTER);
        
        
        
        
        JPanel tableWrapper = createRoundedWrapper();
        
        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitle = new JLabel("Daftar Obat & Status Stok");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        topTablePanel.add(tableTitle, BorderLayout.WEST);
        
        tableWrapper.add(topTablePanel, BorderLayout.NORTH);
        
        String[] columns = {"Kode", "Nama", "Kategori", "Stok", "Satuan", "Harga", "Stok Min", "Status"};
        tabelModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tabelObat = new JTable(tabelModel);
        styleTable(tabelObat);
        
        tabelObat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isEditing = btnSimpan.isEnabled(); 
                boolean hasSelection = tabelObat.getSelectedRow() != -1;
                
                btnHapus.setEnabled(hasSelection);
                btnBatal.setEnabled(isEditing || hasSelection);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tabelObat);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(formWrapper);
        splitPane.setRightComponent(tableWrapper);
        add(splitPane, BorderLayout.CENTER);
        
        setFormEnabled(false);
        setButtonsState(false);
    }
    
    
    
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
        
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                try {
                    int stok = Integer.parseInt(table.getValueAt(row, 3).toString());
                    int stokMin = Integer.parseInt(table.getValueAt(row, 6).toString());
                    
                    boolean isKritis = (stok <= stokMin);
                    
                    
                    if (column == 7) {
                        setText(isKritis ? "Kritis" : "Aman");
                        setFont(new Font("Poppins", Font.BOLD, 12));
                    }
                    
                    if (!isSelected) {
                        if (isKritis) {
                            c.setBackground(new Color(255, 240, 240)); 
                        } else {
                            c.setBackground(COLOR_CARD); 
                        }
                        
                        if (column == 7) {
                            c.setForeground(isKritis ? Color.RED : new Color(39, 174, 96)); 
                        } else {
                            c.setForeground(COLOR_TEXT);
                        }
                    } else {
                        
                        c.setBackground(COLOR_PRIMARY);
                        c.setForeground(Color.WHITE);
                    }
                } catch (Exception e) {
                    if (!isSelected) {
                        c.setBackground(COLOR_CARD);
                        c.setForeground(COLOR_TEXT);
                    }
                }
                
                return c;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
    
    
    
    public void setFormEnabled(boolean enabled) {
        
        
        txtNamaObat.setEnabled(enabled);
        cbKategori.setEnabled(enabled);
        txtStok.setEnabled(enabled);
        cbSatuan.setEnabled(enabled);
        txtHarga.setEnabled(enabled);
        txtStokMinimum.setEnabled(enabled);
    }
    
    public void clearForm() {
        txtKodeObat.setText("");
        txtNamaObat.setText("");
        if (cbKategori.getItemCount() > 0) cbKategori.setSelectedIndex(0);
        txtStok.setText("");
        if (cbSatuan.getItemCount() > 0) cbSatuan.setSelectedIndex(0);
        txtHarga.setText("");
        txtStokMinimum.setText("10");
        tabelObat.clearSelection();
    }
    
    public void setButtonsState(boolean isEditing) {
        btnTambah.setEnabled(!isEditing);
        btnSimpan.setEnabled(isEditing);
        
        boolean hasSelection = tabelObat.getSelectedRow() != -1;
        btnHapus.setEnabled(hasSelection);
        btnBatal.setEnabled(isEditing || hasSelection);
    }
    
    
    
    public String getKodeObatInput() { return txtKodeObat.getText().trim(); }
    public void setKodeObatInput(String text) { txtKodeObat.setText(text); }
    
    public String getNamaObatInput() { return txtNamaObat.getText().trim(); }
    public void setNamaObatInput(String text) { txtNamaObat.setText(text); }
    
    public String getKategoriInput() { return cbKategori.getSelectedItem() != null ? cbKategori.getSelectedItem().toString() : ""; }
    public void setKategoriInput(String text) { cbKategori.setSelectedItem(text); }
    
    public String getStokInput() { return txtStok.getText().trim(); }
    public void setStokInput(String text) { txtStok.setText(text); }
    
    public String getSatuanInput() { return cbSatuan.getSelectedItem() != null ? cbSatuan.getSelectedItem().toString() : ""; }
    public void setSatuanInput(String text) { cbSatuan.setSelectedItem(text); }
    
    public String getHargaInput() { return txtHarga.getText().trim(); }
    public void setHargaInput(String text) { txtHarga.setText(text); }
    
    public String getStokMinimumInput() { return txtStokMinimum.getText().trim(); }
    public void setStokMinimumInput(String text) { txtStokMinimum.setText(text); }
    
    public void setStatusText(String text) { lblStatus.setText("Status: " + text); }
    
    public DefaultTableModel getTableModel() { return tabelModel; }
    public JTable getTable() { return tabelObat; }
    
    public String getSelectedKode() {
        int row = tabelObat.getSelectedRow();
        if (row != -1) {
            return tabelObat.getValueAt(row, 0).toString();
        }
        return null;
    }
    
    
    
    public void addTambahListener(ActionListener l) { btnTambah.addActionListener(l); }
    public void addSimpanListener(ActionListener l) { btnSimpan.addActionListener(l); }
    public void addHapusListener(ActionListener l) { btnHapus.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    
    public void addTableMouseListener(MouseAdapter m) { tabelObat.addMouseListener(m); }
}
