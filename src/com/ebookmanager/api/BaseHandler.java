package com.ebookmanager.api;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.ebookmanager.service.Auth;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class BaseHandler implements HttpHandler {
    protected final Gson gson;  // Changed to protected so child classes can access
    protected final Auth auth;  // Changed to protected so child classes can access

    public BaseHandler(Auth auth) {
        this.gson = new Gson();
        this.auth = auth;
    }
    
    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;  // Force child classes to implement
    protected Integer getUserIdFromRequest(HttpExchange exchange) {        
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7); //Remove "Bearer "
        return auth.getUserIdFromToken(token);
    }
    protected void sendResponse(HttpExchange exchange, int status, String jsonResponse) throws IOException {
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
    
}
