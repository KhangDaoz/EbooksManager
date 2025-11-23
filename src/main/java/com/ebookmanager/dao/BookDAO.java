package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;

public class BookDAO {
    public void addBook(Book book) {
        String sql = "INSERT INTO book (book_title, author_name, file_path, publisher,genre) VALUES (?, ?, ?, ?, ?);";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, book.getBookTitle());
            query.setString(2, book.getAuthorName());
            query.setString(3, book.getFilePath());
            query.setString(4, book.getPublisher());
            query.setString(5, book.getGenre());
        } catch(SQLException e) {
            System.err.println("ERROR adding book: " + e.getMessage());
        }
    }
    public void updateBook(Book book) {
        String sql = "UPDATE book SET book_title = ?, author_name = ?, file_path = ?, publisher = ?, genre = ? WHERE book_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, book.getBookTitle());
            query.setString(2, book.getAuthorName());
            query.setString(3, book.getFilePath());
            query.setString(4, book.getPublisher());
            query.setString(5, book.getGenre());
            query.setInt(6, book.getBookId());
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR updating book: " + e.getMessage());
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

    public boolean deleteBook(int bookID, User requestingUser) {
        Book existingBook = findBookById(bookID);
        if (existingBook == null || (!requestingUser.getRole().equals("Admin") && !requestingUser.checkUploaded(existingBook))) {
            return false; 
        }   
        
        String sql = "DELETE FROM book WHERE book_id = ?;";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {

            query.setInt(1, bookID);
            
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