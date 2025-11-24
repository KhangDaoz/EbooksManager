package com.ebookmanager.service;

import com.ebookmanager.model.User;
import com.ebookmanager.model.Member;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.util.SessionManager;


public class AuthenticationService {
    
    private UserDAO userDAO;
    
    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }
    
    public User login(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty");
        }
        
        if (password == null || password.isEmpty()) {
            throw new Exception("Password cannot be empty");
        }

        User user = userDAO.findByUsername(username.trim());
        
        if (user == null) {
            throw new Exception("User not found");
        }
        
        String hashedInputPassword = user.hash(password.trim());
        if (!user.verifyPassword(hashedInputPassword)) {
            throw new Exception("Invalid credentials");
        }
        
        SessionManager.getInstance().login(user);
        
        return user;
    }
    
    public void register(String username, String password, String confirmPassword) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new Exception("Username cannot be empty");
        }
        
        if (password == null || password.length() < 8) {
            throw new Exception("Password must be at least 8 characters");
        }
        
        if (!password.equals(confirmPassword)) {
            throw new Exception("Passwords don't match");
        }
        
        User existingUser = userDAO.findByUsername(username.trim());
        if (existingUser != null) {
            throw new Exception("Username already taken");
        }
        
        String hashedPassword = User.hash(password);
        userDAO.addUser(username.trim(), hashedPassword, "Member");
    }
    
    public void logout() {
        SessionManager.getInstance().logout();
    }
    
    public boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }
}

