package com.ebookmanager.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.*;
public class UserBookDAO {
    public void addUserBook(UserBook userbook)
    {
        String sql="INSERT INTO user_books (user_id, book_id,"
        + " reading_progress, date_added) VALUES (?, ?, ?, ?) ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setInt(1, userbook.getUser_id());
            stm.setInt(2, userbook.getBook_id());
            stm.setFloat(3, 0.0f);
            stm.setString(4, userbook.getDate_added());    
            stm.executeUpdate();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUserBook(UserBook userbook)
    {
        String sql = "DELETE FROM user_books WHERE user_id = ? AND book_id = ?;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setInt(1, userbook.getUser_id());    
            stm.setInt(2, userbook.getBook_id());
            stm.executeQuery();
        } 
        catch (SQLException ex) {
            
            ex.printStackTrace();
        }
    }

    public UserBook findUserBook(int user_id, int book_id)
    {
        String sql = "SELECT * FROM user_books WHERE user_id = ? AND book_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setInt(1, user_id);
            stm.setInt(2, book_id);
            ResultSet rs = stm.executeQuery();
            if(rs.next()) {
                UserBook userbook = new UserBook(
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getString("date_added"),
                    rs.getFloat("reading_progress")
                );
                return userbook;
            }

        } catch (SQLException e) {
            
            e.printStackTrace();
        } 
        return null;
    }

    public List<UserBook> getUserBooks(int user_id)
    {
        List<UserBook> userBooks = new ArrayList<>();
        String sql = "SELECT * FROM user_books WHERE user_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setInt(1, user_id);
            ResultSet rs = stm.executeQuery();
            while(rs.next())
            {
                UserBook userbook = new UserBook(
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getString("date_added"),
                    rs.getFloat("reading_progress")
                );
                userBooks.add(userbook);
            }

        } catch (SQLException e) {
            
            e.printStackTrace();
        } 
        return userBooks;
    }

    // // Take progress of book in user List
    // public float getProgress(int user_id, Book book)
    // {
    //     String sql = "SELECT reading_progress FROM user_books"
    //     + "WHERE user_id= ? and book_id= ? ;";
    //     try
    //     (
    //         Connection conn = DatabaseConnector.getConnection();
    //         PreparedStatement stm = conn.prepareStatement(sql) 
    //     )
    //     {
    //         stm.setInt(1, user_id);
    //         stm.setInt(2, book.getBookId());
    //         ResultSet rs = stm.executeQuery();
    //         float progress = 0.0f;
    //         while(rs.next())
    //         {
    //             progress = rs.getFloat("reading_progress");
    //         }
    //         return progress;
    //     } catch (SQLException ex) {
    //         ex.printStackTrace();
    //     }
    //     return 0.0f;
    // }    

    public void updateProgress(int user_id, int book_id, float progress)
    {
        String sql = "UPDATE user_books SET reading_progress = ?"
        + "WHERE user_id = ? AND book_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql)
        )
        {
            stm.setFloat(1, progress);
            stm.setString(2, Integer.toString(user_id));
            stm.setString(3, Integer.toString(book_id));
            stm.executeUpdate();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    // public boolean findBook(int user_id, Book book)
    // {
    //     String sql = "SELECT book_id FROM user_books "
    //     + "WHERE user_id = ? AND book_id = ? ;";
    //     try
    //     (
    //         Connection conn = DatabaseConnector.getConnection();
    //         PreparedStatement stm = conn.prepareStatement(sql);
    //     ) 
    //     {
    //         stm.setInt(1, user_id);
    //         stm.setInt(2, book.getBookId());
    //         ResultSet rs = stm.executeQuery();

    //         int cnt = 0;
    //         while(rs.next())
    //         {
    //             cnt ++;
    //             if(cnt>0)
    //             {
    //                 return false;
    //             }
    //         }
    //         if(cnt == 0) return true;
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     return false;
    // }

    // public void addBook(Book book, int user_id)
    // {
    //     if(findBook(user_id, book))
    //     {
    //         String sql = "INSERT INTO user_books (user_id, book_id) VALUES"
    //         + "(?, ?);";
    //         try
    //         (

    //             Connection conn = DatabaseConnector.getConnection();
    //             PreparedStatement stm = conn.prepareStatement(sql)
    //         )
    //         {
    //             stm.setInt(1, user_id);
    //             stm.setInt(2, book.getBookId());
    //             stm.executeUpdate();
    //         } catch (SQLException e) {
                
    //             e.printStackTrace();
    //         }
    //     }
    //     else
    //     {
    //         System.out.println("Book existed");
    //     }
    // }
}
