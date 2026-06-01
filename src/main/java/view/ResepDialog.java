package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResepDialog extends JDialog {

    private JComboBox<ComboItem> cbObat;
    private JTextField txtJumlah;
    private JTextField txtDosis;
    private JButton btnTambahkan;
    private JButton btnSimpan;
    private JButton btnTutup;

    private JTable tabelDetail;
    private DefaultTableModel detailModel;

    private final Color COLOR_BG      = new Color(240, 246, 246);
    private final Color COLOR_CARD     = Color.WHITE;
    private final Color COLOR_PRIMARY  = new Color(55, 194, 174);
    private final Color COLOR_SUCCESS  = new Color(39, 174, 96);
    private final Color COLOR_DANGER   = new Color(231, 76, 60);
    private final Color COLOR_TEXT     = new Color(51, 51, 51);
    private final Color COLOR_MUTED    = new Color(130, 140, 145);
    private final Color COLOR_BORDER   = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);

    public ResepDialog(Window parent, int idKunjungan, String namaPasien) {
        super(parent, "Resep Obat", ModalityType.APPLICATION_MODAL);
        setSize(760, 460);
        setResizable(false);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        // ============================================================
        // NORTH — Header
        // ============================================================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 10, 20));

        JLabel lblTitle = new JLabel("Resep Obat");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JLabel lblPasien = new JLabel("Pasien: " + namaPasien);
        lblPasien.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblPasien.setForeground(COLOR_MUTED);
        headerPanel.add(lblPasien, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ============================================================
        // CENTER — 2 kolom: form kiri | tabel kanan
        // ============================================================
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 4, 0, 4);
        c.gridy = 0;
        c.weighty = 1.0;

        // ---- KIRI: Form input obat ----
        c.gridx = 0;
        c.weightx = 0.38;
        JPanel formCard = createRoundedCard();
        formCard.setLayout(new GridBagLayout());

        GridBagConstraints fg = new GridBagConstraints();
        fg.fill = GridBagConstraints.HORIZONTAL;
        fg.insets = new Insets(4, 0, 4, 0);
        fg.gridx = 0;
        fg.weightx = 1.0;

        fg.gridy = 0;
        JLabel lblFormTitle = new JLabel("Tambah Obat");
        lblFormTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        lblFormTitle.setForeground(COLOR_TEXT);
        formCard.add(lblFormTitle, fg);

        // separator
        fg.gridy++;
        fg.insets = new Insets(2, 0, 8, 0);
        JPanel sep = new JPanel();
        sep.setBackground(COLOR_BORDER);
        sep.setPreferredSize(new Dimension(0, 1));
        formCard.add(sep, fg);

        fg.insets = new Insets(4, 0, 2, 0);

        // Pilih Obat
        fg.gridy++;
        formCard.add(fieldLabel("Pilih Obat"), fg);
        fg.gridy++;
        fg.insets = new Insets(0, 0, 8, 0);
        cbObat = new JComboBox<>();
        cbObat.setFont(new Font("Poppins", Font.PLAIN, 12));
        cbObat.setBackground(COLOR_INPUT_BG);
        cbObat.setForeground(COLOR_TEXT);
        cbObat.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        cbObat.setPreferredSize(new Dimension(0, 34));
        formCard.add(cbObat, fg);

        // Jumlah
        fg.gridy++;
        fg.insets = new Insets(4, 0, 2, 0);
        formCard.add(fieldLabel("Jumlah"), fg);
        fg.gridy++;
        fg.insets = new Insets(0, 0, 8, 0);
        txtJumlah = new JTextField();
        styleTextField(txtJumlah);
        formCard.add(txtJumlah, fg);

        // Dosis
        fg.gridy++;
        fg.insets = new Insets(4, 0, 2, 0);
        formCard.add(fieldLabel("Dosis (contoh: 3x1 sesudah makan)"), fg);
        fg.gridy++;
        fg.insets = new Insets(0, 0, 14, 0);
        txtDosis = new JTextField();
        styleTextField(txtDosis);
        formCard.add(txtDosis, fg);

        // Tombol Tambahkan
        fg.gridy++;
        fg.insets = new Insets(0, 0, 0, 0);
        btnTambahkan = new JButton("+ Tambahkan ke Resep");
        styleButton(btnTambahkan, COLOR_PRIMARY);
        formCard.add(btnTambahkan, fg);

        // Spacer agar form rapat ke atas
        fg.gridy++;
        fg.weighty = 1.0;
        formCard.add(Box.createVerticalGlue(), fg);

        centerPanel.add(formCard, c);

        // ---- KANAN: Tabel item resep ----
        c.gridx = 1;
        c.weightx = 0.62;
        c.insets = new Insets(0, 4, 0, 4);
        JPanel tableCard = createRoundedCard();
        tableCard.setLayout(new BorderLayout(0, 8));

        JLabel lblTableTitle = new JLabel("Item Resep");
        lblTableTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        lblTableTitle.setForeground(COLOR_TEXT);
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        tableCard.add(lblTableTitle, BorderLayout.NORTH);

        // Tabel
        String[] cols = {"ID_OBAT", "Nama Obat", "Jumlah", "Dosis", "×"};
        detailModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelDetail = new JTable(detailModel);
        tabelDetail.setFont(new Font("Poppins", Font.PLAIN, 12));
        tabelDetail.setRowHeight(30);
        tabelDetail.setBackground(COLOR_CARD);
        tabelDetail.setForeground(COLOR_TEXT);
        tabelDetail.setGridColor(COLOR_BORDER);
        tabelDetail.setShowGrid(true);
        tabelDetail.setSelectionBackground(COLOR_PRIMARY);
        tabelDetail.setSelectionForeground(Color.WHITE);

        JTableHeader th = tabelDetail.getTableHeader();
        th.setFont(new Font("Poppins", Font.BOLD, 12));
        th.setBackground(new Color(245, 245, 245));
        th.setForeground(COLOR_TEXT);
        th.setPreferredSize(new Dimension(0, 34));

        // Hide ID_OBAT column
        tabelDetail.getColumnModel().getColumn(0).setMinWidth(0);
        tabelDetail.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelDetail.getColumnModel().getColumn(0).setWidth(0);

        // Kolom "×" hapus
        tabelDetail.getColumnModel().getColumn(4).setMinWidth(40);
        tabelDetail.getColumnModel().getColumn(4).setMaxWidth(40);
        tabelDetail.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JButton btn = new JButton("×");
                btn.setFont(new Font("Poppins", Font.BOLD, 13));
                btn.setForeground(Color.WHITE);
                btn.setBackground(COLOR_DANGER);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setOpaque(true);
                return btn;
            }
        });

        tabelDetail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabelDetail.columnAtPoint(e.getPoint());
                int row = tabelDetail.rowAtPoint(e.getPoint());
                if (row >= 0 && col == 4) detailModel.removeRow(row);
            }
        });

        JScrollPane scrollTabel = new JScrollPane(tabelDetail);
        scrollTabel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollTabel.getViewport().setBackground(COLOR_CARD);
        tableCard.add(scrollTabel, BorderLayout.CENTER);

        // Label hint kosong
        JLabel lblHint = new JLabel("Klik × untuk menghapus item");
        lblHint.setFont(new Font("Poppins", Font.ITALIC, 10));
        lblHint.setForeground(COLOR_MUTED);
        tableCard.add(lblHint, BorderLayout.SOUTH);

        centerPanel.add(tableCard, c);
        add(centerPanel, BorderLayout.CENTER);

        // ============================================================
        // SOUTH — Tombol aksi
        // ============================================================
        JPanel bottomPanel = new JPanel(new BorderLayout(12, 0));
        bottomPanel.setBackground(COLOR_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 18, 20));

        btnSimpan = new JButton("Simpan Resep");
        styleButton(btnSimpan, COLOR_SUCCESS);
        bottomPanel.add(btnSimpan, BorderLayout.CENTER);

        btnTutup = new JButton("Tutup");
        styleButton(btnTutup, COLOR_MUTED);
        btnTutup.setPreferredSize(new Dimension(100, 38));
        bottomPanel.add(btnTutup, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ---- Helpers ----

    private JPanel createRoundedCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return p;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 11));
        lbl.setForeground(COLOR_MUTED);
        return lbl;
    }

    private void styleTextField(JTextField f) {
        f.setFont(new Font("Poppins", Font.PLAIN, 12));
        f.setBackground(COLOR_INPUT_BG);
        f.setForeground(COLOR_TEXT);
        f.setCaretColor(COLOR_TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.isEnabled() ? bg : new Color(200, 200, 200));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    // ---- Public API ----
    public ComboItem getSelectedObat()        { return (ComboItem) cbObat.getSelectedItem(); }
    public String getJumlahInput()            { return txtJumlah.getText().trim(); }
    public String getDosisInput()             { return txtDosis.getText().trim(); }
    public DefaultTableModel getDetailModel() { return detailModel; }

    public void setObatList(ComboItem[] items) {
        cbObat.removeAllItems();
        for (ComboItem item : items) cbObat.addItem(item);
    }

    public void clearFormObat() {
        if (cbObat.getItemCount() > 0) cbObat.setSelectedIndex(0);
        txtJumlah.setText("");
        txtDosis.setText("");
    }

    public void addTambahkanListener(ActionListener l) { btnTambahkan.addActionListener(l); }
    public void addSimpanListener(ActionListener l)    { btnSimpan.addActionListener(l); }
    public void addTutupListener(ActionListener l)     { btnTutup.addActionListener(l); }
}
