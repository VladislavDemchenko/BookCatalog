package org.example.bookcatalog;


import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import org.checkerframework.checker.units.qual.C;
import org.example.bookcatalog.controller.CatalogController;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.exception.controllerAdvice.GlobalExceptionHandler;
import org.example.bookcatalog.service.DataAccessService;
import org.example.bookcatalog.service.DataService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(CatalogController.class)
@Import(GlobalExceptionHandler.class)
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
                .description("my new test catalog")
                .build();
        dataService = new DataService(Persistence.createEntityManagerFactory("book-catalog unit"));
        dataAccessService = new DataAccessService(Persistence.createEntityManagerFactory("book-catalog unit"));
        catalogController = new CatalogController(dataService);
        MockitoAnnotations.initMocks(this);
    }
    @After
    public void cleaningDB(){
        try {
            dataService.delete(findIdByName(), Catalog.class);
        }catch (Exception e){}
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
    public void testCreateCatalog_HasErrors_BAD_REQUEST_BindingResultException() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> response = catalogController.create(catalog, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bindingResult).hasErrors();
    }

    @Test(expected = InvalidRequestException.class)
    public void testCreateCatalog_HasErrors_BAD_REQUEST_UniqueValue(){
        // Arrange
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Catalog catalog1 = Catalog.builder().name("catalog").build();

        //Act
        catalogController.create(catalog1, bindingResult);

    }

    @Test
    public void testDeleteCatalog_Successful() {
        // Arrange
        catalog.setName("testCatalogName");
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Long id = findIdByName();

        // Act
        ResponseEntity<?> response = catalogController.delete(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Validation successful", response.getBody());
    }

    @Test(expected = InvalidRequestException.class)
    public void testDeleteCatalog_HasErrors_BAD_REQUEST() {
        // Arrange
        Long id = -1L;

        // Act
        catalogController.delete(id);
    }

    @Test
    public void testFindByNameCatalog_Successful(){
        // Arrange
        String name = "testCatalogName2";
        catalog.setName(name);
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);

        // Act
        ResponseEntity<?> response = catalogController.findByName(name);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test(expected = InvalidRequestException.class)
    public void testFindByNameCatalog_HasErrors_BAD_REQUEST_NotFoundName(){
        // Arrange
        String name = "";

        // Act
        catalogController.findByName(name);
    }


    @Test
    public void testFindAllCatalogs_Successful(){
        // Arrange
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);

        // Act
        ResponseEntity<?> response = catalogController.findAll();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testFindAllCatalogs_HasErrors_NO_CONTENT(){

        // Act
        ResponseEntity<?> response = catalogController.findAll();

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testChangeNameCatalog_Successful(){

        //Arrange
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Long id = catalog.getId();
        catalog.setName("newCatalogName");

        //Act
        ResponseEntity<?> response = catalogController.changeName(id, "newCatalogName");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private Long findIdByName() {
        return (Long)dataAccessService.executeInTransactionReturning(em -> em.createQuery("select c.id from Catalog c where c.name = :name")
                .setParameter("name", catalog.getName())
                .getSingleResult());
    }
}
