package com.askisi5.app.rest.service;

import com.askisi5.app.rest.dto.BookRequest;
import com.askisi5.app.rest.dto.BookResponse;

import java.util.List;

public interface BookService {

    List<BookResponse> getAllBooks();

    BookResponse getBookByIsbn(String isbn);

    BookResponse createBook(BookRequest request);

    BookResponse updateBook(String isbn, BookRequest request);

    void deleteBook(String isbn);
}
