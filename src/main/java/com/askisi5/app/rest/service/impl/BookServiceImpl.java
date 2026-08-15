package com.askisi5.app.rest.service.impl;

import com.askisi5.app.rest.dto.BookRequest;
import com.askisi5.app.rest.dto.BookResponse;
import com.askisi5.app.rest.exception.DuplicateResourceException;
import com.askisi5.app.rest.exception.InvalidRequestException;
import com.askisi5.app.rest.exception.ResourceNotFoundException;
import com.askisi5.app.rest.mapper.BookMapper;
import com.askisi5.app.rest.model.Book;
import com.askisi5.app.rest.repository.BookRepository;
import com.askisi5.app.rest.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookResponse> getAllBooks() {
        logger.info("Fetching all books");
        return bookRepository.findAll().stream()
                .map(BookMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        logger.info("Fetching book with ISBN: {}", isbn);
        Book book = findBookOrThrow(isbn);
        return BookMapper.toResponse(book);
    }

    @Override
    public BookResponse createBook(BookRequest request) {
        logger.info("Creating new book: {}", request.getTitle());
        if (bookRepository.existsById(request.getIsbn())) {
            logger.warn("Attempted to create a book with duplicate ISBN: {}", request.getIsbn());
            throw new DuplicateResourceException("A book with ISBN " + request.getIsbn() + " already exists");
        }
        Book savedBook = bookRepository.save(BookMapper.toEntity(request));
        return BookMapper.toResponse(savedBook);
    }

    @Override
    public BookResponse updateBook(String isbn, BookRequest request) {
        logger.info("Updating book with ISBN: {}", isbn);
        if (request.getIsbn() != null && !request.getIsbn().equals(isbn)) {
            logger.warn("ISBN mismatch on update: path={}, body={}", isbn, request.getIsbn());
            throw new InvalidRequestException(
                    "ISBN in request body (" + request.getIsbn() + ") does not match the URL path (" + isbn + ")");
        }
        Book book = findBookOrThrow(isbn);
        BookMapper.copyToEntity(request, book);
        Book updatedBook = bookRepository.save(book);
        return BookMapper.toResponse(updatedBook);
    }

    @Override
    public void deleteBook(String isbn) {
        logger.info("Deleting book with ISBN: {}", isbn);
        Book book = findBookOrThrow(isbn);
        bookRepository.delete(book);
    }

    private Book findBookOrThrow(String isbn) {
        return bookRepository.findById(isbn)
                .orElseThrow(() -> {
                    logger.warn("Book with ISBN {} not found", isbn);
                    return new ResourceNotFoundException("Book not found with ISBN: " + isbn);
                });
    }
}
