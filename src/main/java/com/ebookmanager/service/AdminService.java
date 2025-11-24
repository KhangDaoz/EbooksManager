package com.ebookmanager.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.Admin;
import com.ebookmanager.model.Book;

public class AdminService {
    private BookService bookService;
    private UserDAO userDAO;
    
    public AdminService() {
        this.bookService = new BookService();
        this.userDAO = new UserDAO();
    }
    
    public void deleteBook(int bookId, Admin admin) throws IOException {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        if (bookId <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }
        bookService.deleteBook(bookId, admin);
    }
    
    public void deleteUserAccount(int userId, Admin admin) {
        if (admin == null) {
            throw new IllegalArgumentException("Admin cannot be null");
        }
        
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (admin.getUserId() == userId) {
            throw new IllegalArgumentException("Admin cannot delete their own account");
        }
        
        userDAO.deleteUser(userId);
    }
    
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        
        int totalBooks = bookService.findAllBooks().size();
        int totalUsers = userDAO.findAllUsers().size();
        
        stats.put("totalBooks", totalBooks);
        stats.put("totalUsers", totalUsers);
        
        return stats;
    }
    
    public void updateBookMetadata(Book book, Admin admin) {
        if (admin == null || !"Admin".equals(admin.getRole())) {
            throw new SecurityException("Only admins can update book metadata");
        }
        
        BookDAO bookDAO = new BookDAO();
        bookDAO.updateBook(book.getBookId(), book.getBookTitle(), book.getAuthorName(), book.getFilePath(), book.getPublisher(), book.getGenre());
    }
}
