package com.ebookmanager.service;

import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.model.User;
import com.ebookmanager.util.SessionManager;

public class UserAccountService {
    private UserDAO userDAO;

    public UserAccountService() {
        this.userDAO = new UserDAO();
    }

    public void deleteOwnAccount(String password) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            throw new SecurityException("No user logged in");
        }
        
        String hashedPassword = User.hash(password);
        if (!currentUser.verifyPassword(hashedPassword)) {
            throw new SecurityException("Incorrect password");
        }
        
        // if ("Admin".equals(currentUser.getRole())) {
        //     int adminCount = userDAO.countAdmins();
        //     if (adminCount <= 1) {
        //         throw new IllegalStateException("Cannot delete the last admin account");
        //     }
        // }
        
        userDAO.deleteUser(currentUser.getUserId());
        
        SessionManager.getInstance().logout();
    }
    
    public void changePassword(String oldPassword, String newPassword) {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            throw new SecurityException("No user logged in");
        }
        
        String hashedOldPassword = User.hash(oldPassword);
        if (!currentUser.verifyPassword(hashedOldPassword)) {
            throw new SecurityException("Old password is incorrect");
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        String hashedNewPassword = User.hash(newPassword);
        userDAO.updateUserPassword(currentUser.getUserId(), hashedNewPassword);
        
        currentUser.setHashedPassword(hashedNewPassword);
    }
    
    public void logout() {
        SessionManager.getInstance().logout();
    }

}
