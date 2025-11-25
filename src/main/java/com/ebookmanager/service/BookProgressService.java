package com.ebookmanager.service;

import com.ebookmanager.dao.BookProgressDAO;
import com.ebookmanager.dao.BookmarkDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Member;

public class BookProgressService {
    private BookProgressDAO bookProgressDAO;
    private BookmarkDAO bookmarkDAO;

    public BookProgressService() {
        this.bookProgressDAO = new BookProgressDAO();
        this.bookmarkDAO = new BookmarkDAO();
    }

    public BookProgress getBookProgress(int userId, int bookId) {
        BookProgress progress = bookProgressDAO.getBookProgress(userId, bookId);
        if (progress == null) return null;
        progress.setBookmarks(bookmarkDAO.getBookmarksForBook(userId, bookId));
        return progress;
    }

    // --- [SỬA LỖI 1] Hàm thêm sách vào thư viện bằng ID ---
    public void addBookToLibrary(int userId, int bookId) {
        bookProgressDAO.addBookToLibrary(userId, bookId);
    }

    // --- [SỬA LỖI 2] Hàm kiểm tra sách trong thư viện bằng ID ---
    public boolean isBookInLibrary(int userId, int bookId) {
        return bookProgressDAO.isBookInLibrary(userId, bookId);
    }

    // --- [SỬA LỖI 3] Hàm cập nhật tiến độ bằng ID ---
    public void updateBookProgress(int userId, int bookId, int currentPage, int personalRating) {
        bookProgressDAO.updateBookProgress(userId, bookId, currentPage, personalRating);
    }
    
    // Giữ lại các hàm cũ nếu cần tương thích ngược, hoặc xóa đi cũng được
    public void addBookToLibrary(Member member, Book book) {
        addBookToLibrary(member.getUserId(), book.getBookId());
    }
    
    // Các hàm khác giữ nguyên...
    public void removeBookFromLibrary(int userId, int bookId) {
        bookProgressDAO.removeBookFromLibrary(userId, bookId);
    }
    
    public java.util.ArrayList<BookProgress> getBookProgresses(int userId) {
        return bookProgressDAO.getBookProgresses(userId);
    }
}