package com.ebookmanager.util;

import com.ebookmanager.model.User;
import com.ebookmanager.model.Member;
import com.ebookmanager.model.Admin;


public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    
    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.currentUser = user;
        System.out.println("User logged in: " + user.getUserName() + " (Role: " + user.getRole() + ")");
    }
    
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getUserName());
        }
        this.currentUser = null;
    }


    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean isAdmin() {
        return currentUser != null && currentUser instanceof Admin;
    }
    

    public boolean isMember() {
        return currentUser != null && currentUser instanceof Member;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}

