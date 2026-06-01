package view;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;

public class KunjunganDialog extends JDialog {

    private JTextArea txtKeluhan;
    private JTextArea txtDiagnosa;
    private JButton btnInputResep;
    private JButton btnSelesaikan;

    private final Color COLOR_BG      = new Color(240, 246, 246);
    private final Color COLOR_CARD     = Color.WHITE;
    private final Color COLOR_PRIMARY  = new Color(55, 194, 174);
    private final Color COLOR_SUCCESS  = new Color(39, 174, 96);
    private final Color COLOR_TEXT     = new Color(51, 51, 51);
    private final Color COLOR_MUTED    = new Color(130, 140, 145);
    private final Color COLOR_BORDER   = new Color(230, 230, 230);
    private final Color COLOR_INPUT_BG = new Color(250, 250, 250);

    public KunjunganDialog(Window parent, int idAntrian, int idPasien,
                           String namaPasien, String noRM, String namaDokter) {
        super(parent, "Kunjungan Pasien", ModalityType.APPLICATION_MODAL);
        setSize(520, 600);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_BG);
        setLayout(new BorderLayout(0, 0));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ---- HEADER CARD ----
        JPanel headerCard = createCard();
        headerCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        headerCard.setLayout(new BoxLayout(headerCard, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("Kunjungan Pasien");
        lblTitle.setFont(new Font("Poppins", Font.BOLD, 16));
        lblTitle.setForeground(COLOR_TEXT);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = new JLabel(namaPasien);
        lblNama.setFont(new Font("Poppins", Font.BOLD, 14));
        lblNama.setForeground(COLOR_PRIMARY);
        lblNama.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInfo = new JLabel("No. RM: " + noRM + "   |   Dokter: " + namaDokter);
        lblInfo.setFont(new Font("Poppins", Font.PLAIN, 11));
        lblInfo.setForeground(COLOR_MUTED);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        headerCard.add(lblTitle);
        headerCard.add(Box.createRigidArea(new Dimension(0, 6)));
        headerCard.add(lblNama);
        headerCard.add(Box.createRigidArea(new Dimension(0, 4)));
        headerCard.add(lblInfo);
        headerCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        mainPanel.add(headerCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // ---- FORM CARD ----
        JPanel formCard = createCard();
        formCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(createLabel("Keluhan Pasien"));
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        txtKeluhan = createTextArea(4);
        JScrollPane scrollKeluhan = new JScrollPane(txtKeluhan);
        scrollKeluhan.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollKeluhan.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollKeluhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        formCard.add(scrollKeluhan);

        formCard.add(Box.createRigidArea(new Dimension(0, 12)));

        formCard.add(createLabel("Diagnosa"));
        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        txtDiagnosa = createTextArea(4);
        JScrollPane scrollDiagnosa = new JScrollPane(txtDiagnosa);
        scrollDiagnosa.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        scrollDiagnosa.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollDiagnosa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        formCard.add(scrollDiagnosa);

        formCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        mainPanel.add(formCard);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // ---- TOMBOL ----
        JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btnInputResep = new JButton("Input Resep");
        styleButton(btnInputResep, COLOR_PRIMARY);
        btnRow.add(btnInputResep);

        btnSelesaikan = new JButton("Selesaikan Kunjungan");
        styleButton(btnSelesaikan, COLOR_SUCCESS);
        btnSelesaikan.setEnabled(false);
        btnRow.add(btnSelesaikan);

        mainPanel.add(btnRow);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_BG);
        add(scroll, BorderLayout.CENTER);

        // Enable/disable Selesaikan based on field content
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { checkFields(); }
            public void removeUpdate(DocumentEvent e)  { checkFields(); }
            public void changedUpdate(DocumentEvent e) { checkFields(); }
        };
        txtKeluhan.getDocument().addDocumentListener(dl);
        txtDiagnosa.getDocument().addDocumentListener(dl);
    }

    private void checkFields() {
        boolean ready = !txtKeluhan.getText().trim().isEmpty()
                     && !txtDiagnosa.getText().trim().isEmpty();
        btnSelesaikan.setEnabled(ready);
    }

    private JPanel createCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 12));
        lbl.setForeground(COLOR_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextArea createTextArea(int rows) {
        JTextArea ta = new JTextArea(rows, 20);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setFont(new Font("Poppins", Font.PLAIN, 13));
        ta.setBackground(COLOR_INPUT_BG);
        ta.setForeground(COLOR_TEXT);
        ta.setCaretColor(COLOR_TEXT);
        ta.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return ta;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(c.isEnabled() ? bg : new Color(200, 200, 200));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 12, 12);
                super.paint(g2, c);
                g2.dispose();
            }
        });
    }

    public String getKeluhan()  { return txtKeluhan.getText().trim(); }
    public String getDiagnosa() { return txtDiagnosa.getText().trim(); }
    public void addInputResepListener(ActionListener l)  { btnInputResep.addActionListener(l); }
    public void addSelesaikanListener(ActionListener l)  { btnSelesaikan.addActionListener(l); }
}
