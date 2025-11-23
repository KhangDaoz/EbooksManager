package com.ebookmanager.service;

import java.io.IOException;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.Admin;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;

public class AdminService {

    private BookDAO bookDAO;
    private UserDAO userDAO;
    private FileStorageService fileService;
    
    public void removeBook(Book book, Admin admin) throws IOException {
        bookDAO.deleteBook(book.getBookId(), admin);

        fileService.deleteFile(book.getFilePath());
    }
    
    public void deleteUserAccount(User user) {
        userDAO.deleteUser(user.getUserId());
    }
}

