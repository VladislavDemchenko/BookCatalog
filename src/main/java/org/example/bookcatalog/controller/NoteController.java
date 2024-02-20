package org.example.bookcatalog.controller;

import org.example.bookcatalog.entity.Note;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

    @PostMapping("/addNote")
    public String addNote(@RequestBody Note userNote){
        return null;
    }

    @DeleteMapping("/deleteNote/{id}")
    public String deleteNote(@PathVariable Long id){
        return null;
    }

    @PutMapping("/updateNote/{id}")
    public String changeNote(@PathVariable Long id, @RequestParam String descriptionName){
        return null;
    }

    @GetMapping("/getNote/{id}")
    public String getNote(@PathVariable Long id){
        return null;
    }

    @GetMapping("/getAllNote")
    public String getAllNote(){
        return null;
    }
}
