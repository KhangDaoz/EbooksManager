package com.ebookmanager.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.dao.UserDAO;

public class Admin extends User {
    // Constructors
    public Admin() {
        super();
    }
    public Admin(int userId, String userName, String hashedPassword) {
        super(userId, userName, hashedPassword);
    }
    @Override
    public String getRole() {
        return "Admin";
    }

    public void removeBook(Book book, BookDAO bookDAO) throws IOException {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        bookDAO.deleteBook(book.getBookId(),this);
        Files.deleteIfExists(Paths.get(book.getFilePath()));
    }

    public void deleteUserAccount(User user, UserDAO userDAO) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        userDAO.deleteUser(user.getUserId());
    }
    // public String viewSystemStats() {
        
    // }
    
}
