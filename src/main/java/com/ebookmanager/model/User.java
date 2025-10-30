package com.ebookmanager.model;

public class User {
    private int user_id;
    private String user_name;
    private String hashed_password;
    
    // Constructors
    public User() {}

    public User(int user_id, String user_name, String hashed_password) {
        if(user_id < 0 || user_name.isEmpty() || hashed_password.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument(s) for User constructor");
        }
        this.user_id = user_id;
        this.user_name = user_name;
        this.hashed_password = hashed_password;
    }

    // Getters and Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        if(user_id < 0) {
           throw new IllegalArgumentException("user_id cannot be negative");
        }
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        if(user_name.isEmpty()) {
           throw new IllegalArgumentException("user_name cannot be empty");
        }
        this.user_name = user_name;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public void setHashed_password(String hashed_password) {
        if(hashed_password.isEmpty()) {
            throw new IllegalArgumentException("hashed_password cannot be empty");
        }
        this.hashed_password = hashed_password;
    } 
}
