package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarMenuView extends JPanel {
    private JLabel lblLogo;
    private JButton btnDashboard, btnPasien, btnKunjungan, btnAntrian;

    private final Color COLOR_PRIMARY = new Color(55, 194, 174); // #37c2ae
    
    public SidebarMenuView() {
        initUI();
    }

    private void initUI() {
        setBackground(COLOR_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(240, 0)); // Mengunci lebar sidebar

        // LOGO & BRAND
        lblLogo = new JLabel("Medika Center");
        lblLogo.setFont(new Font("Poppins", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLogo.setIconTextGap(15);
        try {
            ImageIcon iconAsli = new ImageIcon(getClass().getResource("/assets/logo.png"));
            Image gambar = iconAsli.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(gambar));
        } catch (Exception e) {
            System.err.println("Gagal memuat logo: " + e.getMessage());
        }

        add(lblLogo);
        add(Box.createRigidArea(new Dimension(0, 50)));

        // TOMBOL MENU
        btnDashboard = createMenuButton("Dashboard");
        btnPasien = createMenuButton("Pasien");
        btnKunjungan = createMenuButton("Kunjungan");
        btnAntrian = createMenuButton("Antrian");

        add(btnDashboard);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnPasien);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnKunjungan);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(btnAntrian);

        setMenuAktif(btnDashboard);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

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
            public void mousePressed(MouseEvent evt) {
                setMenuAktif(btn);
            }
        });

        return btn;
    }

    public void setMenuAktif(JButton btnAktif) {
        JButton[] buttons = {btnDashboard, btnPasien, btnKunjungan, btnAntrian};
        for (JButton btn : buttons) {
            btn.setBackground(COLOR_PRIMARY); 
            btn.setForeground(Color.WHITE);
        }
        btnAktif.setBackground(Color.WHITE); 
        btnAktif.setForeground(COLOR_PRIMARY);
    }

    // Eksekusi eksternal untuk interaksi routing dengan CardLayout di MainFrame
    public JButton getBtnDashboard() { return btnDashboard; }
    public JButton getBtnPasien() { return btnPasien; }
    public JButton getBtnKunjungan() { return btnKunjungan; }
    public JButton getBtnAntrian() { return btnAntrian; }
}