package org.example.bookcatalog;


import jakarta.persistence.Persistence;
import org.example.bookcatalog.controller.CatalogController;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.service.DataAccessService;
import org.example.bookcatalog.service.DataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(CatalogController.class)
public class CatalogControllerTest {

    private Catalog catalog;

    @Mock
    private BindingResult bindingResult;

    private DataAccessService dataAccessService;
    private DataService dataService;

    private CatalogController catalogController;

    @Before
    public void setup(){
        catalog = Catalog.builder()
                .name("catalog")
                .description("my new catalog")
                .build();
        dataService = new DataService(Persistence.createEntityManagerFactory("book-catalog unit"));
        dataAccessService = new DataAccessService(Persistence.createEntityManagerFactory("book-catalog unit"));
        catalogController = new CatalogController(dataService);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateCatalog_Successful() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        ResponseEntity<?> response = catalogController.create(catalog, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Validation successful", response.getBody());
    }


    @Test
    public void testCreateCatalog_HasErrors() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> response = catalogController.create(catalog, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals(bindingResult.getFieldError(), response.getBody());
        verify(bindingResult).hasErrors();
    }


    @Test
    public void testDeleteCatalog_Successful() {
        // Arrange
//        dataService.create(catalog, bindingResult);
        Long id = dataAccessService.executeInTransactionReturning(em -> em.find(Catalog.class, catalog)).getId();
        // Act
        ResponseEntity<?> response = catalogController.delete(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Validation successful", response.getBody());
    }

    @Test(expected = InvalidRequestException.class)
    public void testDeleteCatalog_HasErrors_NullableId() {
        // Arrange
        Long id = -1L;

        // Act
        ResponseEntity<?> response = catalogController.delete(id);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testFindByNameCatalog_Successful(){
        // Arrange
        String lookingName = "Steve";

        // Act
        ResponseEntity<?> response = catalogController.findByName(lookingName);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
