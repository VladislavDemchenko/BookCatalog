package org.example.bookcatalog.controller;


import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/catalogs")
public class CatalogController {
    private final DataService dataService;
    @Autowired
    public CatalogController(DataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Catalog catalog,  BindingResult bindingResult){
        return dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
    }

    @DeleteMapping("/delete/{id}")
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

    @PutMapping("/updateName/{id}")
    public ResponseEntity<?> changeName(@PathVariable Long id, @RequestParam String catalogName){
        return dataService.updateName(id, catalogName, new FieldDto<String>("name"), Catalog.class);
    }
//    @PutMapping("/updateDescription/{id}")
//    public ResponseEntity<?> changeDescriptionName(@PathVariable Long id, @RequestParam String descriptionName){
//        return dataService.updateCatalogDescription(id, descriptionName);
//    }
}
