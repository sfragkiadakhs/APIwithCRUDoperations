package com.askisi5.app.rest.Repo;

import com.askisi5.app.rest.Models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepo extends JpaRepository<Book, String> {
}
