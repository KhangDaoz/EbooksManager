package com.ebookmanager.service;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class FileStorageService {
    private static final String STORAGE_FOLDER_NAME = "book_storage";
    private final Path storageLocation;
    

    public FileStorageService() {
        this.storageLocation = Paths.get("src/main/resources/books", STORAGE_FOLDER_NAME);
        
        try {
            System.out.println("Creating storage directory: " + this.storageLocation);
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            System.err.println("Could not create storage directory: " + storageLocation);
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Could not create storage directory: " + storageLocation, e);
        }
    }

    public String saveFile(File file, String originalFilename) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        long maxSize = 100 * 1024 * 1024; // 100MB
        if (file.length() > maxSize) {
            throw new IOException("File size exceeds maximum allowed size (100MB)");
        }
        
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        Path targetLocation = this.storageLocation.resolve(uniqueFilename);
        
        Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return targetLocation.toString();
    }
    
    public InputStream readFileAsResource(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        
        if (!Files.isReadable(path)) {
            throw new IOException("File is not readable: " + filePath);
        }
        
        return new FileInputStream(path.toFile());
    }
    
    public void deleteFile(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            return;
        }
        
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IOException("Failed to delete file: " + filePath, e);
        }
    }
    
    public String getFilePath(String filename) {
        return this.storageLocation.resolve(filename).toString();
    }
    
    public long calculateTotalStorage() throws IOException {
        long totalSize = 0;
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.storageLocation)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    totalSize += Files.size(path);
                }
            }
        }
        
        return totalSize;
    }
    
    public String calculateTotalStorageFormatted() throws IOException {
        long bytes = calculateTotalStorage();
        return formatBytes(bytes);
    }
    
    public Path getStorageDirectory() {
        return this.storageLocation;
    }
    
    public boolean fileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        return Files.exists(Paths.get(filePath));
    }
    
    public long getFileSize(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }
        return Files.size(path);
    }
    
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    public boolean isValidBookFormat(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return extension.equals(".pdf");
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
    
}
