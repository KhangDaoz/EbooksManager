/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.ebookmanager.ui;

/**
 *
 * @author trank
 */
import com.ebookmanager.model.Admin;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;
import com.ebookmanager.service.AdminService;
import com.ebookmanager.service.BookService;
import com.ebookmanager.util.SessionManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class AdminBookManagementPanel extends javax.swing.JPanel {

    private BookService bookService;
    private AdminService adminService;
    public AdminBookManagementPanel() {
        // Khởi tạo Services
        this.bookService = new BookService();
        this.adminService = new AdminService();
        
        initComponents(); 
        
        customInit(); 
        loadBooks();  
    }

    private void customInit() {
        // Tăng tốc độ lăn chuột cho mượt
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        
        // Làm trong suốt Viewport để thấy nền nếu cần
        jScrollPane1.getViewport().setOpaque(false);
    }

    // --- LOGIC TẢI DANH SÁCH SÁCH ---
    private void loadBooks() {
        listPanel.removeAll(); // Xóa danh sách cũ
        ArrayList<Book> books = bookService.findAllBooks();

        if (books.isEmpty()) {
            JLabel emptyLbl = new JLabel("No books in system.");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLbl);
        } else {
            for (Book book : books) {
                // 1. Tạo Panel chứa thông tin 1 cuốn sách (Row)
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); // Chiều cao cố định 50px
                row.setPreferredSize(new Dimension(0, 50));
                
                // Viền dưới mờ để ngăn cách các hàng
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(10, 15, 10, 15) // Padding bên trong
                ));

                // 2. Hiển thị Tên sách + Tác giả
                JLabel lblTitle = new JLabel(book.getBookTitle() + " - " + book.getAuthorName());
                lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                row.add(lblTitle, BorderLayout.CENTER);

                // 3. Tạo nút Delete
                JButton btnDelete = new JButton("Delete");
                styleDeleteButton(btnDelete); // Gọi hàm tô màu đỏ

                // Sự kiện bấm nút Delete
                btnDelete.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(
                        this, 
                        "Are you sure you want to delete book: '" + book.getBookTitle() + "'?\nThis action cannot be undone.", 
                        "Delete Book", 
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteBookProcess(book);
                    }
                });

                row.add(btnDelete, BorderLayout.EAST);

                // 4. Hiệu ứng Hover chuột (đổi màu nền khi di chuột vào)
                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { 
                        row.setBackground(new Color(245, 245, 250)); // Màu xám rất nhạt
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
        
        // Cập nhật lại giao diện
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    // Hàm style riêng cho nút Xóa (Nền đỏ nhạt, Chữ đỏ đậm)
    private void styleDeleteButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(255, 230, 230)); 
        btn.setForeground(Color.RED);                
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // --- LOGIC XÓA SÁCH (Gọi Service) ---
    private void deleteBookProcess(Book book) {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            
            // Kiểm tra quyền Admin
            if (currentUser instanceof Admin) {
                adminService.deleteBook(book.getBookId(), (Admin) currentUser);
                
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                loadBooks(); // Tải lại danh sách sau khi xóa
            } else {
                JOptionPane.showMessageDialog(this, "Error: You are not logged in as Admin.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listPanel = new javax.swing.JPanel();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1200, 750));
        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(44, 62, 80));
        jLabel1.setText("Manage Books");
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setBorder(null);
        jScrollPane1.setOpaque(false);

        listPanel.setBackground(new java.awt.Color(255, 255, 255));
        listPanel.setLayout(new javax.swing.BoxLayout(listPanel, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane1.setViewportView(listPanel);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel listPanel;
    // End of variables declaration//GEN-END:variables
}
