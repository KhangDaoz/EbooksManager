package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.BookProgress;

public class BookProgressDAO {
    public void addBookProgress(BookProgress bookProgress) {
        String sql = "INSERT INTO book_progress (user_id, book_id, current_page, last_read, personal_rating, bookmarks) VALUES (?, ?, ?, ?, ?, ?);";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, bookProgress.getUserId());
            query.setInt(2, bookProgress.getBookId());
            query.setInt(3, bookProgress.getCurrentPage());
            query.setDate(4, bookProgress.getLastRead());
            query.setInt(5, bookProgress.getPersonalRating());
            query.setString(6, bookProgress.getBookmarks());
        } catch (SQLException e) {
            System.err.println("ERROR adding book progress: " + e.getMessage());
        }
    }
    public void updateBookProgress(BookProgress bookProgress) {
        String sql = "UPDATE book_progress SET current_page = ?, last_read = ?, personal_rating = ?, bookmarks = ? WHERE user_id = ? AND book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, bookProgress.getCurrentPage());
            query.setDate(2, bookProgress.getLastRead());
            query.setInt(3, bookProgress.getPersonalRating());
            query.setString(4, bookProgress.getBookmarks());
        }
        catch (SQLException e) {
            System.err.println("ERROR updating book progress: " + e.getMessage());
        }
    }
    public void deleteBookProgress(int userId, int bookId) {
        String sql = "DELETE FROM book_progress WHERE user_id = ? AND book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setInt(2, bookId);
        }
        catch (SQLException e) {
            System.err.println("ERROR deleting book progress: " + e.getMessage());
        }
    }
    public ArrayList<BookProgress> getBookProgresses(int userId, int bookId) {
        String sql = "SELECT * FROM book_progress WHERE user_id = ? AND book_id = ?;";
        ArrayList<BookProgress> bookProgresses = new ArrayList<>();
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, userId);
            query.setInt(2, bookId);
            ResultSet res = query.executeQuery();
            while (res.next()) {
                BookProgress bookProgress = new BookProgress(
                    res.getInt("current_page"),
                    res.getDate("last_read"),
                    res.getInt("personal_rating"),
                    res.getArray("bookmarks")
                );
                bookProgress.setBookProgressId(res.getInt("book_progress_id"));
                bookProgresses.add(bookProgress);
            }
            return bookProgresses;
        } catch (SQLException e) {
            System.err.println("ERROR fetching book progresses: " + e.getMessage());
            return null;
        }
    }
    