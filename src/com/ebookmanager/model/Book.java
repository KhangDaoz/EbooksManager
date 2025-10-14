package com.ebookmanager.model;

public class Book {
    private String bookId;
    private String bookTitle;
    private String hashedPassword;
    private String author;
    private String publishDate;
    private String filePath = "";
    private String imagePath = "";
    
    // Getters
    public String getBookId() {
        return bookId;
    }
    public String getBookTitle() {
        return bookTitle;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public String getAuthor() {
        return author;
    }
    public String getPublishDate() {
        return publishDate;
    }
    public String getFilePath() {
        return filePath;
    }
    public String getImagePath() {
        return imagePath;
    }

        
}
