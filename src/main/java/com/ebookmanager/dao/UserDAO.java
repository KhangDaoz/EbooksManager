package com.ebookmanager.dao;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.Admin;
import com.ebookmanager.model.Member;
import com.ebookmanager.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO {
    

    public void addUser(String userName, String hashedPassword, String role) {
        String sql = "INSERT INTO user (user_name, hashed_password, role) VALUES (?, ?, ?) RETURNING user_id;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, userName);
            query.setString(2, hashedPassword);
            query.setString(3, role);
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
    
    public ArrayList<User> findAllUsers() {
        String sql = "SELECT user_id, user_name, hashed_password, role FROM user;";
        ArrayList<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement query = conn.prepareStatement(sql);
             ResultSet res = query.executeQuery()) {
            
            while (res.next()) {
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
                users.add(user);
            }
            
        } catch (SQLException e) {
            System.err.println("ERROR finding all users: " + e.getMessage());
        }
        
        return users;
    }

    public void updateUserPassword(int user_id, String new_hashed_password) {
        String sql = "UPDATE user SET hashed_password = ? WHERE user_id = ?;";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            query.setString(1, new_hashed_password);
            query.setInt(2, user_id);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR updating user password: " + e.getMessage());
        }
    }

    public int countAdmins() {
        String sql = "SELECT COUNT(*) FROM user WHERE role = 'Admin';";
        try(Connection conn = DatabaseConnector.getConnection();
            PreparedStatement query = conn.prepareStatement(sql)) {
            ResultSet res = query.executeQuery();
            return res.getInt(1);
        } catch (SQLException e) {
            System.err.println("ERROR counting admins: " + e.getMessage());
        }
        return 0;
    }
}
