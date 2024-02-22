package org.example.bookcatalog.controller;

import org.example.bookcatalog.entity.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

    @PostMapping("/addNote")
    public ResponseEntity<?> addNote(@RequestBody Note userNote){
        return null;
    }

    @DeleteMapping("/deleteNote/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Long id){
        return null;
    }

    @PutMapping("/updateNote/{id}")
    public ResponseEntity<?> changeNote(@PathVariable Long id, @RequestParam String descriptionName){
        return null;
    }

    @GetMapping("/getNote/{id}")
    public ResponseEntity<?> getNote(@PathVariable Long id){
        return null;
    }

    @GetMapping("/getAllNote")
    public ResponseEntity<?> getAllNote(){
        return null;
    }
}
