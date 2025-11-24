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

    public BookProgress getBookProgress(Member member, Book book) {
        BookProgress progress = bookProgressDAO.getBookProgress(member.getUserId(), book.getBookId());
        if (progress == null) {
            return null;
        }
        progress.setBookmarks(bookmarkDAO.getBookmarksForBook(member.getUserId(), book.getBookId()));
        progress.setBookReading(book);
        return progress;
    }
    
    public BookProgress getOrCreateBookProgress(Member member, Book book) {
        BookProgress progress = getBookProgress(member, book);
        
        if (progress == null) {
            progress = new BookProgress(book, member);
        }
        
        return progress;
    }
    
    public void addBookToLibrary(Member member, Book book) {
        bookProgressDAO.addBookToLibrary(member.getUserId(), book.getBookId());
    }
    public void removeBookFromLibrary(Member member, Book book) {
        bookProgressDAO.removeBookFromLibrary(member.getUserId(), book.getBookId());
    }
    public void updateBookProgress(Member member, Book book, int currentPage, int personalRating) {
        bookProgressDAO.updateBookProgress(member.getUserId(), book.getBookId(), currentPage, personalRating);
    }
    public void rateBook(Member member, Book book, int personalRating) {
        bookProgressDAO.rateBook(member.getUserId(), book.getBookId(), personalRating);
    }
    public boolean isBookInLibrary(Member member, Book book) {
        return bookProgressDAO.isBookInLibrary(member.getUserId(), book.getBookId());
    }
}