package com.ebookmanager.model;

import java.util.ArrayList;

public class Collection {
    private Integer collectionId;
    private String collectionName;
    private ArrayList<Book> books;


    public Collection(int collectionId, String collectionName) {
        if (collectionId < 0) {
            throw new IllegalArgumentException("Collection ID cannot be negative");
        }
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.books = new ArrayList<>();
    }

    public Collection(String collectionName) {
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }
        this.collectionId = null;
        this.collectionName = collectionName;
        this.books = new ArrayList<>();
    }
    public String getCollectionName() {
        return collectionName;
    }
    public void setCollectionName(String collectionName) {
        if (collectionName == null || collectionName.isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be null or empty");
        }
        this.collectionName = collectionName;
    }
    public int getCollectionId() {
        return this.collectionId;
    }
    public ArrayList<Book> getBooks() {
        return books;
    }
    public void addBookToCollection(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        this.books.add(book);
    }
    public void removeBookFromCollection(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        this.books.remove(book);
    }
}