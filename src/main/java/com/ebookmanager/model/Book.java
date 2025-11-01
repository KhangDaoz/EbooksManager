package com.ebookmanager.model;

public class Book {
    private int bookId;
    private String bookTitle;
    private String authorName;
    private String format; // epub or pdf
    private String filePath;
    private String publishDate;
    private int uploaderId;
    
    public Book() {}

    public Book(int bookId, String bookTitle, String authorName,String format,
                String filePath, String publishDate, int uploaderId) {
        // Required fields: bookId, bookTitle, filePath, uploaderId, format
        // Optional fields: authorName, publishDate (can be null)
        if(bookId < 0) {
            throw new IllegalArgumentException("bookId cannot be negative");
        }
        if(bookTitle == null || bookTitle.isEmpty()) {
            throw new IllegalArgumentException("bookTitle cannot be null or empty");
        }
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be null or empty");
        }
        if(uploaderId < 0) {
            throw new IllegalArgumentException("uploaderId cannot be negative");
        }
        if(format == null || format.isEmpty()) {
            throw new IllegalArgumentException("format cannot be null or empty");
        }
        
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.authorName = authorName; // Can be null
        this.filePath = filePath;
        this.publishDate = publishDate; // Can be null
        this.uploaderId = uploaderId;
        this.format = format;
                    
    }
        
    public Book(String bookTitle, String authorName,String format,
                String filePath, String publishDate) {
        // Required fields: bookTitle, filePath, format
        // Optional fields: authorName, publishDate (can be null)
        if(bookTitle == null || bookTitle.isEmpty()) {
            throw new IllegalArgumentException("bookTitle cannot be null or empty");
        }
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be null or empty");
        }
        if(format == null || format.isEmpty()) {
            throw new IllegalArgumentException("format cannot be null or empty");
        }
        
        this.bookTitle = bookTitle;
        this.authorName = authorName; // Can be null
        this.filePath = filePath;
        this.publishDate = publishDate; // Can be null
        this.format = format;
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
        // authorName can be null (optional field)
        if(authorName != null && authorName.isEmpty()) {
            throw new IllegalArgumentException("authorName cannot be empty string (use null for no author)");
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
        // publishDate can be null (optional field)
        if(publishDate != null && publishDate.isEmpty()) {
            throw new IllegalArgumentException("publishDate cannot be empty string (use null for no date)");
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

    public String getFormat() {
        return this.format;
    } 
    
}