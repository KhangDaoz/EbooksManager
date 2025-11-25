package com.ebookmanager.service;

import com.ebookmanager.dao.BookProgressDAO;
import com.ebookmanager.dao.BookmarkDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import java.util.ArrayList;

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

    public void addBookToLibrary(int userId, int bookId) {
        bookProgressDAO.addBookToLibrary(userId, bookId);
    }

    public void removeBookFromLibrary(int userId, int bookId) {
        bookProgressDAO.removeBookFromLibrary(userId, bookId);
    }

    public void updateBookProgress(int userId, int bookId, int currentPage, int personalRating) {
        bookProgressDAO.updateBookProgress(userId, bookId, currentPage, personalRating);
    }

    // [QUAN TRỌNG] Hàm này sửa lỗi gạch đỏ của bạn
    public void rateBook(int userId, int bookId, int personalRating) {
        bookProgressDAO.rateBook(userId, bookId, personalRating);
    }

    public boolean isBookInLibrary(int userId, int bookId) {
        return bookProgressDAO.isBookInLibrary(userId, bookId);
    }
    
    public ArrayList<BookProgress> getBookProgresses(int userId) {
        return bookProgressDAO.getBookProgresses(userId);
    }
}