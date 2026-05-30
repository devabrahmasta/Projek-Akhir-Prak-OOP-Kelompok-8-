package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.util.List;

public class ResepMasukView extends JPanel {
    
    // Panel Kiri - Daftar Resep
    private JList<Object> listResep;
    private DefaultListModel<Object> listResepModel;
    private JButton btnKonfirmasi;
    private JButton btnRefresh;
    
    // Panel Kanan - Detail Resep
    private JLabel lblNoResep;
    private JLabel lblPasien;
    private JLabel lblDokter;
    private JLabel lblTanggal;
    private JLabel lblStatusResep;
    
    private JTable tabelDetail;
    private DefaultTableModel tabelDetailModel;
    
    // --- COLOR PALETTE MODERN ---
    private final Color COLOR_BG = new Color(240, 246, 246);
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); 
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); 
    private final Color COLOR_TEXT = new Color(51, 51, 51); 
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    
    public ResepMasukView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Konfirmasi Resep Masuk");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        btnRefresh = new JButton("Refresh Data");
        styleRoundedButton(btnRefresh, new Color(52, 152, 219), new Color(41, 128, 185)); // Biru
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320); 
        splitPane.setBorder(null);
        splitPane.setBackground(COLOR_BG);
        splitPane.setOpaque(false);
        
        // ==========================================
        // PANEL KIRI: Daftar Resep Pending
        // ==========================================
        JPanel leftWrapper = createRoundedWrapper();
        
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setOpaque(false);
        
        JLabel lblTitleResep = new JLabel("Resep Masuk");
        lblTitleResep.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTitleResep.setForeground(COLOR_TEXT);
        lblTitleResep.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        leftPanel.add(lblTitleResep, BorderLayout.NORTH);
        
        listResepModel = new DefaultListModel<>();
        listResep = new JList<>(listResepModel);
        listResep.setBackground(COLOR_CARD);
        listResep.setFont(new Font("Poppins", Font.PLAIN, 13));
        listResep.setSelectionBackground(COLOR_PRIMARY);
        listResep.setSelectionForeground(Color.WHITE);
        listResep.setFixedCellHeight(40);
        
        listResep.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
        
        listResep.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = listResep.getSelectedIndex() != -1;
                btnKonfirmasi.setEnabled(hasSelection);
            }
        });
        
        JScrollPane scrollResep = new JScrollPane(listResep);
        scrollResep.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollResep.getViewport().setBackground(COLOR_CARD);
        leftPanel.add(scrollResep, BorderLayout.CENTER);
        
        btnKonfirmasi = new JButton("Konfirmasi Obat Selesai");
        styleRoundedButton(btnKonfirmasi, COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        btnKonfirmasi.setEnabled(false);
        leftPanel.add(btnKonfirmasi, BorderLayout.SOUTH);
        
        leftWrapper.add(leftPanel, BorderLayout.CENTER);
        
        // ==========================================
        // PANEL KANAN: Detail Resep
        // ==========================================
        JPanel rightWrapper = createRoundedWrapper();
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setOpaque(false);
        
        // 1. Info Header Resep
        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setOpaque(false);
        
        JLabel lblDetailTitle = new JLabel("Detail Resep Pasien");
        lblDetailTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblDetailTitle.setForeground(COLOR_TEXT);
        lblDetailTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        infoWrapper.add(lblDetailTitle, BorderLayout.NORTH);
        
        JPanel infoGrid = new JPanel(new GridLayout(4, 2, 10, 10));
        infoGrid.setOpaque(false);
        
        infoGrid.add(createFormLabel("No. Resep:"));
        lblNoResep = new JLabel("-");
        lblNoResep.setFont(new Font("Poppins", Font.BOLD, 13));
        lblNoResep.setForeground(COLOR_TEXT);
        infoGrid.add(lblNoResep);
        
        infoGrid.add(createFormLabel("Nama Pasien:"));
        lblPasien = new JLabel("-");
        lblPasien.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblPasien.setForeground(COLOR_TEXT);
        infoGrid.add(lblPasien);
        
        infoGrid.add(createFormLabel("Dokter Penulis:"));
        lblDokter = new JLabel("-");
        lblDokter.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblDokter.setForeground(COLOR_TEXT);
        infoGrid.add(lblDokter);
        
        infoGrid.add(createFormLabel("Tanggal/Jam Kunjungan:"));
        lblTanggal = new JLabel("-");
        lblTanggal.setFont(new Font("Poppins", Font.PLAIN, 13));
        lblTanggal.setForeground(COLOR_TEXT);
        infoGrid.add(lblTanggal);
        
        infoWrapper.add(infoGrid, BorderLayout.CENTER);
        
        lblStatusResep = new JLabel("Belum Dipilih");
        lblStatusResep.setFont(new Font("Poppins", Font.BOLD, 14));
        lblStatusResep.setForeground(COLOR_TEXT_MUTED);
        lblStatusResep.setHorizontalAlignment(SwingConstants.RIGHT);
        infoWrapper.add(lblStatusResep, BorderLayout.EAST);
        
        rightPanel.add(infoWrapper, BorderLayout.NORTH);
        
        // 2. Tabel Detail Obat
        String[] columns = {"Nama Obat", "Jumlah", "Satuan", "Dosis"};
        tabelDetailModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelDetail = new JTable(tabelDetailModel);
        styleTable(tabelDetail);
        
        JScrollPane scrollDetail = new JScrollPane(tabelDetail);
        scrollDetail.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollDetail.getViewport().setBackground(COLOR_CARD);
        rightPanel.add(scrollDetail, BorderLayout.CENTER);
        
        rightWrapper.add(rightPanel, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftWrapper);
        splitPane.setRightComponent(rightWrapper);
        add(splitPane, BorderLayout.CENTER);
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
    
    // --- GETTERS & SETTERS ---
    
    public void setDaftarResep(List<?> items) {
        listResepModel.clear();
        if (items != null) {
            for (Object item : items) {
                listResepModel.addElement(item);
            }
        }
    }
    
    public int getSelectedResepIndex() {
        return listResep.getSelectedIndex();
    }
    
    public void clearDetail() {
        lblNoResep.setText("-");
        lblPasien.setText("-");
        lblDokter.setText("-");
        lblTanggal.setText("-");
        lblStatusResep.setText("Belum Dipilih");
        lblStatusResep.setForeground(COLOR_TEXT_MUTED);
        tabelDetailModel.setRowCount(0);
        btnKonfirmasi.setEnabled(false);
    }
    
    public void setDetail(String noResep, String pasien, String dokter, String tanggal, boolean isSiap) {
        lblNoResep.setText(noResep);
        lblPasien.setText(pasien);
        lblDokter.setText(dokter);
        lblTanggal.setText(tanggal);
        
        if (isSiap) {
            lblStatusResep.setText("Sudah Disiapkan");
            lblStatusResep.setForeground(new Color(39, 174, 96)); // Hijau
            btnKonfirmasi.setEnabled(false);
        } else {
            lblStatusResep.setText("Belum Disiapkan");
            lblStatusResep.setForeground(new Color(230, 126, 34)); // Oranye
            btnKonfirmasi.setEnabled(true);
        }
    }
    
    public void setStatusResepLabel(String status, boolean isSiap) {
        lblStatusResep.setText(status);
        if (isSiap) {
            lblStatusResep.setForeground(new Color(39, 174, 96)); // Hijau
        } else {
            lblStatusResep.setForeground(new Color(230, 126, 34)); // Oranye
        }
    }
    
    public DefaultTableModel getTabelDetailModel() {
        return tabelDetailModel;
    }
    
    public JList<Object> getListResep() {
        return listResep;
    }
    
    // --- LISTENERS ---
    
    public void addListResepSelectListener(javax.swing.event.ListSelectionListener l) {
        listResep.addListSelectionListener(l);
    }
    
    public void addBtnKonfirmasiListener(ActionListener l) {
        btnKonfirmasi.addActionListener(l);
    }
    
    public void addBtnRefreshListener(ActionListener l) {
        btnRefresh.addActionListener(l);
    }
}
