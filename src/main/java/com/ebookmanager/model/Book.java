package com.ebookmanager.model;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.ebookmanager.service.FileStorageService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class Book {
    private Integer bookId;
    private String bookTitle;
    private String authorName;
    private String genre;
    private String filePath;
    private String publisher;
    
    public Book() {}

    public Book(int bookId, String bookTitle, String authorName,
                String filePath, String publisher, String genre) {
        if(bookTitle == null || bookTitle.isEmpty()) {
            throw new IllegalArgumentException("bookTitle cannot be null or empty");
        }
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be null or empty");
        }
        if(bookId < 0) {
            throw new IllegalArgumentException("bookId cannot be negative");
        }
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.authorName = authorName; 
        this.filePath = filePath;
        this.publisher = publisher; 
        this.genre = genre;            
    }
        
    public Book(String bookTitle, String authorName,
                String filePath, String publisher, String genre) {

        if(bookTitle == null || bookTitle.isEmpty()) {
            throw new IllegalArgumentException("bookTitle cannot be null or empty");
        }
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be null or empty");
        }
        this.bookId = null;
        this.bookTitle = bookTitle;
        this.authorName = authorName; 
        this.filePath = filePath;
        this.publisher = publisher;
        this.genre = genre;
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
    

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public int getTotalPages() throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be null or empty");
        }
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }
        try (PDDocument document = PDDocument.load(file)) {
            return document.getNumberOfPages();
        } catch (IOException e) {
            throw new IOException("Error reading PDF file: " + e.getMessage(), e);
        }
    }
    public void setFilePath(String filePath) {
        if(filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("filePath cannot be null or empty");
        }
        this.filePath = filePath;
    }
    public InputStream readBook() throws IOException {
        return new FileStorageService().readFileAsResource(filePath);
    }

}