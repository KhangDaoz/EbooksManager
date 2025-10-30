package com.ebookmanager.model;

public class Book {
    private int bookId;
    private String bookTitle;
    private String authorName;
    private String filePath;
    private String publishDate;
    private int uploaderId;
    
    public Book() {}

    public Book(int bookId, String bookTitle, String authorName,
                String filePath, String publishDate, int uploaderId) {
        if(bookId < 0 || bookTitle.isEmpty() || authorName.isEmpty() || filePath.isEmpty() || publishDate.isEmpty() || uploaderId < 0) {
            throw new IllegalArgumentException("Invalid argument(s) for Book constructor");
        }
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.filePath = filePath;
        this.publishDate = publishDate;
        this.uploaderId = uploaderId;
    }

    public Book(String bookTitle, String authorName, String filePath, 
                String publishDate) {
        if(bookTitle.isEmpty() || authorName.isEmpty() || filePath.isEmpty() || publishDate.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument(s) for Book constructor");
        }
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.filePath = filePath;
        this.publishDate = publishDate;
    }

    public int getBookId() {    
        return bookId;
    }

    public void setBookId(int bookId) {
        if(bookId < 0) {
            throw new IllegalArgumentException("bookId cannot be negative");
        }
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        if(bookTitle.isEmpty()) {
            throw new IllegalArgumentException("bookTitle cannot be empty");
        }
        this.bookTitle = bookTitle;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        if(authorName.isEmpty()) {
            throw new IllegalArgumentException("authorName cannot be empty");
        }
        this.authorName = authorName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        if(filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be empty");
        }
        this.filePath = filePath;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        if(publishDate.isEmpty()) {
            throw new IllegalArgumentException("publishDate cannot be empty");
        }
        this.publishDate = publishDate;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        if(uploaderId < 0) {
            throw new IllegalArgumentException("uploaderId cannot be negative");
        }
        this.uploaderId = uploaderId;
    }
    
}