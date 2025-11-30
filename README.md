# EbooksManager
Link file docs workflow: https://docs.google.com/document/d/1H3rGL4Q0Fe9xqaYvJgN97AYgULdsxxGJMtkHL8mLt9Q/edit?usp=sharing

A Java-based desktop application for managing and reading ebooks with user authentication, library management, and administrative features.

## Features

- **User Authentication**: Login and registration system with separate admin and member roles
- **Library Management**: Browse, search, and organize your ebook collection
- **Reading Interface**: Built-in PDF reader with bookmark support
- **Progress Tracking**: Track reading progress for each book
- **Collections**: Create custom collections to organize your books
- **Community Features**: Share and discover books with other users
- **Admin Panel**: Manage users and books (admin only)

## Technologies

- **Java 21**
- **MySQL Database**
- **Maven** (Build tool)
- **Swing** (GUI framework)
- **Apache PDFBox** (PDF rendering)
- **Gson** (JSON processing)

## Prerequisites

- Java JDK 21 or higher
- MySQL Server
- Maven 3.x

## Database Setup

1. Create a MySQL database named `ebookmanager` with run file CreateDB.sql
2. Configure database connection in `src/main/java/com/ebookmanager/db/DatabaseConnector.java`:
   - Default URL: `jdbc:mysql://localhost:3306/ebookmanager`
   - Default User: `root`
   - Default Password: `123456`

## Installation

1. Clone the repository:
```bash
git clone https://github.com/KhangDaoz/EbooksManager.git
cd EbooksManager
```

2. Build the project:
```bash
mvn clean compile
```

3. Run the application:
```bash
mvn exec:java
```

## Project Structure

```
src/main/java/com/ebookmanager/
├── dao/           # Data Access Objects
├── db/            # Database connection
├── main/          # Application entry point
├── model/         # Data models (User, Book, etc.)
├── service/       # Business logic services
├── ui/            # User interface components
└── util/          # Utility classes
```

## Usage

1. Launch the application
2. Register a new account or login with existing credentials
3. Browse your library and add books
4. Click on a book to start reading
5. Use bookmarks and track your reading progress

## License

This project is licensed under the MIT License - see the LICENSE file for details.