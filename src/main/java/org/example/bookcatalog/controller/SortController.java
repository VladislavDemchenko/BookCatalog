package org.example.bookcatalog.controller;

import org.example.bookcatalog.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sort")
public class SortController {


    private final DataService dataService;
    @GetMapping("/byName")
    public <T> ResponseEntity<?> sortByName (@RequestParam Class<T> entityType){ //sort name by alphabet
        return dataService.sortByName(entityType);
    }

    @GetMapping("/byLastUpdate")
    public <T> ResponseEntity<?> sortByLastUpdate (@RequestParam Class<T> entityType){
        return dataService.sortByLastUpdate(entityType);
    }


    @Autowired
    public SortController(DataService dataService) {
        this.dataService = dataService;
    }
}
