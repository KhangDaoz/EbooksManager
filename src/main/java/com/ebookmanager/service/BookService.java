package com.ebookmanager.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public int uploadBook(File bookFile, Book book) throws IOException {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) throw new SecurityException("No user logged in");
        
        // Lưu file vật lý
        String storedPath = fileService.saveFile(bookFile, bookFile.getName());
        book.setFilePath(storedPath);

        // [CẬP NHẬT] Truyền thêm currentUser.getUserId()
        int bookId = bookDAO.addBook(
            book.getBookTitle(),
            book.getAuthorName(),
            book.getFilePath(),
            book.getPublisher(),
            book.getGenre(),
            currentUser.getUserId() // <--- ID người upload
        );
        
        if (bookId > 0) book.setBookId(bookId);
        return bookId;
    }
    
    // [MỚI] Lấy sách do user upload
    public ArrayList<Book> getUploadedBooks(int userId) {
        return bookDAO.findBooksByUploader(userId);
    }

    // --- CÁC HÀM KHÁC GIỮ NGUYÊN ---
    public InputStream readBook(Book book) throws IOException { return fileService.readFileAsResource(book.getFilePath()); }
    public ArrayList<Book> findAllBooks() { return bookDAO.findAllBooks(); }
    public ArrayList<Book> searchBooks(String term) { return bookDAO.searchBooks(term); }
    public Book findBookById(int id) { return bookDAO.findBookById(id); }
    public boolean deleteBook(int id, User user) throws IOException { return bookDAO.deleteBook(id); }
    public boolean updateBook(Book book) { return bookDAO.updateBook(book.getBookId(), book.getBookTitle(), book.getAuthorName(), book.getFilePath(), book.getPublisher(), book.getGenre()); }
}