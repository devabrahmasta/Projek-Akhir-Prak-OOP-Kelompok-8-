package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class ResepView extends JPanel {
    
    
    private JLabel lblKunjungan;
    private JComboBox<ComboItem> cbObat;
    private JTextField txtJumlah;
    private JTextField txtDosis;
    
    private JButton btnTambahItem;
    private JTable tabelDetailResep;
    private DefaultTableModel tabelDetailModel;
    
    private JButton btnSimpanResep;
    private JButton btnBatal;
    
    
    private JTable tabelRiwayatResep;
    private DefaultTableModel tabelRiwayatModel;
    
    
    private JLabel lblStatus;
    
    
    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); 
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);
    
    public ResepView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Manajemen Resep Obat");
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
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);
        
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel formTitle = new JLabel("Form Resep");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        formPanel.add(formTitle, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("No Kunjungan Aktif:"), gbc);
        gbc.gridy++;
        
        lblKunjungan = new JLabel("-");
        lblKunjungan.setFont(new Font("Poppins", Font.BOLD, 13));
        lblKunjungan.setForeground(COLOR_PRIMARY);
        JPanel kunjunganWrapper = new JPanel(new BorderLayout());
        kunjunganWrapper.setBackground(COLOR_INPUT_BG);
        kunjunganWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        kunjunganWrapper.add(lblKunjungan, BorderLayout.WEST);
        formPanel.add(kunjunganWrapper, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Pilih Obat:"), gbc);
        gbc.gridy++;
        cbObat = new JComboBox<>();
        styleComboBox(cbObat);
        formPanel.add(cbObat, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Jumlah:"), gbc);
        gbc.gridy++;
        txtJumlah = new JTextField();
        styleTextField(txtJumlah);
        formPanel.add(txtJumlah, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Dosis (misal: 3x1 sesudah makan):"), gbc);
        gbc.gridy++;
        txtDosis = new JTextField();
        styleTextField(txtDosis);
        formPanel.add(txtDosis, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 6, 6, 6);
        btnTambahItem = new JButton("Tambah Obat");
        styleRoundedButton(btnTambahItem, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        formPanel.add(btnTambahItem, gbc);
        
        
        JPanel detailWrapper = new JPanel(new BorderLayout(0, 5));
        detailWrapper.setOpaque(false);
        
        JLabel detailTitle = new JLabel("Detail Obat");
        detailTitle.setFont(new Font("Poppins", Font.BOLD, 13));
        detailTitle.setForeground(COLOR_TEXT);
        detailWrapper.add(detailTitle, BorderLayout.NORTH);
        
        String[] detailCols = {"ID Obat", "Nama Obat", "Jumlah", "Dosis", "Aksi (Hapus)"};
        tabelDetailModel = new DefaultTableModel(detailCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelDetailResep = new JTable(tabelDetailModel);
        styleTable(tabelDetailResep);
        
        
        tabelDetailResep.getColumnModel().getColumn(0).setMinWidth(0);
        tabelDetailResep.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelDetailResep.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollDetail = new JScrollPane(tabelDetailResep);
        scrollDetail.getViewport().setBackground(COLOR_CARD);
        scrollDetail.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollDetail.setPreferredSize(new Dimension(0, 150));
        detailWrapper.add(scrollDetail, BorderLayout.CENTER);
        
        
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.setOpaque(false);
        
        btnSimpanResep = new JButton("Simpan Resep");
        styleRoundedButton(btnSimpanResep, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        
        btnBatal = new JButton("Batal");
        styleRoundedButton(btnBatal, new Color(149, 165, 166), new Color(120, 140, 140));
        
        bottomPanel.add(btnSimpanResep);
        bottomPanel.add(btnBatal);
        
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        leftPanel.add(detailWrapper, BorderLayout.CENTER);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        formWrapper.add(leftPanel, BorderLayout.CENTER);
        
        
        
        
        JPanel rightWrapper = createRoundedWrapper();
        
        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel tableTitle = new JLabel("Riwayat Resep Pasien");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        topTablePanel.add(tableTitle, BorderLayout.WEST);
        
        lblStatus = new JLabel("Status: Siap");
        lblStatus.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblStatus.setForeground(COLOR_TEXT_MUTED);
        topTablePanel.add(lblStatus, BorderLayout.EAST);
        
        rightWrapper.add(topTablePanel, BorderLayout.NORTH);
        
        String[] riwayatCols = {"No. Resep", "Tanggal Kunjungan", "Jumlah Item Obat"};
        tabelRiwayatModel = new DefaultTableModel(riwayatCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelRiwayatResep = new JTable(tabelRiwayatModel);
        styleTable(tabelRiwayatResep);
        
        JScrollPane scrollRiwayat = new JScrollPane(tabelRiwayatResep);
        scrollRiwayat.getViewport().setBackground(COLOR_CARD);
        scrollRiwayat.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        rightWrapper.add(scrollRiwayat, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(formWrapper);
        splitPane.setRightComponent(rightWrapper);
        add(splitPane, BorderLayout.CENTER);
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
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    
    
    public void setLblKunjungan(String text) {
        lblKunjungan.setText(text);
    }
    
    public void setObatList(ComboItem[] items) {
        cbObat.removeAllItems();
        if (items != null) {
            for (ComboItem item : items) {
                cbObat.addItem(item);
            }
        }
    }
    
    public ComboItem getSelectedObat() {
        return (ComboItem) cbObat.getSelectedItem();
    }
    
    public String getJumlahInput() {
        return txtJumlah.getText().trim();
    }
    
    public String getDosisInput() {
        return txtDosis.getText().trim();
    }
    
    public void clearFormObat() {
        if (cbObat.getItemCount() > 0) cbObat.setSelectedIndex(0);
        txtJumlah.setText("");
        txtDosis.setText("");
    }
    
    public void setStatusText(String text) {
        lblStatus.setText("Status: " + text);
    }
    
    public JTable getTabelDetailResep() {
        return tabelDetailResep;
    }
    
    public DefaultTableModel getTabelDetailModel() {
        return tabelDetailModel;
    }
    
    public JTable getTabelRiwayatResep() {
        return tabelRiwayatResep;
    }
    
    public DefaultTableModel getTabelRiwayatModel() {
        return tabelRiwayatModel;
    }
    
    public int getSelectedDetailResepRow() {
        return tabelDetailResep.getSelectedRow();
    }
    
    public int getSelectedRiwayatResepRow() {
        return tabelRiwayatResep.getSelectedRow();
    }
    
    
    
    public void addBtnTambahItemListener(ActionListener l) {
        btnTambahItem.addActionListener(l);
    }
    
    public void addBtnSimpanResepListener(ActionListener l) {
        btnSimpanResep.addActionListener(l);
    }
    
    public void addBtnBatalListener(ActionListener l) {
        btnBatal.addActionListener(l);
    }
    
    public void addDetailResepMouseListener(MouseAdapter m) {
        tabelDetailResep.addMouseListener(m);
    }
    
    public void addRiwayatResepMouseListener(MouseAdapter m) {
        tabelRiwayatResep.addMouseListener(m);
    }
}
