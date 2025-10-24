package com.ebookmanager.api;

import java.io.IOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.ebookmanager.dao.UserBookDAO;
import com.ebookmanager.model.UserBook;
import com.ebookmanager.service.Auth;
import com.sun.net.httpserver.HttpExchange;

public class UserBookHandler extends BaseHandler {
    private final UserBookDAO userBookDAO;

    public UserBookHandler(Auth auth) {
        super(auth);
        this.userBookDAO = new UserBookDAO();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "POST" -> handlePostRequest(exchange);
            case "GET" -> handleGetRequest(exchange);
            case "PUT" -> handlePutRequest(exchange);
            case "DELETE" -> handleDeleteRequest(exchange);
            default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    // Handle GET requests
    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if(path.matches("/api/users/books")) {
            handleGetUserBook(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleGetUserBook(HttpExchange exchange) throws IOException {
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        List<UserBook> userBook = userBookDAO.getUserBooks(userId);
        String jsonResponse = gson.toJson(userBook);
        sendResponse(exchange, 200, jsonResponse);
    }

    // Handle POST requests 
    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if(path.matches("/api/users/books/\\d+")) {
            handleAddUserBook(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleAddUserBook(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(body, mapType);
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(data.get("bookId"));
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID\"}");
            return;
        }

        if(userBookDAO.findUserBook(userId, bookId) != null) {
            sendResponse(exchange, 400, "{\"error\":\"Book already in user's library\"}");
            return;
        }

        String dateAdded = data.get("dateAdded");
        UserBook userBook = new UserBook(userId, bookId, dateAdded, 0);
        userBookDAO.addUserBook(userBook);
        sendResponse(exchange, 201, "{\"message\":\"Book added to user's library\"}");
    }

    // Handle PUT requests 
    private void handlePutRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if(path.matches("/api/users/books/\\d+/progress")) {
            handleUpdateProgress(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleUpdateProgress(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
        Type mapType = new TypeToken<java.util.Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(body, mapType);
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(data.get("bookId").toString());
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID\"}");
            return;
        }

        UserBook userBook = userBookDAO.findUserBook(userId, bookId);
        if (userBook == null) {
            sendResponse(exchange, 404, "{\"error\":\"Book not found in user's library\"}");
            return;
        }

        float progress;
        try {
            progress = Float.parseFloat(data.get("readingProgress").toString());
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid reading progress value\"}");
            return;
        }

        userBookDAO.updateProgress(userId, bookId, progress);
        sendResponse(exchange, 200, "{\"message\":\"Reading progress updated\"}");
    }

    // Handle DELETE requests 
    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if(path.matches("/api/users/books/\\d+")) {
            handleDeleteUserBook(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleDeleteUserBook(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
        Type mapType = new TypeToken<java.util.Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(body, mapType);
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(data.get("bookId"));
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID\"}");
            return;
        }

        UserBook userBook = userBookDAO.findUserBook(userId, bookId);
        if (userBook == null) {
            sendResponse(exchange, 404, "{\"error\":\"Book not found in user's library\"}");
            return;
        }

        userBookDAO.deleteUserBook(userBook);
        sendResponse(exchange, 200, "{\"message\":\"Book removed from user's library\"}");
    }
}
