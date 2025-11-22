package com.ebookmanager.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class BookProgress {
    private int currentPage;
    private Date lastRead;
    private int personalRating;
    private ArrayList<Bookmark> bookmarks;
    private Book bookReading;
    // Constructors
    public BookProgress(Book book) {
        this.bookmarks = new ArrayList<>();
        this.bookReading = book;
    }
    public BookProgress(int currentPage, Date lastRead, int personalRating, ArrayList<Bookmark> bookmarks, Book bookReading) {
        this.currentPage = currentPage;
        this.lastRead = lastRead;
        this.personalRating = personalRating;
        this.bookmarks = bookmarks;
        this.bookReading = bookReading;
    }
    public void addBookmark(Bookmark bookmark) {
        this.bookmarks.add(bookmark);
    }
    public int getCurrentPage() {
        return currentPage;
    }
    public void setRating(int rating) {
        this.personalRating = rating;
    }
    public void removeBookmark(Bookmark bookmark) {
        this.bookmarks.remove(bookmark);
        }
    public void updateProgress(int newPage, Book book) throws IOException {
        int totalPages = book.getTotalPages();
        if (newPage < 0 || newPage > totalPages) {
            throw new IllegalArgumentException("Invalid page number");
        }
        this.currentPage = newPage;
        this.lastRead = new Date();
    }
    
    public Date getLastRead() {
        return lastRead;
    }
    
    public void setLastRead(Date lastRead) {
        this.lastRead = lastRead;
    }
    
    public int getPersonalRating() {
        return personalRating;
    }
    
    public void setPersonalRating(int personalRating) {
        if (personalRating < 0 || personalRating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.personalRating = personalRating;
    }
    
    public ArrayList<Bookmark> getBookmarks() {
        return bookmarks;
    }
    
    public Book getBookReading() {
        return bookReading;
    }
    
    public void setBookReading(Book bookReading) {
        this.bookReading = bookReading;
    }
}
