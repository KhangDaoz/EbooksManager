package com.ebookmanager.dao;

import java.sql.*;
import java.util.ArrayList;
import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;

public class BookDAO {
    
    // [CẬP NHẬT] Thêm tham số uploaderId
    public int addBook(String bookTitle, String authorName, String filePath, String publisher, String genre, int uploaderId) {
        String sql = "INSERT INTO book (book_title, author_name, file_path, publisher, genre, uploaded_by) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            query.setString(1, bookTitle);
            query.setString(2, authorName);
            query.setString(3, filePath);
            query.setString(4, publisher);
            query.setString(5, genre);
            query.setInt(6, uploaderId); // Lưu ID người upload
            
            int affectedRows = query.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = query.getGeneratedKeys()) {
                    if (generatedKeys.next()) return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    // [MỚI] Tìm sách theo người upload
    public ArrayList<Book> findBooksByUploader(int userId) {
        String sql = "SELECT * FROM book WHERE uploaded_by = ?;";
        return executeSelect(sql, userId);
    }

    // --- CÁC HÀM CŨ GIỮ NGUYÊN ---
    public ArrayList<Book> findAllBooks() {
        String sql = "SELECT * FROM book;";
        return executeSelect(sql);
    }

    public ArrayList<Book> searchBooks(String searchTerm) {
        String sql = "SELECT * FROM book WHERE book_title LIKE ? OR author_name LIKE ? OR publisher LIKE ? OR genre LIKE ?;";
        String term = "%" + searchTerm + "%";
        return executeSelect(sql, term, term, term, term);
    }
    
    public Book findBookById(int bookId) {
        String sql = "SELECT * FROM book WHERE book_id = ?";
        ArrayList<Book> list = executeSelect(sql, bookId);
        return list.isEmpty() ? null : list.get(0);
    }
    
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM book WHERE book_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, bookId); return query.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    
    public boolean updateBook(int bookId, String title, String author, String filePath, String publisher, String genre) {
        String sql = "UPDATE book SET book_title=?, author_name=?, file_path=?, publisher=?, genre=? WHERE book_id=?";
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, title); query.setString(2, author); query.setString(3, filePath);
            query.setString(4, publisher); query.setString(5, genre); query.setInt(6, bookId);
            return query.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private ArrayList<Book> executeSelect(String sql, Object... params) {
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) query.setInt(i + 1, (Integer) params[i]);
                else query.setString(i + 1, (String) params[i]);
            }
            try (ResultSet res = query.executeQuery()) {
                while (res.next()) {
                    books.add(new Book(res.getInt("book_id"), res.getString("book_title"), 
                        res.getString("author_name"), res.getString("file_path"), 
                        res.getString("publisher"), res.getString("genre")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return books;
    }
}