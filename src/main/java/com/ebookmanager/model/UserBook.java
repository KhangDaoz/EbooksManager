package com.ebookmanager.model;

public class UserBook {
    private int user_id;
    private int book_id;
    private String date_added;
    // Reading progress of each book in user list;
    private float reading_progress;

    // Constructors
    public UserBook(Integer user_id, int book_id, String date_added, float reading_progress) {
        if(user_id < 0 || book_id < 0 || date_added.isEmpty() || reading_progress < 0) {
            throw new IllegalArgumentException("Invalid argument(s) for UserBook constructor");
        }
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
}
