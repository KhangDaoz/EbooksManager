// package com.ebookmanager.api;

// import java.io.IOException;
// import java.nio.charset.StandardCharsets;
// import java.util.List;
// import java.util.Map;

// import com.ebookmanager.dao.HighlightDAO;
// import com.ebookmanager.dao.UserBookDAO;
// import com.ebookmanager.model.Highlight;
// import com.ebookmanager.service.Auth;
// import com.sun.net.httpserver.HttpExchange;

// public class HighlightHandler extends BaseHandler {
//     private final HighlightDAO highlightDAO;
//     private final UserBookDAO userBookDAO;

//     public HighlightHandler(Auth auth) {
//         super(auth);
//         this.highlightDAO = new HighlightDAO();
//         this.userBookDAO = new UserBookDAO();
//     }
    
//     // Helper method for logging
//     private void log(String message) {
//         System.out.println("  │ " + message);
//     }
    
//     @Override
//     public void handle(HttpExchange exchange) throws IOException{
//         String method = exchange.getRequestMethod(); // e.g. /user/books/123/hightlights
//         switch (method) {
//             case "GET" -> handleGetRequest(exchange); // get all hightlight for userbook
//             case "POST" -> handlePostRequest(exchange); // add a hightligh in userbook
//             case "PUT" -> handlePutRequest(exchange); // update a note in highlight
//             case "DELETE" -> handleDeleteRequest(exchange); // delete highlight
//             default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");

//         }
//     }




//     //get all highlight for the book
//     private void handleGetRequest(HttpExchange exchange) throws IOException {
//         log("→ Processing: Get highlights for book");
        
//         // is the user valid
//         Integer userId = getUserIdFromRequest(exchange);
//         if (userId == null) {
//             log("  ✗ Unauthorized user");
//             sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
//             return;
//         }
//         log("  User ID: " + userId);

//         // is the book exist?
//         int bookId = -1;
//         try {
//             String path = exchange.getRequestURI().getPath();
//             String[] parts = path.split("/");
//             if (parts.length < 4) {
//                 log("  ✗ Invalid URL format");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
//                 return;
//             }
//             bookId = Integer.parseInt(parts[parts.length-2]);
//             log("  Book ID: " + bookId);
//         } catch (NumberFormatException e) {
//             log("  ✗ Invalid Book ID format");
//             sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
//             return;
//         } catch (ArrayIndexOutOfBoundsException e) {
//             log("  ✗ Malformed URL");
//             sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
//             return;
//         }

//         try {
//             // is the book in the user's library?
//             if (userBookDAO.findUserBook(userId, bookId) != null) {
//                 log("  ✓ Book found in user's library");
//                 List<Highlight> highlights = highlightDAO.getHighlightForUserBook(userId, bookId);
//                 log("  Found " + highlights.size() + " highlights");
//                 String jsonResponse = gson.toJson(highlights);
//                 sendResponse(exchange, 200, jsonResponse);
//             } else {
//                 log("  ✗ Book not in user's library");
//                 sendResponse(exchange, 200, "[]"); // Return empty array instead of empty string
//             }
//         } catch (Exception e) {
//             log("  ✗ Database error: " + e.getMessage());
//             System.err.println("Error fetching highlights: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
//         }
//     }


//     //add highlight to a book
//     private void handlePostRequest(HttpExchange exchange) throws IOException {
//         log("→ Processing: Add highlight to book");
        
//         //check if user is valid
//         Integer userId = getUserIdFromRequest(exchange);
//         if (userId == null) {
//             log("  ✗ Unauthorized user");
//             sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
//             return;
//         }
//         log("  User ID: " + userId);
        
//         // is the book exist?
//         int bookId = -1;
//         try {
//             String path = exchange.getRequestURI().getPath();
//             String[] parts = path.split("/");
//             if (parts.length < 1) {
//                 log("  ✗ Invalid URL format");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
//                 return;
//             }
//             bookId = Integer.parseInt(parts[parts.length-2]);
//             log("  Book ID: " + bookId);
//         } catch (NumberFormatException e) {
//             log("  ✗ Invalid Book ID format");
//             sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
//             return;
//         } catch (ArrayIndexOutOfBoundsException e) {
//             log("  ✗ Malformed URL");
//             sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
//             return;
//         }
        
//         try {
//             // is the book in the user's library?
//             if (userBookDAO.findUserBook(userId, bookId) == null) {
//                 log("  ✗ Book not in user's library");
//                 sendResponse(exchange, 403, "{\"error\":\"User need to add the book to their personal library first\"}");
//                 return;
//             }
//             log("  ✓ Book found in user's library");
            
//             // Read and parse request body
//             String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//             Map<String, Object> data = gson.fromJson(requestBody, Map.class);
            
//             if (data == null) {
//                 log("  ✗ Empty or invalid JSON");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid JSON data\"}");
//                 return;
//             }
            
            // Validate required fields for new schema
            // if (!data.containsKey("location_data")) {
            //     log("  ✗ Missing required field: location_data");
            //     sendResponse(exchange, 400, "{\"error\":\"Missing required field: location_data (JSON string)\"}");
            //     return;
            // }
            
            // String locationData = null;
            // try {
            //     Object locationObj = data.get("location_data");
            //     if (locationObj == null) {
            //         log("Null location_data");
            //         sendResponse(exchange, 400, "{\"error\":\"location_data cannot be null\"}");
            //         return;
            //     }

            //     if (locationObj instanceof String) {
            //         locationData = (String) locationObj;
            //     } else {
            //         locationData = gson.toJson(locationObj);
            //     }

            //     log("Location data: " + locationData);
                
            // } catch (Exception e) {
            //     log("  ✗  Invalid location_data format");
            //     sendResponse(exchange, 400, "{\"error\":\"Invalid location_data format\"}");
            //     return;
            // } 
            
            // String backgroundColor = "yellow"; // default
            // if (data.containsKey("background_color") && data.get("background_color") != null) {
            //     backgroundColor = String.valueOf(data.get("background_color"));
            // }
            
            // String noteContent = null;
            // if (data.containsKey("note_content") && data.get("note_content") != null) {
            //     noteContent = String.valueOf(data.get("note_content"));
            // }
            
            // // Create new highlight
            // Highlight newHighlight = new Highlight(userId, bookId, locationData, backgroundColor, noteContent);
            
//             // Save to database
//             highlightDAO.createHighlight(newHighlight);
//             log("  ✓ Highlight created successfully");
            
//             // Return created highlight
//             String jsonResponse = gson.toJson(newHighlight);
//             sendResponse(exchange, 201, jsonResponse);
            
//         } catch (IOException e) {
//             log("  ✗ IO Error: " + e.getMessage());
//             System.err.println("Error processing highlight: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
//         } catch (Exception e) {
//             log("  ✗ Database error: " + e.getMessage());
//             System.err.println("Database error creating highlight: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
//         }
//     }

//     // PUT /user/books/{bookId}/highlights - Update highlight note
//     private void handlePutRequest(HttpExchange exchange) throws IOException {
//         log("→ Processing: Update highlight note");
        
//         // check if user is valid
//         Integer userId = getUserIdFromRequest(exchange);
//         if (userId == null) {
//             log("  ✗ Unauthorized user");
//             sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
//             return;
//         }
//         log("  User ID: " + userId);
        
//         // is the book valid?
//         int bookId = -1;
//         try {
//             String path = exchange.getRequestURI().getPath();
//             String[] parts = path.split("/");
//             if (parts.length < 1) {
//                 log("  ✗ Invalid URL format");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
//                 return;
//             }
//             bookId = Integer.parseInt(parts[parts.length-1]);
//             log("  Book ID: " + bookId);
//         } catch (NumberFormatException e) {
//             log("  ✗ Invalid Book ID format");
//             sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
//             return;
//         } catch (ArrayIndexOutOfBoundsException e) {
//             log("  ✗ Malformed URL");
//             sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
//             return;
//         }
        
//         try {
//             // is the book in the user's library?
//             if (userBookDAO.findUserBook(userId, bookId) == null) {
//                 log("  ✗ Book not in user's library");
//                 sendResponse(exchange, 403, "{\"error\":\"User need to add the book to their personal library first\"}");
//                 return;
//             }
//             log("  ✓ Book found in user's library");

//             // Read and parse request body
//             String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//             Map<String, Object> data = gson.fromJson(requestBody, Map.class);

//             if (data == null) {
//                 log("  ✗ Empty or invalid JSON");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid JSON data\"}");
//                 return;
//             }

//             if (!data.containsKey("highlight_id") || !data.containsKey("note_content")) {
//                 log("  ✗ Missing required fields");
//                 sendResponse(exchange, 400, "{\"error\":\"Missing required fields: highlight_id and note_content\"}");
//                 return;
//             }

//             int highlightId = -1;
//             try {
//                 if (data.get("highlight_id") == null) {
//                     log("  ✗ Null highlight_id");
//                     sendResponse(exchange, 400, "{\"error\":\"highlight_id cannot be null\"}");
//                     return;
//                 }
//                 highlightId = ((Number) data.get("highlight_id")).intValue();
//                 log("  Highlight ID: " + highlightId);
//             } catch (ClassCastException e) {
//                 log("  ✗ Invalid highlight_id type");
//                 sendResponse(exchange, 400, "{\"error\":\"highlight_id must be a number\"}");
//                 return;
//             }

//             // Verify the highlight belongs to this user (security check)
//             List<Highlight> userHighlights = highlightDAO.getHighlightForUserBook(userId, bookId);
//             boolean highlightBelongsToUser = false;
//             for (Highlight h : userHighlights) {
//                 if (h.getHighlightId() == highlightId) {
//                     highlightBelongsToUser = true;
//                     break;
//                 }
//             }
            
//             if (!highlightBelongsToUser) {
//                 log("  ✗ Highlight doesn't belong to this user");
//                 sendResponse(exchange, 403, "{\"error\":\"You don't have permission to update this highlight\"}");
//                 return;
//             }
//             log("  ✓ Highlight ownership verified");

//             String newNoteContent = String.valueOf(data.get("note_content"));
            
//             highlightDAO.updateNoteContent(highlightId, newNoteContent);
//             log("  ✓ Note content updated successfully");
            
//             sendResponse(exchange, 200, "{\"message\":\"Highlight's note content updated\"}");
            
//         } catch (IOException e) {
//             log("  ✗ IO Error: " + e.getMessage());
//             System.err.println("Error processing highlight update: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
//         } catch (Exception e) {
//             log("  ✗ Database error: " + e.getMessage());
//             System.err.println("Database error updating highlight: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
//         }
//     }


//     // DELETE /user/books/{bookId}/highlights
//     // Requires highlight_id in request body
//     private void handleDeleteRequest(HttpExchange exchange) throws IOException {
//         log("→ Processing: Delete highlight");
        
//         // Check if user is valid
//         Integer userId = getUserIdFromRequest(exchange);
//         if (userId == null) {
//             log("  ✗ Unauthorized user");
//             sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
//             return;
//         }
//         log("  User ID: " + userId);
        
//         // Extract book_id from URL path
//         int bookId = -1;
//         try {
//             String path = exchange.getRequestURI().getPath();
//             String[] parts = path.split("/");
//             // /user/books/123/highlights -> parts[3] is bookId
//             if (parts.length < 4) {
//                 log("  ✗ Invalid URL format");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid URL format\"}");
//                 return;
//             }
//             bookId = Integer.parseInt(parts[3]);
//             log("  Book ID: " + bookId);
//         } catch (NumberFormatException e) {
//             log("  ✗ Invalid Book ID format");
//             sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
//             return;
//         } catch (ArrayIndexOutOfBoundsException e) {
//             log("  ✗ Malformed URL");
//             sendResponse(exchange, 400, "{\"error\":\"Malformed URL\"}");
//             return;
//         }
        
//         try {
//             // Check if book is in user's library
//             if (userBookDAO.findUserBook(userId, bookId) == null) {
//                 log("  ✗ Book not in user's library");
//                 sendResponse(exchange, 403, "{\"error\":\"Book not in your library\"}");
//                 return;
//             }
//             log("  ✓ Book found in user's library");

//             // Get highlight_id from request body
//             String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
//             Map<String, Object> data = gson.fromJson(requestBody, Map.class);
            
//             if (data == null) {
//                 log("  ✗ Empty or invalid JSON");
//                 sendResponse(exchange, 400, "{\"error\":\"Invalid JSON data\"}");
//                 return;
//             }
            
//             if (!data.containsKey("highlight_id")) {
//                 log("  ✗ Missing highlight_id");
//                 sendResponse(exchange, 400, "{\"error\":\"Missing required field: highlight_id\"}");
//                 return;
//             }
            
//             int highlightId = -1;
//             try {
//                 if (data.get("highlight_id") == null) {
//                     log("  ✗ Null highlight_id");
//                     sendResponse(exchange, 400, "{\"error\":\"highlight_id cannot be null\"}");
//                     return;
//                 }
//                 highlightId = ((Number) data.get("highlight_id")).intValue();
//                 log("  Highlight ID: " + highlightId);
//             } catch (ClassCastException e) {
//                 log("  ✗ Invalid highlight_id type");
//                 sendResponse(exchange, 400, "{\"error\":\"highlight_id must be a number\"}");
//                 return;
//             }
            
//             // Verify the highlight belongs to this user (security check)
//             List<Highlight> userHighlights = highlightDAO.getHighlightForUserBook(userId, bookId);
//             boolean highlightBelongsToUser = false;
//             for (Highlight h : userHighlights) {
//                 if (h.getHighlightId() == highlightId) {
//                     highlightBelongsToUser = true;
//                     break;
//                 }
//             }
            
//             if (!highlightBelongsToUser) {
//                 log("  ✗ Highlight doesn't belong to this user");
//                 sendResponse(exchange, 403, "{\"error\":\"You don't have permission to delete this highlight\"}");
//                 return;
//             }
//             log("  ✓ Highlight ownership verified");
            
//             // Delete the highlight
//             highlightDAO.deleteHighlight(highlightId);
//             log("  ✓ Highlight deleted successfully");
            
//             sendResponse(exchange, 200, "{\"message\":\"Highlight deleted successfully\"}");
            
//         } catch (IOException e) {
//             log("  ✗ IO Error: " + e.getMessage());
//             System.err.println("Error deleting highlight: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
//         } catch (Exception e) {
//             log("  ✗ Database error: " + e.getMessage());
//             System.err.println("Database error deleting highlight: " + e.getMessage());
//             e.printStackTrace();
//             sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
//         }
//     }


// }
