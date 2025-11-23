package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.Bookmark;
import com.ebookmanager.model.Member; 

public class BookmarkDAO { // Renamed class
    public void createBookmark(Member member, Book book, Bookmark bookmark) {
        String sql = "INSERT INTO bookmark (user_id, book_id, location_data) VALUES (?,?,?);";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            query.setInt(1, member.getUserId());
            query.setInt(2, book.getBookId());
            query.setString(3, bookmark.getLocationData());
            query.executeUpdate();

            try (ResultSet generatedKeys = query.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bookmark.setBookmarkId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("ERROR creating bookmark: " + e.getMessage());
        }
    }


    public ArrayList<Bookmark> getBookmarksForBook(Member member, Book book) {
        String sql = "SELECT * FROM bookmark WHERE user_id = ? AND book_id = ?;";
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, member.getUserId());
            query.setInt(2, book.getBookId());
            ResultSet res = query.executeQuery();
            while (res.next()) {
                Bookmark bookmark = new Bookmark(
                    res.getInt("bookmark_id"),
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