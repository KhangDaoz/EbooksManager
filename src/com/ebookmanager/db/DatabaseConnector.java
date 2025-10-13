package com.ebookmanager.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ebookmanager";
    private static final String DB_USER = "root";
    private static final String DB_PASSW = "38862639";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    private DatabaseConnector() {} // Prevent instantiation

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSW);
    }
}