/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
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
public class AdminBookManagementPanel extends javax.swing.JPanel {

    private BookService bookService;
    private AdminService adminService;
    public AdminBookManagementPanel() {
        this.bookService = new BookService();
        this.adminService = new AdminService();
        
        initComponents(); 
        
        customInit();     
        loadBooks();     
    }
private void customInit() {
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane2.getViewport().setOpaque(false);
    }
    private void loadBooks() {
        listPanel.removeAll(); 
        ArrayList<Book> books = bookService.findAllBooks();

        if (books.isEmpty()) {
            JLabel emptyLbl = new JLabel("No books in system.");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLbl);
        } else {
            for (Book book : books) {
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(Color.WHITE);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50)); 
                row.setPreferredSize(new Dimension(0, 50));
                
                
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    new EmptyBorder(10, 15, 10, 15) 
                ));

                
                JLabel lblTitle = new JLabel(book.getBookTitle() + " - " + book.getAuthorName());
                lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                row.add(lblTitle, BorderLayout.CENTER);

                
                JButton btnDelete = new JButton("Delete");
                styleDeleteButton(btnDelete); 

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

                
                row.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) { 
                        row.setBackground(new Color(245, 245, 250)); 
                    }
                    @Override
                    public void mouseExited(MouseEvent e) { 
                        row.setBackground(Color.WHITE); 
                    }
                });

                listPanel.add(row);
            }
        }
        
        listPanel.revalidate();
        listPanel.repaint();
    }
    
    
    private void styleDeleteButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(255, 230, 230)); 
        btn.setForeground(Color.RED);                 
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    
    private void deleteBookProcess(Book book) {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            
            
            if (currentUser instanceof Admin) {
                adminService.deleteBook(book.getBookId(), (Admin) currentUser);
                
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                loadBooks(); 
            } else {
                JOptionPane.showMessageDialog(this, "Error: You are not logged in as Admin.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jLabel1.setText("Manage Books");
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
