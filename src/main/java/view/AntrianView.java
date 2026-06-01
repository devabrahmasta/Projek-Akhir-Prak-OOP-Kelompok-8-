package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;

public class AntrianView extends JPanel {
    private JComboBox<ComboItem> cbFilterDokter;
    private JTextField txtFilterTanggal;
    private JButton btnFilter;

    private JLabel lblStatMenunggu, lblStatDiperiksa, lblStatSelesai;
    private JLabel lblNowServing;
    
    private JPanel panelKartuAntrian;
    private JTable tabelSelesai;
    private DefaultTableModel tableModelSelesai;
    private JPanel emptyStatePanel;

    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174);
    private final Color COLOR_TEXT = new Color(51, 51, 51);
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);

    public AntrianView() {
        setLayout(new BorderLayout(10, 10));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initTopBar();
        initCenterPanel();
    }

    private void initTopBar() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);

        JLabel titleLabel = new JLabel("Manajemen Antrian Aktif");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(COLOR_BG);

        filterPanel.add(createFormLabel("Dokter:"));
        cbFilterDokter = new JComboBox<>();
        cbFilterDokter.setPreferredSize(new Dimension(180, 32));
        cbFilterDokter.setBackground(Color.WHITE);
        filterPanel.add(cbFilterDokter);

        filterPanel.add(createFormLabel("Tanggal:"));
        txtFilterTanggal = new JTextField(10);
        txtFilterTanggal.setPreferredSize(new Dimension(100, 32));
        filterPanel.add(txtFilterTanggal);

        btnFilter = new JButton("Filter");
        styleRoundedButton(btnFilter, COLOR_PRIMARY, new Color(45, 175, 155));
        filterPanel.add(btnFilter);

        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void initCenterPanel() {
        JSplitPane splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneVertical.setDividerLocation(120);
        splitPaneVertical.setDividerSize(0);
        splitPaneVertical.setBorder(null);
        splitPaneVertical.setOpaque(false);

        JPanel panelInfo = new JPanel(new BorderLayout(10, 10));
        panelInfo.setOpaque(false);

        JPanel panelStats = new JPanel(new GridLayout(1, 3, 10, 0));
        panelStats.setOpaque(false);
        lblStatMenunggu = createStatLabel("Menunggu: 0", new Color(243, 156, 18));
        lblStatDiperiksa = createStatLabel("Diperiksa: 0", new Color(41, 128, 185));
        lblStatSelesai = createStatLabel("Selesai: 0", new Color(39, 174, 96));
        panelStats.add(lblStatMenunggu);
        panelStats.add(lblStatDiperiksa);
        panelStats.add(lblStatSelesai);
        panelInfo.add(panelStats, BorderLayout.NORTH);

        JPanel panelNowServing = new JPanel(new BorderLayout());
        panelNowServing.setBackground(COLOR_PRIMARY);
        panelNowServing.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        lblNowServing = new JLabel("TIDAK ADA PASIEN DI RUANGAN");
        lblNowServing.setFont(new Font("Poppins", Font.BOLD, 18));
        lblNowServing.setForeground(Color.WHITE);
        panelNowServing.add(lblNowServing, BorderLayout.CENTER);
        panelInfo.add(panelNowServing, BorderLayout.CENTER);

        splitPaneVertical.setTopComponent(panelInfo);

        JSplitPane splitPaneHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneHorizontal.setDividerLocation(280);
        splitPaneHorizontal.setDividerSize(5);
        splitPaneHorizontal.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        splitPaneHorizontal.setOpaque(false);

        panelKartuAntrian = new JPanel();
        panelKartuAntrian.setLayout(new BoxLayout(panelKartuAntrian, BoxLayout.Y_AXIS));
        panelKartuAntrian.setBackground(COLOR_BG);
        JScrollPane scrollKartu = new JScrollPane(panelKartuAntrian);
        scrollKartu.setBorder(null);
        scrollKartu.getVerticalScrollBar().setUnitIncrement(16);
        splitPaneHorizontal.setLeftComponent(scrollKartu);

        JPanel panelTabel = new JPanel(new BorderLayout());
        panelTabel.setBackground(COLOR_CARD);
        panelTabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblTabel = new JLabel("Pasien Selesai Hari Ini");
        lblTabel.setFont(new Font("Poppins", Font.BOLD, 14));
        panelTabel.add(lblTabel, BorderLayout.NORTH);

        String[] columns = {"No", "Nama Pasien", "No.RM", "Dokter", "Status"};
        tableModelSelesai = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelSelesai = new JTable(tableModelSelesai);
        styleTable(tabelSelesai);
        JScrollPane scrollTabel = new JScrollPane(tabelSelesai);
        scrollTabel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panelTabel.add(scrollTabel, BorderLayout.CENTER);

        splitPaneHorizontal.setRightComponent(panelTabel);
        splitPaneVertical.setBottomComponent(splitPaneHorizontal);

        add(splitPaneVertical, BorderLayout.CENTER);
    }

    public void setNowServing(String namaPasien, String dokter) {
        if (namaPasien == null || namaPasien.isEmpty()) {
            lblNowServing.setText("TIDAK ADA PASIEN DI RUANGAN");
        } else {
            lblNowServing.setText("SEDANG DIPERIKSA: " + namaPasien + " (Ruang " + dokter + ")");
        }
    }

    public void setStatCount(int menunggu, int diperiksa, int selesai) {
        lblStatMenunggu.setText("Menunggu: " + menunggu);
        lblStatDiperiksa.setText("Diperiksa: " + diperiksa);
        lblStatSelesai.setText("Selesai: " + selesai);
    }

    public void clearAntrianCards() {
        panelKartuAntrian.removeAll();
        showEmptyState();
        panelKartuAntrian.revalidate();
        panelKartuAntrian.repaint();
    }

    public void showEmptyState() {
        emptyStatePanel = new JPanel();
        emptyStatePanel.setLayout(new BoxLayout(emptyStatePanel, BoxLayout.Y_AXIS));
        emptyStatePanel.setBackground(COLOR_BG);
        emptyStatePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyStatePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel icon = new JLabel("—", SwingConstants.CENTER);
        icon.setFont(new Font("Poppins", Font.BOLD, 32));
        icon.setForeground(COLOR_TEXT_MUTED);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg1 = new JLabel("Belum ada antrian", SwingConstants.CENTER);
        msg1.setFont(new Font("Poppins", Font.BOLD, 13));
        msg1.setForeground(COLOR_TEXT_MUTED);
        msg1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg2 = new JLabel("Antrian muncul setelah pasien didaftarkan", SwingConstants.CENTER);
        msg2.setFont(new Font("Poppins", Font.PLAIN, 11));
        msg2.setForeground(COLOR_TEXT_MUTED);
        msg2.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyStatePanel.add(Box.createVerticalGlue());
        emptyStatePanel.add(icon);
        emptyStatePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        emptyStatePanel.add(msg1);
        emptyStatePanel.add(Box.createRigidArea(new Dimension(0, 4)));
        emptyStatePanel.add(msg2);
        emptyStatePanel.add(Box.createVerticalGlue());

        panelKartuAntrian.add(emptyStatePanel);
        panelKartuAntrian.revalidate();
        panelKartuAntrian.repaint();
    }

    public void addAntrianCard(int nomorUrut, String nama, String rm, String dokter,
                               ActionListener onPanggil, ActionListener onBatal) {
        if (emptyStatePanel != null && emptyStatePanel.getParent() == panelKartuAntrian) {
            panelKartuAntrian.remove(emptyStatePanel);
        }

        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_PRIMARY),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(300, 100));

        JLabel lblNo = new JLabel("#" + nomorUrut);
        lblNo.setFont(new Font("Poppins", Font.BOLD, 20));
        lblNo.setForeground(COLOR_PRIMARY);
        card.add(lblNo, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel lblNama = new JLabel(nama);
        lblNama.setFont(new Font("Poppins", Font.BOLD, 12));
        JLabel lblSub = new JLabel(rm + " | " + dokter);
        lblSub.setFont(new Font("Poppins", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        info.add(lblNama);
        info.add(lblSub);
        card.add(info, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        if (onPanggil != null) {
            JButton btnPanggil = new JButton("Panggil");
            btnPanggil.setFont(new Font("Poppins", Font.BOLD, 10));
            btnPanggil.setBackground(COLOR_PRIMARY);
            btnPanggil.setForeground(Color.WHITE);
            btnPanggil.setFocusPainted(false);
            btnPanggil.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnPanggil.addActionListener(onPanggil);
            btnPanel.add(btnPanggil);
            btnPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        if (onBatal != null) {
            JButton btnBatal = new JButton("Batal");
            btnBatal.setFont(new Font("Poppins", Font.BOLD, 10));
            btnBatal.setBackground(new Color(231, 76, 60));
            btnBatal.setForeground(Color.WHITE);
            btnBatal.setFocusPainted(false);
            btnBatal.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnBatal.addActionListener(onBatal);
            btnPanel.add(btnBatal);
        }

        card.add(btnPanel, BorderLayout.EAST);

        panelKartuAntrian.add(card);
        panelKartuAntrian.add(Box.createRigidArea(new Dimension(0, 10)));
        panelKartuAntrian.revalidate();
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Poppins", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(color);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return lbl;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Poppins", Font.BOLD, 12));
        label.setForeground(COLOR_TEXT_MUTED);
        return label;
    }

    private void styleRoundedButton(JButton button, Color bgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Poppins", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 10, 10);
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    private void styleTable(JTable table) {
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setFont(new Font("Poppins", Font.PLAIN, 12));
        table.setRowHeight(30);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(245, 245, 245));
        header.setFont(new Font("Poppins", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }
    
    public void addFilterListener(ActionListener l) {
        btnFilter.addActionListener(l);
    }
    
    public ComboItem getFilterDokter() {
        return (ComboItem) cbFilterDokter.getSelectedItem();
    }
    
    public String getFilterTanggal() {
        return txtFilterTanggal.getText().trim();
    }
    
    public void setFilterTanggal(String tgl) {
        txtFilterTanggal.setText(tgl);
    }
    
    public DefaultTableModel getTableModelSelesai() {
        return tableModelSelesai;
    }
    
    public void setDokterList(ComboItem[] items) {
        cbFilterDokter.removeAllItems();
        cbFilterDokter.addItem(new ComboItem(0, "Semua Dokter")); // Opsi default
        for (ComboItem item : items) {
            cbFilterDokter.addItem(item);
        }
    }
}