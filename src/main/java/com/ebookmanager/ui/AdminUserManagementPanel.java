/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.ebookmanager.ui;

import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.User;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class AdminUserManagementPanel extends javax.swing.JPanel {

    private UserDAO userDAO;
    public AdminUserManagementPanel() {
        this.userDAO = new UserDAO();
        
        initComponents(); // NetBeans sinh giao diện
        
        customInit();     // Cấu hình thêm (Scroll, trong suốt...)
        loadUsers();      // Tải danh sách user ngay khi mở
    }
private void customInit() {
        // Tăng tốc độ cuộn chuột
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16);
        
        // Làm trong suốt vùng hiển thị để thấy nền nếu cần
        jScrollPane2.getViewport().setOpaque(false);
    }

    // --- LOGIC TẢI DANH SÁCH USER ---
    private void loadUsers() {
        listPanel.removeAll(); // Xóa danh sách cũ
        ArrayList<User> users = userDAO.findAllUsers();

        if (users == null || users.isEmpty()) {
            JLabel emptyLbl = new JLabel("No users found.");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLbl);
        } else {
            for (User user : users) {
                // 1. Tạo hàng (Row) chứa thông tin User
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Chiều cao cố định 50px
                row.setPreferredSize(new Dimension(0, 50));
                
                // Viền mờ ngăn cách giữa các hàng
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(10, 15, 10, 15)
                ));

                // 2. Hiển thị Tên User + Vai trò
                String roleText = user.getRole().equalsIgnoreCase("ADMIN") ? " [Admin]" : " [User]";
                JLabel lblName = new JLabel(user.getUserName() + roleText);
                lblName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                // Nếu là Admin thì tô đậm và đổi màu chữ để nổi bật
                if (user.getRole().equalsIgnoreCase("ADMIN")) {
                    lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    lblName.setForeground(new Color(44, 62, 80)); // Màu xanh đen
                }
                
                row.add(lblName, BorderLayout.CENTER);

                // 3. Tạo nút Xóa (Delete)
                JButton btnDelete = new JButton("Delete");
                styleDeleteButton(btnDelete); // Gọi hàm trang trí nút đỏ

                // Sự kiện bấm nút Xóa
                btnDelete.addActionListener(e -> {
                    // Logic bảo mật: Không cho phép tự xóa chính mình
                    User currentUser = SessionManager.getInstance().getCurrentUser();
                    if (currentUser != null && currentUser.getUserId() == user.getUserId()) {
                        JOptionPane.showMessageDialog(this, "You cannot delete your own account!", "Action Denied", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Hộp thoại xác nhận
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete user: " + user.getUserName() + "?\nThis action cannot be undone.",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteUserProcess(user.getUserId());
                    }
                });

                row.add(btnDelete, BorderLayout.EAST);

                // 4. Hiệu ứng Hover chuột (đổi màu nền khi di vào)
                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { 
                        row.setBackground(new Color(245, 245, 250)); // Màu xám nhạt
                    }
                    @Override
                    public void mouseExited(MouseEvent e) { 
                        row.setBackground(Color.WHITE); // Trả về màu trắng
                    }
                });

                // Thêm hàng vào danh sách
                listPanel.add(row);
            }
        }
        
        // Cập nhật giao diện sau khi thêm xong
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    // Hàm trang trí nút Xóa (Màu đỏ nhạt, chữ đỏ)
    private void styleDeleteButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(255, 230, 230)); 
        btn.setForeground(Color.RED);                
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- LOGIC XÓA USER (Sửa lỗi hàm void) ---
    private void deleteUserProcess(int userId) {
        try {
            // Vì UserDAO trả về void nên ta gọi trực tiếp
            userDAO.deleteUser(userId);
            
            // Mặc định báo thành công và load lại
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            loadUsers(); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1200, 750));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(44, 62, 80));
        jLabel1.setText("Manage Users");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jScrollPane2.setBorder(null);
        jScrollPane2.setOpaque(false);

        listPanel.setBackground(new java.awt.Color(255, 255, 255));
        listPanel.setLayout(new javax.swing.BoxLayout(listPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(listPanel);

        add(jScrollPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel listPanel;
    // End of variables declaration//GEN-END:variables
}
