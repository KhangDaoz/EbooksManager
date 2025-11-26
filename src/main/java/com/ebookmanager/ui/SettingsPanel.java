package com.ebookmanager.ui;

import com.ebookmanager.service.UserAccountService;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SettingsPanel extends JPanel {
    public SettingsPanel() {
        setLayout(new GridBagLayout()); 
        setOpaque(false);
        JPanel box = new JPanel(new GridLayout(4, 1, 0, 15));
        box.setBackground(new Color(240, 240, 240)); 
        box.setBorder(new EmptyBorder(30, 50, 30, 50));
        JPasswordField oldP = new JPasswordField(20); 
        oldP.setBorder(BorderFactory.createTitledBorder("Old Password"));
        
        JPasswordField newP = new JPasswordField(20); 
        newP.setBorder(BorderFactory.createTitledBorder("New Password"));
        
        JPasswordField cfmP = new JPasswordField(20); 
        cfmP.setBorder(BorderFactory.createTitledBorder("Confirm Password"));
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0)); 
        btns.setOpaque(false);
        
        JButton btnChange = new JButton("Change");
        btnChange.setPreferredSize(new Dimension(100, 35));
        
        JButton btnDel = new JButton("Delete Account"); 
        btnDel.setForeground(Color.RED);
        btnDel.setPreferredSize(new Dimension(120, 35));
        
        UserAccountService svc = new UserAccountService();
        btnChange.addActionListener(e -> {
            try { 
                svc.changePassword(new String(oldP.getPassword()), new String(newP.getPassword())); 
                JOptionPane.showMessageDialog(this, "Password Changed Successfully!"); 
                oldP.setText(""); newP.setText(""); cfmP.setText("");
            } catch(Exception ex) { 
                JOptionPane.showMessageDialog(this, ex.getMessage()); 
            }
        });
        btnDel.addActionListener(e -> {
            String password = new String(oldP.getPassword());
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your 'Old Password' to confirm deletion.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete your account?\nThis action cannot be undone!", 
                "Delete Account", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    svc.deleteOwnAccount(password);
                    
                    JOptionPane.showMessageDialog(this, "Account Deleted. Goodbye!");
                    SessionManager.getInstance().logout();
                    Window win = SwingUtilities.getWindowAncestor(this);
                    if (win != null) win.dispose();
                    new LoginView().setVisible(true);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });
        box.add(oldP); 
        box.add(newP); 
        box.add(cfmP);
        
        btns.add(btnChange); 
        btns.add(btnDel); 
        box.add(btns);
        
        add(box);
    }
}