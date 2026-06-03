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
import java.awt.event.MouseEvent;

public class PasienView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtNama;
    private JTextField txtNoRM;
    private JTextArea txtAlamat;
    private JTextField txtNoTelp;
    private JTextField txtTanggalLahir; 
    private JComboBox<String> cbGolDarah;
    private JTextField txtAlergi;
    private JButton btnDaftarAntrian;
    
    private JTextField txtCari;
    private JButton btnCari;
    private JComboBox<String> cbSortWaktu;
    
    private JButton btnTambah;
    private JButton btnSimpan;
    private JButton btnHapus;
    private JButton btnBatal;
    
    private JLabel lblStatus;
    
    
    private final Color COLOR_BG = new Color(240, 246, 246); 
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); 
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);
    
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
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(COLOR_BG);
        
        JLabel lblCari = new JLabel("Cari Nama/RM:");
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
        
        
        JPanel formWrapper = createRoundedWrapper();
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel formTitle = new JLabel("Form Data Pasien");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        formPanel.add(createFormLabel("No. Rekam Medis (RM) [Otomatis]:"), gbc);
        gbc.gridy++;
        txtNoRM = new JTextField();
        styleTextField(txtNoRM);
        txtNoRM.setEditable(false); 
        txtNoRM.setFocusable(false);
        txtNoRM.setBackground(new Color(245, 245, 245)); 
        formPanel.add(txtNoRM, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Nama Lengkap:"), gbc);
        gbc.gridy++;
        txtNama = new JTextField();
        styleTextField(txtNama);
        formPanel.add(txtNama, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Alamat:"), gbc);
        gbc.gridy++;
        txtAlamat = new JTextArea(3, 15);
        txtAlamat.setLineWrap(true);
        txtAlamat.setWrapStyleWord(true);
        txtAlamat.setBackground(COLOR_INPUT_BG);
        txtAlamat.setForeground(COLOR_TEXT);
        txtAlamat.setCaretColor(COLOR_TEXT);
        txtAlamat.setFont(new Font("Poppins", Font.PLAIN, 13));
        txtAlamat.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8)); 
        
        JScrollPane scrollAlamat = new JScrollPane(txtAlamat);
        scrollAlamat.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1)); 
        formPanel.add(scrollAlamat, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("No. Telepon:"), gbc);
        gbc.gridy++;
        txtNoTelp = new JTextField();
        styleTextField(txtNoTelp);
        formPanel.add(txtNoTelp, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Tanggal Lahir (YYYY-MM-DD):"), gbc);
        gbc.gridy++;
        txtTanggalLahir = new JTextField();
        styleTextField(txtTanggalLahir);
        formPanel.add(txtTanggalLahir, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Golongan Darah:"), gbc);
        gbc.gridy++;
        cbGolDarah = new JComboBox<>(new String[]{"-", "A", "B", "AB", "O"});
        cbGolDarah.setFont(new Font("Poppins", Font.PLAIN, 13));
        cbGolDarah.setBackground(COLOR_INPUT_BG);
        cbGolDarah.setForeground(COLOR_TEXT);
        cbGolDarah.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        formPanel.add(cbGolDarah, gbc);
        
        gbc.gridy++;
        formPanel.add(createFormLabel("Alergi:"), gbc);
        gbc.gridy++;
        txtAlergi = new JTextField();
        styleTextField(txtAlergi);
        formPanel.add(txtAlergi, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(15, 8, 8, 8);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBackground(COLOR_CARD);
        
        btnTambah = new JButton("Tambah");
        styleRoundedButton(btnTambah, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        
        btnSimpan = new JButton("Simpan");
        styleRoundedButton(btnSimpan, COLOR_PRIMARY, COLOR_PRIMARY_HOVER); 
        
        btnHapus = new JButton("Hapus");
        styleRoundedButton(btnHapus, new Color(231, 76, 60), new Color(200, 60, 50)); 
        
        btnBatal = new JButton("Batal");
        styleRoundedButton(btnBatal, new Color(117, 117, 117), new Color(90, 90, 90)); 
        
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

        JPanel topTablePanel = new JPanel();
        topTablePanel.setLayout(new BoxLayout(topTablePanel, BoxLayout.Y_AXIS));
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        
        JLabel tableTitle = new JLabel("Daftar Pasien Terdaftar");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topTablePanel.add(tableTitle);

        topTablePanel.add(Box.createVerticalStrut(6));

        topTablePanel.add(Box.createRigidArea(new Dimension(0, 6)));

        
        JPanel row2 = new JPanel(new BorderLayout());
        row2.setOpaque(false);
        row2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        sortPanel.setOpaque(false);
        JLabel lblSort = new JLabel("Urutkan:");
        lblSort.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblSort.setForeground(COLOR_TEXT_MUTED);
        sortPanel.add(lblSort);

        cbSortWaktu = new JComboBox<>(new String[]{"Paling Baru", "Paling Lama"});
        styleComboBox(cbSortWaktu);
        cbSortWaktu.setPreferredSize(new Dimension(140, 30));
        sortPanel.add(cbSortWaktu);
        row2.add(sortPanel, BorderLayout.WEST);

        btnDaftarAntrian = new JButton("+ Daftar Antrian");
        styleRoundedButton(btnDaftarAntrian, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        row2.add(btnDaftarAntrian, BorderLayout.EAST);
        topTablePanel.add(row2);
        topTablePanel.add(Box.createRigidArea(new Dimension(0, 8)));

        tableWrapper.add(topTablePanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "No. RM", "Nama", "No. Telp", "Tgl Lahir", "Gol. Darah", "Alergi", "Alamat"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean isEditing = btnSimpan.isEnabled(); 
                    boolean hasSelection = table.getSelectedRow() != -1;
                    
                    btnHapus.setEnabled(hasSelection && !isEditing);
                    btnBatal.setEnabled(isEditing || hasSelection);
                }
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
    
    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(COLOR_INPUT_BG);
        combo.setForeground(COLOR_TEXT);
        combo.setFont(new Font("Poppins", Font.PLAIN, 13));
        combo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
    }
    
    private void styleRoundedButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!button.isEnabled()) {
                    g2.setColor(new Color(235, 235, 235)); 
                    button.setForeground(new Color(160, 160, 160)); 
                } else {
                    g2.setColor(button.getBackground());
                    button.setForeground(Color.WHITE); 
                }
                
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(bgColor);
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
    
    
    
    public void setFormEnabled(boolean enabled) {
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
    
    public void addTambahListener(ActionListener l) { btnTambah.addActionListener(l); }
    public void addSimpanListener(ActionListener l) { btnSimpan.addActionListener(l); }
    public void addHapusListener(ActionListener l) { btnHapus.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    public void addCariListener(ActionListener l) { btnCari.addActionListener(l); }
    public void addTableMouseListener(MouseAdapter l) { table.addMouseListener(l); }
    
    
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
    
    
    public String getSortOption() { 
        return cbSortWaktu.getSelectedItem() != null ? (String) cbSortWaktu.getSelectedItem() : "Paling Baru"; 
    }
    public void addSortListener(ActionListener l) { cbSortWaktu.addActionListener(l); }
    
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return table; }
    public JButton getBtnDaftarAntrian() { return btnDaftarAntrian; }
    
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