package org.example.bookcatalog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sort")
public class SortController {

    @GetMapping("byName")
    public ResponseEntity<?> sortByName (){ //sort name by alphabet
        return null;
    }

    @GetMapping("/byLastUpdate")
    public ResponseEntity<?> sortByLastUpdate (){
        return null;
    }

    @GetMapping("/byCreationDate")
    public ResponseEntity<?> sortByCreationDate (){
        return null;
    }
}
