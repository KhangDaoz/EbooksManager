package com.ebookmanager.dao;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.User;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ebookmanager.db.*;
public class getProgressDAO {
    public float getProgress(User user, Book book)
    {
        String sql = "SELECT reading_progress FROM user_books"
        + "WHERE user_id=" + user.getUser_id()
        +"and book_id=" + book.getBookId() + ";";
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
}
