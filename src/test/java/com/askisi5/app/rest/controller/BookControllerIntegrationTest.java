package com.askisi5.app.rest.controller;

import com.askisi5.app.rest.dto.BookRequest;
import com.askisi5.app.rest.model.Book;
import com.askisi5.app.rest.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for {@link BookController}, exercised through MockMvc
 * against a real (in-memory H2) database via {@code @SpringBootTest}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/books";
    private static final String VALID_ISBN = "978-3-16-148410-0";
    private static final String OTHER_VALID_ISBN = "978-0-13-468599-1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    private BookRequest validRequest(String isbn) {
        BookRequest request = new BookRequest();
        request.setIsbn(isbn);
        request.setTitle("Effective Java");
        request.setShortSummary("Best practices for the Java platform");
        request.setPublishYear(2018);
        return request;
    }

    private Book persistBook(String isbn) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle("Effective Java");
        book.setShortSummary("Best practices for the Java platform");
        book.setPublishYear(2018);
        return bookRepository.save(book);
    }

    @Test
    @DisplayName("GET /api/v1/books returns an empty array when there are no books")
    void getAllBooks_empty() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/v1/books returns every persisted book")
    void getAllBooks_returnsAll() throws Exception {
        persistBook(VALID_ISBN);
        persistBook(OTHER_VALID_ISBN);

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/v1/books with valid data creates the book and returns 201")
    void createBook_valid_returnsCreated() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(VALID_ISBN))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value(VALID_ISBN))
                .andExpect(jsonPath("$.title").value("Effective Java"));

        assertPersistedCount(1);
    }

    @Test
    @DisplayName("POST /api/v1/books with a blank title returns 400 with a field error")
    void createBook_blankTitle_returnsBadRequest() throws Exception {
        BookRequest request = validRequest(VALID_ISBN);
        request.setTitle("");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());

        assertPersistedCount(0);
    }

    @Test
    @DisplayName("POST /api/v1/books with an invalid ISBN format returns 400")
    void createBook_invalidIsbn_returnsBadRequest() throws Exception {
        BookRequest request = validRequest("not-a-valid-isbn");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isbn").exists());
    }

    @Test
    @DisplayName("POST /api/v1/books with an out-of-range publish year returns 400")
    void createBook_publishYearTooLow_returnsBadRequest() throws Exception {
        BookRequest request = validRequest(VALID_ISBN);
        request.setPublishYear(999);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.publishYear").exists());
    }

    @Test
    @DisplayName("POST /api/v1/books with a duplicate ISBN returns 409 and does not modify the existing book")
    void createBook_duplicateIsbn_returnsConflict() throws Exception {
        persistBook(VALID_ISBN);

        BookRequest duplicate = validRequest(VALID_ISBN);
        duplicate.setTitle("A Completely Different Book");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());

        assertPersistedCount(1);
        assertThatTitleIs(VALID_ISBN, "Effective Java");
    }

    @Test
    @DisplayName("POST /api/v1/books with malformed JSON returns 400 instead of 500")
    void createBook_malformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Effective Java\", "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/v1/books/{isbn} returns the book when it exists")
    void getBookByIsbn_found() throws Exception {
        persistBook(VALID_ISBN);

        mockMvc.perform(get(BASE_URL + "/{isbn}", VALID_ISBN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(VALID_ISBN));
    }

    @Test
    @DisplayName("GET /api/v1/books/{isbn} returns 404 with an error body when the book does not exist")
    void getBookByIsbn_notFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{isbn}", VALID_ISBN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/books/{isbn} updates the book when it exists")
    void updateBook_found_returnsUpdated() throws Exception {
        persistBook(VALID_ISBN);

        BookRequest update = validRequest(VALID_ISBN);
        update.setTitle("Effective Java, 3rd Edition");

        mockMvc.perform(put(BASE_URL + "/{isbn}", VALID_ISBN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Effective Java, 3rd Edition"));

        assertThatTitleIs(VALID_ISBN, "Effective Java, 3rd Edition");
    }

    @Test
    @DisplayName("PUT /api/v1/books/{isbn} returns 404 when the book does not exist")
    void updateBook_notFound() throws Exception {
        mockMvc.perform(put(BASE_URL + "/{isbn}", VALID_ISBN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(VALID_ISBN))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/books/{isbn} returns 400 when the body ISBN does not match the path")
    void updateBook_mismatchedIsbn_returnsBadRequest() throws Exception {
        persistBook(VALID_ISBN);

        BookRequest update = validRequest(OTHER_VALID_ISBN);

        mockMvc.perform(put(BASE_URL + "/{isbn}", VALID_ISBN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());

        assertThatTitleIs(VALID_ISBN, "Effective Java");
    }

    @Test
    @DisplayName("DELETE /api/v1/books/{isbn} removes the book when it exists")
    void deleteBook_found_returnsNoContent() throws Exception {
        persistBook(VALID_ISBN);

        mockMvc.perform(delete(BASE_URL + "/{isbn}", VALID_ISBN))
                .andExpect(status().isNoContent());

        assertPersistedCount(0);
    }

    @Test
    @DisplayName("DELETE /api/v1/books/{isbn} returns 404 when the book does not exist")
    void deleteBook_notFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{isbn}", VALID_ISBN))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    private void assertPersistedCount(int expected) {
        org.assertj.core.api.Assertions.assertThat(bookRepository.count()).isEqualTo(expected);
    }

    private void assertThatTitleIs(String isbn, String expectedTitle) {
        org.assertj.core.api.Assertions.assertThat(bookRepository.findById(isbn))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo(expectedTitle);
    }
}
