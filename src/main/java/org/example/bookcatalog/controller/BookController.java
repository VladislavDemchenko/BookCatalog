package org.example.bookcatalog.controller;

import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/books")
public class BookController {

    private final DataService dataService;

    @PostMapping("/create")
    public ResponseEntity<?> addBook(@RequestBody Book book, BindingResult bindingResult){
        return dataService.create(book,new FieldDto<String>("name"), bindingResult);
    }


    @DeleteMapping("/deleteBook{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return dataService.delete(id, Book.class);
    }


    @GetMapping("/getBook/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return dataService.findById(id, Book.class);
    }


    @GetMapping("/allBook")
    public ResponseEntity<?> getAllUsers(){
        return dataService.findAll(Book.class);
    }
    @Autowired
    public BookController(DataService dataService) {
        this.dataService = dataService;
    }
}
