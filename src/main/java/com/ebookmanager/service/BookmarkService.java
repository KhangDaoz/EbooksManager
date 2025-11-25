package com.ebookmanager.service;

import java.util.ArrayList;
import com.ebookmanager.dao.BookmarkDAO;
import com.ebookmanager.model.Bookmark;

public class BookmarkService {
    private BookmarkDAO bookmarkDAO;
    
    public BookmarkService() {
        this.bookmarkDAO = new BookmarkDAO();
    }
    
    public ArrayList<Bookmark> getBookmarksForBook(int userId, int bookId) {
        return bookmarkDAO.getBookmarksForBook(userId, bookId);
    }

    // Đã cập nhật: Thêm tham số 'name'
    public void createBookmark(int userId, int bookId, String locationData, String name) {
        bookmarkDAO.createBookmark(userId, bookId, locationData, name);
    }

    public void deleteBookmark(int bookmarkId) {
        bookmarkDAO.deleteBookmark(bookmarkId);
    }
}