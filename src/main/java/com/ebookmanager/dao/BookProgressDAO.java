package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.BookProgress;
import com.ebookmanager.model.Bookmark;
import com.ebookmanager.model.Member;

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
        String sql = "SELECT bp.*, b.bookmark_id, b.name, b.location_data " +
        "FROM book_progress bp " +
        "LEFT JOIN bookmarks b ON bp.book_id = b.book_id AND bp.user_id = b.user_id " +
        "WHERE bp.user_id = ? AND bp.book_id = ? " +
        "ORDER BY b.bookmark_id DESC;";
        
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, userId);
            query.setInt(2, bookId);
            
            try (ResultSet res = query.executeQuery()) {
                ArrayList<Bookmark> bookmarks = new ArrayList<>();
                
                int currentPage = 0;
                Date lastRead = null;
                int rating = 0;
                boolean foundProgress = false;

                while (res.next()) {
                    foundProgress = true;
                    currentPage = res.getInt("current_page");
                    lastRead = res.getDate("last_read");
                    rating = res.getInt("personal_rating");
                    
                    int bmId = res.getInt("bookmark_id");
                    if (bmId > 0) { 
                        bookmarks.add(new Bookmark(
                            bmId,
                            res.getString("name"),
                            res.getString("location_data")
                        ));
                    }
                }

                if (foundProgress) {
                    // Return progress data without book - Service will fetch book
                    return new BookProgress(currentPage, lastRead, rating, bookmarks, null);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR getting book progress: " + e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

}