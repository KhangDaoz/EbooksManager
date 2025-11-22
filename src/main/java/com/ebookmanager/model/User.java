package com.ebookmanager.model;

import java.util.ArrayList;


public abstract class User {
    private int userId;
    private String userName;
    private String hashedPassword;
    private ArrayList<Book> uploadedBooks;
    public abstract String getRole();
    
    // Constructors
    public User() {
        this.uploadedBooks = new ArrayList<>();
    }

    public User(int userId, String userName, String hashedPassword) {
        if (userId < 0) {
            throw new IllegalArgumentException("userId cannot be negative");
        }
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("userName cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("hashedPassword cannot be null or empty");
        }
        this.userId = userId;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
        this.uploadedBooks = new ArrayList<>();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("userId cannot be negative");
        }
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null || userName.isEmpty()) {
            throw new IllegalArgumentException("userName cannot be null or empty");
        }
        this.userName = userName;
    }

    public void setHashedPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("hashedPassword cannot be null or empty");
        }
        this.hashedPassword = hashedPassword;
    }

    public ArrayList<Book> getUploadedBooks() {
        return this.uploadedBooks;
    }

    public void addUploadedBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("book cannot be null");
        }
        this.uploadedBooks.add(book);
    }

    public boolean verifyPassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("hashedPassword cannot be null or empty");
        }
        return this.hashedPassword.equals(hashedPassword);
    }

    public void changePassword(String newPass) {
        if (newPass == null || newPass.isEmpty()) {
            throw new IllegalArgumentException("New Password cannot be null or empty");
        }
        this.hashedPassword = hash(newPass);
    }
    public String hash(String pass) {
        return Integer.toString(pass.hashCode());
    }

    public boolean uploaded(Book book) {
        if (book == null) {
            return false;
        }
        return this.uploadedBooks.contains(book);
    }
}


