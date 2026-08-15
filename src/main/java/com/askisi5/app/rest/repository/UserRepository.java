package com.askisi5.app.rest.repository;

import com.askisi5.app.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
