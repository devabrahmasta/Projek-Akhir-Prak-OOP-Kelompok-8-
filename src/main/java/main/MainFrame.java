package main;

import controller.AntrianController;
import controller.DashboardController;
import controller.KunjunganController;
import controller.PasienController;
import view.AntrianView;
import view.DashboardView;
import view.KunjunganView;
import view.PasienView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    // Sidebar buttons
    private JButton btnDashboard;
    private JButton btnPasien;
    private JButton btnKunjungan;
    private JButton btnAntrian;
    
    // Views
    private DashboardView dashboardView;
    private PasienView pasienView;
    private KunjunganView kunjunganView;
    private AntrianView antrianView;
    
    // Controllers
    private DashboardController dashboardController;
    private PasienController pasienController;
    private KunjunganController kunjunganController;
    private AntrianController antrianController;
    
    // Theme colors
    private final Color COLOR_SIDEBAR = new Color(0x1F, 0x6F, 0x5F); // Dark Teal
    private final Color COLOR_BG = new Color(0xEE, 0xEE, 0xEE); // Light Grey
    private final Color COLOR_PRIMARY = new Color(0x2F, 0xA0, 0x84); // Sea Green
    private final Color COLOR_TEXT = Color.WHITE;
    private final Color COLOR_TEXT_MUTED = new Color(0x9E, 0xDF, 0xD4); // Mint/Light Grey-Teal for sidebar labels
    
    public MainFrame() {
        setTitle("Sistem Manajemen Klinik - Medika Center");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        initMVC();
        
        // Show dashboard by default
        switchPanel("DASHBOARD");
        
        // Handle clean thread shutdown on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (antrianController != null) {
                    antrianController.stopRefreshThread();
                }
            }
        });
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(45, 45, 52)));
        
        // Branding/Logo
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 25));
        brandPanel.setBackground(COLOR_SIDEBAR);
        brandPanel.setMaximumSize(new Dimension(240, 80));
        
        JLabel logoLabel = new JLabel("✚ ");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(COLOR_PRIMARY);
        
        JLabel brandName = new JLabel("Medika Center");
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brandName.setForeground(COLOR_TEXT);
        
        brandPanel.add(logoLabel);
        brandPanel.add(brandName);
        sidebar.add(brandPanel);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Sidebar Navigation Buttons
        btnDashboard = createSidebarButton("Dashboard", "DASHBOARD");
        btnPasien = createSidebarButton("Data Pasien", "PASIEN");
        btnKunjungan = createSidebarButton("Catatan Kunjungan", "KUNJUNGAN");
        btnAntrian = createSidebarButton("Antrian Klinik", "ANTRIAN");
        
        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnPasien);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnKunjungan);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnAntrian);
        
        // Footer / Version
        sidebar.add(Box.createVerticalGlue());
        JLabel versionLabel = new JLabel("v1.0.0 - Person A Module");
        versionLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        versionLabel.setForeground(COLOR_TEXT_MUTED);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        sidebar.add(versionLabel);
        
        add(sidebar, BorderLayout.WEST);
        
        // Right Main Content Panel (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_BG);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(COLOR_TEXT_MUTED);
        btn.setBackground(COLOR_SIDEBAR);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getForeground() != COLOR_TEXT) { // If not active
                    btn.setBackground(new Color(0x14, 0x48, 0x3E)); // Darker Teal Hover
                    btn.setForeground(COLOR_TEXT);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getForeground() != COLOR_TEXT || btn.getBackground() != COLOR_PRIMARY) { // If not active
                    btn.setBackground(COLOR_SIDEBAR);
                    btn.setForeground(COLOR_TEXT_MUTED);
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                switchPanel(cardName);
            }
        });
        
        return btn;
    }
    
    private void initMVC() {
        // Instantiate Views
        dashboardView = new DashboardView();
        pasienView = new PasienView();
        kunjunganView = new KunjunganView();
        antrianView = new AntrianView();
        
        // Add to CardLayout
        contentPanel.add(dashboardView, "DASHBOARD");
        contentPanel.add(pasienView, "PASIEN");
        contentPanel.add(kunjunganView, "KUNJUNGAN");
        contentPanel.add(antrianView, "ANTRIAN");
        
        // Instantiate Controllers
        dashboardController = new DashboardController(dashboardView);
        pasienController = new PasienController(pasienView);
        kunjunganController = new KunjunganController(kunjunganView);
        antrianController = new AntrianController(antrianView);
    }
    
    private void switchPanel(String cardName) {
        cardLayout.show(contentPanel, cardName);
        
        // Update sidebar buttons visual active states
        resetSidebarButtons();
        
        if ("DASHBOARD".equals(cardName)) {
            styleActiveButton(btnDashboard);
            if (dashboardController != null) dashboardController.loadData();
        } else if ("PASIEN".equals(cardName)) {
            styleActiveButton(btnPasien);
            if (pasienController != null) pasienController.loadData();
        } else if ("KUNJUNGAN".equals(cardName)) {
            styleActiveButton(btnKunjungan);
            if (kunjunganController != null) {
                kunjunganController.loadDropdowns(); // Patients could have been updated
                kunjunganController.loadData();
            }
        } else if ("ANTRIAN".equals(cardName)) {
            styleActiveButton(btnAntrian);
            if (antrianController != null) {
                antrianController.loadDropdowns(); // Dropdowns could have been updated
                antrianController.loadData(true);
            }
        }
    }
    
    private void resetSidebarButtons() {
        JButton[] buttons = {btnDashboard, btnPasien, btnKunjungan, btnAntrian};
        for (JButton btn : buttons) {
            btn.setBackground(COLOR_SIDEBAR);
            btn.setForeground(COLOR_TEXT_MUTED);
            btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        }
    }
    
    private void styleActiveButton(JButton btn) {
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(COLOR_TEXT);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(12, 20, 12, 20),
            BorderFactory.createMatteBorder(0, 4, 0, 0, Color.WHITE)
        ));
    }
}
