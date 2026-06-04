package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;

public class TagihanView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JComboBox<ComboItem> cbKunjungan;
    private JLabel lblAutoNamaPasien;
    private JLabel lblAutoNamaDokter;
    private JLabel lblAutoTanggal;
    
    private JLabel lblSubtotalKonsultasi;
    private JLabel lblSubtotalObat;
    private JLabel lblPotongan;
    private JComboBox<String> cbJenisPembayaran;
    private JLabel lblTotalBesar;
    
    private JButton btnProsesBayar;
    private JButton btnBatal;

    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174);
    private final Color COLOR_TEXT = new Color(51, 51, 51);
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(224, 224, 224);

    public TagihanView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initHeader();
        initContent();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        JLabel titleLabel = new JLabel("Manajemen Pembayaran & Tagihan");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);

        
        JPanel formWrapper = createRoundedWrapper();
        formWrapper.setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel formTitle = new JLabel("Form Tagihan");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        formPanel.add(formTitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Pilih Kunjungan (Belum Lunas):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        cbKunjungan = new JComboBox<>();
        styleComboBox(cbKunjungan);
        formPanel.add(cbKunjungan, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Data Pasien:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblAutoNamaPasien = new JLabel("-");
        lblAutoNamaPasien.setForeground(COLOR_TEXT);
        lblAutoNamaPasien.setFont(new Font("Poppins", Font.PLAIN, 12));
        formPanel.add(lblAutoNamaPasien, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Dokter Pemeriksa:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblAutoNamaDokter = new JLabel("-");
        lblAutoNamaDokter.setForeground(COLOR_TEXT);
        lblAutoNamaDokter.setFont(new Font("Poppins", Font.PLAIN, 12));
        formPanel.add(lblAutoNamaDokter, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Tanggal Kunjungan:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblAutoTanggal = new JLabel("-");
        lblAutoTanggal.setForeground(COLOR_TEXT);
        lblAutoTanggal.setFont(new Font("Poppins", Font.PLAIN, 12));
        formPanel.add(lblAutoTanggal, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Subtotal Konsultasi:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblSubtotalKonsultasi = new JLabel("Rp 0,00");
        lblSubtotalKonsultasi.setFont(new Font("Poppins", Font.PLAIN, 12));
        formPanel.add(lblSubtotalKonsultasi, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Subtotal Obat (Resep):"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblSubtotalObat = new JLabel("Rp 0,00");
        lblSubtotalObat.setFont(new Font("Poppins", Font.PLAIN, 12));
        formPanel.add(lblSubtotalObat, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Potongan / Diskon:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblPotongan = new JLabel("Rp 0,00");
        lblPotongan.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblPotongan.setForeground(new Color(231, 76, 60)); // Red color for discount/deduction
        formPanel.add(lblPotongan, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("Metode Pembayaran:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        cbJenisPembayaran = new JComboBox<>(new String[]{"Tunai", "BPJS", "Asuransi Swasta"});
        styleComboBox(cbJenisPembayaran);
        formPanel.add(cbJenisPembayaran, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 8, 8);
        formPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 8, 0, 8);
        formPanel.add(createFormLabel("TOTAL PEMBAYARAN:"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(0, 8, 15, 8);
        lblTotalBesar = new JLabel("Rp 0,00");
        lblTotalBesar.setFont(new Font("Poppins", Font.BOLD, 22));
        lblTotalBesar.setForeground(COLOR_PRIMARY);
        formPanel.add(lblTotalBesar, gbc);

        gbc.gridy++;
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        btnPanel.setBackground(COLOR_CARD);
        btnProsesBayar = new JButton("Bayar");
        styleRoundedButton(btnProsesBayar, new Color(46, 204, 113), new Color(39, 174, 96));
        btnBatal = new JButton("Batal");
        styleRoundedButton(btnBatal, new Color(231, 76, 60), new Color(192, 57, 43));
        
        btnPanel.add(btnProsesBayar);
        btnPanel.add(btnBatal);
        formPanel.add(btnPanel, gbc);

        gbc.gridy++; gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        formWrapper.add(formPanel, BorderLayout.CENTER);

        
        JPanel tableWrapper = createRoundedWrapper();
        tableWrapper.setLayout(new BorderLayout());

        JLabel tableTitle = new JLabel("Riwayat Tagihan");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"No. Tagihan", "Pasien", "Dokter", "Total", "Metode", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        styleTable(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollPane.getViewport().setBackground(COLOR_CARD);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        splitPane.setLeftComponent(formWrapper);
        splitPane.setRightComponent(tableWrapper);
        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Poppins", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MUTED);
        return label;
    }

    private JPanel createRoundedWrapper() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        return panel;
    }

    public void styleTextField(JTextField textField) {
        textField.setFont(new Font("Poppins", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Poppins", Font.PLAIN, 12));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void styleRoundedButton(JButton btn, Color bgColor, Color hoverColor) {
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                AbstractButton b = (AbstractButton) c;
                b.setOpaque(false);
            }
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();
                Color bg = b.isEnabled() ? (model.isRollover() || model.isPressed() ? hoverColor : bgColor) : Color.GRAY;
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
                g2.setColor(Color.WHITE);
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 0 || i == 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    public JComboBox<ComboItem> getCbKunjungan() { return cbKunjungan; }
    public JComboBox<String> getCbJenisPembayaran() { return cbJenisPembayaran; }
    
    public void setAutoFillData(String pasien, String dokter, String tgl) {
        lblAutoNamaPasien.setText(pasien);
        lblAutoNamaDokter.setText(dokter);
        lblAutoTanggal.setText(tgl);
    }
    
    public void setSubtotals(double konsultasi, double obat, double potongan, double totalBesar) {
        lblSubtotalKonsultasi.setText(String.format("Rp %,.2f", konsultasi));
        lblSubtotalObat.setText(String.format("Rp %,.2f", obat));
        lblPotongan.setText(String.format("- Rp %,.2f", potongan));
        lblTotalBesar.setText(String.format("Rp %,.2f", totalBesar));
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public void addProsesBayarListener(ActionListener l) { btnProsesBayar.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    public void addKunjunganSelectListener(ActionListener l) { cbKunjungan.addActionListener(l); }
    public void addJenisPembayaranSelectListener(ActionListener l) { cbJenisPembayaran.addActionListener(l); }
}