package com.ebookmanager.model;
import java.util.*;
import com.ebookmanager.dao.*;

public class UserBook {
    private ArrayList<Book> books;
    private ArrayList<Highlight> highlights;
    private String user_id;
    private String book_id;
    private String date_added;
    private float reading_progress;
    

    public UserBook(String user_id, String date_added)
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
    public String getUser_id() {
        return user_id;
    }
    public String getBook_id() {
        return book_id;
    }
    public String getDate_added() {
        return date_added;
    }
    public float getReading_progress(String user_id) {
        return reading_progress;
    }

    // Setters

    public void saveProgress(User user, Book book, float progress)
    {
        /*
         * This is place for a function which take progress from book file
         */
        takeProgress tp = new takeProgress();
        progress = tp.collectProgress(book);
        saveProgressDAO spD = new saveProgressDAO();
        spD.saveProgress(user, book, progress);
    }
    public void addHighlight(User user, Book book)
    {
        // Take index from book file
        // when user make highlight on screen
        takeProgress tp = new takeProgress();
        int start = tp.takeFirstPos(book);
        int end = tp.takeLastPos(book);

        // save index into new highlight object
        Highlight highlight = new Highlight();
        highlight.setStartPos(start);
        highlight.setEndPos(end);

        // Save to database
        HightlightDAO hlD = new HightlightDAO();
        hlD.createHighlight(highlight);
    }
}
