package org.example.bookcatalog.service.entityService;

import jakarta.persistence.EntityManagerFactory;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CatalogService extends DataService {
    public ResponseEntity<?> updateCatalogDescription(Long id, String newDescription){ //Catalog method
        validId(id, Catalog.class);
        Catalog catalog = executeInTransactionReturning((em) -> em.find(Catalog.class, id));
        if(catalog.getDescription().equals(newDescription)){
            throw new InvalidRequestException("This description already exist");
        }
        validateAndUpdateCreationDate(catalog);
        catalog.setDescription(newDescription);
        return ResponseEntity.ok("Validation successful");
    }

    public CatalogService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }
}
