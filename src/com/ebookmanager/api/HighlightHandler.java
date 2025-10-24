package com.ebookmanager.api;

import com.ebookmanager.dao.HighlightDAO;
import com.ebookmanager.dao.UserBookDAO;
import com.ebookmanager.model.Highlight;
import com.ebookmanager.service.Auth;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HighlightHandler extends BaseHandler {
    private final HighlightDAO highlightDAO;
    private final UserBookDAO userBookDAO;

    public HighlightHandler(Auth auth) {
        super(auth);
        this.highlightDAO = new HighlightDAO();
        this.userBookDAO = new UserBookDAO();
    }
    
    @Override
    public void handle(HttpExchange exchange) throws IOException{
        String method = exchange.getRequestMethod(); // e.g. /user/books/123/hightlights
        switch (method) {
            case "GET" -> handleGetRequest(exchange);
            case "POST" -> handlePostRequest(exchange);
            case "DELETE" -> handleDeleteRequest(exchange);
            default -> sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");

        }
    }



    //get all highlight for the book
    private void handleGetRequest(HttpExchange exchange) throws IOException { // e.g. /user/books/123/
        // is the user valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        // is the book exist?
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length-2]);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        }


        //is the book in the user's lib
        if(userBookDAO.findUserBook(userId, bookId) != null) {
            List<Highlight> highlights = highlightDAO.getHighlightForUserBook(userId, bookId);
            String jsonResponse= gson.toJson(highlights);
            sendResponse(exchange, 200, jsonResponse);
        } else {
            sendResponse(exchange, 200, "");
        }
    }


    //add highlight to a book
    private void handlePostRequest(HttpExchange exchange) throws IOException {
        //check if user is valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        
        // is the book exist?
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length-1]);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        }
        
        // is the book in the user's library?
        if(userBookDAO.findUserBook(userId, bookId) == null) {
            sendResponse(exchange, 403, "{\"error\":\"User need to add the book to their personal library first\"}");
            return;
        }
        
        try {
            // Read and parse request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = gson.fromJson(requestBody, Map.class);
            
            // Validate required fields
            if (!data.containsKey("start_pos") || !data.containsKey("end_pos")) {
                sendResponse(exchange, 400, "{\"error\":\"Missing required fields: start_pos and end_pos\"}");
                return;
            }
            
            // Extract highlight data
            int pageNumber = data.containsKey("page_number") ? ((Number) data.get("page_number")).intValue() : 0;
            int startPos = ((Number) data.get("start_pos")).intValue();
            int endPos = ((Number) data.get("end_pos")).intValue();
            String backgroundColor = data.containsKey("color") ? (String) data.get("color") : "yellow";
            String noteContent = data.containsKey("note") ? (String) data.get("note") : null;
            
            // Validate position range
            if (startPos >= endPos) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid range: start_pos must be less than end_pos\"}");
                return;
            }

            if (startPos < 0) {
                sendResponse(exchange, 400, "{\"error\":\"Invalid range: positions must be non-negative\"}");
                return;
            }
            
            // Get existing highlights for this user and book
            List<Highlight> existingHighlights = highlightDAO.getHighlightForUserBook(userId, bookId);
            
            // Check for EXACT duplicate (same start AND end position on same page)
            // Allow overlaps, but not exact same position
            for (Highlight existing : existingHighlights) {
                if (existing.getPageNumber() == pageNumber && 
                    existing.getStartPos() == startPos && 
                    existing.getEndPos() == endPos) {
                    sendResponse(exchange, 409, "{\"error\":\"Highlight already exists at this exact position\"}");
                    return;
                }
            }
            
            // Create new highlight using constructor (overlaps are OK!)
            Highlight newHighlight = new Highlight(userId, bookId, pageNumber, startPos, endPos, backgroundColor, noteContent);
            
            // Save to database
            highlightDAO.createHighlight(newHighlight);
            
            // Return created highlight
            String jsonResponse = gson.toJson(newHighlight);
            sendResponse(exchange, 201, jsonResponse);
            
        } catch (IOException e) {
            System.err.println("Error processing highlight: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }


    // DELETE /user/books/{bookId}/highlights
    // Requires highlight_id in request body
    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        // Check if user is valid
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        
        // Extract book_id from URL path
        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            // /user/books/123/highlights -> parts[3] is bookId
            bookId = Integer.parseInt(parts[3]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid Book ID in the URL\"}");
            return;
        }
        
        // Check if book is in user's library
        if (userBookDAO.findUserBook(userId, bookId) == null) {
            sendResponse(exchange, 403, "{\"error\":\"Book not in your library\"}");
            return;
        }

        try {
            // Get highlight_id from request body
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> data = gson.fromJson(requestBody, Map.class);
            
            if (!data.containsKey("highlight_id")) {
                sendResponse(exchange, 400, "{\"error\":\"Missing required field: highlight_id\"}");
                return;
            }
            
            int highlightId = ((Number) data.get("highlight_id")).intValue();
            
            // Delete the highlight
            highlightDAO.deleteHighlight(highlightId);
            sendResponse(exchange, 200, "{\"message\":\"Highlight deleted successfully\"}");
            
        } catch (IOException e) {
            System.err.println("Error deleting highlight: " + e.getMessage());
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data\"}");
        }
    }

}
