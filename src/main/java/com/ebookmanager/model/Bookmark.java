package com.ebookmanager.model;

public class Bookmark {

    private int bookmarkId;
    private int userId;
    private int bookId;
    private String locationData; // Will store: "{\"pageIndex\":42, ...}" or "{\"spineIndex\":5, ...}"

    // Default constructor
    public Bookmark() {
    }

    // Constructor for creating a new bookmark
    public Bookmark(int userId, int bookId, String locationData) {
        this.userId = userId;
        this.bookId = bookId;
        this.locationData = locationData;
    }

    // --- Getters ---

    public int getBookmarkId() {
        return this.bookmarkId;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getBookId() {
        return this.bookId;
    }

    public String getLocationData() {
        return this.locationData;
    }

    // --- Setters ---

    public void setBookmarkId(int bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setLocationData(String locationData) {
        this.locationData = locationData;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "bookmarkId=" + bookmarkId +
                ", userId=" + userId +
                ", bookId=" + bookId +
                ", locationData='" + locationData + '\'' +
                '}';
    }
}