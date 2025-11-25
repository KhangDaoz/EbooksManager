package com.ebookmanager.model;

public class Bookmark {

    private int bookmarkId;
    private String name;
    private String locationData; // Will store: "{\"pageIndex\":42, ...}" or "{\"spineIndex\":5, ...}"

    // Constructor for creating a new bookmark
    public Bookmark(int bookmarkId, String name, String locationData) {
        this.bookmarkId = bookmarkId;
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
        if(bookmarkId < 0)
            throw new IllegalArgumentException("BookmarkId can't negative");
        this.bookmarkId = bookmarkId;
    }

    public void setName(String name) {
        if(name == null || name.isEmpty())
            throw new IllegalArgumentException("Bookmark name can't empty");
        this.name = name;
    }

    public void setLocationData(String locationData) {
        if(locationData == null || locationData.isEmpty())
            throw new IllegalArgumentException("Location of bookmark can't empty");
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