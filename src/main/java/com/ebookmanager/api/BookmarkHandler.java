package com.ebookmanager.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.dao.BookmarkDAO;
import com.ebookmanager.dao.UserBookDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.model.Bookmark;
import com.ebookmanager.service.Auth;
import com.sun.net.httpserver.HttpExchange;

public class BookmarkHandler extends BaseHandler {
    private final BookmarkDAO bookmarkDAO; // Renamed DAO
    private final UserBookDAO userBookDAO;
    private final BookDAO bookDAO;

    public BookmarkHandler(Auth auth) {
        super(auth);
        this.bookmarkDAO = new BookmarkDAO(); // Renamed DAO
        this.userBookDAO = new UserBookDAO();
        this.bookDAO = new BookDAO();
    }
    
    // Helper method for logging
    private void log(String message) {
        System.out.println("  │ " + message);
    }
    
    /**
     * Validates bookmark location data based on book format.
     * 
     * For PDF books, expects:
     * {
     *   "pageIndex": number (required),
     *   "scrollY": number (optional)
     * }
     * 
     * For EPUB books, expects:
     * {
     *   "spineIndex": number (required),
     *   "cfiRange": string (required),
     *   "percentage": number (optional)
     * }
     * 
     * @param locationData The parsed location data Map
     * @param bookFormat "PDF" or "EPUB"
     * @return Error message if validation fails, null if valid
     */
    private String validateLocationData(Map<String, Object> locationData, String bookFormat) {
        if (locationData == null || locationData.isEmpty()) {
            return "Location data cannot be empty";
        }
        
        if ("PDF".equalsIgnoreCase(bookFormat)) {
            // PDF format validation
            if (!locationData.containsKey("pageIndex")) {
                return "PDF bookmark requires 'pageIndex' field";
            }
            
            try {
                Object pageIndexObj = locationData.get("pageIndex");
                if (pageIndexObj == null) {
                    return "'pageIndex' cannot be null";
                }
                
                // Convert to integer to validate it's a number
                int pageIndex = ((Number) pageIndexObj).intValue();
                if (pageIndex < 0) {
                    return "'pageIndex' must be a non-negative number";
                }
                
                // Optional: validate scrollY if present
                if (locationData.containsKey("scrollY")) {
                    Object scrollYObj = locationData.get("scrollY");
                    if (scrollYObj != null) {
                        double scrollY = ((Number) scrollYObj).doubleValue();
                        if (scrollY < 0) {
                            return "'scrollY' must be a non-negative number";
                        }
                    }
                }
                
            } catch (ClassCastException | NullPointerException e) {
                return "Invalid data type for PDF bookmark fields";
            }
            
        } else if ("EPUB".equalsIgnoreCase(bookFormat)) {
            // EPUB format validation
            if (!locationData.containsKey("spineIndex")) {
                return "EPUB bookmark requires 'spineIndex' field";
            }
            if (!locationData.containsKey("cfiRange")) {
                return "EPUB bookmark requires 'cfiRange' field";
            }
            
            try {
                // Validate spineIndex
                Object spineIndexObj = locationData.get("spineIndex");
                if (spineIndexObj == null) {
                    return "'spineIndex' cannot be null";
                }
                int spineIndex = ((Number) spineIndexObj).intValue();
                if (spineIndex < 0) {
                    return "'spineIndex' must be a non-negative number";
                }
                
                // Validate cfiRange
                Object cfiRangeObj = locationData.get("cfiRange");
                if (cfiRangeObj == null) {
                    return "'cfiRange' cannot be null";
                }
                String cfiRange = cfiRangeObj.toString();
                if (cfiRange.trim().isEmpty()) {
                    return "'cfiRange' cannot be empty";
                }
                
                // Optional: validate percentage if present
                if (locationData.containsKey("percentage")) {
                    Object percentageObj = locationData.get("percentage");
                    if (percentageObj != null) {
                        double percentage = ((Number) percentageObj).doubleValue();
                        if (percentage < 0 || percentage > 100) {
                            return "'percentage' must be between 0 and 100";
                        }
                    }
                }
                
            } catch (ClassCastException | NullPointerException e) {
                return "Invalid data type for EPUB bookmark fields";
            }
            
        } else {
            return "Unsupported book format: " + bookFormat + ". Must be PDF or EPUB";
        }
        
        return null; // Valid
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // e.g. /user/books/123/bookmarks
        switch (method) {
            case "GET" -> handleGetRequest(exchange); // get all bookmarks for userbook
            case "POST" -> handlePostRequest(exchange); // add a bookmark in userbook
            case "PUT" -> handlePutRequest(exchange); // update a bookmark's location
            case "DELETE" -> handleDeleteRequest(exchange); // delete bookmark
            default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    // GET /user/books/{bookId}/bookmarks
    // get all bookmarks for the book
    private void handleGetRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Get bookmarks for book");
        
        // is the user valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            log("  ✗ Unauthorized user");
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        log("  User ID: " + userId);

        // is the book exist?
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            // /api/user/books/{bookId}/bookmarks
            // parts[0]="" parts[1]="api" parts[2]="user" parts[3]="books" parts[4]="{bookId}" parts[5]="bookmarks"
            if (parts.length < 6) { // Needs at least 6 parts
                log("  ✗ Invalid URL format");
                sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
                return;
            }
            bookId = Integer.parseInt(parts[4]); // Changed from parts[3] to parts[4]
            log("  Book ID: " + bookId);
        } catch (NumberFormatException e) {
            log("  ✗ Invalid Book ID format");
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            log("  ✗ Malformed URL");
            sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
            return;
        }

        try {
            // is the book in the user's library?
            if (userBookDAO.findUserBook(userId, bookId) != null) {
                log("  ✓ Book found in user's library");
                List<Bookmark> bookmarks = bookmarkDAO.getBookmarkForUserBook(userId, bookId); // Renamed
                log("  Found " + bookmarks.size() + " bookmarks"); // Renamed
                String jsonResponse = gson.toJson(bookmarks);
                sendResponse(exchange, 200, jsonResponse);
            } else {
                log("  ✗ Book not in user's library");
                sendResponse(exchange, 200, "[]"); // Return empty array
            }
        } catch (Exception e) {
            log("  ✗ Database error: " + e.getMessage());
            System.err.println("Error fetching bookmarks: " + e.getMessage()); // Renamed
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    // POST /user/books/{bookId}/bookmarks
    // add bookmark to a book
    private void handlePostRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Add bookmark to book");
        
        //check if user is valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            log("  ✗ Unauthorized user");
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        log("  User ID: " + userId);
        
        // is the book exist?
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 6) {
                log("  ✗ Invalid URL format");
                sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
                return;
            }
            bookId = Integer.parseInt(parts[4]); // Changed from parts[3] to parts[4]
            log("  Book ID: " + bookId);
        } catch (NumberFormatException e) {
            log("  ✗ Invalid Book ID format");
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            log("  ✗ Malformed URL");
            sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
            return;
        }
        
        try {
            // is the book in the user's library?
            if (userBookDAO.findUserBook(userId, bookId) == null) {
                log("  ✗ Book not in user's library");
                sendResponse(exchange, 403, "{\"error\":\"User need to add the book to their personal library first\"}");
                return;
            }
            log("  ✓ Book found in user's library");
            
            // Get book to check format
            Book book = bookDAO.findBookById(bookId);
            if (book == null) {
                log("  ✗ Book not found in database");
                sendResponse(exchange, 404, "{\"error\":\"Book not found\"}");
                return;
            }
            String bookFormat = book.getFormat(); // "PDF" or "EPUB"
            log("  Book format: " + bookFormat);
            
            // Read and parse request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = gson.fromJson(requestBody, Map.class);
            
            if (data == null) {
                log("  ✗ Empty or invalid JSON");
                sendResponse(exchange, 400, "{\"error\":\"Invalid JSON data\"}");
                return;
            }
            
            // Validate required field
            if (!data.containsKey("location_data")) {
                log("  ✗ Missing required field: location_data");
                sendResponse(exchange, 400, "{\"error\":\"Missing required field: location_data\"}");
                return;
            }
            
            Object locationObj = data.get("location_data");
            if (locationObj == null) {
                log("  ✗ Null location_data");
                sendResponse(exchange, 400, "{\"error\":\"location_data cannot be null\"}");
                return;
            }
            
            // Parse location_data as Map
            Map<String, Object> locationDataMap;
            try {
                if (locationObj instanceof Map) {
                    locationDataMap = (Map<String, Object>) locationObj;
                } else if (locationObj instanceof String) {
                    // If it's a string, parse it as JSON
                    locationDataMap = gson.fromJson((String) locationObj, Map.class);
                } else {
                    log("  ✗ Invalid location_data type");
                    sendResponse(exchange, 400, "{\"error\":\"location_data must be a JSON object\"}");
                    return;
                }
            } catch (Exception e) {
                log("  ✗ Failed to parse location_data: " + e.getMessage());
                sendResponse(exchange, 400, "{\"error\":\"Invalid location_data format\"}");
                return;
            }
            
            // Validate location data based on book format
            String validationError = validateLocationData(locationDataMap, bookFormat);
            if (validationError != null) {
                log("  ✗ Validation failed: " + validationError);
                sendResponse(exchange, 400, "{\"error\":\"" + validationError + "\"}");
                return;
            }
            log("  ✓ Location data validated for " + bookFormat + " format");
            
            // Convert validated map back to JSON string for storage
            String locationData = gson.toJson(locationDataMap);
            log("  Location data: " + locationData);
            
            // Create new bookmark
            Bookmark newBookmark = new Bookmark(userId, bookId, locationData);
            
            // Save to database
            bookmarkDAO.createBookmark(newBookmark); // Renamed
            log("  ✓ Bookmark created successfully"); // Renamed
            
            // Return created bookmark
            String jsonResponse = gson.toJson(newBookmark);
            sendResponse(exchange, 201, jsonResponse);
            
        } catch (IOException e) {
            log("  ✗ IO Error: " + e.getMessage());
            System.err.println("Error processing bookmark: " + e.getMessage()); // Renamed
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        } catch (Exception e) {
            log("  ✗ Database error: " + e.getMessage());
            System.err.println("Database error creating bookmark: " + e.getMessage()); // Renamed
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }

    // PUT /user/books/{bookId}/bookmarks - Update bookmark location
    private void handlePutRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Update bookmark location");
        
        // check if user is valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            log("  ✗ Unauthorized user");
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        log("  User ID: " + userId);
        
        // is the book valid?
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            // /api/user/books/{bookId}/bookmarks/{bookmarkId}
            // parts[0]="" parts[1]="api" parts[2]="user" parts[3]="books" parts[4]="{bookId}" parts[5]="bookmarks" parts[6]="{bookmarkId}"
            if (parts.length < 7) {
                log("  ✗ Invalid URL format");
                sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
                return;
            }
            bookId = Integer.parseInt(parts[4]); // Changed from parts[3] to parts[4]
            log("  Book ID: " + bookId);
        } catch (NumberFormatException e) {
            log("  ✗ Invalid Book ID format");
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            log("  ✗ Malformed URL");
            sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
            return;
        }
        
        try {
            // is the book in the user's library?
            if (userBookDAO.findUserBook(userId, bookId) == null) {
                log("  ✗ Book not in user's library");
                sendResponse(exchange, 403, "{\"error\":\"User need to add the book to their personal library first\"}");
                return;
            }
            log("  ✓ Book found in user's library");
            
            // Get book to check format
            Book book = bookDAO.findBookById(bookId);
            if (book == null) {
                log("  ✗ Book not found in database");
                sendResponse(exchange, 404, "{\"error\":\"Book not found\"}");
                return;
            }
            String bookFormat = book.getFormat(); // "PDF" or "EPUB"
            log("  Book format: " + bookFormat);

            // Read and parse request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = gson.fromJson(requestBody, Map.class);

            if (data == null) {
                log("  ✗ Empty or invalid JSON");
                sendResponse(exchange, 400, "{\"error\":\"Invalid JSON data\"}");
                return;
            }

            // Updated required fields
            if (!data.containsKey("bookmark_id") || !data.containsKey("location_data")) {
                log("  ✗ Missing required fields");
                sendResponse(exchange, 400, "{\"error\":\"Missing required fields: bookmark_id and location_data\"}");
                return;
            }

            int bookmarkId = -1;
            try {
                if (data.get("bookmark_id") == null) {
                    log("  ✗ Null bookmark_id");
                    sendResponse(exchange, 400, "{\"error\":\"bookmark_id cannot be null\"}");
                    return;
                }
                bookmarkId = ((Number) data.get("bookmark_id")).intValue();
                log("  Bookmark ID: " + bookmarkId);
            } catch (ClassCastException e) {
                log("  ✗ Invalid bookmark_id type");
                sendResponse(exchange, 400, "{\"error\":\"bookmark_id must be a number\"}");
                return;
            }

            // Verify the bookmark belongs to this user (security check)
            List<Bookmark> userBookmarks = bookmarkDAO.getBookmarkForUserBook(userId, bookId);
            boolean bookmarkBelongsToUser = false;
            for (Bookmark b : userBookmarks) {
                if (b.getBookmarkId() == bookmarkId) {
                    bookmarkBelongsToUser = true;
                    break;
                }
            }
            
            if (!bookmarkBelongsToUser) {
                log("  ✗ Bookmark doesn't belong to this user");
                sendResponse(exchange, 403, "{\"error\":\"You don't have permission to update this bookmark\"}");
                return;
            }
            log("  ✓ Bookmark ownership verified");

            // Get and validate new location data
            Object locationObj = data.get("location_data");
            if (locationObj == null) {
                log("  ✗ Null location_data");
                sendResponse(exchange, 400, "{\"error\":\"location_data cannot be null\"}");
                return;
            }
            
            // Parse location_data as Map
            Map<String, Object> locationDataMap;
            try {
                if (locationObj instanceof Map) {
                    locationDataMap = (Map<String, Object>) locationObj;
                } else if (locationObj instanceof String) {
                    // If it's a string, parse it as JSON
                    locationDataMap = gson.fromJson((String) locationObj, Map.class);
                } else {
                    log("  ✗ Invalid location_data type");
                    sendResponse(exchange, 400, "{\"error\":\"location_data must be a JSON object\"}");
                    return;
                }
            } catch (Exception e) {
                log("  ✗ Failed to parse location_data: " + e.getMessage());
                sendResponse(exchange, 400, "{\"error\":\"Invalid location_data format\"}");
                return;
            }
            
            // Validate location data based on book format
            String validationError = validateLocationData(locationDataMap, bookFormat);
            if (validationError != null) {
                log("  ✗ Validation failed: " + validationError);
                sendResponse(exchange, 400, "{\"error\":\"" + validationError + "\"}");
                return;
            }
            log("  ✓ Location data validated for " + bookFormat + " format");
            
            // Convert validated map back to JSON string for storage
            String newLocationData = gson.toJson(locationDataMap);
            
            bookmarkDAO.updateLocation(bookmarkId, newLocationData);
            log("  ✓ Bookmark location updated successfully");
            
            sendResponse(exchange, 200, "{\"message\":\"Bookmark's location updated\"}");
            
        } catch (IOException e) {
            log("  ✗ IO Error: " + e.getMessage());
            System.err.println("Error processing bookmark update: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        } catch (Exception e) {
            log("  ✗ Database error: " + e.getMessage());
            System.err.println("Database error updating bookmark: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }


    // DELETE /user/books/{bookId}/bookmarks
    // Requires bookmark_id in request body
    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Delete bookmark");
        
        // Check if user is valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            log("  ✗ Unauthorized user");
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        log("  User ID: " + userId);
        
        // Extract book_id from URL path
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            // /api/user/books/{bookId}/bookmarks/{bookmarkId}
            // parts[0]="" parts[1]="api" parts[2]="user" parts[3]="books" parts[4]="{bookId}" parts[5]="bookmarks" parts[6]="{bookmarkId}"
            if (parts.length < 7) {
                log("  ✗ Invalid URL format");
                sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
                return;
            }
            bookId = Integer.parseInt(parts[4]); // Changed from parts[3] to parts[4]
            log("  Book ID: " + bookId);
        } catch (NumberFormatException e) {
            log("  ✗ Invalid Book ID format");
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        } catch (ArrayIndexOutOfBoundsException e) {
            log("  ✗ Malformed URL");
            sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
            return;
        }
        
        try {
            // Check if book is in user's library
            if (userBookDAO.findUserBook(userId, bookId) == null) {
                log("  ✗ Book not in user's library");
                sendResponse(exchange, 403, "{\"error\":\"Book not in your library\"}");
                return;
            }
            log("  ✓ Book found in user's library");

            // Get bookmark_id from request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = gson.fromJson(requestBody, Map.class);
            
            if (data == null) {
                log("  ✗ Empty or invalid JSON");
                sendResponse(exchange, 400, "{\"error\":\"Invalid JSON data\"}");
                return;
            }
            
            if (!data.containsKey("bookmark_id")) {
                log("  ✗ Missing bookmark_id");
                sendResponse(exchange, 400, "{\"error\":\"Missing required field: bookmark_id\"}");
                return;
            }
            
            int bookmarkId = -1;
            try {
                if (data.get("bookmark_id") == null) {
                    log("  ✗ Null bookmark_id");
                    sendResponse(exchange, 400, "{\"error\":\"bookmark_id cannot be null\"}");
                    return;
                }
                bookmarkId = ((Number) data.get("bookmark_id")).intValue();
                log("  Bookmark ID: " + bookmarkId);
            } catch (ClassCastException e) {
                log("  ✗ Invalid bookmark_id type");
                sendResponse(exchange, 400, "{\"error\":\"bookmark_id must be a number\"}");
                return;
            }
            
            // Verify the bookmark belongs to this user (security check)
            List<Bookmark> userBookmarks = bookmarkDAO.getBookmarkForUserBook(userId, bookId);
            boolean bookmarkBelongsToUser = false;
            for (Bookmark b : userBookmarks) {
                if (b.getBookmarkId() == bookmarkId) {
                    bookmarkBelongsToUser = true;
                    break;
                }
            }
            
            if (!bookmarkBelongsToUser) {
                log("  ✗ Bookmark doesn't belong to this user");
                sendResponse(exchange, 403, "{\"error\":\"You don't have permission to delete this bookmark\"}");
                return;
            }
            log("  ✓ Bookmark ownership verified");
            
            // Delete the bookmark
            bookmarkDAO.deleteBookmark(bookmarkId);
            log("  ✓ Bookmark deleted successfully");
            
            sendResponse(exchange, 200, "{\"message\":\"Bookmark deleted successfully\"}");
            
        } catch (IOException e) {
            log("  ✗ IO Error: " + e.getMessage());
            System.err.println("Error deleting bookmark: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        } catch (Exception e) {
            log("  ✗ Database error: " + e.getMessage());
            System.err.println("Database error deleting bookmark: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
        }
    }
}