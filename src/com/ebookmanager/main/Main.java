package com.ebookmanager.main;

import com.ebookmanager.api.UserHandler;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.service.Auth;
import com.ebookmanager.service.SessionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Test class for UserHandler API
 * Tests registration, login, and logout endpoints
 */
public class Main {
    
    private static final String BASE_URL = "http://localhost:8080";
    private static Scanner scanner = new Scanner(System.in);
    private static String currentToken = null;
    
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   UserHandler API Testing Tool");
        System.out.println("=================================================\n");
        
        // Start the server in a separate thread
        Thread serverThread = new Thread(() -> {
            try {
                UserDAO userDAO = new UserDAO();
                SessionManager sessionManager = new SessionManager();
                Auth auth = new Auth(userDAO, sessionManager);
                UserHandler userHandler = new UserHandler(auth);
                
                System.out.println("Starting UserHandler server...\n");
                userHandler.start();
            } catch (Exception e) {
                System.err.println("Failed to start server: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        serverThread.setDaemon(true);
        serverThread.start();
        
        // Wait for server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Interactive menu
        boolean running = true;
        while (running) {
            displayMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        testRegister();
                        break;
                    case 2:
                        testLogin();
                        break;
                    case 3:
                        testLogout();
                        break;
                    case 4:
                        showCurrentToken();
                        break;
                    case 0:
                        running = false;
                        System.out.println("\nExiting... Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
        System.exit(0);
    }
    
    private static void displayMenu() {
        System.out.println("\n=================================================");
        System.out.println("              TEST MENU");
        System.out.println("=================================================");
        System.out.println("1. Test Register (POST /register)");
        System.out.println("2. Test Login (POST /login)");
        System.out.println("3. Test Logout (POST /logout)");
        System.out.println("4. Show Current Token");
        System.out.println("0. Exit");
        System.out.println("=================================================");
        System.out.print("Enter your choice: ");
    }
    
    /**
     * Test user registration
     */
    private static void testRegister() throws Exception {
        System.out.println("\n--- Testing Register API ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        
        HttpResponse response = sendRequest("POST", BASE_URL + "/register", jsonPayload, null);
        
        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);
        
        if (response.statusCode == 201) {
            System.out.println("✓ Registration successful!");
        } else if (response.statusCode == 400) {
            System.out.println("✗ Registration failed - username already exists");
        } else {
            System.out.println("✗ Unexpected response");
        }
    }
    
    /**
     * Test user login
     */
    private static void testLogin() throws Exception {
        System.out.println("\n--- Testing Login API ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        
        HttpResponse response = sendRequest("POST", BASE_URL + "/login", jsonPayload, null);
        
        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);
        
        if (response.statusCode == 200) {
            // Extract token from response
            if (response.body.contains("\"token\"")) {
                int tokenStart = response.body.indexOf("\"token\":\"") + 9;
                int tokenEnd = response.body.indexOf("\"", tokenStart);
                currentToken = response.body.substring(tokenStart, tokenEnd);
                System.out.println("✓ Login successful!");
                System.out.println("✓ Token saved: " + currentToken);
            }
        } else if (response.statusCode == 401) {
            System.out.println("✗ Login failed - invalid credentials");
        } else {
            System.out.println("✗ Unexpected response");
        }
    }
    
    /**
     * Test user logout
     */
    private static void testLogout() throws Exception {
        System.out.println("\n--- Testing Logout API ---");
        
        if (currentToken == null) {
            System.out.print("No token saved. Enter token manually (or press Enter to skip): ");
            String token = scanner.nextLine();
            if (!token.isEmpty()) {
                currentToken = token;
            } else {
                System.out.println("✗ No token provided. Please login first.");
                return;
            }
        }
        
        System.out.println("Using token: " + currentToken);
        
        HttpResponse response = sendRequest("POST", BASE_URL + "/logout", "", currentToken);
        
        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);
        
        if (response.statusCode == 200) {
            System.out.println("✓ Logout successful!");
            currentToken = null;
        }
    }
    
    
    /**
     * Show current stored token
     */
    private static void showCurrentToken() {
        System.out.println("\n--- Current Token ---");
        if (currentToken != null) {
            System.out.println("Token: " + currentToken);
        } else {
            System.out.println("No token stored. Please login first.");
        }
    }
    
    /**
     * Send HTTP request and get response
     */
    private static HttpResponse sendRequest(String method, String urlString, String jsonPayload, String token) throws Exception {
        URI uri = new URI(urlString);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        
        if (token != null) {
            conn.setRequestProperty("Authorization", token);
        }
        
        if (jsonPayload != null && !jsonPayload.isEmpty()) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        int statusCode = conn.getResponseCode();
        
        // Read response
        BufferedReader br;
        if (statusCode >= 200 && statusCode < 300) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }
        
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        return new HttpResponse(statusCode, response.toString());
    }
    
    /**
     * Simple HTTP response wrapper
     */
    static class HttpResponse {
        int statusCode;
        String body;
        
        HttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}