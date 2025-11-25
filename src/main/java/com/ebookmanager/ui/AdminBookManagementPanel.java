package com.ebookmanager.ui;

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

public class AdminBookManagementPanel extends JPanel {
    private JPanel listPanel;
    private BookService bookService;
    private AdminService adminService;

    public AdminBookManagementPanel() {
        this.bookService = new BookService();
        this.adminService = new AdminService();
        
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);

        // --- HEADER ---
        JLabel lblHeader = new JLabel("Manage Books (Admin Mode)");
        lblHeader.setFont(UIUtils.FONT_HEADER);
        lblHeader.setForeground(UIUtils.COLOR_TEXT_PRIMARY);
        lblHeader.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- LIST ---
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);

        // Tải danh sách sách
        loadBooks();
    }

    private void loadBooks() {
        listPanel.removeAll();
        ArrayList<Book> books = bookService.findAllBooks();

        if (books.isEmpty()) {
            listPanel.add(new JLabel("No books in system."));
        } else {
            for (Book book : books) {
                // Panel cho mỗi cuốn sách
                JPanel p = new JPanel(new BorderLayout());
                p.setBackground(Color.WHITE);
                p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                p.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(10, 15, 10, 15)
                ));
                
                // Tên sách
                JLabel lblTitle = new JLabel(book.getBookTitle() + " - " + book.getAuthorName());
                lblTitle.setFont(UIUtils.FONT_GENERAL);
                p.add(lblTitle, BorderLayout.CENTER);
                
                // Nút Delete
                JButton btnDelete = new JButton("Delete");
                btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnDelete.setBackground(new Color(255, 230, 230)); // Nền đỏ nhạt
                btnDelete.setForeground(Color.RED);
                btnDelete.setFocusPainted(false);
                btnDelete.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
                btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // --- SỰ KIỆN XÓA SÁCH ---
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

                p.add(btnDelete, BorderLayout.EAST);
                
                // Hiệu ứng hover dòng
                p.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { p.setBackground(new Color(245, 245, 250)); }
                    @Override
                    public void mouseExited(MouseEvent e) { p.setBackground(Color.WHITE); }
                });

                listPanel.add(p);
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void deleteBookProcess(Book book) {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            
            // Kiểm tra quyền Admin
            if (currentUser instanceof Admin) {
                // Gọi Service để xóa
                adminService.deleteBook(book.getBookId(), (Admin) currentUser);
                
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                
                // Tải lại danh sách ngay lập tức
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Error: You are not logged in as Admin.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}