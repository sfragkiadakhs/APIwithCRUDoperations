package com.askisi5.app.rest.Controller;

import com.askisi5.app.rest.Models.User;
import com.askisi5.app.rest.Repo.UserRepo;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepo userRepo;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepo.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable long id) {
        logger.info("Fetching user with id: {}", id);
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            logger.warn("User with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Creating new user: {}", user.getFirstName() + " " + user.getLastName());
        User savedUser = userRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @Valid @RequestBody User userDetails) {
        logger.info("Updating user with id: {}", id);
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setStreet(userDetails.getStreet());
            user.setCity(userDetails.getCity());
            user.setPostalCode(userDetails.getPostalCode());
            user.setCountry(userDetails.getCountry());
            user.setPhoneNumber(userDetails.getPhoneNumber());
            user.setBirthdayDate(userDetails.getBirthdayDate());
            user.setSex(userDetails.getSex());
            User updatedUser = userRepo.save(user);
            return ResponseEntity.ok(updatedUser);
        } else {
            logger.warn("User with id {} not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        logger.info("Deleting user with id: {}", id);
        Optional<User> user = userRepo.findById(id);
        if (user.isPresent()) {
            userRepo.delete(user.get());
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("User with id {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }
}