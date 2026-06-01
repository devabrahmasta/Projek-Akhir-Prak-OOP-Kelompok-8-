package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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
        setSize(480, 520);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---- HEADER ----
        JLabel lblTitle = new JLabel("Resep Obat");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPasien = new JLabel("Pasien: " + namaPasien);
        lblPasien.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblPasien.setForeground(COLOR_MUTED);
        lblPasien.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        mainPanel.add(lblPasien);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 14)));

        // ---- FORM CARD ----
        JPanel formCard = createCard(16);
        formCard.setLayout(new GridBagLayout());
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        formCard.add(formLabel("Obat"), gbc);
        gbc.gridy++;
        cbObat = new JComboBox<>();
        cbObat.setFont(new Font("Poppins", Font.PLAIN, 12));
        cbObat.setBackground(COLOR_INPUT_BG);
        formCard.add(cbObat, gbc);

        gbc.gridy++;
        formCard.add(formLabel("Jumlah"), gbc);
        gbc.gridy++;
        txtJumlah = new JTextField();
        styleTextField(txtJumlah);
        formCard.add(txtJumlah, gbc);

        gbc.gridy++;
        formCard.add(formLabel("Dosis (contoh: 3x1 sesudah makan)"), gbc);
        gbc.gridy++;
        txtDosis = new JTextField();
        styleTextField(txtDosis);
        formCard.add(txtDosis, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 4, 4, 4);
        btnTambahkan = new JButton("Tambahkan ke Resep");
        styleButton(btnTambahkan, COLOR_PRIMARY);
        formCard.add(btnTambahkan, gbc);

        mainPanel.add(formCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // ---- TABEL DETAIL ----
        JLabel lblTabelTitle = new JLabel("Item Resep");
        lblTabelTitle.setFont(new Font("Poppins", Font.BOLD, 13));
        lblTabelTitle.setForeground(COLOR_TEXT);
        lblTabelTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTabelTitle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        String[] cols = {"ID_OBAT", "Nama Obat", "Jumlah", "Dosis", "Hapus"};
        detailModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelDetail = new JTable(detailModel);
        tabelDetail.setFont(new Font("Poppins", Font.PLAIN, 11));
        tabelDetail.setRowHeight(28);
        tabelDetail.setBackground(COLOR_CARD);
        tabelDetail.setForeground(COLOR_TEXT);
        tabelDetail.setGridColor(COLOR_BORDER);
        tabelDetail.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 11));
        tabelDetail.getTableHeader().setBackground(new Color(245, 245, 245));

        // Hide ID_OBAT column
        tabelDetail.getColumnModel().getColumn(0).setMinWidth(0);
        tabelDetail.getColumnModel().getColumn(0).setMaxWidth(0);
        tabelDetail.getColumnModel().getColumn(0).setWidth(0);

        // Hapus button column renderer
        tabelDetail.getColumnModel().getColumn(4).setMaxWidth(50);
        tabelDetail.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                JButton btn = new JButton("×");
                btn.setBackground(COLOR_DANGER);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Poppins", Font.BOLD, 12));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                return btn;
            }
        });

        tabelDetail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabelDetail.columnAtPoint(e.getPoint());
                int row = tabelDetail.rowAtPoint(e.getPoint());
                if (row != -1 && col == 4) {
                    detailModel.removeRow(row);
                }
            }
        });

        JScrollPane scrollTabel = new JScrollPane(tabelDetail);
        scrollTabel.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        scrollTabel.setPreferredSize(new Dimension(0, 140));
        scrollTabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollTabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        mainPanel.add(scrollTabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // ---- TOMBOL BAWAH ----
        btnSimpan = new JButton("Simpan Resep");
        styleButton(btnSimpan, COLOR_SUCCESS);
        btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(btnSimpan);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        btnTutup = new JButton("Tutup");
        styleButton(btnTutup, COLOR_MUTED);
        btnTutup.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTutup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        mainPanel.add(btnTutup);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createCard(int padding) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        return p;
    }

    private JLabel formLabel(String text) {
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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
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

    // ---- Public API ----
    public ComboItem getSelectedObat()       { return (ComboItem) cbObat.getSelectedItem(); }
    public String getJumlahInput()           { return txtJumlah.getText().trim(); }
    public String getDosisInput()            { return txtDosis.getText().trim(); }
    public DefaultTableModel getDetailModel(){ return detailModel; }

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
