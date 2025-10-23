package com.ebookmanager.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.ebookmanager.service.Auth;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class UserHandler {
    private final Auth auth;
    private final Gson gson = new Gson();

    public UserHandler(Auth auth) {
        this.auth = auth;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/register", new RegisterHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/logout", new LogoutHandler());

        server.setExecutor(null); // dùng executor mặc định
        server.start();
        System.out.println("Server dang chay o cong 8080...");
    }

    // --- Handler cho từng API ---
    class RegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, Map.of("message", "Method Not Allowed"));
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Type mapType = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> data = gson.fromJson(body, mapType);

            String username = data.get("username");
            String password = data.get("password");

            boolean success = auth.register(username, password);
            if (success) sendResponse(exchange, 201, Map.of("message", "Registration successful"));
            else sendResponse(exchange, 400, Map.of("message", "Username already exists"));
        }
    }

    class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, Map.of("message", "Method Not Allowed"));
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Type mapType = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> data = gson.fromJson(body, mapType);
            
            String username = data.get("username");
            String password = data.get("password");

            String token = auth.login(username, password);
            if (token != null)
                sendResponse(exchange, 200, Map.of("token", token, "message", "Login successful"));
            else
                sendResponse(exchange, 401, Map.of("message", "Invalid credentials"));
        }
    }

    class LogoutHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String token = exchange.getRequestHeaders().getFirst("Authorization");
            if (token != null) auth.logout(token);
            sendResponse(exchange, 200, Map.of("message", "Logged out successfully"));
        }
    }

    // --- Hàm tiện ích gửi phản hồi ---
    private void sendResponse(HttpExchange exchange, int statusCode, Map<String, Object> body) throws IOException {
        String response = gson.toJson(body);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
