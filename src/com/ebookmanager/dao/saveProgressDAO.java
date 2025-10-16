package com.ebookmanager.dao;
import com.ebookmanager.model.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ebookmanager.db.DatabaseConnector;

public class saveProgressDAO {
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
}
