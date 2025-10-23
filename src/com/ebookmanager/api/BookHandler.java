package com.ebookmanager.api;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.service.Auth;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.google.gson.Gson;

public class BookHandler implements HttpHandler {
    private final BookDAO bookDAO;
    private final Gson gson;
    private final Auth auth;
    private static final String UPLOAD_DIR = "uploads/";

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
        ".pdf", ".epub", ".mobi", ".azw", ".azw3", ".txt", ".djvu", ".fb2"

    );

    private String getMimeType(String filename) {
        String lowercased = filename.toLowerCase();

        if (lowercased.endsWith(".pdf")) return "application/pdf";
        if (lowercased.endsWith(".epub")) return "application/epub+zip";
        if (lowercased.endsWith(".mobi")) return "application/x-mobipocket-ebook";
        if (lowercased.endsWith(".txt")) return "text/plain";
        if (lowercased.endsWith(".djvu")) return "image/vnd.djvu";
        if (lowercased.endsWith(".fb2")) return "application/x-fictionbook+xml";
        if (lowercased.endsWith(".azw") || lowercased.endsWith(".azw3")) {
            return "application/vnd.amazon.ebook"; 
        }

        return "application/octet-stream";
    }



    public BookHandler(Auth auth) {
        this.bookDAO = new BookDAO();
        this.gson = new Gson();
        this.auth = auth;
                // Create uploads directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }
    
    private Integer getUserIdFromRequest(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7); //Remove "Bearer "
        return auth.getUserIdFromToken(token);
    }

    private static class FileData {
        String filename;
        InputStream content;

        FileData(String filename, InputStream content) {
            this.filename = filename;
            this.content = content;
        }
    }
    
    private Map<String, Object> parseMultipartFormData(HttpExchange exchange) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        String boundary = "";

        // 1. Get the boundary string from the Content-Type header
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            boundary = "--" + contentType.substring(contentType.indexOf("boundary=") + 9);
        } else {
            return parameters; // Not a multipart request
        }

        // 2. Read the entire request body
        InputStream requestBody = exchange.getRequestBody();
        byte[] bodyBytes = requestBody.readAllBytes();
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);

        // 3. Split the body into parts using the boundary
        String[] parts = bodyString.split(boundary);

        for (String part : parts) {
            if (part.trim().isEmpty() || part.equals("--")) {
                continue;
            }

            // 4. For each part, find its headers and content
            String[] headersAndContent = part.split("\r\n\r\n", 2);
            if (headersAndContent.length < 2) continue;

            String headers = headersAndContent[0];
            String content = headersAndContent[1].trim();

            String name = null;
            String filename = null;

            // 5. Parse the "Content-Disposition" header to get the field name and filename
            if (headers.contains("Content-Disposition: form-data;")) {
                String[] dispositions = headers.split("\r\n");
                for(String disp : dispositions) {
                    if(disp.trim().startsWith("Content-Disposition")){
                        name = disp.replaceAll("(?i).*name=\"([^\"]+)\".*", "$1");
                        if (disp.contains("filename=\"")) {
                            filename = disp.replaceAll("(?i).*filename=\"([^\"]+)\".*", "$1");
                        }
                    }
                }
            }

            if (name == null) continue;

            // 6. Store the data in the map
            if (filename != null) {
                // It's a file. We need to find its raw bytes from the original body.
                // This is a simplified way to do it for this example.
                int startIndex = bodyString.indexOf(headers) + headers.length() + 4;
                int endIndex = bodyString.indexOf(boundary, startIndex) - 2;
                if (endIndex < startIndex) {
                    endIndex = bodyBytes.length - boundary.length() - 2;
                }
                byte[] fileBytes = Arrays.copyOfRange(bodyBytes, startIndex, endIndex);
                InputStream fileInputStream = new ByteArrayInputStream(fileBytes);
                parameters.put(name, new FileData(filename, fileInputStream));
            } else {
                // It's a regular text field
                parameters.put(name, content);
            }
        }
        return parameters;
    }


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        
        switch (method) {
            case "GET":
                // Matches "/api/books/read/[some number]"
                if (path.matches("/api/books/read/\\d+")) { 
                    handleReadBook(exchange);
                // Matches "/api/books/[some number]"
                } else if (path.matches("/api/books/\\d+")) { 
                    handleGetSingleBookRequest(exchange);
                // Matches "/api/books" (for getting the whole list)
                } else if (path.equals("/api/books")) { 
                    handleGetRequest(exchange);
                } else {
                    // Handle any other invalid GET requests
                    sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
                }
                break;
            case "DELETE":
                handleDeleteRequest(exchange);
                break;
            case "POST":
                handlePostRequest(exchange);
                break;
            case "PUT":
                handlePutRequest(exchange);
                break;
            default:
                sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                break;
        }
    }

    private void handleGetSingleBookRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        Integer bookId = Integer.parseInt(parts[parts.length-1]);
        Book searchForBook = bookDAO.findBookById(bookId);
        String jsonResponse = gson.toJson(searchForBook);
        sendResponse(exchange, 200, jsonResponse);
    }
    
    private void handleReadBook(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        int bookId = -1;
        try {
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length-1]);
        } catch (Exception e) {
            sendResponse(exchange, 400, "\"{\\\"error\\\":\\\"Invalid book ID\\\"}\"");
            return;
        }
        Book bookToRead = bookDAO.findBookById(bookId);
        if (bookToRead == null) {
            sendResponse(exchange, 404, "\"{\\\"Message\\\":\\\"Book Not Found\\\"}\"");
            return;
        }
        String filePathStr = bookToRead.getFilePath();
        Path filePath = Paths.get(filePathStr);

        if (!Files.exists(filePath)) {
            System.err.println("ERROR: missing file at " + filePathStr);
            sendResponse(exchange, 500, "{\"message\":\"Book not available on server\"}");
            return;
        }

        //try to get file's size/type for the ResponseHeader
        try {
            String filename = filePath.getFileName().toString();
            long fileSize = Files.size(filePath);

            exchange.getResponseHeaders().set("Content-Type", getMimeType(filename));
            exchange.sendResponseHeaders(200, fileSize);

            // 4. Get the response body stream and copy the file directly to it
            try (OutputStream os = exchange.getResponseBody()) {
                Files.copy(filePath, os);
            }
        } catch (IOException e) {
            System.err.println("Error streaming file: " + e.getMessage());
        }

    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        List<Book> books = bookDAO.getAllBooks();
        String jsonResponse = gson.toJson(books);
        sendResponse(exchange, 200, jsonResponse);
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {

        //check if user is logged in/authorized
        Integer uploaderId = getUserIdFromRequest(exchange);
        if (uploaderId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        

        // get Parsed data from post request
        Map<String,Object> dataParsed = parseMultipartFormData(exchange);

        //validation all the feild
        Object titleObj = dataParsed.get("title");
        if (titleObj == null || titleObj.toString().trim().isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"Book title is missing or invalid\"}");
            return;
        }
        String bookTitle = titleObj.toString();

        // these can be null
        String publishedDate = dataParsed.get("published_date").toString().trim();
        String bookAuthor = dataParsed.get("author").toString().trim();

        Object fileObject = dataParsed.get("ebookContent");
        FileData parsedFile = null;
        if (fileObject instanceof FileData) {
            parsedFile = (FileData) fileObject;
        } else {
            sendResponse(exchange, 400, "{\"error\":\"Ebook file is missing or invalid\"}");
            return;
        }

        //check the extension
        String originalFileName = parsedFile.filename;
        
        int dotIndex = originalFileName.lastIndexOf('.');
        String extension = (dotIndex > 0) ? originalFileName.substring(dotIndex).toLowerCase() : "";
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid file type. Allowed types are: " + ALLOWED_EXTENSIONS + "\"}");
            return;
        }

        //make a unique_name ?
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        Path filePath = Paths.get(UPLOAD_DIR, uniqueFileName);

        //save the file in the server
        try {
            Files.copy(parsedFile.content, filePath);
        } catch (IOException e) {
            System.out.println("Failed to save uploaded file: "+e.getMessage());
            sendResponse(exchange, 500, "{\"error\":\"Could not save file on the server.\"}");
            return;
        }

        //create new book to store in database
        Book newBook = new Book(bookTitle,bookAuthor,filePath.toString(),publishedDate);
        try {
            bookDAO.addBook(newBook, uploaderId);
            String jsonResponse = gson.toJson(newBook);
            sendResponse(exchange, 200, jsonResponse);
        } catch (Exception e) {
        }
    }
    
    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        int bookId = -1;
        try {
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length-1]);
        } catch (Exception e) {
            sendResponse(exchange, 400, "\"{\\\"error\\\":\\\"Invalid book ID in URL.\\\"}\"");
            return;
        }


        Book bookToDelete = bookDAO.findBookById(bookId);
        if (bookToDelete == null) {
            sendResponse(exchange, 404, "{\"error\":\"Book not found\"}");
            return;
        }


        // Is the user logged in?
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        //Does the logged-in user own this book?
        if (bookToDelete.getUploaderId() != userId) {
            sendResponse(exchange, 403, "{\"error\":\"Forbidden: You do not have permission to delete this book.\"}");
            return;
        }

        try {
            //get the file path
            String filePathStr = bookToDelete.getFilePath();
            Path filePath = Paths.get(filePathStr);

            boolean success = bookDAO.deleteBook(bookId, userId);
            if (success) {
                Files.deleteIfExists(filePath);
                sendResponse(exchange, 200, "{\"message\":\"Book deleted successfully.\"}");
            } else {
                sendResponse(exchange, 404, "{\"message\":\"Failed to delete(Book may no longer exist)\"}");
            }
        } catch (Exception e) {
            System.err.println("Error during book deletion: " + e.getMessage());
            sendResponse(exchange, 500, "\"{\\\"error\\\":\\\"A server error occurred during deletion.\\\"}\"");
        }
    }

    private void handlePutRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        int bookId = -1;
        try {
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length-1]);
        } catch (Exception e) {
            sendResponse(exchange, 400, "\"{\\\"error\\\":\\\"Invalid book ID in URL.\\\"}\"");
            return;
        }


        Book bookToUpdate = bookDAO.findBookById(bookId);
        if (bookToUpdate == null) {
            sendResponse(exchange, 404, "{\"error\":\"Book not found\"}");
            return;
        }


        // Is the user logged in?
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }

        //Does the logged-in user own this book?
        if (bookToUpdate.getUploaderId() != userId) {
            sendResponse(exchange, 403, "{\"error\":\"Forbidden: You do not have permission to delete this book.\"}");
            return;
        }
        try {
            // READ THE REQUEST BODY to get updated fields
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Book updatedBook = gson.fromJson(requestBody, Book.class);
            
            //reserve bookid and userId since they got overwrite
            updatedBook.setBookId(bookId);
            updatedBook.setUploaderId(userId);
            
            boolean success = bookDAO.updateBook(updatedBook, userId);
            
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Book updated successfully.\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to update book.\"}");
            }
        } catch (Exception e) {
            System.err.println("Error updating book: " + e.getMessage());
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data.\"}");
        }

    }

    private void sendResponse(HttpExchange exchange, int status, String jsonResponse) throws IOException {
        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }
}