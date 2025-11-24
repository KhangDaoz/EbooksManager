package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;

public class BookDAO {
    public int addBook(String bookTitle, String authorName, String filePath, String publisher, String genre) {
        String sql = "INSERT INTO book (book_title, author_name, file_path, publisher, genre) VALUES (?, ?, ?, ?, ?) RETURNING book_id;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, bookTitle);
            query.setString(2, authorName);
            query.setString(3, filePath);
            query.setString(4, publisher);
            query.setString(5, genre);
            ResultSet rs = query.executeQuery();
            if (rs.next()) {
                return rs.getInt("book_id");
            }
        } catch(SQLException e) {
            System.err.println("ERROR adding book: " + e.getMessage());
        }
        return -1;
    }
    
    public boolean updateBook(int bookId, String bookTitle, String authorName, String filePath, String publisher, String genre) {
        String sql = "UPDATE book SET book_title = ?, author_name = ?, file_path = ?, publisher = ?, genre = ? WHERE book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, bookTitle);
            query.setString(2, authorName);
            query.setString(3, filePath);
            query.setString(4, publisher);
            query.setString(5, genre);
            query.setInt(6, bookId);
            int rowsAffected = query.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("ERROR updating book: " + e.getMessage());
            return false;
        }
    }
    public ArrayList<Book> findAllBooks() {
        String sql = "SELECT * FROM book;";
        ArrayList<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            ResultSet res = query.executeQuery();
            while (res.next()) {
                Book book = new Book(
                    res.getInt("book_id"),
                    res.getString("book_title"),
                    res.getString("author_name"),
                    res.getString("file_path"),
                    res.getString("publisher"),
                    res.getString("genre")
                );
                books.add(book);
            }
            return books;
        }  catch (SQLException e) {
            System.err.println("ERROR finding all books: " + e.getMessage());
            return new ArrayList<>();
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
                String authorName = res.getString("author_name");
                String publisher = res.getString("publisher");
                String genre = res.getString("genre");
                
                if (authorName == null || authorName.isEmpty()) {
                    authorName = "Unknown";
                }

                if (publisher == null || publisher.isEmpty()) {
                    publisher = "Unknown";
                }
                
                book = new Book(
                        res.getInt("book_id"),
                        res.getString("book_title"),
                        authorName,
                        res.getString("file_path"),
                        publisher,
                        genre  
                );
            }

        } catch (SQLException e) {
            System.err.println("ERROR finding book by ID: " + e.getMessage());
            return null;
        }
        return book;
    }

    // Pure DAO - no business logic, just deletes by ID
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM book WHERE book_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, bookId);
            
            int rowsAffected = query.executeUpdate();
            return rowsAffected > 0; 

        } catch (SQLException e) {
            System.err.println("ERROR deleting book: " + e.getMessage());
            return false;
        }
    }
    public ArrayList<Book> searchBooks(String searchTerm) {
        String sql = "SELECT * FROM book WHERE book_title ILIKE ? OR author_name ILIKE ? OR publisher ILIKE ? OR genre ILIKE ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, "%" + searchTerm + "%");
            query.setString(2, "%" + searchTerm + "%");
            query.setString(3, "%" + searchTerm + "%");
            query.setString(4, "%" + searchTerm + "%");
            ResultSet res = query.executeQuery();
            ArrayList<Book> books = new ArrayList<>();
            while (res.next()) {
                Book book = new Book(
                    res.getString("book_title"),
                    res.getString("author_name"),
                    res.getString("file_path"),
                    res.getString("publisher"),
                    res.getString("genre")
                );
                books.add(book);
            }
            return books;
        } catch (SQLException e) {
            System.err.println("ERROR searching books: " + e.getMessage());
            return null;
        }
    }

}