package com.askisi5.app.rest.Controller;

import com.askisi5.app.rest.Models.Book;
import com.askisi5.app.rest.Models.User;
import com.askisi5.app.rest.Repo.BookRepo;
import com.askisi5.app.rest.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ApiControllers {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BookRepo bookRepo;

    @GetMapping(value = "/api/v1/users")
    public List<User> getUsers(){
        return userRepo.findAll();
    }

    @GetMapping(value = "/api/v1/users/{id}")
    public Optional<User> getUser(@PathVariable long id){
        return userRepo.findById(id);
    }

    @PostMapping(value = "/api/v1/users")
    public String saveUser(@RequestBody User user){
        userRepo.save(user);
        return "saved";
    }

    @PutMapping(value = "/api/v1/users/{id}")
    public String updateUser(@PathVariable long id,@RequestBody User user){
        User updateUser = userRepo.findById(id).get();
        updateUser.setFirstName(user.getFirstName());
        updateUser.setLastName(user.getLastName());
        updateUser.setStreet(user.getStreet());
        updateUser.setCity(user.getCity());
        updateUser.setPostalCode(user.getPostalCode());
        updateUser.setCountry(user.getCountry());
        updateUser.setPhoneNumber(user.getPhoneNumber());
        updateUser.setBirthdayDate(user.getBirthdayDate());
        updateUser.setSex(user.getSex());
        userRepo.save(updateUser);
        return "updated";
    }
    @DeleteMapping(value = "/api/v1/users/{userId}")
    public String deleteUser(@PathVariable long userId){
        User deleteUser = userRepo.findById(userId).get();
        userRepo.delete(deleteUser);
        return "Delete user with the id:" + userId;
    }

    @GetMapping(value = "/api/v1/books")
    public List<Book> getBooks(){
        return bookRepo.findAll();
    }

    @GetMapping(value = "/api/v1/books/{bookId}")
    public Optional<Book> getUser(@PathVariable String bookId){
        return bookRepo.findById(bookId);
    }

    @PostMapping(value = "/api/v1/books")
    public String saveBook(@RequestBody Book book){
        bookRepo.save(book);
        return "saved";
    }

    @PutMapping(value = "/api/v1/books/{bookId}")
    public String updateBook(@PathVariable String bookId,@RequestBody Book book){
        Book updateBook = bookRepo.findById(bookId).get();
        updateBook.setISBN(book.getISBN());
        updateBook.setTitle(book.getTitle());
        updateBook.setShortSummary(book.getShortSummary());
        updateBook.setPublishYear(book.getPublishYear());
        bookRepo.save(updateBook);
        return "updated";
    }
    @DeleteMapping(value = "/api/v1/books/{bookId}")
    public String deleteBook(@PathVariable String bookId){
        Book deleteBook = bookRepo.findById(bookId).get();
        bookRepo.delete(deleteBook);
        return "Delete user with the id:" + bookId;
    }
}
