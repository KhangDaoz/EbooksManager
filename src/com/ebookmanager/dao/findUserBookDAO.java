package com.ebookmanager.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.UserBook;
public class findUserBookDAO {
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
}
