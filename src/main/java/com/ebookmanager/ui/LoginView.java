package com.ebookmanager.ui;

import com.ebookmanager.service.AuthenticationService;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;
    private AuthenticationService authService;
    private JTextField txtLoginUser, txtRegUser;
    private JPasswordField txtLoginPass, txtRegPass, txtRegConfirm;

    public LoginView() {
        authService = new AuthenticationService();
        setTitle("E-Book Manager");
        setSize(1000, 650); // Tăng kích thước cửa sổ lên một chút
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(createAuthPanel(true), "LOGIN");
        mainContainer.add(createAuthPanel(false), "REGISTER");

        add(mainContainer);
    }

    private JPanel createAuthPanel(boolean isLogin) {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        // --- 1. LEFT SIDE (Logo & Branding) ---
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UIUtils.COLOR_DARK_BG);
        
        JLabel title = new JLabel("<html><center>E-BOOK<br>MANAGER</center></html>", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48)); // Font to hơn
        title.setForeground(Color.WHITE);
        left.add(title);

        // --- 2. RIGHT SIDE (Form) ---
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Header
        JLabel header = new JLabel(isLogin ? "Welcome Back" : "Create Account");
        header.setFont(new Font("Segoe UI", Font.BOLD, 32));
        header.setForeground(UIUtils.COLOR_DARK_BG);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0); // Cách lề dưới header
        right.add(header, gbc);

        // Inputs
        int currentRow = 1;
        if (isLogin) {
            txtLoginUser = createStyledField();
            txtLoginPass = createStyledPasswordField();
            
            addLabeledField(right, "Username", txtLoginUser, gbc, currentRow);
            currentRow += 2;
            
            addLabeledField(right, "Password", txtLoginPass, gbc, currentRow);
            currentRow += 2;
        } else {
            txtRegUser = createStyledField();
            txtRegPass = createStyledPasswordField();
            txtRegConfirm = createStyledPasswordField();
            
            addLabeledField(right, "Username", txtRegUser, gbc, currentRow);
            currentRow += 2;
            
            addLabeledField(right, "Password", txtRegPass, gbc, currentRow);
            currentRow += 2;
            
            addLabeledField(right, "Confirm Password", txtRegConfirm, gbc, currentRow);
            currentRow += 2;
        }

        // Main Action Button (LOGIN / REGISTER)
        JButton btn = new JButton(isLogin ? "LOGIN" : "REGISTER");
        stylePrimaryButton(btn);
        
        btn.addActionListener(e -> {
            try {
                if(isLogin) { 
                    authService.login(txtLoginUser.getText(), new String(txtLoginPass.getPassword())); 
                    new MainView().setVisible(true); 
                    dispose(); 
                } else { 
                    authService.register(txtRegUser.getText(), new String(txtRegPass.getPassword()), new String(txtRegConfirm.getPassword())); 
                    JOptionPane.showMessageDialog(this, "Registration Successful!"); 
                    cardLayout.show(mainContainer, "LOGIN"); 
                }
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
            }
        });
        
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(20, 0, 15, 0); // Khoảng cách nút
        right.add(btn, gbc);
        
        // Switch Link
        JButton switchBtn = new JButton(isLogin ? "No account? Register here" : "Have an account? Login");
        switchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        switchBtn.setForeground(UIUtils.COLOR_ACCENT);
        switchBtn.setBorderPainted(false); 
        switchBtn.setContentAreaFilled(false);
        switchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        switchBtn.addActionListener(e -> cardLayout.show(mainContainer, isLogin ? "REGISTER" : "LOGIN"));
        
        gbc.gridy = currentRow++;
        gbc.insets = new Insets(0, 0, 0, 0);
        right.add(switchBtn, gbc);

        panel.add(left); 
        panel.add(right);
        return panel;
    }

    // --- HELPER METHODS CHO UI ĐẸP HƠN ---

    // Helper thêm Label + Input Field vào panel gọn gàng
    private void addLabeledField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.GRAY);
        
        gbc.gridy = y;
        gbc.insets = new Insets(10, 0, 5, 0); // Khoảng cách trên dưới label
        panel.add(label, gbc);
        
        gbc.gridy = y + 1;
        gbc.insets = new Insets(0, 0, 5, 0); // Khoảng cách dưới input
        panel.add(field, gbc);
    }

    // Tạo ô nhập văn bản to đẹp
    private JTextField createStyledField() {
        JTextField tf = new JTextField(20);
        tf.setPreferredSize(new Dimension(350, 45)); // Rộng 350, Cao 45
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Chữ to
        tf.setBorder(UIUtils.createRoundedBorder());
        return tf;
    }

    // Tạo ô nhập mật khẩu to đẹp
    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField(20);
        pf.setPreferredSize(new Dimension(350, 45)); // Rộng 350, Cao 45
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pf.setBorder(UIUtils.createRoundedBorder());
        return pf;
    }

    // Style nút bấm chính to đẹp
    private void stylePrimaryButton(JButton btn) {
        btn.setPreferredSize(new Dimension(350, 50)); // Nút to (Rộng 350, Cao 50)
        btn.setBackground(UIUtils.COLOR_ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Chữ to đậm
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}