package org.example.bookcatalog;

import jakarta.persistence.Persistence;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.DataAccessService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookCatalogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookCatalogApplication.class, args);
    }

}
