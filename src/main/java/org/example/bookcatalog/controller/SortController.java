package org.example.bookcatalog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sort")
public class SortController {

    @GetMapping("byName")
    public String sortByName (){ //sort name by alphabet
        return null;
    }

    @GetMapping("/byLastUpdate")
    public String sortByLastUpdate (){
        return null;
    }

    @GetMapping("/byCreationDate")
    public String sortByCreationDate (){
        return null;
    }
}
