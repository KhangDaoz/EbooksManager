package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Bookmark;

public class BookmarkDAO { 

    // Đã cập nhật: Thêm tham số 'name' và sửa SQL
    public void createBookmark(int userId, int bookId, String locationData, String name) {
        // Thêm cột name vào câu lệnh INSERT
        String sql = "INSERT INTO bookmark (user_id, book_id, location_data, name) VALUES (?, ?, ?, ?);";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, userId);
            query.setInt(2, bookId);
            query.setString(3, locationData);
            query.setString(4, name); // Set giá trị cho cột name
            
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR creating bookmark: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    public ArrayList<Bookmark> getBookmarksForBook(int userId, int bookId) {
        String sql = "SELECT * FROM bookmark WHERE user_id = ? AND book_id = ?;";
        ArrayList<Bookmark> bookmarks = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setInt(1, userId);
            query.setInt(2, bookId);
            
            try (ResultSet res = query.executeQuery()) {
                while (res.next()) {
                    // Đảm bảo constructor của Bookmark khớp với thứ tự này
                    Bookmark bookmark = new Bookmark(
                        res.getInt("bookmark_id"),
                        res.getString("name"), // Lấy cột name từ DB
                        res.getString("location_data")
                    );
                    bookmarks.add(bookmark);
                }
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
            e.printStackTrace();
        }
    }
    
    public void deleteAllBookmarks(int userId) {
         String sql = "DELETE FROM bookmark WHERE user_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR deleting all bookmarks: " + e.getMessage());
        }
    }
}