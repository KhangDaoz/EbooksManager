package com.ebookmanager.main;
import com.ebookmanager.ui.LoginView;
import javax.swing.SwingUtilities;
public class Main {
    public static void main(String[] args) {
        // Chạy giao diện Swing trên luồng Event Dispatch Thread (EDT) để đảm bảo an toàn luồng
        SwingUtilities.invokeLater(() -> {
            // Mở màn hình đăng nhập đầu tiên
            LoginView loginView = new LoginView();
            loginView.setVisible(true);
        });
    }
}
    