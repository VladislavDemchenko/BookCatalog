package org.example.bookcatalog.controller;

import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.service.entityService.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoteController {

    private final NoteService noteService;
    @PostMapping("/addNote")
    public ResponseEntity<?> addNote(@RequestBody Note note, BindingResult bindingResult){
        noteService.addNoteToBook(note);
        return noteService.create(note, null, bindingResult);
    }

    @DeleteMapping("/deleteNote/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return noteService.delete(id, Note.class);
    }

    @PutMapping("/updateNote/{id}")
    public ResponseEntity<?> change(@PathVariable Long id, @RequestParam String descriptionName){
        return noteService.updateName(id, descriptionName,new FieldDto<String>("name"), Note.class);
    }

    @GetMapping("/getNote/{id}")
    public ResponseEntity<?> getNote(@PathVariable Long id){
        return noteService.findById(id, Note.class);
    }

    @GetMapping("/getAllNote")
    public ResponseEntity<?> getAllNote(){
        return noteService.findAll(Note.class);
    }

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
}
