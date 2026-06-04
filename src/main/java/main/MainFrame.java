package main;

import controller.*;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    private SidebarMenuView sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    
    private DashboardView dashboardView;
    private PasienView pasienView;
    private DokterView dokterView;
    private KunjunganView kunjunganView;
    private AntrianView antrianView;
    private ObatView obatView;
    private ResepMasukView resepMasukView;
    private TagihanView tagihanView;

    
    private DashboardController dashboardController;
    private PasienController pasienController;
    private DokterController dokterController;
    private KunjunganController kunjunganController;
    private AntrianController antrianController;
    private ObatController obatController;
    private ResepMasukController resepMasukController;
    private TagihanController tagihanController;

    private StokMonitorThread stokMonitorThread;
    
    private final Color COLOR_SURFACE = new Color(240, 246, 246); 
    
    public MainFrame() {
        setTitle("Sistem Manajemen Klinik - Medika Center");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        initMVC();
        
        
        String role = SessionManager.isLoggedIn() ? SessionManager.getUser().getRole().toLowerCase().trim() : "";
        if (role.equals("apoteker")) {
            switchPanel("OBAT");
        } else if (role.equals("dokter")) {
            switchPanel("ANTRIAN");
        } else {
            switchPanel("DASHBOARD");
        }
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (antrianController != null) {
                    antrianController.stopRefreshThread();
                }
                if (stokMonitorThread != null) {
                    stokMonitorThread.stopMonitor();
                }
            }
        });
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        
        sidebar = new SidebarMenuView();
        String role = SessionManager.isLoggedIn() ? SessionManager.getUser().getRole().toLowerCase().trim() : "";
        String name = SessionManager.isLoggedIn() ? SessionManager.getUser().getNama() : "User";
        sidebar.setupMenuForRole(role, name);
        
        add(sidebar, BorderLayout.WEST);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(COLOR_SURFACE);
        add(contentPanel, BorderLayout.CENTER);
        
        
        if (sidebar.getBtnDashboard() != null) sidebar.getBtnDashboard().addActionListener(e -> switchPanel("DASHBOARD"));
        if (sidebar.getBtnPasien() != null) sidebar.getBtnPasien().addActionListener(e -> switchPanel("PASIEN"));
        if (sidebar.getBtnDokter() != null) sidebar.getBtnDokter().addActionListener(e -> switchPanel("DOKTER"));
        if (sidebar.getBtnKunjungan() != null) sidebar.getBtnKunjungan().addActionListener(e -> switchPanel("KUNJUNGAN"));
        if (sidebar.getBtnAntrian() != null) sidebar.getBtnAntrian().addActionListener(e -> switchPanel("ANTRIAN"));
        if (sidebar.getBtnObat() != null) sidebar.getBtnObat().addActionListener(e -> switchPanel("OBAT"));
        if (sidebar.getBtnResepMasuk() != null) sidebar.getBtnResepMasuk().addActionListener(e -> switchPanel("RESEP_MASUK"));
        if (sidebar.getBtnTagihan() != null) sidebar.getBtnTagihan().addActionListener(e -> switchPanel("TAGIHAN"));
        
        if (sidebar.getBtnLogout() != null) {
            sidebar.getBtnLogout().addActionListener(e -> {
                if (antrianController != null) {
                    antrianController.stopRefreshThread();
                }
                if (stokMonitorThread != null) {
                    stokMonitorThread.stopMonitor();
                }
                SessionManager.logout();
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    new LoginController(loginView);
                    loginView.setVisible(true);
                });
            });
        }
    }
    
    private void initMVC() {
        String role = SessionManager.isLoggedIn() ? SessionManager.getUser().getRole().toLowerCase().trim() : "";
        
        if (role.equals("admin")) {
            
            dashboardView = new DashboardView();
            contentPanel.add(dashboardView, "DASHBOARD");
            dashboardController = new DashboardController(dashboardView);
            
            pasienView = new PasienView();
            contentPanel.add(pasienView, "PASIEN");
            pasienController = new PasienController(pasienView);
            
            dokterView = new DokterView();
            contentPanel.add(dokterView, "DOKTER");
            dokterController = new DokterController(dokterView);
            
            kunjunganView = new KunjunganView();
            contentPanel.add(kunjunganView, "KUNJUNGAN");
            kunjunganController = new KunjunganController(kunjunganView);
            
            antrianView = new AntrianView();
            contentPanel.add(antrianView, "ANTRIAN");
            antrianController = new AntrianController(antrianView);
            
            tagihanView = new TagihanView();
            contentPanel.add(tagihanView, "TAGIHAN");
            tagihanController = new TagihanController(tagihanView);

            obatView = new ObatView();
            contentPanel.add(obatView, "OBAT");
            obatController = new ObatController(obatView);
            
            resepMasukView = new ResepMasukView();
            contentPanel.add(resepMasukView, "RESEP_MASUK");
            resepMasukController = new ResepMasukController(resepMasukView);

            stokMonitorThread = new StokMonitorThread(obatController);
            stokMonitorThread.start();

        } else if (role.equals("resepsionis")) {
            
            dashboardView = new DashboardView();
            contentPanel.add(dashboardView, "DASHBOARD");
            dashboardController = new DashboardController(dashboardView);
            
            pasienView = new PasienView();
            contentPanel.add(pasienView, "PASIEN");
            pasienController = new PasienController(pasienView);
            
            dokterView = new DokterView();
            contentPanel.add(dokterView, "DOKTER");
            dokterController = new DokterController(dokterView);
            
            kunjunganView = new KunjunganView();
            contentPanel.add(kunjunganView, "KUNJUNGAN");
            kunjunganController = new KunjunganController(kunjunganView);
            
            antrianView = new AntrianView();
            contentPanel.add(antrianView, "ANTRIAN");
            antrianController = new AntrianController(antrianView);
            
            tagihanView = new TagihanView();
            contentPanel.add(tagihanView, "TAGIHAN");
            tagihanController = new TagihanController(tagihanView);
            
        } else if (role.equals("dokter")) {
            
            antrianView = new AntrianView();
            contentPanel.add(antrianView, "ANTRIAN");
            antrianController = new AntrianController(antrianView);

            kunjunganView = new KunjunganView();
            contentPanel.add(kunjunganView, "KUNJUNGAN");
            kunjunganController = new KunjunganController(kunjunganView);

        } else if (role.equals("apoteker")) {
            
            obatView = new ObatView();
            contentPanel.add(obatView, "OBAT");
            obatController = new ObatController(obatView);

            resepMasukView = new ResepMasukView();
            contentPanel.add(resepMasukView, "RESEP_MASUK");
            resepMasukController = new ResepMasukController(resepMasukView);

            stokMonitorThread = new StokMonitorThread(obatController);
            stokMonitorThread.start();
        }

        
        if (antrianController != null && kunjunganController != null) {
            final KunjunganController kc = kunjunganController;
            antrianController.setOnPanggilListener(info -> {
                kc.autoFillFromAntrian(
                    info.idPasien, info.nama, info.noRM,
                    info.namaDokter, info.idDokter
                );
                switchPanel("KUNJUNGAN");
            });

            kunjunganController.setOnSelesaiListener(() -> {
                if (antrianController != null) {
                    antrianController.loadData(true);
                }
            });
        }
    }
    
    private void switchPanel(String cardName) {
        if (cardLayout == null || contentPanel == null) return;
        
        
        boolean exists = false;
        if ("DASHBOARD".equals(cardName) && dashboardView != null) exists = true;
        else if ("PASIEN".equals(cardName) && pasienView != null) exists = true;
        else if ("DOKTER".equals(cardName) && dokterView != null) exists = true;
        else if ("KUNJUNGAN".equals(cardName) && kunjunganView != null) exists = true;
        else if ("ANTRIAN".equals(cardName) && antrianView != null) exists = true;
        else if ("OBAT".equals(cardName) && obatView != null) exists = true;
        else if ("RESEP_MASUK".equals(cardName) && resepMasukView != null) exists = true;
        else if ("TAGIHAN".equals(cardName) && tagihanView != null) exists = true;
        
        if (!exists) return; 
        
        cardLayout.show(contentPanel, cardName);
        if (sidebar != null) {
            sidebar.setMenuAktif(cardName);
        }
        
        
        if ("DASHBOARD".equals(cardName)) {
            if (dashboardController != null) dashboardController.loadData();
        } else if ("PASIEN".equals(cardName)) {
            if (pasienController != null) pasienController.loadData();
        } else if ("KUNJUNGAN".equals(cardName)) {
            if (kunjunganController != null) {
                kunjunganController.loadData();
            }
        } else if ("ANTRIAN".equals(cardName)) {
            if (antrianController != null) {
                antrianController.loadDropdowns();
                antrianController.loadData(true);
            }
        } else if ("OBAT".equals(cardName)) {
            if (obatController != null) obatController.loadData();
        } else if ("RESEP_MASUK".equals(cardName)) {
            if (resepMasukController != null) resepMasukController.loadDaftarResep();
        }
    }
}