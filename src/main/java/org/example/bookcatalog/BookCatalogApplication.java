package org.example.bookcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class BookCatalogApplication {

    public static void main(String[] args) {
                SpringApplication.run(BookCatalogApplication.class, args);
    }

}
