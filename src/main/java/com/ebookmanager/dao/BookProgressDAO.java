package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;

public class BookProgressDAO {
    
    public ArrayList<BookProgress> getBookProgresses(int userId) {
        String sql = "SELECT * FROM book_progress JOIN book ON book_progress.book_id = book.book_id WHERE user_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            ResultSet res = query.executeQuery();
            ArrayList<BookProgress> bookProgresses = new ArrayList<>();
            while(res.next()) {
                BookProgress progress = new BookProgress(
                    res.getInt("current_page"), 
                    res.getDate("last_read"), 
                    res.getInt("personal_rating"), 
                    new ArrayList<>(), 
                    new Book(res.getInt("book_id"), 
                            res.getString("book_title"), 
                            res.getString("author_name"), 
                            res.getString("file_path"), 
                            res.getString("publisher"), 
                            res.getString("genre"))
                    
                    );
                bookProgresses.add(progress);
            }
            return bookProgresses;
        } catch (SQLException e) {
            System.err.println("ERROR getting book progress: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public boolean checkIfBookIsInLibrary(int userId, int bookId) {
        String sql = "SELECT 1 FROM book_progress WHERE user_id = ? AND book_id = ?";
    
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
    
            query.setInt(1, userId);
            query.setInt(2, bookId);
    
            try (ResultSet res = query.executeQuery()) {
                return res.next();
            }
    
        } catch (SQLException e) {
            System.err.println("ERROR checking library status: " + e.getMessage());
            throw new RuntimeException("Database error occurred while checking library", e);
        }
    }
    public void addBookToLibrary(int userId, int bookId) {
        String sql = "INSERT INTO book_progress (user_id, book_id) VALUES (?, ?);";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setInt(2, bookId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR adding book to library: " + e.getMessage());
        }
    }
    
    public void removeBookFromLibrary(int userId, int bookId) {
        String sql = "DELETE FROM book_progress WHERE user_id = ? AND book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setInt(2, bookId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR removing book from library: " + e.getMessage());
        }
    }

    public BookProgress getBookProgress(int userId, int bookId) {
        String sql = "SELECT * FROM book_progress WHERE user_id = ? AND book_id = ?;";
        
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, userId);
            query.setInt(2, bookId);
            
            ResultSet res = query.executeQuery();
            if (res.next()) {
                return new BookProgress(
                    res.getInt("current_page"),
                    res.getDate("last_read"),
                    res.getInt("personal_rating"),
                    new ArrayList<>(),
                    null
                );
            }
            return null;
        } catch (SQLException e) {
            System.err.println("ERROR getting book progress: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }
    public void updateBookProgress(int userId, int bookId, int currentPage, int personalRating) {
        String sql = "UPDATE book_progress SET current_page = ?, personal_rating = ? WHERE user_id = ? AND book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, currentPage);
            query.setInt(2, personalRating);
            query.setInt(3, userId);
            query.setInt(4, bookId);
            query.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println("ERROR updating book progress: " + e.getMessage());
        }
    }
    public void rateBook(int userId, int bookId, int personalRating) {
        String sql = "UPDATE book_progress SET personal_rating = ? WHERE user_id = ? AND book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, personalRating);
            query.setInt(2, userId);
            query.setInt(3, bookId);
            query.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println("ERROR rating book: " + e.getMessage());
    }
}
    public boolean isBookInLibrary(int userId, int bookId) {
        String sql = "SELECT 1 FROM book_progress WHERE user_id = ? AND book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setInt(2, bookId);
            ResultSet res = query.executeQuery();
            return res.next();
        }
        catch (SQLException e) {
            System.err.println("ERROR checking if book is in library: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }
}