package org.example.bookcatalog.controller;

import org.example.bookcatalog.conf.BookCatalogConfig;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.service.DataService;
import org.example.bookcatalog.service.entityService.CatalogService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = BookCatalogConfig.class)
public class TestingControllerTest {

    private Catalog catalog;

    private Book book;

    private Note note;
    @Mock
    private BindingResult bindingResult;

    @Autowired
    private BookController bookController;

    @Autowired
    private CatalogController catalogController;

    @Autowired
    private NoteController noteController;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private DataService dataService;


    @Before
    public void setup(){
        catalog = new Catalog();
        catalog.setName("testCatalog");

        book = new Book();
        book.setName("testBook");
        book.setCatalog(catalog);

        note = new Note();
        note.setBody("testNote");
        note.setBook(book);

    }

    @After
    public void cleaningDB(){
        try {
            dataService.delete(catalog.getId(), Catalog.class);
        }catch (Exception ignored){}
    }
    @Test
    public void createEntity_Successful() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);

        // Act
        ResponseEntity<?> responseCatalog = catalogController.create(catalog, bindingResult);
        ResponseEntity<?> responseBook = bookController.addBook(book, bindingResult);
        ResponseEntity<?> responseNote = noteController.addNote(note, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, responseCatalog.getStatusCode());
        assertEquals(HttpStatus.OK, responseBook.getStatusCode());
        assertEquals(HttpStatus.OK, responseNote.getStatusCode());
    }


    @Test
    public void createEntity_HasErrors_BAD_REQUEST_BindingResultException() {
        // Arrange
        when(bindingResult.hasErrors()).thenReturn(false);
        catalogController.create(catalog, bindingResult);
        bookController.addBook(book, bindingResult);
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        ResponseEntity<?> responseCatalog = catalogController.create(catalog, bindingResult);
        ResponseEntity<?> responseBook = bookController.addBook(book, bindingResult);
        ResponseEntity<?> responseNote = noteController.addNote(note, bindingResult);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseCatalog.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, responseBook.getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, responseNote.getStatusCode());
    }

    @Test
    public void createEntity_HasErrors_BAD_REQUEST_UniqueValue(){
        // Arrange
        catalogController.create(catalog, bindingResult);
        bookController.addBook(book, bindingResult);

        // catalog for test book unique value
        Catalog testCatalogForBook = Catalog.builder().name("testCatalog2").build();
        catalogController.create(testCatalogForBook, bindingResult);

        // Objects with same unique value
        Catalog catalogWithSameUniqueValue = Catalog.builder().name(catalog.getName()).build();
        Book bookWithSameUniqueValue = Book.builder().name(book.getName()).catalog(testCatalogForBook).build();


        // Act & Assert
        InvalidRequestException catalogException = assertThrows(InvalidRequestException.class, () -> {
            catalogController.create(catalogWithSameUniqueValue, bindingResult);
        });
        InvalidRequestException bookException = assertThrows(InvalidRequestException.class, () -> {
            bookController.addBook(bookWithSameUniqueValue, bindingResult);
        });

        assertEquals("This field name " + catalog.getName() + " already exists", catalogException.getMessage());
        assertEquals("This field name " + book.getName() + " already exists", bookException.getMessage());

        //Clearing db
        dataService.delete(testCatalogForBook.getId(), Catalog.class);
    }

    @Test
    public void deleteEntity_Successful() {
        // Arrange
        catalogController.create(catalog, bindingResult);
        bookController.addBook(book, bindingResult);
        Long catalogId = catalog.getId();

        // Act
        ResponseEntity<?> catalogResponse = catalogController.delete(catalogId); //cascade remove

        // Assert
        assertEquals(HttpStatus.OK, catalogResponse.getStatusCode());
        assertEquals("Validation successful", catalogResponse.getBody());
    }
    @Test
    public void testDeleteCatalog_HasErrors_BAD_REQUEST_InvalidId() {
        // Arrange
        Long id = -1L;

        // Act & Assert
        InvalidRequestException invalidCatalogId = assertThrows(InvalidRequestException.class, () -> {
            catalogController.delete(id);
        });
        InvalidRequestException invalidBookId = assertThrows(InvalidRequestException.class, () -> {
            bookController.delete(id);
        });
        InvalidRequestException invalidNoteId = assertThrows(InvalidRequestException.class, () -> {
            noteController.delete(id);
        });
        assertEquals("Not found value id. Please provide a valid identifier.", invalidCatalogId.getMessage());
        assertEquals("Not found value id. Please provide a valid identifier.", invalidBookId.getMessage());
        assertEquals("Not found value id. Please provide a valid identifier.", invalidNoteId.getMessage());
    }

    @Test
    public void testDeleteCatalog_HasErrors_BAD_REQUEST_IdIsNull() {
        // Arrange
        Long id = null;

        // Act & Assert
        InvalidRequestException invalidCatalogId = assertThrows(InvalidRequestException.class, () -> {
            catalogController.delete(id);
        });
        InvalidRequestException invalidBookId = assertThrows(InvalidRequestException.class, () -> {
            bookController.delete(id);
        });
        InvalidRequestException invalidNoteId = assertThrows(InvalidRequestException.class, () -> {
            noteController.delete(id);
        });
        assertEquals("Operation cannot be performed: identifier is missing. Please check the entered data and try again.", invalidCatalogId.getMessage());
        assertEquals("Operation cannot be performed: identifier is missing. Please check the entered data and try again.", invalidBookId.getMessage());
        assertEquals("Operation cannot be performed: identifier is missing. Please check the entered data and try again.", invalidNoteId.getMessage());
    }
}
