package com.ebookmanager.model;

import java.util.ArrayList;

public class Member extends User {
    private ArrayList<Collection> collections;
    private ArrayList<BookProgress> readingProgress;

    public Member() {
        super();
        this.collections = new ArrayList<>();
        this.readingProgress = new ArrayList<>();
    }

    public Member(int userId, String userName, String hashedPassword) {
        super(userId, userName, hashedPassword);
        this.collections = new ArrayList<>();
        this.readingProgress = new ArrayList<>();
    }

    public ArrayList<Collection> getCollections() {
        return collections;
    }

    public ArrayList<BookProgress> getReadingProgress() {
        return readingProgress;
    }

    @Override
    public String getRole() {
        return "Member";
    }

    public void addBookToLibrary(BookProgress progress) {
        if (progress == null) {
            throw new IllegalArgumentException("Book progress cannot be null");
        }
        this.readingProgress.add(progress);
    }

    public void removeBookFromLibrary(BookProgress progress) {
        if (progress == null) {
            throw new IllegalArgumentException("Book progress cannot be null");
        }
        this.readingProgress.remove(progress);
    }

    public void createCollection(String collectionName) {
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }
        this.collections.add(new Collection(collectionName));
    }

    public void removeCollection(String collectionName) {
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }
        this.collections.removeIf(collection -> collection.getCollectionName().equals(collectionName));
    }
}