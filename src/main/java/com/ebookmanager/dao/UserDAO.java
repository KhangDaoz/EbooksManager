package com.ebookmanager.dao;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Admin;
import com.ebookmanager.model.Member;
import com.ebookmanager.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public void addUser(Member member) {
        String sql = "INSERT INTO user (user_name, hashed_password, role) VALUES (?, ?, ?);";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, member.getUserName());
            query.setString(2, member.getHashedPassword());
            query.setString(3, member.getRole());
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR adding user: " + e.getMessage());
        }
    }

    public User findByUsername(String userName) {
        String sql = "SELECT user_id, user_name, hashed_password, role FROM user WHERE user_name = ?;";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql)) {
            
            query.setString(1, userName);
            ResultSet res = query.executeQuery();
            
            if (res.next()) {
                int userId = res.getInt("user_id");
                String username = res.getString("user_name");
                String hashedPassword = res.getString("hashed_password");
                String role = res.getString("role");
                
                User user;
                if ("ADMIN".equals(role)) {
                    user = new Admin();
                } else {
                    user = new Member();
                }
                
                user.setUserId(userId);
                user.setUserName(username);
                user.setHashedPassword(hashedPassword);
                
                return user;
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR finding user: " + e.getMessage());
        }
        
        return null;
    }
    
    public boolean checkUserExists(String userName) {
        return findByUsername(userName) != null;
    }

    public void deleteUser(int user_id) {
        String sql = "DELETE FROM user WHERE user_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setInt(1, user_id);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR deleting user: " + e.getMessage());
        }
    }

    public boolean updateUserPassword(int user_id, String new_hashed_password) {
        String sql = "UPDATE user SET hashed_password = ? WHERE user_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, new_hashed_password);
            query.setInt(2, user_id);
            int rowsAffected = query.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("ERROR updating user password: " + e.getMessage());
            return false;
        }
    }
}
