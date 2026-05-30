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
    private JComboBox<String> cbJenisPembayaran;
    private JLabel lblTotalBesar;
    
    private JButton btnBuatTagihan;
    private JButton btnProsesBayar;
    private JButton btnBatal;

    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174);
    private final Color COLOR_TEXT = new Color(51, 51, 51);
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);

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

        // --- PANEL KIRI: FORM TAGIHAN ---
        JPanel formWrapper = new JPanel(new BorderLayout(10, 10));
        formWrapper.setBackground(COLOR_CARD);
        formWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(COLOR_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;

        JLabel formTitle = new JLabel("Form Tagihan");
        formTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        formPanel.add(formTitle, gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Pilih Kunjungan (Belum Lunas):"), gbc);
        gbc.gridy++;
        cbKunjungan = new JComboBox<>();
        formPanel.add(cbKunjungan, gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Data Pasien:"), gbc);
        gbc.gridy++;
        lblAutoNamaPasien = new JLabel("-");
        lblAutoNamaPasien.setForeground(Color.GRAY);
        formPanel.add(lblAutoNamaPasien, gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Dokter Pemeriksa:"), gbc);
        gbc.gridy++;
        lblAutoNamaDokter = new JLabel("-");
        lblAutoNamaDokter.setForeground(Color.GRAY);
        formPanel.add(lblAutoNamaDokter, gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Tanggal Kunjungan:"), gbc);
        gbc.gridy++;
        lblAutoTanggal = new JLabel("-");
        lblAutoTanggal.setForeground(Color.GRAY);
        formPanel.add(lblAutoTanggal, gbc);

        gbc.gridy++;
        formPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Subtotal Konsultasi:"), gbc);
        gbc.gridy++;
        lblSubtotalKonsultasi = new JLabel("Rp 0,00");
        formPanel.add(lblSubtotalKonsultasi, gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Subtotal Obat (Resep):"), gbc);
        gbc.gridy++;
        lblSubtotalObat = new JLabel("Rp 0,00");
        formPanel.add(lblSubtotalObat, gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("Metode Pembayaran:"), gbc);
        gbc.gridy++;
        cbJenisPembayaran = new JComboBox<>(new String[]{"Tunai", "BPJS", "Asuransi Swasta"});
        formPanel.add(cbJenisPembayaran, gbc);

        gbc.gridy++;
        formPanel.add(new JSeparator(), gbc);

        gbc.gridy++;
        formPanel.add(createFormLabel("TOTAL PEMBAYARAN:"), gbc);
        gbc.gridy++;
        lblTotalBesar = new JLabel("Rp 0,00");
        lblTotalBesar.setFont(new Font("Poppins", Font.BOLD, 22));
        lblTotalBesar.setForeground(COLOR_PRIMARY);
        formPanel.add(lblTotalBesar, gbc);

        gbc.gridy++;
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        btnPanel.setBackground(COLOR_CARD);
        btnBuatTagihan = new JButton("Hitung");
        btnProsesBayar = new JButton("Bayar");
        btnBatal = new JButton("Batal");
        btnPanel.add(btnBuatTagihan);
        btnPanel.add(btnProsesBayar);
        btnPanel.add(btnBatal);
        formPanel.add(btnPanel, gbc);

        // Spacer
        gbc.gridy++; gbc.weighty = 1.0;
        formPanel.add(Box.createVerticalGlue(), gbc);
        formWrapper.add(formPanel, BorderLayout.CENTER);

        // --- PANEL KANAN: TABEL ---
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(COLOR_CARD);
        tableWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel tableTitle = new JLabel("Riwayat Tagihan");
        tableTitle.setFont(new Font("Poppins", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        tableWrapper.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"No. Tagihan", "Pasien", "Dokter", "Total", "Metode", "Status"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        styleTable(table);
        tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);

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

    private void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setFont(new Font("Poppins", Font.BOLD, 12));
    }

    // --- API / Getters untuk Controller ---
    public JComboBox<ComboItem> getCbKunjungan() { return cbKunjungan; }
    public JComboBox<String> getCbJenisPembayaran() { return cbJenisPembayaran; }
    
    public void setAutoFillData(String pasien, String dokter, String tgl) {
        lblAutoNamaPasien.setText(pasien);
        lblAutoNamaDokter.setText(dokter);
        lblAutoTanggal.setText(tgl);
    }
    
    public void setSubtotals(double konsultasi, double obat, double totalBesar) {
        lblSubtotalKonsultasi.setText(String.format("Rp %,.2f", konsultasi));
        lblSubtotalObat.setText(String.format("Rp %,.2f", obat));
        lblTotalBesar.setText(String.format("Rp %,.2f", totalBesar));
    }

    public DefaultTableModel getTableModel() { return tableModel; }
    public void addBuatTagihanListener(ActionListener l) { btnBuatTagihan.addActionListener(l); }
    public void addProsesBayarListener(ActionListener l) { btnProsesBayar.addActionListener(l); }
    public void addBatalListener(ActionListener l) { btnBatal.addActionListener(l); }
    public void addKunjunganSelectListener(ActionListener l) { cbKunjungan.addActionListener(l); }
    public void addJenisPembayaranSelectListener(ActionListener l) { cbJenisPembayaran.addActionListener(l); }
}