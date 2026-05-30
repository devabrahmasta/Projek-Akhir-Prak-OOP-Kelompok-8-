package main;

import controller.AntrianController;
import controller.DashboardController;
import controller.KunjunganController;
import controller.PasienController;
import controller.SessionManager;
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
import view.LoginView;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    private JButton btnDashboard;
    private JButton btnPasien;
    private JButton btnKunjungan;
    private JButton btnAntrian;
    
    private DashboardView dashboardView;
    private PasienView pasienView;
    private KunjunganView kunjunganView;
    private AntrianView antrianView;
    
    private DashboardController dashboardController;
    private PasienController pasienController;
    private KunjunganController kunjunganController;
    private AntrianController antrianController;
    
    // --- COLOR PALETTE MODERN ---
    private final Color COLOR_PRIMARY = new Color(55, 194, 174); // #37c2ae
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); // #2daf9b
    private final Color COLOR_SURFACE = new Color(240, 246, 246); // #f0f6f6
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51); // #333333
    
    public MainFrame() {
        setTitle("Sistem Manajemen Klinik - Medika Center");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        initMVC();
        
        // Mulai dari halaman Dashboard
        switchPanel("DASHBOARD");
        
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
        
        // --- SIDEBAR MODERN ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBackground(COLOR_PRIMARY);
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        // Logo & Brand
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.X_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblLogoImage = new JLabel();
        try {
            ImageIcon iconAsli = new ImageIcon(getClass().getResource("/assets/logo.png"));
            Image gambar = iconAsli.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            lblLogoImage.setIcon(new ImageIcon(gambar));
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
        }
        
        JLabel lblLogoText = new JLabel("Medika Center");
        lblLogoText.setFont(new Font("Poppins", Font.BOLD, 20));
        lblLogoText.setForeground(Color.WHITE);
        
        brandPanel.add(lblLogoImage);
        brandPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        brandPanel.add(lblLogoText);
        
        sidebar.add(brandPanel);
        
        // Tampilkan info user yang login
        if (SessionManager.isLoggedIn()) {
            JLabel lblUser = new JLabel("Halo, " + SessionManager.getUser().getNama());
            lblUser.setFont(new Font("Poppins", Font.PLAIN, 12));
            lblUser.setForeground(new Color(255, 255, 255, 200));
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(lblUser);
        }

        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Menu Buttons - Inisialisasi
        btnDashboard = createSidebarButton("Dashboard", "DASHBOARD");
        btnPasien = createSidebarButton("Data Pasien", "PASIEN");
        btnKunjungan = createSidebarButton("Kunjungan", "KUNJUNGAN");
        btnAntrian = createSidebarButton("Antrian", "ANTRIAN");
        // Asumsi tombol tagihan dibuat (sesuaikan dengan inisialisasi view Tagihan)
        // btnTagihan = createSidebarButton("Tagihan", "TAGIHAN"); 
        
        // --- LOGIKA FILTER ROLE ---
        String role = SessionManager.isLoggedIn() ? SessionManager.getUser().getRole().toLowerCase() : "";
        
        if (role.equals("admin") || role.equals("resepsionis")) {
            sidebar.add(btnDashboard);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btnPasien);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            sidebar.add(btnAntrian);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
            // sidebar.add(btnTagihan); // Buka komentar jika TagihanView di-load di MainFrame
        }
        
        if (role.equals("admin") || role.equals("dokter") || role.equals("resepsionis")) {
            sidebar.add(btnKunjungan); // Resepsionis bisa melihat riwayat
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());
        
        // Logout Button
        JButton btnLogout = createSidebarButton("Keluar", "");
        btnLogout.setForeground(new Color(255, 200, 200));
        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            this.dispose();
            new LoginView().setVisible(true); // Kembali ke halaman login
        });
        sidebar.add(btnLogout);
        
        add(sidebar, BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_SURFACE);
        add(contentPanel, BorderLayout.CENTER);
        
        contentPanel.add(new DashboardView(), "DASHBOARD");
        contentPanel.add(new PasienView(), "PASIEN");
        contentPanel.add(new KunjunganView(), "KUNJUNGAN");
        contentPanel.add(new AntrianView(), "ANTRIAN");
        
        add(contentPanel, BorderLayout.CENTER);
        
        if (btnDashboard != null) btnDashboard.addActionListener(e -> showView("DASHBOARD"));
        if (btnPasien != null) btnPasien.addActionListener(e -> showView("PASIEN"));
        if (btnKunjungan != null) btnKunjungan.addActionListener(e -> showView("KUNJUNGAN"));
        if (btnAntrian != null) btnAntrian.addActionListener(e -> showView("ANTRIAN"));
    }
    
    public void showView(String viewName) {
        if (cardLayout != null && contentPanel != null) {
            cardLayout.show(contentPanel, viewName);
        }
    }
    
    private JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        // Matikan default styling Swing
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        
        // Custom Rounded UI
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 15, 15);
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                switchPanel(cardName);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground().equals(COLOR_PRIMARY)) {
                    btn.setBackground(COLOR_PRIMARY_HOVER);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground().equals(COLOR_PRIMARY_HOVER)) {
                    btn.setBackground(COLOR_PRIMARY);
                }
            }
        });
        
        return btn;
    }
    
    private void initMVC() {
        // Inisialisasi View
        dashboardView = new DashboardView();
        pasienView = new PasienView();
        kunjunganView = new KunjunganView();
        antrianView = new AntrianView();
        
        // Daftarkan View ke CardLayout
        contentPanel.add(dashboardView, "DASHBOARD");
        contentPanel.add(pasienView, "PASIEN");
        contentPanel.add(kunjunganView, "KUNJUNGAN");
        contentPanel.add(antrianView, "ANTRIAN");
        
        // Inisialisasi Controller
        dashboardController = new DashboardController(dashboardView);
        pasienController = new PasienController(pasienView);
        kunjunganController = new KunjunganController(kunjunganView);
        antrianController = new AntrianController(antrianView);
    }
    
    private void switchPanel(String cardName) {
        cardLayout.show(contentPanel, cardName);
        
        // Reset warna semua tombol ke default
        JButton[] buttons = {btnDashboard, btnPasien, btnKunjungan, btnAntrian};
        for (JButton btn : buttons) {
            btn.setBackground(COLOR_PRIMARY);
            btn.setForeground(Color.WHITE);
        }
        
        // Beri warna khusus pada tombol yang aktif (Background Putih, Teks Tosca)
        if ("DASHBOARD".equals(cardName)) {
            styleActiveButton(btnDashboard);
            if (dashboardController != null) dashboardController.loadData();
        } else if ("PASIEN".equals(cardName)) {
            styleActiveButton(btnPasien);
            if (pasienController != null) pasienController.loadData();
        } else if ("KUNJUNGAN".equals(cardName)) {
            styleActiveButton(btnKunjungan);
            if (kunjunganController != null) {
                kunjunganController.loadDropdowns(); 
                kunjunganController.loadData();
            }
        } else if ("ANTRIAN".equals(cardName)) {
            styleActiveButton(btnAntrian);
            if (antrianController != null) {
                antrianController.loadDropdowns(); 
                antrianController.loadData(true);
            }
        }
    }
    
    private void styleActiveButton(JButton btn) {
        btn.setBackground(Color.WHITE);
        btn.setForeground(COLOR_PRIMARY);
    }
}