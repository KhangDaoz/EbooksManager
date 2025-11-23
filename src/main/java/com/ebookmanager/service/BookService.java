package com.ebookmanager.service;

import java.io.IOException;
import java.io.InputStream;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;

public class BookService {
    private BookDAO bookDAO;
    private FileStorageService fileService;
    
    public BookService() {
        this.bookDAO = new BookDAO();
        this.fileService = new FileStorageService();
    }
    
    public InputStream readBook(Book book) throws IOException {
        String filePath = book.getFilePath();
        return fileService.readFileAsResource(filePath);
    }
    public void addBook(Book book, User user) throws IOException {
        bookDAO.addBook(book);
    }
    public void updateBook(Book book, User user) throws IOException {
        bookDAO.updateBook(book);
    }
    public void deleteBook(Book book, User user) throws IOException {
        bookDAO.deleteBook(book.getBookId(), user);
    }
}
