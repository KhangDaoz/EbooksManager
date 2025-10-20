package com.ebookmanager.model;
import java.util.*;
import com.ebookmanager.dao.*;
import com.ebookmanager.dao.UserBookDAO;
public class UserBook {
    private ArrayList<Book> books;
    private ArrayList<Highlight> highlights;
    private int user_id;
    private int book_id;
    private String date_added;
    // Reading progress of each book in user list;
    private float reading_progress;
    
    // Declare a userbookdao object for userbook of user.
    UserBookDAO userbookdao = new UserBookDAO();

    // Getters
    public UserBook(Integer user_id, String date_added)
    {
        this.user_id = user_id;
        this.date_added = date_added;
    }
    public ArrayList<Book> getBooks() {
        return books;
    }
    public ArrayList<Highlight> getHighlights() {
        return highlights;
    }
    public int getUser_id() {
        return user_id;
    }
    public int getBook_id() {
        return book_id;
    }
    public String getDate_added() {
        return date_added;
    }
//--------------------------------------------------------------------//
    // Setters

    // Collect lasted reading progress of book in user list
    public float getReading_progress(Book book) {
        return userbookdao.getProgress(user_id, book);
    }


    // ADD BOOK INTO USERBOOK
    public void addBook(Book book)
    {
        books.add(book);
        userbookdao.addBook(book, user_id);
    }

    // SAVE BOOK'S PROGRESS IN USER LIST
    public void saveProgress(User user, Book book, float progress)
    {
        /*
         * This is place for a function which take indexes
         * in reading screen
         */
        userbookdao.saveProgress(user, book, progress);
    }

    // ADD HIGHLIGHT INTO BOOK'S PAGE AND 
    // ADD HIGHLIGHT'S INFORMATION INTO DATABASE
    public void addHighlight(User user, Book book)
    {
        // Take index from book file
        // when user make highlight on screen

        Scanner sc = new Scanner(System.in);
        int start = sc.nextInt();
        int end = sc.nextInt();
        sc.close();
        // save index into new highlight object
        Highlight highlight = new Highlight();
        highlight.setStartPos(start);
        highlight.setEndPos(end);

        // Save to database
        HightlightDAO hlD = new HightlightDAO();
        hlD.createHighlight(highlight);
    }
}//
