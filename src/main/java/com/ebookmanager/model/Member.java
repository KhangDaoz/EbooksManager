package com.ebookmanager.model;
import java.util.ArrayList;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Collection;

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
    @Override
    public String getRole() {
        return "Member";
    }
    public void addBookToLibrary(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        this.readingProgress.add(new BookProgress(book));
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
    public ArrayList<Collection> getCollections() {
        return collections;
    }
    public ArrayList<BookProgress> getReadingProgress() {
        return readingProgress;
    }
}