package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;

public class CollectionDAO {
    public void createCollection(int userId, String collectionName) {
        String sql = "INSERT INTO collection (user_id, collection_name) VALUES (?, ?);";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setString(2, collectionName);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR creating collection: " + e.getMessage());
        }
    }
    public void addBookToCollection(int bookId, int collectionId) {
        String sql = "INSERT INTO collection_books (book_id, collection_id) VALUES (?, ?);";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, bookId);
            query.setInt(2, collectionId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR adding book to collection: " + e.getMessage());
        }
    }
    public void removeBookFromCollection(int bookId, int collectionId) {
        String sql = "DELETE FROM collection_books WHERE book_id = ? AND collection_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, bookId);
            query.setInt(2, collectionId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR removing book from collection: " + e.getMessage());
        }
    }
    public void deleteCollection(int collectionId) {
        String sql = "DELETE FROM collection WHERE collection_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, collectionId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR deleting collection: " + e.getMessage());
        }
    }
    public void renameCollection(int collectionId, String newName) {
        String sql = "UPDATE collection SET collection_name = ? WHERE collection_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, newName);
            query.setInt(2, collectionId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR renaming collection: " + e.getMessage());
        }
    }
    public ArrayList<Book> getBooksInCollection(int collectionId) {
        String sql = "SELECT * FROM books WHERE collection_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, collectionId);
            ResultSet res = query.executeQuery();
            ArrayList<Book> books = new ArrayList<>();
            while(res.next()) {
                Book book = new Book(
                    res.getInt("book_id"), 
                    res.getString("book_title"), 
                    res.getString("author_name"), 
                    res.getString("file_path"), 
                    res.getString("publisher"), 
                    res.getString("genre"));
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            System.err.println("ERROR getting books in collection: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public int getCollectionOwner(int collectionId) {
        String sql = "SELECT user_id FROM collection WHERE collection_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, collectionId);
            ResultSet res = query.executeQuery();
            if (res.next()) {
                return res.getInt("user_id");
            }
        } catch (SQLException e) {
            System.err.println("ERROR getting collection owner: " + e.getMessage());
            throw new RuntimeException("ERROR getting collection owner: " + e.getMessage());
        }
        return -1;
    }
}
