package com.ebookmanager.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;
import com.ebookmanager.util.SessionManager;

public class BookService {
    private BookDAO bookDAO;
    private FileStorageService fileService;

    public BookService() {
        this.bookDAO = new BookDAO();
        this.fileService = new FileStorageService();
    }

    public InputStream readBook(Book book) throws IOException {
        if (book == null || book.getFilePath() == null) {
            throw new IllegalArgumentException("Invalid book or file path");
        }
        return fileService.readFileAsResource(book.getFilePath());
    }

    public int addBook(Book book) throws IOException {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        return bookDAO.addBook(
                book.getBookTitle(),
                book.getAuthorName(),
                book.getFilePath(),
                book.getPublisher(),
                book.getGenre());
    }

    public boolean updateBook(Book book) {
        if (book == null || book.getBookId() <= 0) {
            throw new IllegalArgumentException("Invalid book or book ID");
        }
        return bookDAO.updateBook(
                book.getBookId(),
                book.getBookTitle(),
                book.getAuthorName(),
                book.getFilePath(),
                book.getPublisher(),
                book.getGenre());
    }

    public boolean deleteBook(int bookId, User requestingUser) throws IOException {
        if (requestingUser == null) {
            throw new IllegalArgumentException("Requesting user cannot be null");
        }

        Book existingBook = bookDAO.findBookById(bookId);
        if (existingBook == null) {
            return false;
        }

        boolean isAdmin = "Admin".equals(requestingUser.getRole());
        boolean isOwner = requestingUser.checkUploaded(existingBook);

        if (!isAdmin && !isOwner) {
            throw new SecurityException("User not authorized to delete this book");
        }

        boolean deleted = bookDAO.deleteBook(bookId);
        if (deleted) {
            Files.deleteIfExists(Paths.get(existingBook.getFilePath()));
        }
        return deleted;
    }

    public Book findBookById(int bookId) {
        return bookDAO.findBookById(bookId);
    }

    public ArrayList<Book> findAllBooks() {
        return bookDAO.findAllBooks();
    }

    public ArrayList<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return bookDAO.searchBooks(searchTerm);
    }

    public int uploadBook(File bookFile, Book book) throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            throw new SecurityException("No user logged in");
        }
        
        if (bookFile == null || !bookFile.exists()) {
            throw new IllegalArgumentException("Book file does not exist");
        }
        
        if (book == null) {
            throw new IllegalArgumentException("Book metadata cannot be null");
        }
        
        if (!fileService.isValidBookFormat(bookFile.getName())) {
            throw new IllegalArgumentException("Invalid book format. Only PDF files are supported.");
        }
        
        try {

            String storedPath = fileService.saveFile(bookFile, bookFile.getName());
            book.setFilePath(storedPath);


            int bookId = bookDAO.addBook(
                book.getBookTitle(),
                book.getAuthorName(),
                book.getFilePath(),
                book.getPublisher(),
                book.getGenre()
            );
            
            if (bookId <= 0) {
                fileService.deleteFile(storedPath);
                throw new IOException("Failed to add book to database");
            }
            
            book.setBookId(bookId);
            
            currentUser.addUploadedBook(book);
            
            return bookId;
            
        } catch (IOException e) {
            if (book.getFilePath() != null) {
                try {
                    fileService.deleteFile(book.getFilePath());
                } catch (IOException ignored) {

                }
            }
            throw e;
        }
    }
}
