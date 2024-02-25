package org.example.bookcatalog.controller;


import jakarta.validation.Valid;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/catalogs")
public class CatalogController {
    private final DataService dataService;
    @PostMapping("/crateCatalog")
    public ResponseEntity<?> crate(@RequestBody @Valid Catalog catalog, BindingResult bindingResult){
        return dataService.crate(catalog, bindingResult);
    }

    @DeleteMapping("/deleteCatalog/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return dataService.delete(id, Catalog.class);
    }

    @GetMapping("/findByName")
    public ResponseEntity<?> findByName(@RequestParam String catalogName){
        return dataService.findByName(catalogName, Catalog.class);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){
        return dataService.findAll(Catalog.class);
    }

    @PutMapping("/updateCatalogName/{id}")
    public ResponseEntity<?> changeName(@PathVariable Long id, @RequestParam String catalogName){
        return dataService.changeName(id, catalogName, Catalog.class);
    }

    @PutMapping("/updateCatalogDescription/{id}")
    public ResponseEntity<?> changeDescriptionName(@PathVariable Long id, @RequestParam String descriptionName){
        return dataService.changeDescriptionName(id, descriptionName);
    }
    @Autowired
    public CatalogController(DataService dataService) {
        this.dataService = dataService;
    }
}
