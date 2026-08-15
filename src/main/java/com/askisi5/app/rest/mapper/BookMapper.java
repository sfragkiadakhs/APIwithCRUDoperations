package com.askisi5.app.rest.mapper;

import com.askisi5.app.rest.dto.BookRequest;
import com.askisi5.app.rest.dto.BookResponse;
import com.askisi5.app.rest.model.Book;

/**
 * Manual mapping between the Book entity and its request/response DTOs.
 * Kept as simple static methods rather than pulling in a mapping framework,
 * since the field set is small and unlikely to grow quickly.
 */
public final class BookMapper {

    private BookMapper() {
    }

    public static Book toEntity(BookRequest request) {
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setTitle(request.getTitle());
        book.setShortSummary(request.getShortSummary());
        book.setPublishYear(request.getPublishYear());
        return book;
    }

    public static void copyToEntity(BookRequest request, Book book) {
        book.setTitle(request.getTitle());
        book.setShortSummary(request.getShortSummary());
        book.setPublishYear(request.getPublishYear());
    }

    public static BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setShortSummary(book.getShortSummary());
        response.setPublishYear(book.getPublishYear());
        return response;
    }
}
