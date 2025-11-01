package com.ebookmanager.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

import com.ebookmanager.api.BookHandler;
import com.ebookmanager.api.BookmarkHandler;
import com.ebookmanager.api.UserBookHandler;
import com.ebookmanager.api.UserHandler;
import com.ebookmanager.dao.UserDAO;
import com.ebookmanager.service.Auth;
import com.ebookmanager.service.SessionManager;
import com.sun.net.httpserver.HttpServer;

public class Main {

    // API Base URLs - organized by port
    private static final String USER_API_URL = "http://localhost:8080/api/users";
    private static final String SESSION_API_URL = "http://localhost:8080/api/sessions";
    private static final String BOOK_API_URL = "http://localhost:8081/api/books";
    private static final String BOOKMARK_API_URL = "http://localhost:8082/api/user/books";
    private static final String USERBOOK_API_URL = "http://localhost:8083/api/users/books";
    
    private static Scanner scanner = new Scanner(System.in);
    private static String currentToken = null;

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
        startBookmarkServer(auth);
        startUserBookServer(auth);

        // Wait for servers to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("All servers started successfully!");
        System.out.println("  User API:       http://localhost:8080/api/users");
        System.out.println("  Book API:       http://localhost:8081/api/books");
        System.out.println("  Bookmark API:   http://localhost:8082/api/user/books");
        System.out.println("  UserBook API:   http://localhost:8083/api/users/books");
        System.out.println();

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
                    case 5:
                        testGetAllBooks();
                        break;
                    case 6:
                        testGetSingleBook();
                        break;
                    case 7:
                        testUploadBook();
                        break;
                    case 8:
                        testDeleteBook();
                        break;
                    case 9:
                        testUpdateBook();
                        break;
                    case 10:
                        testGetUserBooks();
                        break;
                    case 11:
                        testAddBookToLibrary();
                        break;
                    case 12:
                        testUpdateReadingProgress();
                        break;
                    case 13:
                        testRemoveBookFromLibrary();
                        break;
                    case 14:
                        testGetBookmarks();
                        break;
                    case 15:
                        testCreateBookmark();
                        break;
                    case 16:
                        testUpdateBookmark();
                        break;
                    case 17:
                        testDeleteBookmark();
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
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void startUserBookServer(Auth auth) {
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8083), 0);
                UserBookHandler userBookHandler = new UserBookHandler(auth);
                server.createContext("/api/users/books", userBookHandler);
                server.setExecutor(null);
                server.start();
                System.out.println("UserBookHandler server started on port 8083");
            } catch (Exception e) {
                System.err.println("Failed to start UserBookHandler server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void startBookmarkServer(Auth auth) {
        Thread serverThread = new Thread(() -> {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8082), 0);
                BookmarkHandler bookmarkHandler = new BookmarkHandler(auth);
                server.createContext("/api/user/books", bookmarkHandler);
                server.setExecutor(null);
                server.start();
                System.out.println("BookmarkHandler server started on port 8082");
            } catch (Exception e) {
                System.err.println("Failed to start BookmarkHandler server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void displayMenu() {
        System.out.println("\n=================================================");
        System.out.println("              TEST MENU");
        System.out.println("=================================================");
        System.out.println("USER API (port 8080):");
        System.out.println("1. Test Register (POST /api/users)");
        System.out.println("2. Test Login (POST /api/sessions)");
        System.out.println("3. Test Logout (DELETE /api/sessions)");
        System.out.println("4. Show Current Token");
        System.out.println();
        System.out.println("BOOK API (port 8081):");
        System.out.println("5. Test Get All Books (GET /api/books)");
        System.out.println("6. Test Get Single Book (GET /api/books/{id})");
        System.out.println("7. Test Upload Book (POST /api/books)");
        System.out.println("8. Test Delete Book (DELETE /api/books/{id})");
        System.out.println("9. Test Update Book (PUT /api/books/{id})");
        System.out.println();
        System.out.println("USER LIBRARY API (port 8083):");
        System.out.println("10. Test Get User's Library (GET /api/users/books)");
        System.out.println("11. Test Add Book to Library (POST /api/users/books/{bookId})");
        System.out.println("12. Test Update Reading Progress (PUT /api/users/books/{bookId}/progress)");
        System.out.println("13. Test Remove Book from Library (DELETE /api/users/books/{bookId})");
        System.out.println();
        System.out.println("BOOKMARK API (port 8082):");
        System.out.println("14. Test Get Bookmarks (GET /api/user/books/{bookId}/bookmarks)");
        System.out.println("15. Test Create Bookmark (POST /api/user/books/{bookId}/bookmarks)");
        System.out.println("16. Test Update Bookmark (PUT /api/user/books/{bookId}/bookmarks/{bookmarkId})");
        System.out.println("17. Test Delete Bookmark (DELETE /api/user/books/{bookId}/bookmarks/{bookmarkId})");
        System.out.println();
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

        HttpResponse response = sendRequest("POST", USER_API_URL, jsonPayload, null);

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

        HttpResponse response = sendRequest("POST", SESSION_API_URL, jsonPayload, null);

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

        HttpResponse response = sendRequest("DELETE", SESSION_API_URL, "", currentToken);

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
     * Test get all books
     */
    private static void testGetAllBooks() throws Exception {
        System.out.println("\n--- Testing Get All Books API ---");

        HttpResponse response = sendRequest("GET", BOOK_API_URL, null, null);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Successfully retrieved books!");
        } else {
            System.out.println("✗ Unexpected response");
        }
    }

    /**
     * Test get single book
     */
    private static void testGetSingleBook() throws Exception {
        System.out.println("\n--- Testing Get Single Book API ---");
        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine();

        HttpResponse response = sendRequest("GET", BOOK_API_URL + "/" + bookId, null, null);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Successfully retrieved book!");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found");
        } else {
            System.out.println("✗ Unexpected response");
        }
    }

    /**
     * Test upload book (multipart form-data)
     */
    private static void testUploadBook() throws Exception {
        System.out.println("\n--- Testing Upload Book API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter author name (optional): ");
        String author = scanner.nextLine();
        System.out.print("Enter published date (optional): ");
        String publishedDate = scanner.nextLine();
        System.out.print("Enter file path: ");
        String filePath = scanner.nextLine();
        
        // Remove surrounding quotes if present
        filePath = filePath.trim();
        if ((filePath.startsWith("\"") && filePath.endsWith("\"")) || 
            (filePath.startsWith("'") && filePath.endsWith("'"))) {
            filePath = filePath.substring(1, filePath.length() - 1);
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("✗ File not found: " + filePath);
            System.out.println("  (Tried: " + file.getAbsolutePath() + ")");
            return;
        }

        HttpResponse response = sendMultipartRequest(BOOK_API_URL, title, author, publishedDate, file, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 201) {
            System.out.println("✓ Book uploaded successfully!");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 400) {
            System.out.println("✗ Bad request - check your input");
        } else {
            System.out.println("✗ Upload failed");
        }
    }

    /**
     * Test delete book
     */
    private static void testDeleteBook() throws Exception {
        System.out.println("\n--- Testing Delete Book API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        System.out.print("Enter book ID to delete: ");
        String bookId = scanner.nextLine();

        HttpResponse response = sendRequest("DELETE", BOOK_API_URL + "/" + bookId, null, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Book deleted successfully!");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized");
        } else if (response.statusCode == 403) {
            System.out.println("✗ Forbidden - you don't own this book");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found");
        } else {
            System.out.println("✗ Delete failed");
        }
    }

    /**
     * Test update book
     */
    private static void testUpdateBook() throws Exception {
        System.out.println("\n--- Testing Update Book API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        System.out.print("Enter book ID to update: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new author: ");
        String author = scanner.nextLine();
        System.out.print("Enter new published date: ");
        String publishedDate = scanner.nextLine();

        String jsonPayload = String.format(
                "{\"bookTitle\":\"%s\",\"authorName\":\"%s\",\"publishedDate\":\"%s\"}",
                title, author, publishedDate);

        HttpResponse response = sendRequest("PUT", BOOK_API_URL + "/" + bookId, jsonPayload, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Book updated successfully!");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized");
        } else if (response.statusCode == 403) {
            System.out.println("✗ Forbidden - you don't own this book");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found");
        } else {
            System.out.println("✗ Update failed");
        }
    }

    /**
     * Test get user's library (all books in user's personal library)
     */
    private static void testGetUserBooks() throws Exception {
        System.out.println("\n--- Testing Get User's Library API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        String url = USERBOOK_API_URL;
        HttpResponse response = sendRequest("GET", url, null, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Successfully retrieved user's library!");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else {
            System.out.println("✗ Unexpected response");
        }
    }

    /**
     * Test add book to user's library
     */
    private static void testAddBookToLibrary() throws Exception {
        System.out.println("\n--- Testing Add Book to Library API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        System.out.print("Enter book ID to add to your library: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter date added (YYYY-MM-DD, optional): ");
        String dateAdded = scanner.nextLine();
        if (dateAdded.isEmpty()) {
            dateAdded = java.time.LocalDate.now().toString();
        }

        String jsonPayload = String.format("{\"bookId\":\"%s\",\"dateAdded\":\"%s\"}", bookId, dateAdded);

        String url = USERBOOK_API_URL + "/" + bookId;
        HttpResponse response = sendRequest("POST", url, jsonPayload, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 201) {
            System.out.println("✓ Book added to your library successfully!");
        } else if (response.statusCode == 400) {
            System.out.println("✗ Bad request - book might already be in library");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else {
            System.out.println("✗ Failed to add book to library");
        }
    }

    /**
     * Test update reading progress
     */
    private static void testUpdateReadingProgress() throws Exception {
        System.out.println("\n--- Testing Update Reading Progress API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        System.out.print("Enter book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter reading progress (0.0 to 1.0, e.g., 0.5 for 50%): ");
        String progress = scanner.nextLine();

        String jsonPayload = String.format("{\"bookId\":\"%s\",\"readingProgress\":\"%s\"}", bookId, progress);

        String url = USERBOOK_API_URL + "/" + bookId + "/progress";
        HttpResponse response = sendRequest("PUT", url, jsonPayload, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Reading progress updated successfully!");
        } else if (response.statusCode == 400) {
            System.out.println("✗ Bad request - check your input");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found in your library");
        } else {
            System.out.println("✗ Failed to update progress");
        }
    }

    /**
     * Test remove book from user's library
     */
    private static void testRemoveBookFromLibrary() throws Exception {
        System.out.println("\n--- Testing Remove Book from Library API ---");

        if (currentToken == null) {
            System.out.println("✗ No token stored. Please login first (option 2).");
            return;
        }

        System.out.print("Enter book ID to remove from your library: ");
        String bookId = scanner.nextLine();

        String jsonPayload = String.format("{\"bookId\":\"%s\"}", bookId);

        String url = USERBOOK_API_URL + "/" + bookId;
        HttpResponse response = sendRequest("DELETE", url, jsonPayload, currentToken);

        System.out.println("\n✓ Response Status: " + response.statusCode);
        System.out.println("✓ Response Body: " + response.body);

        if (response.statusCode == 200) {
            System.out.println("✓ Book removed from your library successfully!");
        } else if (response.statusCode == 400) {
            System.out.println("✗ Bad request - check your input");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found in your library");
        } else {
            System.out.println("✗ Failed to remove book");
        }
    }

    /**
     * Test get bookmarks for a book
     */
    private static void testGetBookmarks() throws Exception {
        if (currentToken == null) {
            System.out.println("⚠ Please login first");
            return;
        }

        System.out.println("\n--- Testing Get Bookmarks API ---");
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();

        String url = BOOKMARK_API_URL + "/" + bookId + "/bookmarks";
        HttpResponse response = sendRequest("GET", url, null, currentToken);

        if (response.statusCode == 200) {
            System.out.println("✓ Bookmarks retrieved successfully:");
            System.out.println(response.body);
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found");
        } else {
            System.out.println("✗ Failed to retrieve bookmarks: " + response.body);
        }
    }

    /**
     * Test create bookmark
     */
    private static void testCreateBookmark() throws Exception {
        if (currentToken == null) {
            System.out.println("⚠ Please login first");
            return;
        }

        System.out.println("\n--- Testing Create Bookmark API ---");
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();

        System.out.print("Is this a PDF or EPUB? (pdf/epub): ");
        String format = scanner.nextLine().toLowerCase();

        String locationData;
        if (format.equals("pdf")) {
            System.out.print("Enter page index (e.g., 5): ");
            String pageIndex = scanner.nextLine();
            System.out.print("Enter scroll Y position (optional, press Enter to skip): ");
            String scrollY = scanner.nextLine();
            
            if (scrollY.isEmpty()) {
                locationData = String.format("{\"pageIndex\":%s}", pageIndex);
            } else {
                locationData = String.format("{\"pageIndex\":%s,\"scrollY\":%s}", pageIndex, scrollY);
            }
        } else {
            System.out.print("Enter spine index (e.g., 3): ");
            String spineIndex = scanner.nextLine();
            System.out.print("Enter CFI range (e.g., epubcfi(/6/4[chap01ref]!/4/2/1:0)): ");
            String cfiRange = scanner.nextLine();
            System.out.print("Enter percentage (0-100, optional, press Enter to skip): ");
            String percentage = scanner.nextLine();
            
            if (percentage.isEmpty()) {
                locationData = String.format("{\"spineIndex\":%s,\"cfiRange\":\"%s\"}", spineIndex, cfiRange);
            } else {
                locationData = String.format("{\"spineIndex\":%s,\"cfiRange\":\"%s\",\"percentage\":%s}", 
                    spineIndex, cfiRange, percentage);
            }
        }

        String jsonPayload = String.format("{\"bookId\":\"%s\",\"location_data\":%s}", bookId, locationData);
        String url = BOOKMARK_API_URL + "/" + bookId + "/bookmarks";
        HttpResponse response = sendRequest("POST", url, jsonPayload, currentToken);

        if (response.statusCode == 201) {
            System.out.println("✓ Bookmark created successfully:");
            System.out.println(response.body);
        } else if (response.statusCode == 400) {
            System.out.println("✗ Invalid bookmark data: " + response.body);
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Book not found");
        } else {
            System.out.println("✗ Failed to create bookmark: " + response.body);
        }
    }

    /**
     * Test update bookmark
     */
    private static void testUpdateBookmark() throws Exception {
        if (currentToken == null) {
            System.out.println("⚠ Please login first");
            return;
        }

        System.out.println("\n--- Testing Update Bookmark API ---");
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter Bookmark ID: ");
        String bookmarkId = scanner.nextLine();

        System.out.print("Is this a PDF or EPUB? (pdf/epub): ");
        String format = scanner.nextLine().toLowerCase();

        String locationData;
        if (format.equals("pdf")) {
            System.out.print("Enter new page index (e.g., 5): ");
            String pageIndex = scanner.nextLine();
            System.out.print("Enter new scroll Y position (optional, press Enter to skip): ");
            String scrollY = scanner.nextLine();
            
            if (scrollY.isEmpty()) {
                locationData = String.format("{\"pageIndex\":%s}", pageIndex);
            } else {
                locationData = String.format("{\"pageIndex\":%s,\"scrollY\":%s}", pageIndex, scrollY);
            }
        } else {
            System.out.print("Enter new spine index (e.g., 3): ");
            String spineIndex = scanner.nextLine();
            System.out.print("Enter new CFI range (e.g., epubcfi(/6/4[chap01ref]!/4/2/1:0)): ");
            String cfiRange = scanner.nextLine();
            System.out.print("Enter new percentage (0-100, optional, press Enter to skip): ");
            String percentage = scanner.nextLine();
            
            if (percentage.isEmpty()) {
                locationData = String.format("{\"spineIndex\":%s,\"cfiRange\":\"%s\"}", spineIndex, cfiRange);
            } else {
                locationData = String.format("{\"spineIndex\":%s,\"cfiRange\":\"%s\",\"percentage\":%s}", 
                    spineIndex, cfiRange, percentage);
            }
        }

        String jsonPayload = String.format("{\"location_data\":%s}", locationData);
        String url = BOOKMARK_API_URL + "/" + bookId + "/bookmarks/" + bookmarkId;
        HttpResponse response = sendRequest("PUT", url, jsonPayload, currentToken);

        if (response.statusCode == 200) {
            System.out.println("✓ Bookmark updated successfully:");
            System.out.println(response.body);
        } else if (response.statusCode == 400) {
            System.out.println("✗ Invalid bookmark data: " + response.body);
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Bookmark not found");
        } else {
            System.out.println("✗ Failed to update bookmark: " + response.body);
        }
    }

    /**
     * Test delete bookmark
     */
    private static void testDeleteBookmark() throws Exception {
        if (currentToken == null) {
            System.out.println("⚠ Please login first");
            return;
        }

        System.out.println("\n--- Testing Delete Bookmark API ---");
        System.out.print("Enter Book ID: ");
        String bookId = scanner.nextLine();
        System.out.print("Enter Bookmark ID: ");
        String bookmarkId = scanner.nextLine();

        String url = BOOKMARK_API_URL + "/" + bookId + "/bookmarks/" + bookmarkId;
        HttpResponse response = sendRequest("DELETE", url, null, currentToken);

        if (response.statusCode == 200) {
            System.out.println("✓ Bookmark deleted successfully");
        } else if (response.statusCode == 401) {
            System.out.println("✗ Unauthorized - please login first");
        } else if (response.statusCode == 404) {
            System.out.println("✗ Bookmark not found");
        } else {
            System.out.println("✗ Failed to delete bookmark: " + response.body);
        }
    }

    /**
     * Send multipart/form-data request for file upload
     */
    private static HttpResponse sendMultipartRequest(String urlString, String title, String author,
            String publishedDate, File ebookContent, String token) throws Exception {
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        String LINE_FEED = "\r\n";

        URI uri = new URI(urlString);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        if (token != null) {
            String authHeader = "Bearer " + token;
            conn.setRequestProperty("Authorization", authHeader);
        }

        try (OutputStream os = conn.getOutputStream()) {
            // Add title field
            os.write(("--" + boundary + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.write(("Content-Disposition: form-data; name=\"title\"" + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.write((LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.write((title + LINE_FEED).getBytes(StandardCharsets.UTF_8));

            // Add author field if provided
            if (author != null && !author.isEmpty()) {
                os.write(("--" + boundary + LINE_FEED).getBytes(StandardCharsets.UTF_8));
                os.write(("Content-Disposition: form-data; name=\"author\"" + LINE_FEED)
                        .getBytes(StandardCharsets.UTF_8));
                os.write((LINE_FEED).getBytes(StandardCharsets.UTF_8));
                os.write((author + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            }

            // Add published_date field if provided
            if (publishedDate != null && !publishedDate.isEmpty()) {
                os.write(("--" + boundary + LINE_FEED).getBytes(StandardCharsets.UTF_8));
                os.write(("Content-Disposition: form-data; name=\"published_date\"" + LINE_FEED)
                        .getBytes(StandardCharsets.UTF_8));
                os.write((LINE_FEED).getBytes(StandardCharsets.UTF_8));
                os.write((publishedDate + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            }

            // Add file field
            os.write(("--" + boundary + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.write(("Content-Disposition: form-data; name=\"ebookContent\"; filename=\"" + ebookContent.getName()
                    + "\"" + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.write(("Content-Type: application/octet-stream" + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.write((LINE_FEED).getBytes(StandardCharsets.UTF_8));

            // Write file content
            Files.copy(ebookContent.toPath(), os);
            os.write((LINE_FEED).getBytes(StandardCharsets.UTF_8));

            // End multipart
            os.write(("--" + boundary + "--" + LINE_FEED).getBytes(StandardCharsets.UTF_8));
            os.flush();
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
     * Send HTTP request and get response
     */
    private static HttpResponse sendRequest(String method, String urlString, String jsonPayload, String token)
            throws Exception {
        URI uri = new URI(urlString);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        if (token != null) {
            // All APIs now use "Bearer " prefix for consistency
            conn.setRequestProperty("Authorization", "Bearer " + token);
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