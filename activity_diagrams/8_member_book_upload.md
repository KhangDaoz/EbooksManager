# Member Book Upload Activity Diagram

```mermaid
flowchart TD
    Start([Start]) --> MemberLoggedIn[Member logged in]
    MemberLoggedIn --> NavToUpload[Navigate to 'Upload Book' section]
    NavToUpload --> ChooseAction{Choose action}
    
    %% UPLOAD NEW BOOK
    ChooseAction -->|Upload New Book| ClickUpload[Click 'Upload Book' button]
    ClickUpload --> ShowUploadForm[Display book upload form]
    ShowUploadForm --> SelectFile[Select book file from computer]
    SelectFile --> ValidateFile{File valid?}
    
    ValidateFile -->|No| ErrorFile[Error: Invalid file type or size]
    ErrorFile --> EndError1([End])
    
    ValidateFile -->|Yes| DisplayFileName[Display selected filename]
    DisplayFileName --> EnterMetadata[Enter book metadata: title, author, publisher, category]
    EnterMetadata --> ValidateMetadata{Metadata valid?}
    
    ValidateMetadata -->|No| ErrorMetadata[Error: Fill required fields]
    ErrorMetadata --> EndError2([End])
    
    ValidateMetadata -->|Yes| CallUpload[User: uploadBook filePath, title, author]
    CallUpload --> CreateBook[Book: Create new object with metadata]
    CreateBook --> SetUploader[Book: Set uploader = current Member]
    SetUploader --> SaveFile[FileStorageService: Save file to storage]
    SaveFile --> FileSuccess{File saved?}
    
    FileSuccess -->|No| ErrorSave[Error: Failed to save file]
    ErrorSave --> EndError3([End])
    
    FileSuccess -->|Yes| SaveBookDB[BookDAO: Save Book to database]
    SaveBookDB --> DBSuccess{Database success?}
    
    DBSuccess -->|No| RollbackFile[FileStorageService: Delete file]
    RollbackFile --> ErrorDB[Error: Database error]
    ErrorDB --> EndError4([End])
    
    DBSuccess -->|Yes| ShowUploadSuccess[Display: Book uploaded successfully]
    ShowUploadSuccess --> AskAddToLib{Add to your library?}
    
    AskAddToLib -->|Yes| AddToLibrary[Member: addToLibrary book]
    AddToLibrary --> CreateProgress[BookProgress: Create with initial values]
    CreateProgress --> SaveProgress[BookProgressDAO: Save to database]
    SaveProgress --> ShowAdded[Display: Book added to your library]
    ShowAdded --> Continue1
    
    AskAddToLib -->|No| Continue1{Continue managing?}
    
    %% VIEW MY UPLOADED BOOKS
    ChooseAction -->|View My Uploads| GetMyUploads[BookDAO: Get books by uploader userId]
    GetMyUploads --> DisplayMyBooks[Display list of uploaded books]
    DisplayMyBooks --> UploadAction{Choose action}
    
    UploadAction -->|Edit Book| SelectToEdit[Select a book to edit]
    SelectToEdit --> LoadBookData[BookDAO: Get book by ID]
    LoadBookData --> ShowEditForm[Display edit form with current data]
    ShowEditForm --> EditMetadata[Edit title, author, publisher, category]
    EditMetadata --> ValidateEdit{Valid?}
    
    ValidateEdit -->|No| ErrorEdit[Error: Invalid data]
    ErrorEdit --> EndError5([End])
    
    ValidateEdit -->|Yes| UpdateBook[Book: updateMetadata newTitle, newAuthor, ...]
    UpdateBook --> SaveUpdate[BookDAO: Update book in database]
    SaveUpdate --> ShowUpdateSuccess[Display: Book updated successfully]
    ShowUpdateSuccess --> Continue1
    
    UploadAction -->|Delete Uploaded Book| SelectToDelete[Select a book to delete]
    SelectToDelete --> ShowDeleteWarning[Show warning: Book will be removed from all users]
    ShowDeleteWarning --> ConfirmDelete[Confirm deletion]
    ConfirmDelete --> CallDelete[User: deleteUploadedBook book]
    CallDelete --> CheckUsage[Check if book is used by other users]
    CheckUsage --> ShowAffected[Show: X users have this book in their library]
    ShowAffected --> FinalConfirm{Still confirm?}
    
    FinalConfirm -->|No| Continue1
    
    FinalConfirm -->|Yes| CascadeDelete[Start cascade deletion]
    CascadeDelete --> DeleteBookmarks[BookmarkDAO: Delete all bookmarks for book]
    DeleteBookmarks --> DeleteProgress[BookProgressDAO: Delete all progress for book]
    DeleteProgress --> RemoveFromColls[CollectionDAO: Remove from all collections]
    RemoveFromColls --> DeleteFile[FileStorageService: Delete file from storage]
    DeleteFile --> DeleteBookRecord[BookDAO: Delete book from database]
    DeleteBookRecord --> ShowDeleteSuccess[Display: Book deleted successfully]
    ShowDeleteSuccess --> Continue1
    
    UploadAction -->|Back| Continue1
    
    Continue1 -->|Yes| NavToUpload
    Continue1 -->|No| End([End])
```

## Required Methods

### User (inherited by Member)
- `uploadBook(String filePath, String title, String author): void` - Upload new book to system
- `deleteUploadedBook(Book book): void` - Delete a book uploaded by this user

### Member
- `addToLibrary(Book book): void` - Add uploaded book to personal library

### Book
- `Book(String title, String author, String publisher, String category)` - Constructor
- `setUploader(User uploader): void` - Set who uploaded the book
- `updateMetadata(String newTitle, String newAuthor, ...): void` - Update book info
- `getUploader(): User` - Get who uploaded the book

### BookDAO
- `addBook(Book book): void` - Add new book to database
- `updateBook(Book book): void` - Update book metadata
- `deleteBook(int bookId): void` - Delete book from database
- `getBooksByUploader(int userId): List<Book>` - Get all books uploaded by a user
- `getBookById(int bookId): Book` - Get book by ID

### BookProgress
- `BookProgress(Book book, Member member)` - Constructor

### BookProgressDAO
- `addBookProgress(BookProgress progress): void` - Add new progress record
- `deleteAllProgressForBook(int bookId): void` - Delete all progress for a book

### BookmarkDAO
- `deleteAllBookmarksForBook(int bookId): void` - Delete all bookmarks for a book

### CollectionDAO
- `removeBookFromAllCollections(int bookId): void` - Remove book from all collections

### FileStorageService
- `saveFile(File file, String filename): String` - Save file to storage, return path
- `deleteFile(String filePath): void` - Delete file from storage

