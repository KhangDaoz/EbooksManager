package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;

public class BookDAO {

    public void addBook(Book book, int userId) {
        String sql = "INSERT INTO book (book_title, author_name, format, file_path, publish_date, uploader_id) VALUES (?, ?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setString(1, book.getBookTitle());
            query.setString(2, book.getAuthorName());
            query.setString(3, book.getFormat());
            query.setString(4, book.getFilePath());
            query.setString(5, book.getPublishDate());
            query.setInt(6, userId);
            query.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR adding book: " + e.getMessage());
        }
    }

    public Book findBookById(int bookId) {
        String sql = "SELECT * FROM book WHERE book_id = ?;";
        Book book = null;
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, bookId);
            ResultSet res = query.executeQuery();

            if (res.next()) {
                book = new Book(
                        res.getInt("book_id"),
                        res.getString("book_title"),
                        res.getString("author_name"),
                        res.getString("format"),
                        res.getString("file_path"),
                        res.getString("publish_date"),
                        res.getInt("uploader_id")
                );
            }

        } catch (SQLException e) {
            System.err.println("ERROR finding book by ID: " + e.getMessage());
        }
        return book;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql);
             ResultSet res = query.executeQuery()) {

            while (res.next()) {
                Book book = new Book(
                        res.getInt("book_id"),
                        res.getString("book_title"),
                        res.getString("author_name"),
                        res.getString("format"),
                        res.getString("file_path"),
                        res.getString("publish_date"),
                        res.getInt("uploader_id")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            System.err.println("ERROR fetching all books: " + e.getMessage());
        }
        return books;
    }


    public boolean updateBook(Book book, int requestingUserId) {
        // First verify ownership
        Book existingBook = findBookById(book.getBookId());
        if (existingBook == null || existingBook.getUploaderId() != requestingUserId) {
            return false; // Not authorized
        }
        
        String sql = "UPDATE book SET book_title = ?, author_name = ?, publish_date = ? WHERE book_id = ? AND uploader_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setString(1, book.getBookTitle());
            query.setString(2, book.getAuthorName());
            query.setString(3, book.getPublishDate());
            query.setInt(4, book.getBookId());
            query.setInt(5, requestingUserId); // Add uploader_id check
            
            int rowsAffected = query.executeUpdate();
            return rowsAffected > 0; // Returns true if update succeeded

        } catch (SQLException e) {
            System.err.println("ERROR updating book: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBook(int bookId, int requestingUserId) {
        // First verify ownership
        Book existingBook = findBookById(bookId);
        if (existingBook == null || existingBook.getUploaderId() != requestingUserId) {
            return false; // Not authorized
        }   
        
        String sql = "DELETE FROM book WHERE book_id = ? AND uploader_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, bookId);
            query.setInt(2, requestingUserId); // Add uploader_id check
            
            int rowsAffected = query.executeUpdate();
            return rowsAffected > 0; 

        } catch (SQLException e) {
            System.err.println("ERROR deleting book: " + e.getMessage());
            return false;
        }
    }

}