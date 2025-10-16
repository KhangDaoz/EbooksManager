package com.ebookmanager.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.*;
public class addUserBookDAO {
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
}
