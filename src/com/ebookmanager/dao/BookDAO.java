package com.ebookmanager.dao;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public void addBook(Book book) {
        String sql = "INSERT INTO book (book_title, author_name, cover_image, file_path, publish_date) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setString(1, book.getBookTitle());
            query.setString(2, book.getAuthorName());
            query.setString(3, book.getCoverImage());
            query.setString(4, book.getFilePath());
            query.setString(5, book.getPublishDate());
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
                        res.getString("cover_image"),
                        res.getString("file_path"),
                        res.getString("publish_date")
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
                        res.getString("cover_image"),
                        res.getString("file_path"),
                        res.getString("publish_date")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            System.err.println("ERROR fetching all books: " + e.getMessage());
        }
        return books;
    }

    public void updateBook(Book book) {
        String sql = "UPDATE book SET book_title = ?, author_name = ?, cover_image = ?, file_path = ?, publish_date = ? WHERE book_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setString(1, book.getBookTitle());
            query.setString(2, book.getAuthorName());
            query.setString(3, book.getCoverImage());
            query.setString(4, book.getFilePath());
            query.setString(5, book.getPublishDate());
            query.setInt(6, book.getBookId());
            query.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR updating book: " + e.getMessage());
        }
    }

    public void deleteBook(int bookId) {
        String sql = "DELETE FROM book WHERE book_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, bookId);
            query.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR deleting book: " + e.getMessage());
        }
    }
}