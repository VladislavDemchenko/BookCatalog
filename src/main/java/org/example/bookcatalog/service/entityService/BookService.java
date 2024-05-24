package org.example.bookcatalog.service.entityService;

import jakarta.persistence.EntityManagerFactory;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.DataService;
import org.springframework.stereotype.Service;

@Service
public class BookService extends DataService {
    public BookService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public void addBookToCatalog(Book book) {
        Catalog catalog = this.executeInTransactionReturning(em -> em.find(Catalog.class, book.getCatalog().getId()));
        catalog.addBook(book);
        this.validateAndUpdateCreationDate(catalog);
    }
}
