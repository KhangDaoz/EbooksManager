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
        String sql = "DELETE FROM user_books WHERE user_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setInt(1, userbook.getUser_id());    
            stm.executeQuery();
        } 
        catch (SQLException ex) {
            
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
            stm.setInt(1, userbook.getUser_id());
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
            
            e.printStackTrace();
        } 
        return null;
    }
    // Take progress of book in user List
    public float getProgress(int user_id, Book book)
    {
        String sql = "SELECT reading_progress FROM user_books"
        + "WHERE user_id= ? and book_id= ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql) 
        )
        {
            stm.setInt(1, user_id);
            stm.setInt(2, book.getBookId());
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
    public boolean findBook(int user_id, Book book)
    {
        String sql = "SELECT book_id FROM user_books "
        + "WHERE user_id = ? AND book_id = ? ;";
        try
        (
            Connection conn = DatabaseConnector.getConnection();
            PreparedStatement stm = conn.prepareStatement(sql);
        ) 
        {
            stm.setInt(1, user_id);
            stm.setInt(2, book.getBookId());
            ResultSet rs = stm.executeQuery();

            int cnt = 0;
            while(rs.next())
            {
                cnt ++;
                if(cnt>0)
                {
                    return false;
                }
            }
            if(cnt == 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void addBook(Book book, int user_id)
    {
        if(findBook(user_id, book))
        {
            String sql = "INSERT INTO user_books (user_id, book_id) VALUES"
            + "(?, ?);";
            try
            (

                Connection conn = DatabaseConnector.getConnection();
                PreparedStatement stm = conn.prepareStatement(sql)
            )
            {
                stm.setInt(1, user_id);
                stm.setInt(2, book.getBookId());
                stm.executeUpdate();
            } catch (SQLException e) {
                
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Book existed");
        }
    }
}
