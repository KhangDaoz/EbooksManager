/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.ebookmanager.ui;

import com.ebookmanager.model.Book;

/**
 *
 * @author trank
 */
public class MainView extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainView.class.getName());
    private javax.swing.JPanel mainContainer;
    public MainView() {
        initComponents();
        customInit();
    }
    private void customInit() {
        // --- BẮT BUỘC PHẢI CÓ DÒNG NÀY Ở ĐẦU TIÊN ---
        // Lấy toàn bộ giao diện mà bạn đã kéo thả gán vào biến mainContainer
        mainContainer = (javax.swing.JPanel) this.getContentPane();
        // -----------------------------------------------------------

        // 1. Cấu hình tiêu đề
        this.setTitle("E-Book Manager");
        this.setLocationRelativeTo(null);

        // 2. Hiển thị tên User
        try {
            String username = com.ebookmanager.util.SessionManager.getInstance().getCurrentUser().getUserName();
            lblWelcome.setText("Welcome, " + username);
        } catch (Exception e) {
            lblWelcome.setText("Welcome Guest");
        }

        // 3. Nạp các màn hình con
        // (Lưu ý: LibraryPanel và CommunityPanel phải có constructor nhận MainView như đã sửa ở bước trước)
        pnlCards.add(new LibraryPanel(this), "LIBRARY");
        pnlCards.add(new CommunityPanel(this), "COMMUNITY");
        pnlCards.add(new SettingPanel(), "SETTINGS");

        boolean isAdmin = com.ebookmanager.util.SessionManager.getInstance().isAdmin();
        if (isAdmin) {
             pnlCards.add(new AdminUserManagementPanel(), "USER_MGMT");
             pnlCards.add(new AdminBookManagementPanel(), "BOOK_MGMT");
        }

        // Ẩn/Hiện nút Admin
        btnManageUsers.setVisible(isAdmin);
        btnManageBooks.setVisible(isAdmin);

        // 4. Mở Library
        showCard("LIBRARY"); 
    }

    // Hàm chuyển trang (dùng cho các nút bấm)
    public void showCard(String cardName) {
        // Code cũ chuyển layout
        if (this.getContentPane() != mainContainer) {
            this.setContentPane(mainContainer);
            this.revalidate();
            this.repaint();
        }
        java.awt.CardLayout cl = (java.awt.CardLayout) pnlCards.getLayout();
        cl.show(pnlCards, cardName);
        
        // --- THÊM DÒNG NÀY ---
        updateSidebarVisuals(cardName); // Cập nhật màu nút ngay lập tức
        // ---------------------
    }
    private void updateSidebarVisuals(String activeCard) {
        // 1. Định nghĩa màu
        java.awt.Color inactiveColor = new java.awt.Color(44, 62, 80);   // Màu tối (mặc định)
        java.awt.Color activeColor = new java.awt.Color(52, 152, 219);   // Màu xanh sáng (khi được chọn)

        // 2. Reset tất cả các nút về màu tối trước
        javax.swing.JButton[] allButtons = {btnLibrary, btnCommunity, btnSettings, btnManageUsers, btnManageBooks};
        for (javax.swing.JButton btn : allButtons) {
            // Kiểm tra khác null để tránh lỗi (phòng trường hợp nút admin bị ẩn/xóa)
            if (btn != null) {
                btn.setBackground(inactiveColor);
                btn.setForeground(new java.awt.Color(189, 195, 199)); // Màu chữ xám nhạt
            }
        }

        // 3. Tô màu xanh cho nút đang chọn
        javax.swing.JButton selectedBtn = null;
        switch (activeCard) {
            case "LIBRARY": selectedBtn = btnLibrary; break;
            case "COMMUNITY": selectedBtn = btnCommunity; break;
            case "SETTINGS": selectedBtn = btnSettings; break;
            case "USER_MGMT": selectedBtn = btnManageUsers; break;
            case "BOOK_MGMT": selectedBtn = btnManageBooks; break;
        }

        if (selectedBtn != null) {
            selectedBtn.setBackground(activeColor);
            selectedBtn.setForeground(java.awt.Color.WHITE); // Đổi chữ thành màu trắng cho nổi
        }
    }
    // --- CÁC HÀM MỚI ĐỂ HỖ TRỢ READING PANEL ---

    // 1. Mở giao diện đọc sách (Thay thế toàn bộ màn hình)
    public void openReadingView(Book book, boolean isPreview) {
        ReadingPanel readingPanel = new ReadingPanel(book, this, isPreview);
        this.setContentPane(readingPanel); // Thay thế mainContainer bằng readingPanel
        this.revalidate();
        this.repaint();
    }
    
    // Quá tải hàm cho tiện dùng
    public void openReadingView(Book book) {
        openReadingView(book, false);
    }

    // 2. Quay về Library từ ReadingPanel
    public void goBackToLibrary() {
        this.setContentPane(mainContainer); // Khôi phục giao diện chính
        this.revalidate();
        this.repaint();
        showCard("LIBRARY");
    }

    // 3. Quay về Community từ ReadingPanel
    public void goBackToCommunity() {
        this.setContentPane(mainContainer);
        this.revalidate();
        this.repaint();
        showCard("COMMUNITY");
    }
    // --- KẾT THÚC ĐOẠN CODE CUSTOM ---

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pnlSidebar = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnCommunity = new javax.swing.JButton();
        btnLibrary = new javax.swing.JButton();
        btnManageUsers = new javax.swing.JButton();
        btnManageBooks = new javax.swing.JButton();
        btnSettings = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        pnlContent = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        lblWelcome = new javax.swing.JLabel();
        pnlCards = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("E-Book Manager");
        setPreferredSize(new java.awt.Dimension(1200, 750));

        pnlSidebar.setBackground(new java.awt.Color(44, 62, 80));
        pnlSidebar.setPreferredSize(new java.awt.Dimension(230, 0));
        pnlSidebar.setLayout(new java.awt.GridBagLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("E-BOOK MANAGER");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 5, 15);
        pnlSidebar.add(jLabel1, gridBagConstraints);

        btnCommunity.setBackground(new java.awt.Color(44, 62, 80));
        btnCommunity.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnCommunity.setForeground(new java.awt.Color(189, 195, 199));
        btnCommunity.setText("Community");
        btnCommunity.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 10));
        btnCommunity.setBorderPainted(false);
        btnCommunity.setContentAreaFilled(false);
        btnCommunity.setFocusPainted(false);
        btnCommunity.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnCommunity.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnCommunity.setPreferredSize(new java.awt.Dimension(250, 45));
        btnCommunity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCommunityActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 60, 60);
        pnlSidebar.add(btnCommunity, gridBagConstraints);

        btnLibrary.setBackground(new java.awt.Color(44, 62, 80));
        btnLibrary.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLibrary.setForeground(new java.awt.Color(189, 195, 199));
        btnLibrary.setText("Library");
        btnLibrary.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 10));
        btnLibrary.setBorderPainted(false);
        btnLibrary.setContentAreaFilled(false);
        btnLibrary.setFocusPainted(false);
        btnLibrary.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLibrary.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnLibrary.setPreferredSize(new java.awt.Dimension(250, 45));
        btnLibrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLibraryActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 60, 60);
        pnlSidebar.add(btnLibrary, gridBagConstraints);

        btnManageUsers.setBackground(new java.awt.Color(44, 62, 80));
        btnManageUsers.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnManageUsers.setForeground(new java.awt.Color(189, 195, 199));
        btnManageUsers.setText("Manage Users");
        btnManageUsers.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 10));
        btnManageUsers.setBorderPainted(false);
        btnManageUsers.setContentAreaFilled(false);
        btnManageUsers.setFocusPainted(false);
        btnManageUsers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnManageUsers.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnManageUsers.setPreferredSize(new java.awt.Dimension(250, 45));
        btnManageUsers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageUsersActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 60, 60);
        pnlSidebar.add(btnManageUsers, gridBagConstraints);

        btnManageBooks.setBackground(new java.awt.Color(44, 62, 80));
        btnManageBooks.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnManageBooks.setForeground(new java.awt.Color(189, 195, 199));
        btnManageBooks.setText("Manage Books");
        btnManageBooks.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 10));
        btnManageBooks.setBorderPainted(false);
        btnManageBooks.setContentAreaFilled(false);
        btnManageBooks.setFocusPainted(false);
        btnManageBooks.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnManageBooks.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnManageBooks.setPreferredSize(new java.awt.Dimension(250, 45));
        btnManageBooks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageBooksActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 60, 60);
        pnlSidebar.add(btnManageBooks, gridBagConstraints);

        btnSettings.setBackground(new java.awt.Color(44, 62, 80));
        btnSettings.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSettings.setForeground(new java.awt.Color(189, 195, 199));
        btnSettings.setText("Setting");
        btnSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 10));
        btnSettings.setBorderPainted(false);
        btnSettings.setContentAreaFilled(false);
        btnSettings.setFocusPainted(false);
        btnSettings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnSettings.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnSettings.setPreferredSize(new java.awt.Dimension(250, 45));
        btnSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 60, 60);
        pnlSidebar.add(btnSettings, gridBagConstraints);

        btnLogout.setBackground(new java.awt.Color(44, 62, 80));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 0, 51));
        btnLogout.setText("Logout");
        btnLogout.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 30, 10, 10));
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnLogout.setPreferredSize(new java.awt.Dimension(250, 45));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 30, 60, 60);
        pnlSidebar.add(btnLogout, gridBagConstraints);

        getContentPane().add(pnlSidebar, java.awt.BorderLayout.LINE_START);

        pnlContent.setBackground(new java.awt.Color(236, 240, 241));
        pnlContent.setLayout(new java.awt.BorderLayout());

        pnlHeader.setBackground(new java.awt.Color(255, 255, 255));

        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblWelcome.setText("Welcome User");
        pnlHeader.add(lblWelcome);

        pnlContent.add(pnlHeader, java.awt.BorderLayout.NORTH);

        pnlCards.setLayout(new java.awt.CardLayout());
        pnlContent.add(pnlCards, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlContent, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        com.ebookmanager.util.SessionManager.getInstance().logout();
        new LoginAndRegister().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnCommunityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCommunityActionPerformed
        showCard("COMMUNITY");// TODO add your handling code here:
    }//GEN-LAST:event_btnCommunityActionPerformed

    private void btnLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLibraryActionPerformed
        showCard("LIBRARY");// TODO add your handling code here:
    }//GEN-LAST:event_btnLibraryActionPerformed

    private void btnManageUsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageUsersActionPerformed
        showCard("USER_MGMT");// TODO add your handling code here:
    }//GEN-LAST:event_btnManageUsersActionPerformed

    private void btnManageBooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageBooksActionPerformed
        showCard("BOOK_MGMT");// TODO add your handling code here:
    }//GEN-LAST:event_btnManageBooksActionPerformed

    private void btnSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSettingsActionPerformed
        showCard("SETTINGS");// TODO add your handling code here:
    }//GEN-LAST:event_btnSettingsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCommunity;
    private javax.swing.JButton btnLibrary;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnManageBooks;
    private javax.swing.JButton btnManageUsers;
    private javax.swing.JButton btnSettings;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblWelcome;
    private javax.swing.JPanel pnlCards;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlSidebar;
    // End of variables declaration//GEN-END:variables
}
