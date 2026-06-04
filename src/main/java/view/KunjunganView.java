package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class KunjunganView extends JPanel {

    
    private JLabel lblPasienNama;
    private JLabel lblPasienRM;
    private JLabel lblDokterNama;
    private JTextArea txtKeluhan;
    private JTextArea txtDiagnosa;
    private JButton btnInputResep;
    private JButton btnSelesaikan;

    private JSplitPane splitPane;
    private JPanel formWrapper;
    private JLabel titleLabel;

    
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtCari;
    private JButton btnCari;
    private JComboBox<String> cbSortWaktu;
    private JLabel lblStatus;

    
    private final Color COLOR_BG      = new Color(240, 246, 246);
    private final Color COLOR_CARD     = Color.WHITE;
    private final Color COLOR_PRIMARY  = new Color(55, 194, 174);
    private final Color COLOR_SUCCESS  = new Color(39, 174, 96);
    private final Color COLOR_TEXT     = new Color(51, 51, 51);
    private final Color COLOR_MUTED    = new Color(130, 140, 145);
    private final Color COLOR_BORDER   = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);

    public KunjunganView() {
        setLayout(new BorderLayout(0, 12));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initHeader();
        initContent();
    }

    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setBackground(COLOR_BG);

        titleLabel = new JLabel("Catatan Medis");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(COLOR_BG);

        JLabel lblFilter = new JLabel("Filter:");
        lblFilter.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblFilter.setForeground(COLOR_MUTED);
        rightPanel.add(lblFilter);

        cbSortWaktu = new JComboBox<>(new String[]{
            "Paling Baru", "Paling Lama", "24 Jam Terakhir", "1 Bulan Terakhir"
        });
        cbSortWaktu.setFont(new Font("Poppins", Font.PLAIN, 12));
        cbSortWaktu.setBackground(COLOR_INPUT_BG);
        cbSortWaktu.setPreferredSize(new Dimension(160, 32));
        rightPanel.add(cbSortWaktu);

        txtCari = new JTextField(14);
        styleTextField(txtCari);
        txtCari.setToolTipText("Cari pasien atau diagnosa...");
        rightPanel.add(txtCari);

        btnCari = new JButton("Cari");
        styleButton(btnCari, COLOR_PRIMARY);
        rightPanel.add(btnCari);

        headerPanel.add(rightPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    
    private void initContent() {
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(380);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        
        formWrapper = createRoundedPanel();
        formWrapper.setLayout(new BorderLayout());
        formWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        
        JLabel formTitle = new JLabel("Form Kunjungan");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        formPanel.add(formTitle, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 4, 0);
        JPanel pasienInfoPanel = new JPanel();
        pasienInfoPanel.setLayout(new BoxLayout(pasienInfoPanel, BoxLayout.Y_AXIS));
        pasienInfoPanel.setBackground(COLOR_INPUT_BG);
        pasienInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        lblPasienNama = new JLabel("—");
        lblPasienNama.setFont(new Font("Poppins", Font.BOLD, 13));
        lblPasienNama.setForeground(COLOR_PRIMARY);
        lblPasienNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPasienRM = new JLabel("No. RM: —");
        lblPasienRM.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblPasienRM.setForeground(COLOR_MUTED);
        lblPasienRM.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblDokterNama = new JLabel("Dokter: —");
        lblDokterNama.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblDokterNama.setForeground(COLOR_MUTED);
        lblDokterNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        pasienInfoPanel.add(lblPasienNama);
        pasienInfoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        pasienInfoPanel.add(lblPasienRM);
        pasienInfoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        pasienInfoPanel.add(lblDokterNama);

        formPanel.add(pasienInfoPanel, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(12, 0, 2, 0);
        JLabel lblKeluhan = new JLabel("Keluhan Pasien:");
        lblKeluhan.setFont(new Font("Poppins", Font.BOLD, 12));
        lblKeluhan.setForeground(COLOR_MUTED);
        formPanel.add(lblKeluhan, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);
        txtKeluhan = new JTextArea(4, 15);
        txtKeluhan.setLineWrap(true);
        txtKeluhan.setWrapStyleWord(true);
        txtKeluhan.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtKeluhan.setBackground(COLOR_INPUT_BG);
        txtKeluhan.setForeground(COLOR_TEXT);
        txtKeluhan.setCaretColor(COLOR_TEXT);
        txtKeluhan.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        txtKeluhan.setEnabled(false);
        JScrollPane scrollKeluhan = new JScrollPane(txtKeluhan);
        scrollKeluhan.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(scrollKeluhan, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 2, 0);
        JLabel lblDiagnosa = new JLabel("Diagnosa:");
        lblDiagnosa.setFont(new Font("Poppins", Font.BOLD, 12));
        lblDiagnosa.setForeground(COLOR_MUTED);
        formPanel.add(lblDiagnosa, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 4, 0);
        txtDiagnosa = new JTextArea(4, 15);
        txtDiagnosa.setLineWrap(true);
        txtDiagnosa.setWrapStyleWord(true);
        txtDiagnosa.setFont(new Font("Poppins", Font.PLAIN, 12));
        txtDiagnosa.setBackground(COLOR_INPUT_BG);
        txtDiagnosa.setForeground(COLOR_TEXT);
        txtDiagnosa.setCaretColor(COLOR_TEXT);
        txtDiagnosa.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        txtDiagnosa.setEnabled(false);
        JScrollPane scrollDiagnosa = new JScrollPane(txtDiagnosa);
        scrollDiagnosa.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(scrollDiagnosa, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(14, 0, 4, 0);
        btnInputResep = new JButton("Input Resep");
        styleButton(btnInputResep, COLOR_PRIMARY);
        btnInputResep.setEnabled(false);
        formPanel.add(btnInputResep, gbc);

        
        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 4, 0);
        btnSelesaikan = new JButton(" Selesaikan Kunjungan");
        styleButton(btnSelesaikan, COLOR_SUCCESS);
        btnSelesaikan.setEnabled(false);
        formPanel.add(btnSelesaikan, gbc);

        
        gbc.gridy++;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);

        
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { checkReady(); }
            public void removeUpdate(DocumentEvent e)  { checkReady(); }
            public void changedUpdate(DocumentEvent e) { checkReady(); }

            private void checkReady() {
                boolean ready = txtKeluhan.isEnabled()
                    && !txtKeluhan.getText().trim().isEmpty()
                    && !txtDiagnosa.getText().trim().isEmpty();
                btnSelesaikan.setEnabled(ready);
            }
        };
        txtKeluhan.getDocument().addDocumentListener(dl);
        txtDiagnosa.getDocument().addDocumentListener(dl);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScroll.getViewport().setBackground(COLOR_CARD);
        formWrapper.add(formScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(formWrapper);

        
        JPanel tableWrapper = createRoundedPanel();
        tableWrapper.setLayout(new BorderLayout(0, 0));
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel lblTableTitle = new JLabel("Riwayat Kunjungan");
        lblTableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTableTitle.setForeground(COLOR_TEXT);
        topRow.add(lblTableTitle, BorderLayout.WEST);
        tableWrapper.add(topRow, BorderLayout.NORTH);

        String[] columns = {"ID", "Pasien", "Dokter", "Waktu Kunjungan", "Keluhan", "Diagnosa", "Obat Diresepkan", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        lblStatus = new JLabel("Memuat data...");
        lblStatus.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblStatus.setForeground(COLOR_MUTED);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        tableWrapper.add(lblStatus, BorderLayout.SOUTH);

        splitPane.setRightComponent(tableWrapper);
        add(splitPane, BorderLayout.CENTER);
    }

    
    private JPanel createRoundedPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private void styleTextField(JTextField f) {
        f.setFont(new Font("Poppins", Font.PLAIN, 12));
        f.setBackground(COLOR_INPUT_BG);
        f.setForeground(COLOR_TEXT);
        f.setCaretColor(COLOR_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.isEnabled() ? bg : new Color(200, 200, 200));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    private void styleTable(JTable t) {
        t.setBackground(COLOR_CARD);
        t.setForeground(COLOR_TEXT);
        t.setGridColor(COLOR_BORDER);
        t.setShowGrid(true);
        t.setFont(new Font("Poppins", Font.PLAIN, 12));
        t.setRowHeight(30);
        t.setSelectionBackground(COLOR_PRIMARY);
        t.setSelectionForeground(Color.WHITE);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("Poppins", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        t.setDefaultRenderer(Object.class, renderer);
    }

    

    
    public void setFormAutoFilled(String namaPasien, String noRM, String namaDokter) {
        lblPasienNama.setText(namaPasien);
        lblPasienRM.setText("No. RM: " + noRM);
        lblDokterNama.setText("Dokter: " + namaDokter);
        txtKeluhan.setText("");
        txtDiagnosa.setText("");
    }

    
    public void setFormEnabled(boolean enabled) {
        txtKeluhan.setEnabled(enabled);
        txtDiagnosa.setEnabled(enabled);
        btnInputResep.setEnabled(enabled);
        if (!enabled) btnSelesaikan.setEnabled(false);
    }

    public void setSelesaikanEnabled(boolean enabled) { btnSelesaikan.setEnabled(enabled); }

    public void setKeluhanAndDiagnosa(String keluhan, String diagnosa) {
        txtKeluhan.setText(keluhan);
        txtDiagnosa.setText(diagnosa);
    }

    
    public void clearForm() {
        lblPasienNama.setText("—");
        lblPasienRM.setText("No. RM: —");
        lblDokterNama.setText("Dokter: —");
        txtKeluhan.setText("");
        txtDiagnosa.setText("");
    }

    public String getKeluhanInput()  { return txtKeluhan.getText().trim(); }
    public String getDiagnosaInput() { return txtDiagnosa.getText().trim(); }

    public void addInputResepListener(ActionListener l)  { btnInputResep.addActionListener(l); }
    public void addSelesaikanListener(ActionListener l)  { btnSelesaikan.addActionListener(l); }

    

    public DefaultTableModel getTableModel()         { return tableModel; }
    public JTable getTable()                          { return table; }
    public String getCariInput()                      { return txtCari.getText().trim(); }
    public String getSortOption()                     { return (String) cbSortWaktu.getSelectedItem(); }
    public void setStatusText(String text)            { lblStatus.setText(text); }

    public int getSelectedId() {
        int row = table.getSelectedRow();
        if (row != -1) return Integer.parseInt(table.getModel().getValueAt(row, 0).toString());
        return -1;
    }

    public void addCariListener(ActionListener l)        { btnCari.addActionListener(l); }
    public void addSortListener(ActionListener l)        { cbSortWaktu.addActionListener(l); }
    public void addTableMouseListener(MouseAdapter l)    { table.addMouseListener(l); }

    public void setReadOnlyMode() {
        if (formWrapper != null) formWrapper.setVisible(false);
        if (splitPane != null) {
            splitPane.setDividerLocation(0);
            splitPane.setDividerSize(0);
        }
        if (titleLabel != null) {
            titleLabel.setText("Riwayat Catatan Medis");
        }
    }
}
