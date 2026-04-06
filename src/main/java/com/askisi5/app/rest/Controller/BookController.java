package com.askisi5.app.rest.Controller;

import com.askisi5.app.rest.Models.Book;
import com.askisi5.app.rest.Repo.BookRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepo bookRepo;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        logger.info("Fetching all books");
        List<Book> books = bookRepo.findAll();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        logger.info("Fetching book with ISBN: {}", isbn);
        Optional<Book> book = bookRepo.findById(isbn);
        if (book.isPresent()) {
            return ResponseEntity.ok(book.get());
        } else {
            logger.warn("Book with ISBN {} not found", isbn);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        logger.info("Creating new book: {}", book.getTitle());
        Book savedBook = bookRepo.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }

    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(@PathVariable String isbn, @Valid @RequestBody Book bookDetails) {
        logger.info("Updating book with ISBN: {}", isbn);
        Optional<Book> optionalBook = bookRepo.findById(isbn);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setIsbn(bookDetails.getIsbn());
            book.setTitle(bookDetails.getTitle());
            book.setShortSummary(bookDetails.getShortSummary());
            book.setPublishYear(bookDetails.getPublishYear());
            Book updatedBook = bookRepo.save(book);
            return ResponseEntity.ok(updatedBook);
        } else {
            logger.warn("Book with ISBN {} not found for update", isbn);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        logger.info("Deleting book with ISBN: {}", isbn);
        Optional<Book> book = bookRepo.findById(isbn);
        if (book.isPresent()) {
            bookRepo.delete(book.get());
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Book with ISBN {} not found for deletion", isbn);
            return ResponseEntity.notFound().build();
        }
    }
}