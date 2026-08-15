package com.askisi5.app.rest.service;

import com.askisi5.app.rest.dto.BookRequest;
import com.askisi5.app.rest.dto.BookResponse;
import com.askisi5.app.rest.exception.DuplicateResourceException;
import com.askisi5.app.rest.exception.InvalidRequestException;
import com.askisi5.app.rest.exception.ResourceNotFoundException;
import com.askisi5.app.rest.model.Book;
import com.askisi5.app.rest.repository.BookRepository;
import com.askisi5.app.rest.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    private BookServiceImpl bookService;

    private static final String ISBN = "978-3-16-148410-0";

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepository);
    }

    private Book sampleBook() {
        Book book = new Book();
        book.setIsbn(ISBN);
        book.setTitle("Effective Java");
        book.setShortSummary("Best practices for the Java platform");
        book.setPublishYear(2018);
        return book;
    }

    private BookRequest sampleRequest() {
        BookRequest request = new BookRequest();
        request.setIsbn(ISBN);
        request.setTitle("Effective Java");
        request.setShortSummary("Best practices for the Java platform");
        request.setPublishYear(2018);
        return request;
    }

    @Nested
    @DisplayName("getAllBooks")
    class GetAllBooks {

        @Test
        @DisplayName("maps every entity returned by the repository to a response DTO")
        void returnsAllBooksMapped() {
            when(bookRepository.findAll()).thenReturn(Arrays.asList(sampleBook(), sampleBook()));

            List<BookResponse> result = bookService.getAllBooks();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getIsbn()).isEqualTo(ISBN);
        }

        @Test
        @DisplayName("returns an empty list when there are no books")
        void returnsEmptyListWhenNoBooks() {
            when(bookRepository.findAll()).thenReturn(Collections.emptyList());

            List<BookResponse> result = bookService.getAllBooks();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getBookByIsbn")
    class GetBookByIsbn {

        @Test
        @DisplayName("returns the mapped book when it exists")
        void returnsBookWhenFound() {
            when(bookRepository.findById(ISBN)).thenReturn(Optional.of(sampleBook()));

            BookResponse result = bookService.getBookByIsbn(ISBN);

            assertThat(result.getIsbn()).isEqualTo(ISBN);
            assertThat(result.getTitle()).isEqualTo("Effective Java");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the ISBN does not exist")
        void throwsWhenNotFound() {
            when(bookRepository.findById("does-not-exist")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.getBookByIsbn("does-not-exist"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("does-not-exist");
        }
    }

    @Nested
    @DisplayName("createBook")
    class CreateBook {

        @Test
        @DisplayName("saves and returns the new book when the ISBN is not already taken")
        void createsBookWhenIsbnIsUnique() {
            when(bookRepository.existsById(ISBN)).thenReturn(false);
            when(bookRepository.save(any(Book.class))).thenReturn(sampleBook());

            BookResponse result = bookService.createBook(sampleRequest());

            assertThat(result.getIsbn()).isEqualTo(ISBN);
            ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
            verify(bookRepository).save(captor.capture());
            assertThat(captor.getValue().getIsbn()).isEqualTo(ISBN);
        }

        @Test
        @DisplayName("throws DuplicateResourceException and never saves when the ISBN already exists")
        void throwsWhenIsbnAlreadyExists() {
            when(bookRepository.existsById(ISBN)).thenReturn(true);

            assertThatThrownBy(() -> bookService.createBook(sampleRequest()))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining(ISBN);

            verify(bookRepository, never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("updateBook")
    class UpdateBook {

        @Test
        @DisplayName("updates and returns the book when it exists and the ISBN matches the path")
        void updatesBookWhenFound() {
            when(bookRepository.findById(ISBN)).thenReturn(Optional.of(sampleBook()));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BookRequest request = sampleRequest();
            request.setTitle("Effective Java, 3rd Edition");

            BookResponse result = bookService.updateBook(ISBN, request);

            assertThat(result.getTitle()).isEqualTo("Effective Java, 3rd Edition");
            verify(bookRepository, times(1)).save(any(Book.class));
        }

        @Test
        @DisplayName("updates the book when the request body omits the ISBN entirely")
        void updatesBookWhenIsbnInBodyIsNull() {
            when(bookRepository.findById(ISBN)).thenReturn(Optional.of(sampleBook()));
            when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BookRequest request = sampleRequest();
            request.setIsbn(null);

            BookResponse result = bookService.updateBook(ISBN, request);

            assertThat(result.getIsbn()).isEqualTo(ISBN);
            verify(bookRepository, times(1)).save(any(Book.class));
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the book does not exist")
        void throwsWhenNotFound() {
            when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.updateBook(ISBN, sampleRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(bookRepository, never()).save(any(Book.class));
        }

        @Test
        @DisplayName("throws InvalidRequestException when the body ISBN does not match the path ISBN")
        void throwsWhenIsbnMismatch() {
            BookRequest request = sampleRequest();
            request.setIsbn("978-0-13-468599-1");

            assertThatThrownBy(() -> bookService.updateBook(ISBN, request))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessageContaining(ISBN)
                    .hasMessageContaining("978-0-13-468599-1");

            verify(bookRepository, never()).findById(anyString());
            verify(bookRepository, never()).save(any(Book.class));
        }
    }

    @Nested
    @DisplayName("deleteBook")
    class DeleteBook {

        @Test
        @DisplayName("deletes the book when it exists")
        void deletesBookWhenFound() {
            Book book = sampleBook();
            when(bookRepository.findById(ISBN)).thenReturn(Optional.of(book));

            bookService.deleteBook(ISBN);

            verify(bookRepository).delete(book);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the book does not exist")
        void throwsWhenNotFound() {
            when(bookRepository.findById(ISBN)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> bookService.deleteBook(ISBN))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(bookRepository, never()).delete(any(Book.class));
        }
    }
}
