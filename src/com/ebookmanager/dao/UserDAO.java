package com.ebookmanager.dao;

import com.ebookmanager.db.DatabaseConnector;
import com.ebookmanager.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;
    
    public UserDAO() throws SQLException {
        this.connection = DatabaseConnector.getConnection();
    }

    public void addUser(User user) {
        String sql = "INSERT INTO user (user_name, hashed_password) VALUES (?, ?);";
        try(PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, user.getUser_name());
            query.setString(2, user.getHashed_password());
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR adding user: " + e.getMessage());
        }
    }

    public User findUserByName(String user_name) {
        String sql = "SELECT * FROM user WHERE user_name = ?;";
        try(PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, user_name);
            ResultSet res = query.executeQuery();
            if(res.next()) {
                User user = new User();
                user.setUser_id(res.getInt("user_id"));
                user.setUser_name(res.getString("user_name"));
                user.setHashed_password(res.getString("hashed_password"));
                return user;
            }
        } catch(SQLException e) {
            System.err.println("ERROR finding user: " + e.getMessage());
        }
        return null;
    }

    public void deleteUser(int user_id) {
        String sql = "DELETE FROM user WHERE user_id = ?;";
        try(PreparedStatement query = connection.prepareStatement(sql)) {
            query.setInt(1, user_id);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR deleting user: " + e.getMessage());
        }
    }

    public void updateUserPassword(int user_id, String new_hashed_password) {
        String sql = "UPDATE user SET hashed_password = ? WHERE user_id = ?;";
        try(PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, new_hashed_password);
            query.setInt(2, user_id);
            query.executeUpdate();
        } catch (SQLException e) {
            System.err.println("ERROR updating user password: " + e.getMessage());
        }
    }
    public User getUserById(int id) { // for authoriztion when making crud operation to book table
        String sql = "SELECT * FROM user WHERE user_id = ?;";
        try(PreparedStatement query = connection.prepareStatement(sql)) {
            query.setInt(1, id);
            ResultSet res = query.executeQuery();
            if(res.next()) {
                User user = new User();
                user.setUser_id(res.getInt("user_id"));
                user.setUser_name(res.getString("user_name"));
                user.setHashed_password(res.getString("hashed_password"));
                return user;
            }
        } catch(SQLException e) {
            System.err.println("ERROR finding user by id: " + e.getMessage());
        }
        return null;
    }
    
}
