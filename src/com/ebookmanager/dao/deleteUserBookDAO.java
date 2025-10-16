package com.ebookmanager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.UserBook;

public class deleteUserBookDAO {
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
}
