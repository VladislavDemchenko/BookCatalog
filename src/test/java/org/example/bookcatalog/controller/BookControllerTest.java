package org.example.bookcatalog.controller;

import jakarta.persistence.Persistence;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.service.DataAccessService;
import org.example.bookcatalog.service.DataService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {


    private Catalog catalog;
    private Book book;

    @Mock
    private BindingResult bindingResult;

    private DataAccessService dataAccessService;
    private DataService dataService;

    private BookController bookController;

    @Before
    public void setup(){
        dataService = new DataService(Persistence.createEntityManagerFactory("book-catalog unit"));
        dataAccessService = new DataAccessService(Persistence.createEntityManagerFactory("book-catalog unit"));
        bookController = new BookController(dataService);

        catalog = Catalog.builder().name("testCatalog").build();
        dataAccessService.executeInTransaction(em -> em.persist(catalog));
        book = Book.builder()
                .name("my new book")
                .catalog(catalog)
                .build();

        MockitoAnnotations.initMocks(this);
    }
    @After
    public void cleaningDB(){
        try {
            dataService.delete(catalog.getId(), Catalog.class);
        }catch (Exception e){}
    }


    @Test
    public void testAddBook_Successful() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        ResponseEntity<?> response = bookController.addBook(book, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Validation successful", response.getBody());
    }


    @Test
    public void testCreateCatalog_HasErrors_BAD_REQUEST_BindingResultException() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> response = bookController.addBook(book, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(bindingResult).hasErrors();
    }

    @Test
    public void testCreateCatalog_HasErrors_BAD_REQUEST_UniqueValue(){
        // Arrange
        dataService.create(book, new FieldDto<String>("name"), bindingResult);
        Book book1 = Book.builder().name("catalog").build();

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            bookController.addBook(book1, bindingResult);
        });
        assertEquals("This field name " + book1.getName() + " already exists", exception.getMessage());
    }
}
