package com.ebookmanager.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.*;
public class UserBookDAO {
    public void addUserBook(UserBook userbook)
    {
        String sql="INSERT INTO user_book (user_id, book_id,"
        + " reading_progress, date_added) VALUES (?, ?, ?, ?) ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setString(1, userbook.getUser_id());
            stm.setString(2, userbook.getBook_id());
            stm.setFloat(3, userbook.getReading_progress(userbook.getUser_id()));
            stm.setString(4, userbook.getDate_added());    
            stm.executeUpdate();
        } 
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void deleteUserBook(UserBook userbook)
    {
        String sql = "DELETE FROM user_books WHERE user_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setString(1, userbook.getUser_id());    
            stm.executeQuery();
        } 
        catch (SQLException ex) {
            // TODO: handle exception
            ex.printStackTrace();
        }
    }

    public ArrayList<ArrayList<String>> findUserBook(UserBook userbook)
    {
        String sql = "SELECT * FROM user_books WHERE user_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setString(1, userbook.getUser_id());
            ResultSet rs = stm.executeQuery();
            ArrayList<ArrayList<String>> ans = new ArrayList<>();

            while(rs.next())
            {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(rs.getString("user_id"));
                temp.add(rs.getString("book_id"));
                temp.add(Float.toString(rs.getFloat("reading_progress")));
                temp.add(rs.getString("date_added"));
                ans.add(temp);
            }
            return ans;
        } catch (SQLException e) {
            // TODO: handle exception
            e.printStackTrace();
        } 
        return null;
    }

    public float getProgress(User user, Book book)
    {
        String sql = "SELECT reading_progress FROM user_books"
        + "WHERE user_id=" + user.getUser_id()
        +" and book_id=" + book.getBookId() + " ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql) 
        )
        {
            ResultSet rs = stm.executeQuery();
            float progress = 0.0f;
            while(rs.next())
            {
                progress = rs.getFloat("reading_progress");
            }
            return progress;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0.0f;
    }    

    public void saveProgress(User user, Book book, float progress)
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
            stm.setString(2, Integer.toString(user.getUser_id()));
            stm.setString(3, Integer.toString(book.getBookId()));
            stm.executeUpdate();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    public void updateUserBookDAO()
    {
        
    }
}
