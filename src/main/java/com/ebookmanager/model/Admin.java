package com.ebookmanager.model;

public class Admin extends User {
    // Constructors
    public Admin() {
        super();
    }
    
    public Admin(int userId, String userName, String hashedPassword) {
        super(userId, userName, hashedPassword);
    }
    
    @Override
    public String getRole() {
        return "Admin";
    }
    

}
