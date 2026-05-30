package view;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DokterView extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    
    private JTextField txtId;
    private JTextField txtNama;
    private JTextField txtSpesialisasi;
    
    private JButton btnTambah;
    private JButton btnUbah;
    private JButton btnHapus;
    private JButton btnRefresh;
    
    // --- COLOR PALETTE MODERN ---
    private final Color COLOR_BG = new Color(240, 246, 246); // Surface
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); // Tosca
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);

    public DokterView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Manajemen Data Dokter");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(340);
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);
        
        // --- PANEL KIRI (FORM) ---
        JPanel formWrapper = createRoundedWrapper();
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        JLabel formTitle = new JLabel("Form Data Dokter");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formTitle.setForeground(COLOR_TEXT);
        gbc.gridwidth = 2;
        formPanel.add(formTitle, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Input ID Dokter (Read Only)
        formPanel.add(createFormLabel("ID Dokter (Otomatis):"), gbc);
        gbc.gridy++;
        txtId = new JTextField();
        styleTextField(txtId);
        txtId.setEditable(false);
        txtId.setFocusable(false);
        txtId.setBackground(new Color(245, 245, 245)); // Visual disabled
        formPanel.add(txtId, gbc);
        
        gbc.gridy++;
        
        // Input Nama
        formPanel.add(createFormLabel("Nama Lengkap:"), gbc);
        gbc.gridy++;
        txtNama = new JTextField();
        styleTextField(txtNama);
        formPanel.add(txtNama, gbc);
        
        gbc.gridy++;
        
        // Input Spesialisasi
        formPanel.add(createFormLabel("Spesialisasi:"), gbc);
        gbc.gridy++;
        txtSpesialisasi = new JTextField();
        styleTextField(txtSpesialisasi);
        formPanel.add(txtSpesialisasi, gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(20, 8, 8, 8);
        
        // Tombol Aksi
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(COLOR_CARD);
        
        btnTambah = new JButton("Tambah");
        styleRoundedButton(btnTambah, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        
        btnUbah = new JButton("Ubah");
        styleRoundedButton(btnUbah, new Color(46, 125, 50), new Color(36, 105, 40)); 
        
        btnHapus = new JButton("Hapus");
        styleRoundedButton(btnHapus, new Color(231, 76, 60), new Color(200, 60, 50)); 
        
        btnRefresh = new JButton("Refresh");
        styleRoundedButton(btnRefresh, new Color(149, 165, 166), new Color(120, 140, 140)); 
        
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUbah);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnRefresh);
        formPanel.add(buttonPanel, gbc);
        
        // Spacer agar form ke atas
        gbc.gridy++;
        gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getViewport().setBackground(COLOR_CARD);
        
        formWrapper.add(formScrollPane, BorderLayout.CENTER);
        
        // --- PANEL KANAN (TABEL) ---
        JPanel tableWrapper = createRoundedWrapper();
        
        JLabel tableTitle = new JLabel("Daftar Dokter Terdaftar");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        tableTitle.setForeground(COLOR_TEXT);
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        
        String[] columns = {"ID Dokter", "Nama", "Spesialisasi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        styleTable(table);
        
        // Listener mandiri agar Hapus & Ubah otomatis menyala saat baris diklik
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = table.getSelectedRow() != -1;
                    btnUbah.setEnabled(hasSelection);
                    btnHapus.setEnabled(hasSelection);
                }
            }
        });

        // Sembunyikan kolom ID secara visual
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
        
        // Inisialisasi State Awal
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
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
    
    private void styleRoundedButton(JButton button, Color bgColor, Color hoverColor) {
        final Color COLOR_DISABLED_BG = new Color(235, 235, 235);
        final Color COLOR_DISABLED_TEXT = new Color(160, 160, 160);

        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
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
    
    // --- GETTER ASLI UNTUK DOKTER CONTROLLER ---
    
    public JTable getTable() { return table; }
    public JTextField getTxtId() { return txtId; }
    public JTextField getTxtNama() { return txtNama; }
    public JTextField getTxtSpesialisasi() { return txtSpesialisasi; }
    
    public JButton getBtnTambah() { return btnTambah; }
    public JButton getBtnUbah() { return btnUbah; }
    public JButton getBtnHapus() { return btnHapus; }
    public JButton getBtnRefresh() { return btnRefresh; }
}