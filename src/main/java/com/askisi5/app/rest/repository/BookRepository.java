package com.askisi5.app.rest.repository;

import com.askisi5.app.rest.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, String> {
}
