# Activity Diagrams - Ebooks Manager System (Mermaid Format)

This folder contains activity diagrams for the Ebooks Manager system, focusing on the **model classes** only (excluding DAO layers). All diagrams are in **Mermaid** format.

## Diagrams Overview

### 1. User Registration and Login (`1_user_registration_login.md`)
**Model Classes Involved:** User, Member, Admin
- User registration process with validation
- User login with authentication
- Role-based navigation (Admin vs Member)

### 2. Member Reading Workflow (`2_member_reading_workflow.md`)
**Model Classes Involved:** Member, Book, BookProgress, Bookmark
- Adding books to personal library
- Starting and managing reading sessions
- Updating reading progress
- Creating bookmarks during reading
- Rating books
- Removing books from library

### 3. Member Collection Management (`3_member_collection_management.md`)
**Model Classes Involved:** Member, Collection, Book
- Creating new collections
- Adding books to collections
- Removing books from collections
- Renaming collections
- Deleting collections
- Viewing collection contents

### 4. Bookmark Management (`4_bookmark_management.md`)
**Model Classes Involved:** Member, BookProgress, Bookmark
- Viewing all bookmarks for a book
- Creating new bookmarks with notes
- Navigating to bookmarked pages
- Deleting bookmarks

### 5. Admin Book Management (`5_admin_book_management.md`)
**Model Classes Involved:** Admin, Book, User
- Uploading new books to the system
- Updating book metadata
- Deleting books system-wide (with cascade effects)
- Viewing system statistics
- Deleting user accounts

### 6. User Account Management (`6_user_account_management.md`)
**Model Classes Involved:** User, Member, Admin
- Changing password
- Logging out
- Deleting own account (with cascade deletion of related data)

### 7. Book Discovery and Preview (`7_book_discovery_and_preview.md`)
**Model Classes Involved:** Member, Book, BookProgress, Collection
- Browsing all books in the system
- Searching books by keywords
- Filtering by category
- Previewing/reading sample content before adding to library
- Adding discovered books to library
- Adding books to collections
- Starting to read immediately after discovery

## Model Classes Architecture

### Core Classes:
1. **User (Abstract)** - Base class for authentication and account management
2. **Member** - Extends User, manages personal library and collections
3. **Admin** - Extends User, manages system-wide operations
4. **Book** - Represents ebook metadata and file information
5. **BookProgress** - Tracks reading progress, ratings, and bookmarks for a specific user-book pair
6. **Collection** - User-created groups of books
7. **Bookmark** - Page markers with notes within books

## How to View the Diagrams

These diagrams are in **Mermaid** format (`.md` files with Mermaid code blocks). You can view them using:

1. **GitHub/GitLab:** Renders automatically in markdown preview
2. **VS Code:** Install the "Markdown Preview Mermaid Support" extension
3. **IntelliJ IDEA:** Built-in Mermaid support in markdown preview
4. **Online:** [Mermaid Live Editor](https://mermaid.live/)
5. **Obsidian/Notion:** Native Mermaid support

### Quick View Instructions:
1. Open any `.md` file in this folder
2. Use your IDE's markdown preview feature
3. Or copy the mermaid code block to [mermaid.live](https://mermaid.live/)

## Key Relationships

- **Member has many BookProgress** (one per book in their library)
- **Member has many Collections**
- **Collection contains many Books**
- **BookProgress has many Bookmarks**
- **Book has one Uploader (User)**
- **User can be either Member or Admin**

## Design Decisions

1. All diagrams focus on **model-level operations** only
2. Database operations (DAO) are abstracted away
3. Cascade deletion effects are shown for data integrity
4. Role-based access control is implemented through User.getRole()
5. Password security uses hashing (User.hash())
6. All diagrams use **flowchart TD** (top-down) layout for clarity

## Mermaid Syntax Used

- **Rounded rectangles `([text])`**: Start/End nodes
- **Rectangles `[text]`**: Process/Action nodes
- **Diamonds `{text}`**: Decision nodes
- **Arrows `-->`**: Flow direction
- **Labels `|text|`**: Condition labels on arrows

