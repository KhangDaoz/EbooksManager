package com.ebookmanager.model;
import java.util.*;
// ReadingProgress uses to connect books and UserBook
class ReadingProgress{
    private float progress;
    

    // Getters
    public float getProgress()
    {
        return progress;
    }
    // Setter
    public void saveProgress(float p)
    {
        this.progress = p;
    }
}
public class UserBook {
    private ArrayList<Book> books;
    private ArrayList<Highlight> highlights;
    
    // Getters
    public ArrayList<Book> getBooks() {
        return books;
    }
    public ArrayList<Highlight> getHighlights() {
        return highlights;
    }
    
    
}
