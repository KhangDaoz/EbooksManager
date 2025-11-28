/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.ebookmanager.ui;

/**
 *
 * @author trank
 */
import com.ebookmanager.service.UserAccountService;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import javax.swing.*;
public class SettingPanel extends javax.swing.JPanel {

    /**
     * Creates new form SettingPanel
     */
    public SettingPanel() {
        initComponents();
        customInit();
    }

    private void customInit() {
        // Style cho nút Change (Màu xanh)
        btnChange.setBackground(new Color(52, 152, 219));
        btnChange.setForeground(Color.WHITE);
        btnChange.setFocusPainted(false);
        btnChange.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Style cho nút Delete (Màu đỏ - Cảnh báo)
        btnDelete.setBackground(new Color(231, 76, 60)); 
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Tăng kích thước nút cho đẹp
        btnChange.setPreferredSize(new Dimension(100, 35));
        btnDelete.setPreferredSize(new Dimension(140, 35));
    }

    // --- LOGIC ĐỔI MẬT KHẨU ---
    private void performChangePassword() {
        String oldP = new String(txtOldPass.getPassword());
        String newP = new String(txtNewPass.getPassword());
        String cfmP = new String(txtConfirmPass.getPassword());
        
        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!newP.equals(cfmP)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Gọi Service để xử lý
        UserAccountService svc = new UserAccountService();
        try { 
            svc.changePassword(oldP, newP); 
            JOptionPane.showMessageDialog(this, "Password Changed Successfully!"); 
            
            // Xóa trắng các ô sau khi đổi thành công
            txtOldPass.setText(""); 
            txtNewPass.setText(""); 
            txtConfirmPass.setText("");
        } catch(Exception ex) { 
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    // --- LOGIC XÓA TÀI KHOẢN ---
    private void performDeleteAccount() {
        String password = new String(txtOldPass.getPassword());
            
        // Yêu cầu nhập mật khẩu cũ để xác thực trước khi xóa
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your 'Old Password' to confirm deletion.", "Security Check", JOptionPane.WARNING_MESSAGE);
            txtOldPass.requestFocus();
            return;
        }

        // Hiện hộp thoại xác nhận (Yes/No)
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete your account?\nThis action CANNOT be undone!", 
            "Delete Account", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            UserAccountService svc = new UserAccountService();
            try {
                svc.deleteOwnAccount(password);

                JOptionPane.showMessageDialog(this, "Account Deleted. Goodbye!");
                
                // Đăng xuất và quay về màn hình Login
                SessionManager.getInstance().logout();
                Window win = SwingUtilities.getWindowAncestor(this);
                if (win != null) win.dispose();
                
                new LoginAndRegister().setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlCard = new javax.swing.JPanel();
        txtOldPass = new javax.swing.JPasswordField();
        txtNewPass = new javax.swing.JPasswordField();
        txtConfirmPass = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        btnChange = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        pnlCard.setBackground(new java.awt.Color(240, 240, 240));
        pnlCard.setBorder(javax.swing.BorderFactory.createEmptyBorder(30, 50, 30, 50));
        pnlCard.setLayout(new java.awt.GridLayout(0, 1, 0, 15));

        txtOldPass.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtOldPass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Old Password", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        txtOldPass.setPreferredSize(new java.awt.Dimension(300, 50));
        txtOldPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOldPassActionPerformed(evt);
            }
        });
        pnlCard.add(txtOldPass);

        txtNewPass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "New Password", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        txtNewPass.setPreferredSize(new java.awt.Dimension(300, 50));
        txtNewPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNewPassActionPerformed(evt);
            }
        });
        pnlCard.add(txtNewPass);

        txtConfirmPass.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Confirm Password", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14))); // NOI18N
        txtConfirmPass.setPreferredSize(new java.awt.Dimension(300, 50));
        txtConfirmPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtConfirmPassActionPerformed(evt);
            }
        });
        pnlCard.add(txtConfirmPass);

        jPanel1.setOpaque(false);

        btnChange.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnChange.setText("Change");
        btnChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangeActionPerformed(evt);
            }
        });
        jPanel1.add(btnChange);

        btnDelete.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnDelete.setText("Delete Account");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jPanel1.add(btnDelete);

        pnlCard.add(jPanel1);

        add(pnlCard, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void txtNewPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNewPassActionPerformed
        txtConfirmPass.requestFocus();// TODO add your handling code here:
    }//GEN-LAST:event_txtNewPassActionPerformed

    private void txtConfirmPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtConfirmPassActionPerformed
        performChangePassword();// TODO add your handling code here:
    }//GEN-LAST:event_txtConfirmPassActionPerformed

    private void btnChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangeActionPerformed
        performChangePassword();// TODO add your handling code here:
    }//GEN-LAST:event_btnChangeActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        performDeleteAccount();// TODO add your handling code here:
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtOldPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOldPassActionPerformed
        txtNewPass.requestFocus();// TODO add your handling code here:
    }//GEN-LAST:event_txtOldPassActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChange;
    private javax.swing.JButton btnDelete;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel pnlCard;
    private javax.swing.JPasswordField txtConfirmPass;
    private javax.swing.JPasswordField txtNewPass;
    private javax.swing.JPasswordField txtOldPass;
    // End of variables declaration//GEN-END:variables
}
