package org.example.bookcatalog.service.entityService;

import jakarta.persistence.EntityManagerFactory;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class BookService extends DataService {
    public BookService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }


    public ResponseEntity<?> updateBookBody(Long id, String newBody){
        validId(id, Book.class);
        Book book = executeInTransactionReturning((em) -> em.find(Book.class, id));
        if(book.getBody().equals(newBody)){
            throw new InvalidRequestException("This body already exist");
        }
        validateAndUpdateCreationDate(book);
        book.setBody(newBody);
        return ResponseEntity.ok("Validation successful");
    }

    public void addBookToCatalog(Book book) {
        Catalog catalog = this.executeInTransactionReturning(em -> em.find(Catalog.class, book.getCatalog().getId()));
        catalog.addBook(book);
        this.validateAndUpdateCreationDate(catalog);
    }
}
