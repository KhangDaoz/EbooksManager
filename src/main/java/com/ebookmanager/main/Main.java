package com.ebookmanager.main;
import com.ebookmanager.ui.LoginAndRegister;
import javax.swing.SwingUtilities;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginAndRegister loginView = new LoginAndRegister();
            loginView.setVisible(true);
        });
    }
}
    