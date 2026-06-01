package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class SidebarMenuView extends JPanel {
    private JLabel lblLogo;
    private JLabel lblUser;
    
    private JButton btnDashboard;
    private JButton btnPasien;
    private JButton btnDokter;
    private JButton btnKunjungan;
    private JButton btnAntrian;
    private JButton btnObat;
    private JButton btnResepMasuk;
    private JButton btnResep;
    private JButton btnTagihan;
    private JButton btnLogout;

    private final Color COLOR_PRIMARY = new Color(55, 194, 174); // #37c2ae
    private final Color COLOR_PRIMARY_HOVER = new Color(45, 175, 155); // #2daf9b
    private Map<String, JButton> buttonMap = new HashMap<>();

    public SidebarMenuView() {
        setBackground(COLOR_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(250, 0)); // Mengunci lebar sidebar
    }

    public void setupMenuForRole(String role, String namaUser) {
        removeAll();
        buttonMap.clear();

        // 1. LOGO & BRAND
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.X_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblLogo = new JLabel();
        try {
            ImageIcon iconAsli = new ImageIcon(getClass().getResource("/assets/logo.png"));
            Image gambar = iconAsli.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(gambar));
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
        }

        JLabel lblLogoText = new JLabel("Medika Center");
        lblLogoText.setFont(new Font("Poppins", Font.BOLD, 20));
        lblLogoText.setForeground(Color.WHITE);

        brandPanel.add(lblLogo);
        brandPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        brandPanel.add(lblLogoText);

        add(brandPanel);

        // 2. USER INFO
        lblUser = new JLabel("Halo, " + namaUser);
        lblUser.setFont(new Font("Poppins", Font.PLAIN, 12));
        lblUser.setForeground(new Color(255, 255, 255, 200));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(lblUser);
        add(Box.createRigidArea(new Dimension(0, 30)));

        // Inisialisasi semua tombol menu
        btnDashboard = createMenuButton("Dashboard", "DASHBOARD");
        btnPasien = createMenuButton("Data Pasien", "PASIEN");
        btnDokter = createMenuButton("Data Dokter", "DOKTER");
        btnKunjungan = createMenuButton("Kunjungan", "KUNJUNGAN");
        btnAntrian = createMenuButton("Antrian", "ANTRIAN");
        btnObat = createMenuButton("Data Obat", "OBAT");
        btnResepMasuk = createMenuButton("Resep Masuk", "RESEP_MASUK");
        btnResep = createMenuButton("Resep", "RESEP");
        btnTagihan = createMenuButton("Tagihan", "TAGIHAN");

        role = role.toLowerCase();

        // 3. TAMBAHKAN MENU SESUAI ROLE
        if (role.equals("admin")) {
            addMenu(btnDashboard);
            addMenu(btnPasien);
            addMenu(btnDokter);
            addMenu(btnKunjungan);
            addMenu(btnAntrian);
            addMenu(btnObat);
            addMenu(btnResepMasuk);
            addMenu(btnResep);
            addMenu(btnTagihan);
        } else if (role.equals("resepsionis")) {
            addMenu(btnDashboard);
            addMenu(btnPasien);
            addMenu(btnDokter);
            addMenu(btnKunjungan);
            addMenu(btnAntrian);
            addMenu(btnTagihan);
        } else if (role.equals("dokter")) {
            addMenu(btnKunjungan);
            addMenu(btnResep);
            addMenu(btnAntrian);
        } else if (role.equals("apoteker")) {
            addMenu(btnObat);
            addMenu(btnResepMasuk);
        }

        add(Box.createVerticalGlue());

        // 4. LOGOUT BUTTON
        btnLogout = createMenuButton("Keluar", "LOGOUT");
        btnLogout.setForeground(new Color(255, 200, 200));
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(btnLogout);

        revalidate();
        repaint();
    }

    private void addMenu(JButton btn) {
        add(btn);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);

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

        buttonMap.put(cardName, btn);
        return btn;
    }

    public void setMenuAktif(String cardName) {
        for (Map.Entry<String, JButton> entry : buttonMap.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(cardName)) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(COLOR_PRIMARY);
            } else {
                btn.setBackground(COLOR_PRIMARY);
                if (entry.getKey().equals("LOGOUT")) {
                    btn.setForeground(new Color(255, 200, 200));
                } else {
                    btn.setForeground(Color.WHITE);
                }
            }
        }
    }

    // Getters untuk Action Listener di MainFrame
    public JButton getBtnDashboard() { return btnDashboard; }
    public JButton getBtnPasien() { return btnPasien; }
    public JButton getBtnDokter() { return btnDokter; }
    public JButton getBtnKunjungan() { return btnKunjungan; }
    public JButton getBtnAntrian() { return btnAntrian; }
    public JButton getBtnObat() { return btnObat; }
    public JButton getBtnResepMasuk() { return btnResepMasuk; }
    public JButton getBtnResep() { return btnResep; }
    public JButton getBtnTagihan() { return btnTagihan; }
    public JButton getBtnLogout() { return btnLogout; }
}