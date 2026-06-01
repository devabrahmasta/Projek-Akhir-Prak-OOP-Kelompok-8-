package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

public class KunjunganView extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtCari;
    private JButton btnCari;
    private JComboBox<String> cbSortWaktu;

    private JLabel lblStatus;

    private final Color COLOR_BG      = new Color(240, 246, 246);
    private final Color COLOR_CARD     = Color.WHITE;
    private final Color COLOR_PRIMARY  = new Color(55, 194, 174);
    private final Color COLOR_TEXT     = new Color(51, 51, 51);
    private final Color COLOR_MUTED    = new Color(130, 140, 145);
    private final Color COLOR_BORDER   = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);

    public KunjunganView() {
        setLayout(new BorderLayout(0, 12));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initHeader();
        initTable();
    }

    // ---- HEADER: judul + search + filter ----
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(12, 0));
        headerPanel.setBackground(COLOR_BG);

        JLabel titleLabel = new JLabel("Catatan Medis");
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

    // ---- TABLE: riwayat kunjungan, full height ----
    private void initTable() {
        JPanel tableWrapper = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table title row
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel lblTableTitle = new JLabel("Riwayat Kunjungan");
        lblTableTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTableTitle.setForeground(COLOR_TEXT);
        topRow.add(lblTableTitle, BorderLayout.WEST);
        tableWrapper.add(topRow, BorderLayout.NORTH);

        // Model: ID hidden | Pasien | Dokter | Waktu | Keluhan | Diagnosa | Status
        String[] columns = {"ID", "Pasien", "Dokter", "Waktu Kunjungan", "Keluhan", "Diagnosa", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        styleTable(table);

        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(COLOR_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        // Status bar
        lblStatus = new JLabel("Memuat data...");
        lblStatus.setFont(new Font("Poppins", Font.ITALIC, 11));
        lblStatus.setForeground(COLOR_MUTED);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        tableWrapper.add(lblStatus, BorderLayout.SOUTH);

        add(tableWrapper, BorderLayout.CENTER);
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
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
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

    // ---- Public API ----
    public DefaultTableModel getTableModel()       { return tableModel; }
    public JTable getTable()                        { return table; }
    public String getCariInput()                    { return txtCari.getText().trim(); }
    public String getSortOption()                   { return (String) cbSortWaktu.getSelectedItem(); }
    public void setStatusText(String text)          { lblStatus.setText(text); }

    public int getSelectedId() {
        int row = table.getSelectedRow();
        if (row != -1) return Integer.parseInt(table.getModel().getValueAt(row, 0).toString());
        return -1;
    }

    public void addCariListener(ActionListener l)        { btnCari.addActionListener(l); }
    public void addSortListener(ActionListener l)        { cbSortWaktu.addActionListener(l); }
    public void addTableMouseListener(MouseAdapter l)    { table.addMouseListener(l); }
}
