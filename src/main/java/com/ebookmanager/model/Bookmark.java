package com.ebookmanager.model;

public class Bookmark {

    private int bookmarkId;
    private String name;
    private String locationData; // Will store: "{\"pageIndex\":42, ...}" or "{\"spineIndex\":5, ...}"

    // Default constructor
    public Bookmark() {
    }

    // Constructor for creating a new bookmark
    public Bookmark(String name, String locationData) {
        this.name = name;
        this.locationData = locationData;
    }

    // --- Getters ---

    public int getBookmarkId() {
        return this.bookmarkId;
    }

    public String getName() {
        return this.name;
    }

    public String getLocationData() {
        return this.locationData;
    }

    // --- Setters ---

    public void setBookmarkId(int bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocationData(String locationData) {
        this.locationData = locationData;
    }

    @Override
    public String toString() {
        return "Bookmark{" +
                "bookmarkId=" + bookmarkId +
                ", name=" + name +
                ", locationData='" + locationData + '\'' +
                '}';
    }
}