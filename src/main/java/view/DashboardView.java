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
    
    // Style constants
    private final Color COLOR_BG = new Color(18, 18, 20);
    private final Color COLOR_CARD = new Color(34, 34, 40);
    private final Color COLOR_PRIMARY = new Color(124, 77, 255); // Electric Violet
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_TEXT_MUTED = new Color(160, 160, 170);
    private final Color COLOR_BORDER = new Color(45, 45, 52);
    
    public DashboardView() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        
        initHeader();
        initContent();
    }
    
    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 4, 4));
        titlePanel.setBackground(COLOR_BG);
        
        JLabel titleLabel = new JLabel("Ringkasan Operasional Klinik");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(COLOR_TEXT);
        titlePanel.add(titleLabel);
        
        lblLastUpdated = new JLabel("Diperbarui: Baru saja");
        lblLastUpdated.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLastUpdated.setForeground(COLOR_TEXT_MUTED);
        titlePanel.add(lblLastUpdated);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        btnRefresh = new JButton("Perbarui Data");
        styleButton(btnRefresh, COLOR_PRIMARY);
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        btnWrapper.setBackground(COLOR_BG);
        btnWrapper.add(btnRefresh);
        headerPanel.add(btnWrapper, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void initContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(COLOR_BG);
        
        // Cards Grid (1 row, 4 columns)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsGrid.setBackground(COLOR_BG);
        
        JPanel cardPasien = createStatCard("TOTAL PASIEN", "0", new Color(124, 77, 255)); // Violet
        lblTotalPasien = (JLabel) cardPasien.getClientProperty("valueLabel");
        
        JPanel cardAntrian = createStatCard("ANTRIAN MENUNGGU", "0", new Color(41, 121, 255)); // Blue
        lblAntrianMenunggu = (JLabel) cardAntrian.getClientProperty("valueLabel");
        
        JPanel cardKunjungan = createStatCard("KUNJUNGAN HARI INI", "0", new Color(76, 175, 80)); // Green
        lblKunjunganHariIni = (JLabel) cardKunjungan.getClientProperty("valueLabel");
        
        JPanel cardDokter = createStatCard("DOKTER AKTIF", "0", new Color(255, 152, 0)); // Orange
        lblDokterAktif = (JLabel) cardDokter.getClientProperty("valueLabel");
        
        cardsGrid.add(cardPasien);
        cardsGrid.add(cardAntrian);
        cardsGrid.add(cardKunjungan);
        cardsGrid.add(cardDokter);
        
        contentPanel.add(cardsGrid, BorderLayout.NORTH);
        
        // Tables split pane (Left: Today's Queue, Right: Today's Visits)
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setBackground(COLOR_BG);
        
        // Left Table Panel
        JPanel antrianPanel = new JPanel(new BorderLayout(10, 10));
        antrianPanel.setBackground(COLOR_CARD);
        antrianPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel antrianTitle = new JLabel("Antrian Pasien Hari Ini");
        antrianTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        antrianTitle.setForeground(COLOR_TEXT);
        antrianPanel.add(antrianTitle, BorderLayout.NORTH);
        
        String[] antrianCols = {"No", "Nama Pasien", "Dokter", "Status"};
        tableModelAntrian = new DefaultTableModel(antrianCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableAntrian = new JTable(tableModelAntrian);
        styleTable(tableAntrian);
        JScrollPane scrollAntrian = new JScrollPane(tableAntrian);
        scrollAntrian.getViewport().setBackground(COLOR_CARD);
        scrollAntrian.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        antrianPanel.add(scrollAntrian, BorderLayout.CENTER);
        
        // Right Table Panel
        JPanel kunjunganPanel = new JPanel(new BorderLayout(10, 10));
        kunjunganPanel.setBackground(COLOR_CARD);
        kunjunganPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel kunjunganTitle = new JLabel("Kunjungan Pasien Terbaru");
        kunjunganTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        kunjunganTitle.setForeground(COLOR_TEXT);
        kunjunganPanel.add(kunjunganTitle, BorderLayout.NORTH);
        
        String[] kunjunganCols = {"Waktu", "Nama Pasien", "Keluhan", "Diagnosa"};
        tableModelKunjungan = new DefaultTableModel(kunjunganCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tableKunjungan = new JTable(tableModelKunjungan);
        styleTable(tableKunjungan);
        JScrollPane scrollKunjungan = new JScrollPane(tableKunjungan);
        scrollKunjungan.getViewport().setBackground(COLOR_CARD);
        scrollKunjungan.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        kunjunganPanel.add(scrollKunjungan, BorderLayout.CENTER);
        
        tablesPanel.add(antrianPanel);
        tablesPanel.add(kunjunganPanel);
        
        contentPanel.add(tablesPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatCard(String title, String initialVal, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(COLOR_TEXT_MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(initialVal);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(COLOR_TEXT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);
        
        // Save referene for later update
        card.putClientProperty("valueLabel", valueLabel);
        
        // Add subtle hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(COLOR_CARD.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_CARD);
            }
        });
        
        return card;
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }
    
    private void styleTable(JTable table) {
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_BORDER);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(26);
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(COLOR_BORDER);
        header.setForeground(COLOR_TEXT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createEmptyBorder());
        
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(COLOR_CARD);
        renderer.setForeground(COLOR_TEXT);
        table.setDefaultRenderer(Object.class, renderer);
    }
    
    // Setters for card stats
    public void setTotalPasien(String value) { lblTotalPasien.setText(value); }
    public void setAntrianMenunggu(String value) { lblAntrianMenunggu.setText(value); }
    public void setKunjunganHariIni(String value) { lblKunjunganHariIni.setText(value); }
    public void setDokterAktif(String value) { lblDokterAktif.setText(value); }
    
    public void setLastUpdatedText(String text) {
        lblLastUpdated.setText("Diperbarui: " + text);
    }
    
    public DefaultTableModel getTableModelAntrian() { return tableModelAntrian; }
    public DefaultTableModel getTableModelKunjungan() { return tableModelKunjungan; }
    
    public void addRefreshListener(ActionListener l) { btnRefresh.addActionListener(l); }
}
