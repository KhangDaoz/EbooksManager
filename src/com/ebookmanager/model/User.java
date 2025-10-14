package com.ebookmanager.model;

public class User {
    private int user_id;
    private String user_name;
    private String hashed_password;
    
    // Constructors
    public User() {

    }

    public User(int user_id, String user_name, String hashed_password) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.hashed_password = hashed_password;
    }

    // Getters and Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getHashed_password() {
        return hashed_password;
    }

    public void setHashed_password(String hashed_password) {
        this.hashed_password = hashed_password;
    }

    
    
}
