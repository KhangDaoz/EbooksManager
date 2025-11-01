# EbooksManager API Documentation

Base URL: `http://localhost`

## Authentication

All authenticated endpoints require a Bearer token in the Authorization header:

```
Authorization: Bearer <token>
```

---

## User API (Port 8080)

### 1. Register User

**POST** `/api/users`

Create a new user account.

**Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Validation:**

- `username`: Required, 3-50 characters
- `password`: Required, minimum 8 characters

**Response:**

- **201 Created**

```json
{
  "message": "Registration successful"
}
```

- **400 Bad Request** - Username exists or validation error

---

### 2. Login

**POST** `/api/sessions`

Login and receive authentication token.

**Request Body:**

```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**

- **200 OK**

```json
{
  "token": "226e0e2d-289f-4215-90a2-16330a42db11",
  "message": "Login successful"
}
```

- **401 Unauthorized** - Invalid credentials

---

### 3. Get User Info

**GET** `/api/users`

Get current user information.

**Headers:**

```
Authorization: Bearer <token>
```

**Response:**

- **200 OK**

```json
{
  "userId": 1,
  "username": "john_doe"
}
```

- **401 Unauthorized** - Invalid or missing token

---

### 4. Change Password

**PUT** `/api/sessions`

Change user password.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "oldPassword": "string",
  "newPassword": "string"
}
```

**Validation:**

- `oldPassword`: Required
- `newPassword`: Required, minimum 8 characters, must differ from old password

**Response:**

- **200 OK**

```json
{
  "message": "Password changed successfully"
}
```

- **400 Bad Request** - Validation error or incorrect old password

---

### 5. Logout

**DELETE** `/api/sessions`

Logout and invalidate token.

**Headers:**

```
Authorization: Bearer <token>
```

**Response:**

- **200 OK**

```json
{
  "message": "Logged out successfully"
}
```

---

## Book API (Port 8081)

### 1. Get All Books

**GET** `/api/books`

Retrieve all books in the system.

**Response:**

- **200 OK**

```json
[
  {
    "bookId": 1,
    "bookTitle": "Sample Book",
    "authorName": "John Doe",
    "format": "PDF",
    "filePath": "uploads/sample.pdf",
    "publishDate": "2024-01-15",
    "uploaderId": 1
  }
]
```

---

### 2. Get Single Book

**GET** `/api/books/{bookId}`

Retrieve a specific book by ID.

**Response:**

- **200 OK**

```json
{
  "bookId": 1,
  "bookTitle": "Sample Book",
  "authorName": "John Doe",
  "format": "PDF",
  "filePath": "uploads/sample.pdf",
  "publishDate": "2024-01-15",
  "uploaderId": 1
}
```

- **404 Not Found** - Book doesn't exist

---

### 3. Upload Book

**POST** `/api/books`

Upload a new book (multipart/form-data).

**Headers:**

```
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Form Data:**

- `title` (required): Book title
- `author` (optional): Author name
- `published_date` (optional): Publication date
- `ebookContent` (required): PDF or EPUB file

**Validation:**

- File must be `.pdf` or `.epub`
- Title is required

**Response:**

- **201 Created**

```json
{
  "message": "Book uploaded successfully.",
  "bookId": 5
}
```

- **400 Bad Request** - Invalid file type or missing title
- **401 Unauthorized** - Not logged in

---

### 4. Update Book

**PUT** `/api/books/{bookId}`

Update book metadata (title, author, publish date).

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "bookTitle": "Updated Title",
  "authorName": "Updated Author",
  "publishDate": "2024-12-01"
}
```

**Response:**

- **200 OK**

```json
{
  "message": "Book updated successfully."
}
```

- **403 Forbidden** - Not the book owner
- **404 Not Found** - Book doesn't exist

---

### 5. Delete Book

**DELETE** `/api/books/{bookId}`

Delete a book (only owner can delete).

**Headers:**

```
Authorization: Bearer <token>
```

**Response:**

- **200 OK**

```json
{
  "message": "Book deleted successfully."
}
```

- **403 Forbidden** - Not the book owner
- **404 Not Found** - Book doesn't exist

---

## User Library API (Port 8083)

### 1. Get User's Library

**GET** `/api/users/books`

Get all books in the user's personal library.

**Headers:**

```
Authorization: Bearer <token>
```

**Response:**

- **200 OK**

```json
[
  {
    "user_id": 1,
    "book_id": 2,
    "date_added": "2024-11-01",
    "reading_progress": 45.5
  }
]
```

---

### 2. Add Book to Library

**POST** `/api/users/books/{bookId}`

Add a book to user's personal library.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "bookId": "2",
  "dateAdded": "2024-11-02"
}
```

**Validation:**

- `dateAdded`: Required, format YYYY-MM-DD

**Response:**

- **201 Created**

```json
{
  "message": "Book added to user's library"
}
```

- **400 Bad Request** - Book already in library or invalid date format
- **404 Not Found** - Book doesn't exist

---

### 3. Update Reading Progress

**PUT** `/api/users/books/{bookId}/progress`

Update reading progress for a book.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "bookId": "2",
  "readingProgress": "67.5"
}
```

**Validation:**

- `readingProgress`: Required, must be between 0 and 100

**Response:**

- **200 OK**

```json
{
  "message": "Reading progress updated"
}
```

- **400 Bad Request** - Progress out of range (0-100)
- **404 Not Found** - Book not in library

---

### 4. Remove Book from Library

**DELETE** `/api/users/books/{bookId}`

Remove a book from user's library.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "bookId": "2"
}
```

**Response:**

- **200 OK**

```json
{
  "message": "Book removed from user's library"
}
```

- **404 Not Found** - Book not in library

---

## Bookmark API (Port 8082)

### 1. Get Bookmarks

**GET** `/api/user/books/{bookId}/bookmarks`

Get all bookmarks for a book.

**Headers:**

```
Authorization: Bearer <token>
```

**Response:**

- **200 OK**

```json
[
  {
    "bookmarkId": 1,
    "userId": 1,
    "bookId": 2,
    "locationData": "{\"pageIndex\":42,\"scrollY\":150}"
  },
  {
    "bookmarkId": 2,
    "userId": 1,
    "bookId": 2,
    "locationData": "{\"spineIndex\":5,\"cfiRange\":\"epubcfi(/6/4[chap01ref]!/4/2/1:0)\",\"percentage\":25.5}"
  }
]
```

---

### 2. Create Bookmark

**POST** `/api/user/books/{bookId}/bookmarks`

Create a new bookmark.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body (PDF):**

```json
{
  "bookId": "2",
  "location_data": {
    "pageIndex": 42,
    "scrollY": 150
  }
}
```

**Request Body (EPUB):**

```json
{
  "bookId": "3",
  "location_data": {
    "spineIndex": 5,
    "cfiRange": "epubcfi(/6/4[chap01ref]!/4/2/1:0)",
    "percentage": 25.5
  }
}
```

**PDF Validation:**

- `pageIndex`: Required, must be >= 0
- `scrollY`: Optional, must be >= 0 if provided

**EPUB Validation:**

- `spineIndex`: Required, must be >= 0
- `cfiRange`: Required, non-empty string
- `percentage`: Optional, must be 0-100 if provided

**Response:**

- **201 Created**

```json
{
  "message": "Bookmark created successfully",
  "bookmarkId": 5
}
```

- **400 Bad Request** - Validation error
- **403 Forbidden** - Book not in user's library

---

### 3. Update Bookmark

**PUT** `/api/user/books/{bookId}/bookmarks/{bookmarkId}`

Update bookmark location.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "bookmark_id": 5,
  "location_data": {
    "pageIndex": 67,
    "scrollY": 200
  }
}
```

**Response:**

- **200 OK**

```json
{
  "message": "Bookmark updated successfully"
}
```

- **400 Bad Request** - Validation error
- **403 Forbidden** - Not your bookmark
- **404 Not Found** - Bookmark doesn't exist

---

### 4. Delete Bookmark

**DELETE** `/api/user/books/{bookId}/bookmarks/{bookmarkId}`

Delete a bookmark.

**Headers:**

```
Authorization: Bearer <token>
```

**Request Body:**

```json
{
  "bookmark_id": 5
}
```

**Response:**

- **200 OK**

```json
{
  "message": "Bookmark deleted successfully"
}
```

- **403 Forbidden** - Not your bookmark
- **404 Not Found** - Bookmark doesn't exist

---

## Error Responses

All endpoints may return these common errors:

**400 Bad Request**

```json
{
  "error": "Detailed error message"
}
```

**401 Unauthorized**

```json
{
  "error": "Unauthorized user"
}
```

**403 Forbidden**

```json
{
  "error": "Permission denied message"
}
```

**404 Not Found**

```json
{
  "error": "Resource not found message"
}
```

**500 Internal Server Error**

```json
{
  "error": "Internal server error"
}
```

---

## Notes

1. **Bookmark Location Data Format:**

   - PDF books use `pageIndex` (required) and `scrollY` (optional)
   - EPUB books use `spineIndex`, `cfiRange` (required), and `percentage` (optional)

2. **Date Format:**

   - All dates should be in ISO format: `YYYY-MM-DD`

3. **Reading Progress:**

   - Float value between 0.0 and 100.0 representing percentage

4. **File Upload:**

   - Only `.pdf` and `.epub` files are accepted
   - Use `multipart/form-data` content type

5. **Authentication:**
   - Tokens are session-based and invalidated on logout
   - Include token in all authenticated requests
