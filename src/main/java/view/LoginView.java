package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnMasuk;
    private JLabel lblError;

    public LoginView() {
        setTitle("Login - Medika Center");
        setSize(420, 320);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 246, 246));

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 246, 246));

        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(224, 224, 224)); 
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(340, 240));
        cardPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 20, 5, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        JLabel lblJudul = new JLabel("Medika Center", SwingConstants.CENTER);
        lblJudul.setFont(new Font("Poppins", Font.BOLD, 18));
        lblJudul.setForeground(new Color(55, 194, 174));
        cardPanel.add(lblJudul, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 20, 15, 20);
        JLabel lblSubtitle = new JLabel("Sistem Manajemen Klinik", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblSubtitle.setForeground(Color.GRAY);
        cardPanel.add(lblSubtitle, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 5, 20);
        txtUsername = new JTextField();
        styleTextField(txtUsername);
        cardPanel.add(txtUsername, gbc);

        gbc.gridy++;
        txtPassword = new JPasswordField();
        styleTextField(txtPassword);
        cardPanel.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(15, 20, 5, 20);
        btnMasuk = new JButton("Masuk");
        styleRoundedButton(btnMasuk);
        cardPanel.add(btnMasuk, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 10, 20);
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblError.setForeground(Color.RED);
        cardPanel.add(lblError, gbc);

        mainPanel.add(cardPanel);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void styleTextField(JTextField field) {
        field.setBackground(new Color(250, 250, 250));
        field.setForeground(new Color(51, 51, 51));
        field.setCaretColor(new Color(51, 51, 51));
        field.setFont(new Font("Poppins", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleRoundedButton(JButton btn) {
        btn.setBackground(new Color(55, 194, 174));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
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

    public void addMasukListener(ActionListener l) { btnMasuk.addActionListener(l); }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public void showError(String msg) { lblError.setText(msg); }
    public void clearError() { lblError.setText(" "); }
}