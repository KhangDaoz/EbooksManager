package com.ebookmanager.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.ebookmanager.dao.BookDAO;
import com.ebookmanager.model.Book;
import com.ebookmanager.service.Auth;
import com.sun.net.httpserver.HttpExchange;

public class BookHandler extends BaseHandler {
    private final BookDAO bookDAO;
    private static final String UPLOAD_DIR = "uploads/";

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf", ".epub"

    );

    private String getMimeType(String filename) {
        String lowercased = filename.toLowerCase();

        if (lowercased.endsWith(".pdf"))
            return "application/pdf";
        if (lowercased.endsWith(".epub"))
            return "application/epub+zip";
        return "application/octet-stream";
    }

    public BookHandler(Auth auth) {
        super(auth);
        this.bookDAO = new BookDAO();

        // Create uploads directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create upload directory: " + e.getMessage());
        }
    }

    // Helper method for logging
    private void log(String message) {
        System.out.println("  │ " + message);
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

        // 1. Get the boundary string from the Content-Type header
        String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            return parameters; // Not a multipart request
        }

        String boundary = "--" + contentType.substring(contentType.indexOf("boundary=") + 9);
        byte[] boundaryBytes = boundary.getBytes(StandardCharsets.UTF_8);

        // 2. Read the entire request body as bytes (DO NOT convert to String yet - this
        // corrupts binary data)
        InputStream requestBody = exchange.getRequestBody();
        byte[] bodyBytes = requestBody.readAllBytes();

        // 3. Find parts by searching for boundary in the byte array
        int pos = 0;
        while (pos < bodyBytes.length) {
            // Find next boundary
            int boundaryStart = indexOf(bodyBytes, boundaryBytes, pos);
            if (boundaryStart == -1)
                break;

            // Move past the boundary and CRLF
            int partStart = boundaryStart + boundaryBytes.length;
            if (partStart + 2 <= bodyBytes.length && bodyBytes[partStart] == '\r' && bodyBytes[partStart + 1] == '\n') {
                partStart += 2;
            }

            // Find next boundary (end of this part)
            int nextBoundaryStart = indexOf(bodyBytes, boundaryBytes, partStart);
            if (nextBoundaryStart == -1)
                nextBoundaryStart = bodyBytes.length;

            // Find the empty line separating headers from content (CRLFCRLF)
            int headerEnd = indexOf(bodyBytes, "\r\n\r\n".getBytes(StandardCharsets.UTF_8), partStart);
            if (headerEnd == -1 || headerEnd >= nextBoundaryStart) {
                pos = nextBoundaryStart;
                continue;
            }

            // Extract headers as string (safe - headers are always text)
            String headers = new String(bodyBytes, partStart, headerEnd - partStart, StandardCharsets.UTF_8);

            // Content starts after CRLFCRLF
            int contentStart = headerEnd + 4;
            // Content ends before CRLF + boundary (subtract 2 for the CRLF before boundary)
            int contentEnd = nextBoundaryStart - 2;
            if (contentEnd < contentStart)
                contentEnd = contentStart;

            // Parse Content-Disposition header
            String name = null;
            String filename = null;

            String[] headerLines = headers.split("\r\n");
            for (String line : headerLines) {
                if (line.trim().startsWith("Content-Disposition")) {
                    // Extract name attribute
                    if (line.matches(".*[\\s;]name=\"[^\"]+\".*")) {
                        name = line.replaceAll(".*[\\s;]name=\"([^\"]+)\".*", "$1");
                    }
                    // Extract filename attribute if present
                    if (line.contains("filename=\"")) {
                        filename = line.replaceAll(".*filename=\"([^\"]+)\".*", "$1");
                    }
                }
            }

            if (name != null) {
                if (filename != null && !filename.isEmpty()) {
                    // It's a file - extract raw bytes (DO NOT convert to String)
                    byte[] fileBytes = Arrays.copyOfRange(bodyBytes, contentStart, contentEnd);
                    InputStream fileInputStream = new ByteArrayInputStream(fileBytes);
                    parameters.put(name, new FileData(filename, fileInputStream));
                } else {
                    // It's a text field - safe to convert to String
                    String textValue = new String(bodyBytes, contentStart, contentEnd - contentStart,
                            StandardCharsets.UTF_8).trim();
                    parameters.put(name, textValue);
                }
            }

            pos = nextBoundaryStart;
        }

        return parameters;
    }

    // Helper method to find byte sequence in byte array
    private int indexOf(byte[] array, byte[] target, int start) {
        if (target.length == 0)
            return start;

        outer: for (int i = start; i <= array.length - target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            switch (method) {
                case "GET" -> {
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
                }
                case "DELETE" -> handleDeleteRequest(exchange);
                case "POST" -> handlePostRequest(exchange);
                case "PUT" -> handlePutRequest(exchange);
                default -> sendResponse(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
            }
        } catch (Exception e) {
            log("✗ CRITICAL ERROR in handle(): " + e.getMessage());
            e.printStackTrace();
            try {
                sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
            } catch (Exception ex) {
                System.err.println("Failed to send error response: " + ex.getMessage());
            }
        }
    }

    private void handleGetSingleBookRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Get single book");
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        Integer bookId = Integer.valueOf(parts[parts.length - 1]);
        log("  Book ID requested: " + bookId);

        Book searchForBook = bookDAO.findBookById(bookId);
        if (searchForBook != null) {
            log("  ✓ Book found: " + searchForBook.getBookTitle() + " by " + searchForBook.getAuthorName());
        } else {
            log("  ✗ Book not found in database");
        }

        String jsonResponse = gson.toJson(searchForBook);
        log("  Sending response with book data");
        sendResponse(exchange, 200, jsonResponse);
    }

    private void handleReadBook(HttpExchange exchange) throws IOException {
        log("→ Processing: Read/Download book");
        

        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length - 1]);
            log("  Book ID requested: " + bookId);
        } catch (NumberFormatException e) {
            log("  ✗ Invalid book ID format");
            sendResponse(exchange, 400, "\"{\"error\":\"Invalid book ID\"}\"");
            return;
        }
        Book bookToRead = bookDAO.findBookById(bookId);
        if (bookToRead == null) {
            log("  ✗ Book not found in database");
            sendResponse(exchange, 404, "\"{\"Message\":\"Book Not Found\"}\"");
            return;
        }

        log("  ✓ Book found: " + bookToRead.getBookTitle());
        String filePathStr = bookToRead.getFilePath();
        Path filePath = Paths.get(filePathStr);
        log("  File path: " + filePathStr);

        if (!Files.exists(filePath)) {
            log("  ✗ File does not exist on disk");
            System.err.println("ERROR: missing file at " + filePathStr);
            sendResponse(exchange, 500, "{\"message\":\"Book not available on server\"}");
            return;
        }

        // try to get file's size/type for the ResponseHeader
        try {
            String filename = filePath.getFileName().toString();
            long fileSize = Files.size(filePath);
            log("  File size: " + fileSize + " bytes");
            log("  MIME type: " + getMimeType(filename));

            exchange.getResponseHeaders().set("Content-Type", getMimeType(filename));

            // Add custom header to inform client about highlights endpoint
            // Client can fetch highlights from: GET
            // http://localhost:8082/user/books/{bookId}/highlights
            exchange.getResponseHeaders().set("X-Highlights-URL",
                    "http://localhost:8082/user/books/" + bookId + "/highlights");
            
            exchange.sendResponseHeaders(200, fileSize);

            // 4. Get the response body stream and copy the file directly to it
            log("  Streaming file to client...");
            try (OutputStream os = exchange.getResponseBody()) {
                Files.copy(filePath, os);
            }
            log("  ✓ File sent successfully");
        } catch (IOException e) {
            log("  ✗ Error streaming file: " + e.getMessage());
            System.err.println("Error streaming file: " + e.getMessage());
        }

    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            log("→ Processing: Get all books");
            List<Book> books = bookDAO.getAllBooks();
            log("  Found " + books.size() + " books in database");
            String jsonResponse = gson.toJson(books);
            log("  JSON response length: " + jsonResponse.length() + " characters");
            log("  Sending book list");
            sendResponse(exchange, 200, jsonResponse);
            log("  ✓ Response sent successfully");
        } catch (Exception e) {
            log("  ✗ ERROR in handleGetRequest: " + e.getMessage());
            e.printStackTrace();
            try {
                sendResponse(exchange, 500, "{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
            } catch (IOException ioEx) {
                log("  ✗ Failed to send error response: " + ioEx.getMessage());
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Upload new book");

        // check if user is logged in/authorized
        Integer uploaderId = getUserIdFromRequest(exchange);
        if (uploaderId == null) {
            log("  ✗ Unauthorized - no valid user");
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        log("  User ID: " + uploaderId);

        // get Parsed data from post request
        log("  Parsing multipart form data...");
        Map<String, Object> dataParsed = parseMultipartFormData(exchange);

        // validation all the feild
        Object titleObj = dataParsed.get("title");
        if (titleObj == null || titleObj.toString().trim().isEmpty()) {
            log("  ✗ Missing book title");
            sendResponse(exchange, 400, "{\"error\":\"Book title is missing or invalid\"}");
            return;
        }
        String bookTitle = titleObj.toString();
        log("  Book title: " + bookTitle);

        // these can be null
        Object dateObj = dataParsed.get("published_date");
        String publishedDate = (dateObj != null) ? dateObj.toString().trim() : null;
        log("  Published date: " + (publishedDate != null ? publishedDate : "not provided"));

        Object authorObj = dataParsed.get("author");
        String bookAuthor = (authorObj != null) ? authorObj.toString().trim() : null;
        log("  Author: " + (bookAuthor != null ? bookAuthor : "not provided"));

        Object fileObject = dataParsed.get("ebookContent");
        FileData parsedFile;
        if (fileObject instanceof FileData) {
            parsedFile = (FileData) fileObject;
        } else {
            log("  ✗ Missing ebook file");
            sendResponse(exchange, 400, "{\"error\":\"Ebook file is missing or invalid\"}");
            return;
        }

        // check the extension
        String originalFileName = parsedFile.filename;
        log("  Original filename: " + originalFileName);

        int dotIndex = originalFileName.lastIndexOf('.');
        String extension = (dotIndex > 0) ? originalFileName.substring(dotIndex).toLowerCase() : "";
        if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
            log("  ✗ Invalid file type: " + extension);
            sendResponse(exchange, 400,
                    "{\"error\":\"Invalid file type. Allowed types are: " + ALLOWED_EXTENSIONS + "\"}");
            return;
        }
        log("  ✓ File type valid: " + extension);
        
        // Determine format from extension
        String format = extension.equalsIgnoreCase(".pdf") ? "PDF" : "EPUB";
        log("  Book format: " + format);

        // make a unique_name ?
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        log("  Unique filename: " + uniqueFileName);

        Path filePath = Paths.get(UPLOAD_DIR, uniqueFileName);

        // save the file in the server
        try {
            log("  Saving file to disk...");
            Files.copy(parsedFile.content, filePath);
            log("  ✓ File saved: " + filePath.toString());
        } catch (IOException e) {
            log("  ✗ Failed to save file: " + e.getMessage());
            System.out.println("Failed to save uploaded file: " + e.getMessage());
            sendResponse(exchange, 500, "{\"error\":\"Could not save file on the server.\"}");
            return;
        }

        // create new book to store in database (now includes format)
        Book newBook = new Book(bookTitle, bookAuthor, format, filePath.toString(), publishedDate);
        try {
            log("  Adding book to database...");
            bookDAO.addBook(newBook, uploaderId);
            log("  ✓ Book added successfully with ID: " + newBook.getBookId());
            String jsonResponse = gson.toJson(newBook);
            sendResponse(exchange, 201, jsonResponse);
        } catch (IOException e) {
            System.err.println("Database error when adding book: " + e.getMessage());
            Files.deleteIfExists(filePath);
            sendResponse(exchange, 500, "{\"error\":\"Could not save book record to database.\"}");
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        log("→ Processing: Delete book");
        String path = exchange.getRequestURI().getPath();

        int bookId = -1;
        try {
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length - 1]);
            log("  Book ID to delete: " + bookId);
        } catch (NumberFormatException e) {
            log("  ✗ Invalid book ID format");
            sendResponse(exchange, 400, "\"{\"error\":\"Invalid book ID in URL.\"}\"");
            return;
        }

        Book bookToDelete = bookDAO.findBookById(bookId);
        if (bookToDelete == null) {
            log("  ✗ Book not found");
            sendResponse(exchange, 404, "{\"error\":\"Book not found\"}");
            return;
        }
        log("  Book found: " + bookToDelete.getBookTitle());

        // Is the user logged in?
        Integer userId = getUserIdFromRequest(exchange);
        if (userId == null) {
            log("  ✗ Unauthorized user");
            sendResponse(exchange, 401, "{\"error\":\"Unauthorized user\"}");
            return;
        }
        log("  User ID: " + userId);

        // Does the logged-in user own this book?
        if (bookToDelete.getUploaderId() != userId) {
            log("  ✗ User doesn't own this book (owner: " + bookToDelete.getUploaderId() + ")");
            sendResponse(exchange, 403, "{\"error\":\"Forbidden: You do not have permission to delete this book.\"}");
            return;
        }
        log("  ✓ User owns this book");

        try {
            // get the file path
            String filePathStr = bookToDelete.getFilePath();
            Path filePath = Paths.get(filePathStr);
            log("  File path: " + filePathStr);

            log("  Deleting from database...");
            boolean success = bookDAO.deleteBook(bookId, userId);
            if (success) {
                log("  ✓ Deleted from database");
                log("  Deleting file from disk...");
                Files.deleteIfExists(filePath);
                log("  ✓ Book deleted successfully");
                sendResponse(exchange, 200, "{\"message\":\"Book deleted successfully.\"}");
            } else {
                log("  ✗ Database deletion failed");
                sendResponse(exchange, 404, "{\"message\":\"Failed to delete(Book may no longer exist)\"}");
            }
        } catch (IOException e) {
            log("  ✗ Error during deletion: " + e.getMessage());
            System.err.println("Error during book deletion: " + e.getMessage());
            sendResponse(exchange, 500, "\"{\"error\":\"A server error occurred during deletion.\"}\"");
        }
    }

    private void handlePutRequest(HttpExchange exchange) throws IOException {

        int bookId = -1;
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            bookId = Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "\"{\"error\":\"Invalid book ID in URL.\"}\"");
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

        // Does the logged-in user own this book?
        if (bookToUpdate.getUploaderId() != userId) {
            sendResponse(exchange, 403, "{\"error\":\"Forbidden: You do not have permission to delete this book.\"}");
            return;
        }
        try {
            // READ THE REQUEST BODY to get updated fields
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Book updatedBook = gson.fromJson(requestBody, Book.class);

            // reserve bookid and userId since they got overwrite
            updatedBook.setBookId(bookId);
            updatedBook.setUploaderId(userId);

            boolean success = bookDAO.updateBook(updatedBook, userId);

            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Book updated successfully.\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to update book.\"}");
            }
        } catch (IOException e) {
            System.err.println("Error updating book: " + e.getMessage());
            sendResponse(exchange, 400, "{\"error\":\"Invalid request data.\"}");
        }

    }

}