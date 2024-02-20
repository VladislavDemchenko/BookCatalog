package org.example.bookcatalog.controller;


import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/catalogs")
public class CatalogController {


    @GetMapping("/findByName")
    public String findByName(@RequestParam String catalogName){
        return null;
    }

    @PostMapping("/crateCatalog")
    public String crate(@RequestBody Catalog catalog, BindingResult bindingResult){
        return null;//create new catalog
    }

    @DeleteMapping("/deleteCatalog/{id}")
    public String delete(@PathVariable Long id){
        return null; //delete catalog
    }


    @PutMapping("/updateCatalogName/{id}")
    public String changeName(@PathVariable Long id, @RequestParam String catalogName){
        return null;
    }

    @PutMapping("/updateCatalogDescription/{id}")
    public String changeDescriptionName(@PathVariable Long id, @RequestParam String descriptionName){
        return null;
    }


}
