package org.example.bookcatalog.controller;

import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/books")
public class BookController {
    private final EntityManagerService entityManagerService;


    @PostMapping("/addBook")
    public ResponseEntity<?> addBook(@RequestBody Book book, BindingResult bindingResult){
        return null;
    }


    @DeleteMapping("/deleteBook{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return null;
    }


    @GetMapping("/getBook/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return null;
    }


    @GetMapping("/allBook")
    public ResponseEntity<?> getAllUsers(){
        return null;
    }


    @Autowired
    public BookController(EntityManagerService entityManagerService) {
        this.entityManagerService = entityManagerService;
    }
}
