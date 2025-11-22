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


    public ArrayList<Bookmark> getBookmarksForBook(User user, Book book) {
        String sql = "SELECT * FROM bookmark WHERE user_id = ? AND book_id = ?;";
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, user.getUserId());
            query.setInt(2, book.getBookId());
            ResultSet res = query.executeQuery();
            while (res.next()) {
                Bookmark bookmark = new Bookmark(
                    res.getString("name"),
                    res.getString("location_data")
                );
                bookmark.setBookmarkId(res.getInt("bookmark_id"));
                bookmarks.add(bookmark);
            }
            return bookmarks;
        } catch (SQLException e) {
            System.err.println("ERROR fetching bookmarks: " + e.getMessage());
            return null;
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