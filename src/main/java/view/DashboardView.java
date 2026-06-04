package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardView extends JPanel {
    
    
    private JLabel lblTotalPasien;
    private JLabel lblAntrianMenunggu;
    private JLabel lblKunjunganHariIni;
    private JLabel lblDokterAktif;
    
    private JTable tableAntrian;
    private DefaultTableModel tableModelAntrian;
    
    private JTable tableKunjungan;
    private DefaultTableModel tableModelKunjungan;
    
    private JButton btnRefresh;
    private JLabel lblLastUpdated;
    
    
    private final Color COLOR_SURFACE = new Color(240, 246, 246); 
    private final Color COLOR_PRIMARY = new Color(55, 194, 174);  
    private final Color COLOR_CARD = Color.WHITE;
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51);
    private final Color COLOR_TEXT_MUTED = new Color(130, 140, 145);
    private final Color COLOR_TABLE_GRAY = new Color(245, 245, 245);
    
    public DashboardView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_SURFACE);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_SURFACE);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 4, 4));
        titlePanel.setBackground(COLOR_SURFACE);
        
        JLabel titleLabel = new JLabel("Ringkasan Operasional Klinik");
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 24));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titlePanel.add(titleLabel);
        
        lblLastUpdated = new JLabel("Diperbarui: Baru saja");
        lblLastUpdated.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblLastUpdated.setForeground(COLOR_TEXT_MUTED);
        titlePanel.add(lblLastUpdated);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        btnRefresh = new JButton("Perbarui Data");
        styleRoundedButton(btnRefresh, COLOR_PRIMARY);
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        btnWrapper.setBackground(COLOR_SURFACE);
        btnWrapper.add(btnRefresh);
        headerPanel.add(btnWrapper, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(COLOR_SURFACE);
        
        
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsGrid.setBackground(COLOR_SURFACE);
        
        lblTotalPasien = new JLabel("0");
        lblAntrianMenunggu = new JLabel("0");
        lblKunjunganHariIni = new JLabel("0");
        lblDokterAktif = new JLabel("0");
        
        cardsGrid.add(createRoundedStatCard("TOTAL PASIEN", lblTotalPasien));
        cardsGrid.add(createRoundedStatCard("ANTRIAN MENUNGGU", lblAntrianMenunggu));
        cardsGrid.add(createRoundedStatCard("KUNJUNGAN HARI INI", lblKunjunganHariIni));
        cardsGrid.add(createRoundedStatCard("DOKTER AKTIF", lblDokterAktif));
        
        contentPanel.add(cardsGrid, BorderLayout.NORTH);
        
        
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setBackground(COLOR_SURFACE);
        
        
        String[] antrianCols = {"No", "Nama Pasien", "Dokter", "Status"};
        tableModelAntrian = new DefaultTableModel(antrianCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableAntrian = new JTable(tableModelAntrian);
        styleBasicTable(tableAntrian);
        tablesPanel.add(createRoundedTableWrapper("Antrian Pasien Hari Ini", tableAntrian));
        
        
        String[] kunjunganCols = {"Waktu", "Nama Pasien", "Keluhan", "Diagnosa"};
        tableModelKunjungan = new DefaultTableModel(kunjunganCols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableKunjungan = new JTable(tableModelKunjungan);
        styleBasicTable(tableKunjungan);
        tablesPanel.add(createRoundedTableWrapper("Kunjungan Pasien Terbaru", tableKunjungan));
        
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    
    
    private JPanel createRoundedStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                
                g2.setColor(COLOR_PRIMARY);
                g2.fillRoundRect(0, 0, 6, getHeight(), 20, 20);
                g2.fillRect(3, 0, 3, getHeight()); 
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 12));
        titleLabel.setForeground(COLOR_TEXT_MUTED);
        
        valueLabel.setFont(new Font("Poppins", Font.BOLD, 36));
        valueLabel.setForeground(COLOR_PRIMARY);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createRoundedTableWrapper(String title, JTable table) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15)) {
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
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT_DARK);
        wrapper.add(lblTitle, BorderLayout.NORTH);
        
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230))); 
        scroll.getViewport().setBackground(COLOR_CARD);
        wrapper.add(scroll, BorderLayout.CENTER);
        
        return wrapper;
    }
    
    private void styleBasicTable(JTable table) {
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT_DARK);
        table.setFont(new Font("Poppins", Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setGridColor(new Color(230, 230, 230)); 
        table.setShowGrid(true); 
        table.setSelectionBackground(COLOR_PRIMARY); 
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_TABLE_GRAY);
        header.setForeground(COLOR_TEXT_DARK);
        header.setFont(new Font("Poppins", Font.BOLD, 12));
        header.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
    }
    
    private void styleRoundedButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Poppins", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }
    
    
    
    public void setTotalPasien(String value) { lblTotalPasien.setText(value); }
    public void setAntrianMenunggu(String value) { lblAntrianMenunggu.setText(value); }
    public void setKunjunganHariIni(String value) { lblKunjunganHariIni.setText(value); }
    public void setDokterAktif(String value) { lblDokterAktif.setText(value); }
    
    public void setLastUpdatedText(String text) {
        lblLastUpdated.setText(text);
    }
    
    public DefaultTableModel getTableModelAntrian() { return tableModelAntrian; }
    public DefaultTableModel getTableModelKunjungan() { return tableModelKunjungan; }
    
    public void addRefreshListener(ActionListener l) { btnRefresh.addActionListener(l); }
}