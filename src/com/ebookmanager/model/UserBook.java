package com.ebookmanager.model;

public class UserBook {
    private int user_id;
    private int book_id;
    private String date_added;
    // Reading progress of each book in user list;
    private float reading_progress;

    // Constructors
    public UserBook(Integer user_id, int book_id, String date_added, float reading_progress)
    {
        this.user_id = user_id;
        this.book_id = book_id;
        this.date_added = date_added;
        this.reading_progress = reading_progress;
    }

    // Getters
    public int getUser_id() {
        return user_id;
    }
    public int getBook_id() {
        return book_id;
    }
    public String getDate_added() {
        return date_added;
    }
    public float getReading_progress() {
        return reading_progress;
    }

    // Setters

    // // ADD HIGHLIGHT INTO BOOK'S PAGE AND 
    // // ADD HIGHLIGHT'S INFORMATION INTO DATABASE
    // public void addHighlight(User user, Book book)
    // {
    //     // Take index from book file
    //     // when user make highlight on screen

    //     Scanner sc = new Scanner(System.in);
    //     int start = sc.nextInt();
    //     int end = sc.nextInt();
    //     sc.close();
    //     // save index into new highlight object
    //     Highlight highlight = new Highlight();
    //     highlight.setStartPos(start);
    //     highlight.setEndPos(end);

    //     // Save to database
    //     HighlightDAO hlD = new HighlightDAO();
    //     hlD.createHighlight(highlight);
    // }
}
