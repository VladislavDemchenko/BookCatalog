package org.example.bookcatalog.service;

import jakarta.persistence.EntityManagerFactory;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CatalogService extends SessionService implements GenericService<ResponseEntity<?>, Catalog>{

    @Override
    public ResponseEntity<?> crate(Catalog catalog, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.getFieldError());
        }
        executeInTransaction((em) -> em.persist(catalog));
        return ResponseEntity.ok("Validation successful");
    }

    @Override
    public ResponseEntity<?> delete(Long id){
        validId(id, Catalog.class); // method provides greater flexibility and control over validation, but you can also use BindingResult
        executeInTransaction((em) -> em.remove(em.find(Catalog.class, id)));
        return ResponseEntity.ok("Validation successful");
    }


    @Override
    public ResponseEntity<?> findById(Long id) {
        validId(id, Catalog.class);
        return ResponseEntity.ok(executeInTransactionReturning((em) -> em.find(Catalog.class, id)));
    }

    @Override
    public ResponseEntity<?> findAll() {
        List<Catalog> catalogs = executeInTransactionReturning((em) -> em.createQuery("select c from Catalog c", Catalog.class).getResultList());
        if (catalogs.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(catalogs);
        }
    }

    public ResponseEntity<?> findByName(String catalogName){
        var catalog =  executeInTransactionReturning((em) -> {
            return em.createQuery("select c from Catalog c where c.name = :name")
                    .setParameter("name", catalogName)
                    .getSingleResult();
        });
        if(catalog == null){
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.ok(catalog);
        }
    }

    public ResponseEntity<?> changeName(Long id, String catalogName){
        validId(id, Catalog.class);
        if(catalogName.isEmpty()){
            throw new InvalidRequestException("name of catalog can`t be empty");
        }
        if(Objects.equals(executeInTransactionReturning((em) -> em.find(Catalog.class, id)).getName(), catalogName)){
            throw new InvalidRequestException("this name catalog is already exist");
        }
        executeInTransaction((em)-> em.find(Catalog.class, id).setName(catalogName)); // there will be no extra request, because the Catalog already attached
        return ResponseEntity.ok("Validation successful");
    }

    public ResponseEntity<?> changeDescriptionName(Long id, String descriptionName){
        validId(id, Catalog.class);
        if(Objects.equals(executeInTransactionReturning((em) -> em.find(Catalog.class, id)).getDescription(), descriptionName)){
            throw new InvalidRequestException("this description is already exist");
        }
        executeInTransaction((em)-> em.find(Catalog.class, id).setDescription(descriptionName));
        return ResponseEntity.ok("Validation successful");
    }




    public CatalogService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }



}
