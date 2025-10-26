package com.ebookmanager.model;

public class Highlight {
    private int highlightId;
    private int userId;
    private int bookId;
    private String locationData;
    private String backgroundColor;
    private String noteContent;

    // Default constructor
    public Highlight() {
    }

    public Highlight(int UserId, int BookId, String LocationData, String BackgroundColor, String NoteContent) {
        this.userId = UserId;
        this.bookId = BookId;
        this.locationData = LocationData;
        this.backgroundColor = BackgroundColor;
        this.noteContent = NoteContent;
    }

    //Getters
    public int getHighlightId() {
        return this.highlightId;
    }

    public int getUserId() {
        return this.userId;
    }

    public int getBookId() {
        return this.bookId;
    }
    


    public String getLocationData() {
        return this.locationData;
    }

    public String getBackgroundColor() {
        return this.backgroundColor;
    }

    public String getNoteContent() {
        return this.noteContent;
    }

    //Setters
    

    public void setHighlightedText(String BackgroundColor) {
        this.backgroundColor = BackgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setNoteContent(String NoteContent) {
        this.noteContent = NoteContent;
    }

}
