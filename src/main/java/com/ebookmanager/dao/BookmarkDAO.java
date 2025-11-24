package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Bookmark;

public class BookmarkDAO { 
    // DAO uses primitives - no Model dependencies in parameters
    public int createBookmark(int userId, int bookId, String locationData) {
        String sql = "INSERT INTO bookmark (user_id, book_id, location_data) VALUES (?,?,?) RETURNING bookmark_id;";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, userId);
            query.setInt(2, bookId);
            query.setString(3, locationData);
            
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                return rs.getInt("bookmark_id");
            }

        } catch (SQLException e) {
            System.err.println("ERROR creating bookmark: " + e.getMessage());
        }
        return -1;
    }


    public ArrayList<Bookmark> getBookmarksForBook(int userId, int bookId) {
        String sql = "SELECT * FROM bookmark WHERE user_id = ? AND book_id = ?;";
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setInt(2, bookId);
            ResultSet res = query.executeQuery();
            while (res.next()) {
                Bookmark bookmark = new Bookmark(
                    res.getInt("bookmark_id"),
                    res.getString("name"),
                    res.getString("location_data")
                );
                bookmarks.add(bookmark);
            }
            return bookmarks;
        } catch (SQLException e) {
            System.err.println("ERROR fetching bookmarks: " + e.getMessage());
            return new ArrayList<>();
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