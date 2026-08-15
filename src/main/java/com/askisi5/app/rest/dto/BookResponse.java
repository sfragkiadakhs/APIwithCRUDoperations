package com.askisi5.app.rest.dto;

/**
 * Response payload for a Book. Field names intentionally mirror the Book
 * entity so the JSON contract seen by API clients is unchanged.
 */
public class BookResponse {

    private String isbn;
    private String title;
    private String shortSummary;
    private int publishYear;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortSummary() {
        return shortSummary;
    }

    public void setShortSummary(String shortSummary) {
        this.shortSummary = shortSummary;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }
}
