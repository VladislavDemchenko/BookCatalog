package org.example.bookcatalog.controller;

import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

    private final DataService dataService;
    @PostMapping("/addNote")
    public ResponseEntity<?> addNote(@RequestBody Note userNote, BindingResult bindingResult){
        return dataService.create(userNote, bindingResult);
    }

    @DeleteMapping("/deleteNote/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id){
        return dataService.delete(id, Note.class);
    }

    @PutMapping("/updateNote/{id}")
    public ResponseEntity<?> changeNote(@PathVariable Long id, @RequestParam String descriptionName){
        return dataService.updateName(id, descriptionName, Note.class);
    }

    @GetMapping("/getNote/{id}")
    public ResponseEntity<?> getNote(@PathVariable Long id){
        return dataService.findById(id, Note.class);
    }

    @GetMapping("/getAllNote")
    public ResponseEntity<?> getAllNote(){
        return dataService.findAll(Note.class);
    }

    @Autowired
    public NoteController(DataService dataService) {
        this.dataService = dataService;
    }
}
