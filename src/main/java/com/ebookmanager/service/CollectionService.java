package com.ebookmanager.service;

import java.util.ArrayList;

import com.ebookmanager.dao.CollectionDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;

public class CollectionService {
    private CollectionDAO collectionDAO;
    
    public CollectionService() {
        this.collectionDAO = new CollectionDAO();
    }
    
    private void checkCollectionAccess(int collectionId, User user, String operation) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if ("Admin".equals(user.getRole())) {
            return;
        }
        
        int ownerId = collectionDAO.getCollectionOwner(collectionId);
        if (user.getUserId() != ownerId) {
            throw new SecurityException("You do not have permission to " + operation + " this collection");
        }
    }
    
    public void createCollection(User user, String collectionName) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (collectionName == null || collectionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be empty");
        }
        collectionDAO.createCollection(user.getUserId(), collectionName.trim());
    }
    
    public void addBookToCollection(int bookId, int collectionId, User user) {
        if (bookId <= 0 || collectionId <= 0) {
            throw new IllegalArgumentException("Invalid book or collection ID");
        }
        
        checkCollectionAccess(collectionId, user, "add books to");
        
        collectionDAO.addBookToCollection(bookId, collectionId);
    }
    
    public void removeBookFromCollection(int bookId, int collectionId, User user) {
        if (bookId <= 0 || collectionId <= 0) {
            throw new IllegalArgumentException("Invalid book or collection ID");
        }
        
        checkCollectionAccess(collectionId, user, "remove books from");
        
        collectionDAO.removeBookFromCollection(bookId, collectionId);
    }
    
    public void deleteCollection(int collectionId, User user) {
        if (collectionId <= 0) {
            throw new IllegalArgumentException("Invalid collection ID");
        }
        
        checkCollectionAccess(collectionId, user, "delete");
        
        collectionDAO.deleteCollection(collectionId);
    }
    
    public void renameCollection(int collectionId, String newName, User user) {
        if (collectionId <= 0) {
            throw new IllegalArgumentException("Invalid collection ID");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collection name cannot be empty");
        }
        
        checkCollectionAccess(collectionId, user, "rename");
        
        collectionDAO.renameCollection(collectionId, newName.trim());
    }
    
    public ArrayList<Book> getBooksInCollection(int collectionId, User user) {
        if (collectionId <= 0) {
            throw new IllegalArgumentException("Invalid collection ID");
        }
        
        checkCollectionAccess(collectionId, user, "view");
        
        return collectionDAO.getBooksInCollection(collectionId);
    }
}
