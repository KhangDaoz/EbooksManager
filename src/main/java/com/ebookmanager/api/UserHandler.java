package com.ebookmanager.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.ebookmanager.service.Auth;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;

public class UserHandler extends BaseHandler {

    public UserHandler(Auth auth) {
        super(auth);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET" -> handleGetRequest(exchange);
            case "POST" -> handlePostRequest(exchange);
            case "PUT" -> handlePutRequest(exchange);
            case "DELETE" -> handleDeleteRequest(exchange);
            default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    // Handle GET requests (if any)
    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if(path.equals("/api/users")) {
            handleGetUserInfo(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleGetUserInfo(HttpExchange exchange) throws IOException {
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        
        // Get token from header to fetch user
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        String token = authHeader.substring(7); // Remove "Bearer "
        var user = auth.getUserFromToken(token);
        
        if (user == null) {
            sendResponse(exchange, 404, "{\"error\":\"User not found\"}");
            return;
        }

        String jsonResponse = "{\"userId\":" + user.getUser_id() + 
            ",\"username\":\"" + user.getUser_name() + "\"}";
        sendResponse(exchange, 200, jsonResponse);
    }

    // Handle POST requests for /register and /login
    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/api/users")) {
            handleRegister(exchange);
        } else if (path.equals("/api/sessions")) {
            handleLogin(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(body, mapType);

        String username = data.get("username");
        String password = data.get("password");

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Username is required\"}");
            return;
        }
        if (username.length() < 3) {
            sendResponse(exchange, 400, "{\"error\":\"Username must be at least 3 characters long\"}");
            return;
        }
        if (username.length() > 50) {
            sendResponse(exchange, 400, "{\"error\":\"Username must not exceed 50 characters\"}");
            return;
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Password is required\"}");
            return;
        }
        if (password.length() < 8) {
            sendResponse(exchange, 400, "{\"error\":\"Password must be at least 8 characters long\"}");
            return;
        }

        boolean success = auth.register(username.trim(), password);
        if (success) {
            sendResponse(exchange, 201, "{\"message\":\"Registration successful\"}");
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Username already exists\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(body, mapType);

        String username = data.get("username");
        String password = data.get("password");

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Username is required\"}");
            return;
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Password is required\"}");
            return;
        }

        String token = auth.login(username.trim(), password);
        if (token != null) {
            sendResponse(exchange, 200, "{\"token\":\"" + token + "\", \"message\":\"Login successful\"}");
        } else {
            sendResponse(exchange, 401, "{\"error\":\"Invalid credentials\"}");
        }
    }

    // Handle PUT requests for /change-password
    private void handlePutRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/api/sessions")) {
            handleChangePassword(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleChangePassword(HttpExchange exchange) throws IOException {
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Type mapType = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = gson.fromJson(body, mapType);

        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");

        // Validate old password
        if (oldPassword == null || oldPassword.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Old password is required\"}");
            return;
        }

        // Validate new password
        if (newPassword == null || newPassword.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"New password is required\"}");
            return;
        }
        if (newPassword.length() < 8) {
            sendResponse(exchange, 400, "{\"error\":\"New password must be at least 8 characters long\"}");
            return;
        }
        if (oldPassword.equals(newPassword)) {
            sendResponse(exchange, 400, "{\"error\":\"New password must be different from old password\"}");
            return;
        }

        boolean success = auth.changePassword(userId, oldPassword, newPassword);
        if (success) {
            sendResponse(exchange, 200, "{\"message\":\"Password changed successfully\"}");
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Failed to change password. Check your old password.\"}");
        }
    }

    // Handle DELETE requests for /logout
    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/api/sessions")) {
            handleLogout(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Remove "Bearer "
            auth.logout(token);
        }
        sendResponse(exchange, 200, "{\"message\":\"Logged out successfully\"}");
    }

    // public void start() throws IOException {
    //     HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

    //     server.createContext("/register", new RegisterHandler());
    //     server.createContext("/login", new LoginHandler());
    //     server.createContext("/logout", new LogoutHandler());

    //     server.setExecutor(null); // dùng executor mặc định
    //     server.start();
    //     System.out.println("Server dang chay o cong 8080...");
    // }

    // // --- Handler cho từng API ---
    // class RegisterHandler implements HttpHandler {
    //     @Override
    //     public void handle(HttpExchange exchange) throws IOException {
    //         if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
    //             sendResponse(exchange, 405, Map.of("message", "Method Not Allowed"));
    //             return;
    //         }

    //         String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    //         Type mapType = new TypeToken<Map<String, String>>() {}.getType();
    //         Map<String, String> data = gson.fromJson(body, mapType);

    //         String username = data.get("username");
    //         String password = data.get("password");

    //         boolean success = auth.register(username, password);
    //         if (success) sendResponse(exchange, 201, Map.of("message", "Registration successful"));
    //         else sendResponse(exchange, 400, Map.of("message", "Username already exists"));
    //     }
    // }

    // class LoginHandler implements HttpHandler {
    //     @Override
    //     public void handle(HttpExchange exchange) throws IOException {
    //         if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
    //             sendResponse(exchange, 405, Map.of("message", "Method Not Allowed"));
    //             return;
    //         }

    //         String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    //         Type mapType = new TypeToken<Map<String, String>>() {}.getType();
    //         Map<String, String> data = gson.fromJson(body, mapType);
            
    //         String username = data.get("username");
    //         String password = data.get("password");

    //         String token = auth.login(username, password);
    //         if (token != null)
    //             sendResponse(exchange, 200, Map.of("token", token, "message", "Login successful"));
    //         else
    //             sendResponse(exchange, 401, Map.of("message", "Invalid credentials"));
    //     }
    // }

    // class LogoutHandler implements HttpHandler {
    //     @Override
    //     public void handle(HttpExchange exchange) throws IOException {
    //         String token = exchange.getRequestHeaders().getFirst("Authorization");
    //         if (token != null) auth.logout(token);
    //         sendResponse(exchange, 200, Map.of("message", "Logged out successfully"));
    //     }
    // }

    // // --- Hàm tiện ích gửi phản hồi ---
    // private void sendResponse(HttpExchange exchange, int statusCode, Map<String, Object> body) throws IOException {
    //     String response = gson.toJson(body);
    //     exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
    //     exchange.sendResponseHeaders(statusCode, response.getBytes().length);
    //     try (OutputStream os = exchange.getResponseBody()) {
    //         os.write(response.getBytes());
    //     }
    // }
}
