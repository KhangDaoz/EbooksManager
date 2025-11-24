package com.ebookmanager.service;

import com.ebookmanager.dao.BookProgressDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Member;

public class BookProgressService {
    private BookProgressDAO bookProgressDAO;
    
    public BookProgressService() {
        this.bookProgressDAO = new BookProgressDAO();
    }

    public BookProgress getBookProgress(Member member, Book book) {
        BookProgress progress = bookProgressDAO.getBookProgress(member.getUserId(), book.getBookId());
        
        if (progress == null) {
            return null;
        }
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
}