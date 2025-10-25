package com.ebookmanager.service;

import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.User;
import com.ebookmanager.model.UserManager;

public class Auth {
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    private final UserManager userManager;

    public Auth(UserDAO userDAO, SessionManager sessionManager) {
        this.userDAO = userDAO;
        this.sessionManager = sessionManager;
        this.userManager = new UserManager(userDAO);
    }

    public boolean register(String username, String password) {
        return userManager.register(username, password);
    }

    public String login(String username,String password) {
        if (userManager.login(username, password)) {
            User user = userManager.getCurrentUser();
            int userId = user.getUser_id();
            userManager.logout();
            return sessionManager.createSession(userId);
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
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        User user = userDAO.getUserById(userId);
        if (user == null) return false;
        
        // Temporarily login to use UserManager's changePassword
        if (userManager.login(user.getUser_name(), oldPassword)) {
            boolean success = userManager.changePassword(oldPassword, newPassword);
            userManager.logout();
            return success;
        }
        return false;
    }

    // Delete user and all their sessions
    public void deleteUser(int userId) {
        sessionManager.removeUserSessions(userId);
        userDAO.deleteUser(userId);
    }



    
}
