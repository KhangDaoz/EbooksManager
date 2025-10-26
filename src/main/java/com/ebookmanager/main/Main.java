package com.ebookmanager.main;

import com.ebookmanager.api.BookHandler;
import com.ebookmanager.api.HighlightHandler;
import com.ebookmanager.api.UserBookHandler;
import com.ebookmanager.api.UserHandler;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.service.Auth;
import com.ebookmanager.service.SessionManager;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        System.out.println("EbooksManager Server v1.0");
        System.out.println();

        UserDAO userDAO;
        try {
            System.out.println("Initializing database connection...");
            userDAO = new UserDAO();
            System.out.println("Database connection established");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        SessionManager sessionManager = new SessionManager();
        Auth auth = new Auth(userDAO, sessionManager);
        System.out.println("Authentication services ready");
        System.out.println();

        System.out.println("Starting HTTP servers...");
        
        startUserServer(auth);
        startBookServer(auth);
        startHighlightServer(auth);
        startUserBookServer(auth);

        System.out.println();
        System.out.println("All servers started successfully!");
        System.out.println("  User API:       http://localhost:8080/api/users");
        System.out.println("  Book API:       http://localhost:8081/api/books");
        System.out.println("  Highlight API:  http://localhost:8082/api/user/books/{bookId}/highlights");
        System.out.println("  UserBook API:   http://localhost:8083/api/users/books");
        System.out.println();
        System.out.println("Press Ctrl+C to stop");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("Servers stopped");
        }
    }

    private static void startUserServer(Auth auth) {
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer userServer = HttpServer.create(new InetSocketAddress(8080), 0);
                UserHandler userHandler = new UserHandler(auth);
                userServer.createContext("/api/users", userHandler);
                userServer.createContext("/api/sessions", userHandler);
                userServer.setExecutor(null);
                userServer.start();
                System.out.println("UserHandler server started on port 8080");
            } catch (Exception e) {
                System.err.println("Failed to start UserHandler server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(false);
        serverThread.start();
    }

    private static void startBookServer(Auth auth) {
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer bookServer = HttpServer.create(new InetSocketAddress(8081), 0);
                BookHandler bookHandler = new BookHandler(auth);
                bookServer.createContext("/api/books", bookHandler);
                bookServer.setExecutor(null);
                bookServer.start();
                System.out.println("BookHandler server started on port 8081");
            } catch (Exception e) {
                System.err.println("Failed to start BookHandler server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(false);
        serverThread.start();
    }

    private static void startHighlightServer(Auth auth) {
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer highlightServer = HttpServer.create(new InetSocketAddress(8082), 0);
                HighlightHandler highlightHandler = new HighlightHandler(auth);
                highlightServer.createContext("/user/books", highlightHandler);
                highlightServer.setExecutor(null);
                highlightServer.start();
                System.out.println("HighlightHandler server started on port 8082");
            } catch (Exception e) {
                System.err.println("Failed to start HighlightHandler server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(false);
        serverThread.start();
    }

    private static void startUserBookServer(Auth auth) {
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer userBookServer = HttpServer.create(new InetSocketAddress(8083), 0);
                UserBookHandler userBookHandler = new UserBookHandler(auth);
                userBookServer.createContext("/api/users/books", userBookHandler);
                userBookServer.setExecutor(null);
                userBookServer.start();
                System.out.println("UserBookHandler server started on port 8083");
            } catch (Exception e) {
                System.err.println("Failed to start UserBookHandler server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(false);
        serverThread.start();
    }
}