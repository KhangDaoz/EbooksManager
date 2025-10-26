package com.ebookmanager.model;

public class Book {
    private int bookId;
    private String bookTitle;
    private String authorName;
    private String format; // epub or pdf
    private String filePath;
    private String publishDate;
    private int uploaderId;

    public Book() {
    }
    public Book(int bookId, String bookTitle, String authorName,String format,
                String filePath, String publishDate, int uploaderId) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.filePath = filePath;
        this.publishDate = publishDate;
        this.uploaderId = uploaderId;
        this.format = format;
                    
    }
    public Book(String bookTitle, String authorName,String format,
                String filePath, String publishDate) {
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.filePath = filePath;
        this.publishDate = publishDate;
        this.format = format;
    }
    public int getBookId() {    
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }
    public int getUploaderId() {
        return uploaderId;
    }
    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getFormat() {
        return this.format;
    } 
    
}