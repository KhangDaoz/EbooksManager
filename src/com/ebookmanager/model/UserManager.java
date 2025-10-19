package com.ebookmanager.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import com.ebookmanager.dao.UserDAO;

public class UserManager {
    private UserDAO userDAO;
    private User currentUser;

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.currentUser = null;
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
        if(userDAO.findUserByName(user_name) != null) {
            return false; 
        }
        User newUser = new User(0, user_name, hashedPassword(password));
        userDAO.addUser(newUser);
        return true;
    }

    public boolean login(String user_name, String password) {
        User user = userDAO.findUserByName(user_name);
        if(user != null && user.getHashed_password().equals(hashedPassword(password))) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void deleteCurrentUser() {
        if(currentUser != null) {
            userDAO.deleteUser(currentUser.getUser_id());
            currentUser = null;
        }
    }

    public boolean changePassword(String old_password, String new_password) {
        if(currentUser != null && currentUser.getHashed_password().equals(hashedPassword(old_password))) {
            String new_hashed_password = hashedPassword(new_password);
            userDAO.updateUserPassword(currentUser.getUser_id(), new_hashed_password);
            currentUser.setHashed_password(new_hashed_password);
            return true;
        }
        return false;
    }
}
