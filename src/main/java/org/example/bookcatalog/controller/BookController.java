package org.example.bookcatalog.controller;

import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/books")
public class BookController {
    private final EntityManagerService entityManagerService;


    @PostMapping("/addBook")
    public String addBook(@RequestBody Book book, BindingResult bindingResult){
        return entityManagerService.addUser(book, bindingResult);
    }


    @DeleteMapping("/deleteBook{id}")
    public String delete(@PathVariable Long id){
        return entityManagerService.delete(id);
    }


    @GetMapping("/getBook/{id}")
    public String findById(@PathVariable Long id){
        return entityManagerService.findById(id);
    }


    @GetMapping("/allBook")
    public List<Book> getAllUsers(){
        return entityManagerService.getAllUsers();
    }


    @Autowired
    public BookController(EntityManagerService entityManagerService) {
        this.entityManagerService = entityManagerService;
    }
}
