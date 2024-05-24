package org.example.bookcatalog.controller;

import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.service.entityService.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/books")
public class BookController {

    private final BookService bookService;

    @PostMapping("/create")
    public ResponseEntity<?> addBook(@RequestBody Book book, BindingResult bindingResult){
        bookService.addBookToCatalog(book);
        return bookService.create(book,new FieldDto<String>("name"), bindingResult);
    }


    @DeleteMapping("/deleteBook{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return bookService.delete(id, Book.class);
    }


    @GetMapping("/getBook/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return bookService.findById(id, Book.class);
    }

    @GetMapping("/findByName")
    public ResponseEntity<?> findByName(@RequestParam String bookName){
        return bookService.findByName(bookName, Book.class);
    }

    @GetMapping("/allBook")
    public ResponseEntity<?> getAllUsers(){
        return bookService.findAll(Book.class);
    }

    @PutMapping("/updateName/{id}")
    public ResponseEntity<?> changeName(@PathVariable Long id, @RequestParam String bookName){
        return bookService.updateName(id, bookName, new FieldDto<String>("name"), Book.class);
    }

    @PutMapping("/updateBody/{id}")
    public ResponseEntity<?> changeDescriptionName(@PathVariable Long id, @RequestParam String bodyName){
        return bookService.updateBookBody(id, bodyName);
    }

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
}
