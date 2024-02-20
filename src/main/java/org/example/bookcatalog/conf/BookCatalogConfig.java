package org.example.bookcatalog.conf;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.example.bookcatalog")
public class BookCatalogConfig {
    @Bean
    public EntityManagerFactory entityManagerFactoryBean(){
        return Persistence.createEntityManagerFactory("book-catalog unit");
    }

}
