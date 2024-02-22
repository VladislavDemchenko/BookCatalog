package org.example.bookcatalog.controller;


import jakarta.validation.Valid;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.CatalogService;
import org.example.bookcatalog.service.EntityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/catalogs")
public class CatalogController {
    private final CatalogService catalogService;
    @PostMapping("/crateCatalog")
    public ResponseEntity<?> crate(@RequestBody @Valid Catalog catalog, BindingResult bindingResult){
        return catalogService.crate(catalog, bindingResult);
    }

    @DeleteMapping("/deleteCatalog/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return catalogService.delete(id);
    }

    @GetMapping("/findByName")
    public ResponseEntity<?> findByName(@RequestParam String catalogName){
        return catalogService.findByName(catalogName);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){
        return catalogService.findAll();
    }

    @PutMapping("/updateCatalogName/{id}")
    public ResponseEntity<?> changeName(@PathVariable Long id, @RequestParam String catalogName){
        return catalogService.changeName(id, catalogName);
    }

    @PutMapping("/updateCatalogDescription/{id}")
    public ResponseEntity<?> changeDescriptionName(@PathVariable Long id, @RequestParam String descriptionName){
        return catalogService.changeDescriptionName(id, descriptionName);
    }
    @Autowired
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
}
