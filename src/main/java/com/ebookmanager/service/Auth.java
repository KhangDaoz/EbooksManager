package com.ebookmanager.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.User;
// import com.ebookmanager.model.UserManager;


public class Auth {
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    // private final UserManager userManager;

    public Auth(UserDAO userDAO, SessionManager sessionManager) {
        if(userDAO == null) {
            throw new IllegalArgumentException("UserDAO cannot be null");   
        }
        if(sessionManager == null) {
            throw new IllegalArgumentException("SessionManager cannot be null");   
        }
        this.userDAO = userDAO;
        this.sessionManager = sessionManager;
        // this.userManager = new UserManager(userDAO);
    }

    private static String hashedPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean register(String user_name, String password) {
        // Validate username
        if (user_name == null || user_name.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (user_name.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters long");
        }
        if (user_name.length() > 50) {
            throw new IllegalArgumentException("Username must not exceed 50 characters");
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }

        if(userDAO.findUserByName(user_name.trim()) != null) {
            return false; 
        }
        User newUser = new User(0, user_name.trim(), hashedPassword(password));
        userDAO.addUser(newUser);
        return true;
    }

    public String login(String user_name, String password) {
        // Validate username
        if (user_name == null || user_name.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        User user = userDAO.findUserByName(user_name.trim());
        if(user != null && user.getHashed_password().equals(hashedPassword(password))) {
            return sessionManager.createSession(user.getUser_id());
        }
        return null;
    }

    public void logout(String token) {
        sessionManager.endSession(token);
    }

    public User getUserFromToken(String Token) {
        Integer userId = sessionManager.getUserIdFromToken(Token);
        if (userId == null) return null;
        return userDAO.getUserById(userId);
    }

    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }

    public Integer getUserIdFromToken(String token) {
        return sessionManager.getUserIdFromToken(token);
    }

    public boolean isValidToken(String token) {
        return sessionManager.isValidSession(token);
    }

    // Change password using UserManager
    public boolean changePassword(int userId, String old_password, String new_password) {
        User user = userDAO.getUserById(userId);
        if(user != null && user.getHashed_password().equals(hashedPassword(old_password))) {
            String new_hashed_password = hashedPassword(new_password);
            return userDAO.updateUserPassword(user.getUser_id(), new_hashed_password);
        }
        return false; 
    }

    // Delete user and all their sessions
    public void deleteUser(int userId) {
        sessionManager.removeUserSessions(userId);
        userDAO.deleteUser(userId);
    }



    
}
