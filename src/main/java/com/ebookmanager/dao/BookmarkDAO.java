package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Bookmark; 

public class BookmarkDAO { // Renamed class
    public void createBookmark(Bookmark bookmark) {
        // Removed background_color and note_content
        String sql = "INSERT INTO bookmark (user_id, book_id, location_data) VALUES (?,?,?);";
        
        // Use try-with-resources to ensure connection and statement are closed
        try (Connection conn = DatabaseConnector.getConnection();
             // Get generated keys to set the ID on the object
             PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            query.setInt(1, bookmark.getUserId());
            query.setInt(2, bookmark.getBookId());
            query.setString(3, bookmark.getLocationData());
            query.executeUpdate();

            // Get the auto-generated bookmarkId and set it on the object
            try (ResultSet generatedKeys = query.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bookmark.setBookmarkId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR creating bookmark: " + e.getMessage());
            e.printStackTrace(); // Added for more detail
        }
    }


    public List<Bookmark> getBookmarkForUserBook(int userId, int bookId) {
        List<Bookmark> bookmarks = new ArrayList<>();
        String sql = "SELECT * FROM bookmark WHERE user_id = ? AND book_id = ?;";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, userId);
            query.setInt(2, bookId);
            
            try (ResultSet res = query.executeQuery()) {
                while (res.next()) {
                    // Create bookmark using the simplified constructor
                    Bookmark bookmark = new Bookmark(
                        res.getInt("user_id"),
                        res.getInt("book_id"),
                        res.getString("location_data")
                    );
                    // IMPORTANT: Set the ID from the database
                    bookmark.setBookmarkId(res.getInt("bookmark_id")); 
                    bookmarks.add(bookmark);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR fetching bookmarks: " + e.getMessage());
            e.printStackTrace(); // Added for more detail
        }
        return bookmarks;
    }


    public void updateLocation(int bookmarkId, String newLocation) {
        String sql = "UPDATE bookmark SET location_data = ? WHERE bookmark_id = ?;";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setString(1, newLocation);
            query.setInt(2, bookmarkId);
            query.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("ERROR updating bookmark location: " + e.getMessage());
            e.printStackTrace(); // Added for more detail
        }
    }
    public void deleteBookmark(int bookmarkId) {
        String sql = "DELETE FROM bookmark WHERE bookmark_id = ?;";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, bookmarkId);
            query.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("ERROR deleting bookmark: " + e.getMessage());
            e.printStackTrace(); // Added for more detail
        }
    }
}