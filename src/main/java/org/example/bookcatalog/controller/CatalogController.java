package org.example.bookcatalog.controller;


import jakarta.validation.Valid;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.entityService.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/catalogs")
public class CatalogController {
    private final CatalogService catalogService;
    @Autowired
    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Catalog catalog,  BindingResult bindingResult){
        return catalogService.create(catalog, new FieldDto<String>("name"), bindingResult);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        return catalogService.delete(id, Catalog.class);
    }


    @GetMapping("/findByName")
    public ResponseEntity<?> findByName(@RequestParam String catalogName){
        return catalogService.findByName(catalogName, Catalog.class);
    }

    @GetMapping("/findAll")
    public ResponseEntity<?> findAll(){
        return catalogService.findAll(Catalog.class);
    }

    @PutMapping("/updateName/{id}")
    public ResponseEntity<?> changeName(@PathVariable Long id, @RequestParam String catalogName){
        return catalogService.updateName(id, catalogName, new FieldDto<String>("name"), Catalog.class);
    }
    @PutMapping("/updateDescription/{id}")
    public ResponseEntity<?> changeDescriptionName(@PathVariable Long id, @RequestParam String descriptionName){
        return catalogService.updateCatalogDescription(id, descriptionName);
    }
}
