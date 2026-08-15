package com.askisi5.app.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request payload for creating or updating a Book. Used for both POST and
 * PUT so that the same validation rules apply consistently on both create
 * and update, matching the original entity-bound behavior. Unknown JSON
 * properties are ignored rather than rejected, so forward-compatible
 * clients don't get a 500 for sending an extra field.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookRequest {

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
             message = "Invalid ISBN format")
    private String isbn;

    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
    private String title;

    @Size(max = 1000, message = "Summary must not exceed 1000 characters")
    private String shortSummary;

    @Min(value = 1000, message = "Publish year must be at least 1000")
    @Max(value = 2100, message = "Publish year must not exceed 2100")
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
