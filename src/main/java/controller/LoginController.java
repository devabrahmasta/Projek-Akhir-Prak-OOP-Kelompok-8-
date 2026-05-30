package controller;

import database.DBConnection;
import main.MainFrame;
import model.User;
import view.LoginView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {
    private LoginView view;
    private Connection connection;

    public LoginController(LoginView view) {
        this.view = view;
        this.connection = DBConnection.getInstance().getConnection();
        initController();
    }

    private void initController() {
        view.addMasukListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username dan password harus diisi!");
            return;
        }

        view.clearError();
        view.showError("Memeriksa kredensial..."); 

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                if (connection == null) throw new Exception("Tidak ada koneksi database");
                
                String sql = "SELECT id, username, nama, role, aktif FROM users WHERE username = ? AND password = ? AND aktif = 1";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            return new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("nama"),
                                rs.getString("role"),
                                rs.getBoolean("aktif")
                            );
                        }
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        int pilihan = JOptionPane.showConfirmDialog(
                                view,
                                "Anda akan masuk sebagai " + user.getRole().toUpperCase() + ".\nLanjutkan?",
                                "Konfirmasi Login",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (pilihan == JOptionPane.YES_OPTION) {
                            SessionManager.login(user);
                            view.dispose();

                            SwingUtilities.invokeLater(() -> {
                                // =========================================================================
                                // TODO: LETAKKAN NAVIGASI KE HALAMAN ROLE DI SINI
                                // =========================================================================
                                // Anda bisa menggunakan SessionManager.getUser().getRole() untuk 
                                // melakukan percabangan form/view mana yang harus dibuka.
                                //
                                // Contoh:
                                // if (SessionManager.hasRole("admin")) {
                                //     new AdminFrame().setVisible(true);
                                // } else if (SessionManager.hasRole("resepsionis")) {
                                //     // Buka view resepsionis
                                // }
                                
                                // Baris ini adalah placeholder sementara menuju MainFrame
                                new MainFrame().setVisible(true); 
                                // =========================================================================
                            });
                        } else {
                            view.showError("Login dibatalkan oleh pengguna.");
                            view.clearPassword();
                        }
                        
                    } else {
                        view.showError("Username atau password salah!");
                    }
                } catch (Exception e) {
                    view.showError("Error database: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
}