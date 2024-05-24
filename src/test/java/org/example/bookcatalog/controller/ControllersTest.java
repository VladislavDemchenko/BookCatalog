package org.example.bookcatalog.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jayway.jsonpath.internal.JsonFormatter;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.bookcatalog.dto.FieldDto;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.exception.InvalidRequestException;
import org.example.bookcatalog.service.entityService.BookService;
import org.example.bookcatalog.service.entityService.CatalogService;
import org.example.bookcatalog.service.DataAccessService;
import org.example.bookcatalog.service.DataService;
import org.example.bookcatalog.service.entityService.NoteService;
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

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(CatalogController.class)
public class ControllersTest {

    private Catalog catalog;

    private Book book;

    private Note note;
    @Mock
    private BindingResult bindingResult;

    private DataAccessService dataAccessService;
    private DataService dataService;

    private CatalogService catalogService;

    private BookService bookService;

    private NoteService noteService;
    private BookController bookController;
    private CatalogController catalogController;
    private NoteController noteController;


    @Before
    public void setup(){

        EntityManagerFactory persistenceUnit = Persistence.createEntityManagerFactory("book-catalog unit");

        dataAccessService = new DataAccessService(persistenceUnit);
        dataService = new DataService(persistenceUnit);

        catalogService = new CatalogService(persistenceUnit);
        bookService = new BookService(persistenceUnit);
        noteService = new NoteService(persistenceUnit);

        catalogController = new CatalogController(catalogService);
        bookController = new BookController(bookService);
        noteController = new NoteController(noteService);

        catalog = new Catalog();
        catalog.setName("testCatalog");
        catalog.setDescription("testDescription");

        book = new Book();
        book.setName("testBook");
        book.setCatalog(catalog);

        note = new Note();
        note.setBody("testNote");
        note.setBook(book);

        MockitoAnnotations.initMocks(this);
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
//        ResponseEntity<?> responseNote = noteController.addNote(note, bindingResult);

        // Assert
        assertEquals(HttpStatus.OK, responseCatalog.getStatusCode());
        assertEquals(HttpStatus.OK, responseBook.getStatusCode());
//        assertEquals(HttpStatus.OK, responseNote.getStatusCode());
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
        noteController.addNote(note, bindingResult);
        Long catalogId = catalog.getId();

        // Act
        ResponseEntity<?> catalogResponse = catalogController.delete(catalogId); //cascade remove
        // Assert
        assertEquals(HttpStatus.OK, catalogResponse.getStatusCode());
        assertEquals("Validation successful", catalogResponse.getBody());
    }



    @Test
    public void testDeleteEntity_HasErrors_BAD_REQUEST_InvalidId() {
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
    public void testDeleteEntity_HasErrors_BAD_REQUEST_IdIsNull() {
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


    @Test
    public void testFindByNameCatalog_Successful(){
        // Arrange
        String testCatalogName = "testCatalogName";
        catalog.setName(testCatalogName);
        catalogController.create(catalog, bindingResult);

        String testBookName = "testBookName";
        catalog.setName(testBookName);
        bookController.addBook(book, bindingResult);

        // Act
        ResponseEntity<?> catalogResponse = catalogController.findByName(testCatalogName);
//        ResponseEntity<?> bookResponse = bookController.findByName(testBookName);

        // Assert
        Catalog actualCatalog = (Catalog) catalogResponse.getBody();
        assertEquals(HttpStatus.OK, catalogResponse.getStatusCode());
        assertEquals(Objects.requireNonNull(actualCatalog).getId(), catalog.getId());

//        Book actualBook = (Book) bookResponse.getBody();
//        assertEquals(HttpStatus.OK, bookResponse.getStatusCode());
//        assertEquals(Objects.requireNonNull(actualBook).getId(), book.getId());
    }

    @Test
    public void testFindByNameCatalog_HasErrors_BAD_REQUEST_NotFoundName(){
        // Arrange
        String name = "";

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.findByName(name);
        });
        assertEquals("Not found current name", exception.getMessage());
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

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    public void testChangeNameCatalog_HasErrors_BAD_REQUEST_IdIsNull(){
        // Arrange
        Long id = null;

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.changeName(id, "newCatalogName");
        });
        assertEquals("Operation cannot be performed: identifier is missing. Please check the entered data and try again.", exception.getMessage());
    }

    @Test
    public void testChangeNameCatalog_HasErrors_BAD_REQUEST_NotFoundId(){
        // Arrange
        Long id = 0L;

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.changeName(id, "newCatalogName");
        });
        assertEquals("Not found value id. Please provide a valid identifier.", exception.getMessage());
    }

    @Test
    public void testChangeNameCatalog_HasErrors_BAD_REQUEST_EmptyName(){
        // Arrange
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Long id = catalog.getId();

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.changeName(id, "");
        });
        assertEquals("New name of field (name) can`t be empty", exception.getMessage());
    }

    @Test
    public void testChangeDescriptionCatalog_Successful(){
        // Arrange
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Long id = catalog.getId();

        // Act
        ResponseEntity<?> response = catalogController.changeDescriptionName(id, "new test description");

        // Assert
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testChangeDescriptionCatalog_BedRequest_InvalidId(){

        // Arrange
        Long id = -1L;

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.changeDescriptionName(id, "new test description");
        });

        assertEquals("Not found value id. Please provide a valid identifier.", exception.getMessage());
    }


    @Test
    public void testChangeDescriptionCatalog_BedRequest_IdIsNull(){

        // Arrange
        Long id = null;

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.changeDescriptionName(id, "new test description");
        });

        assertEquals("Operation cannot be performed: identifier is missing. Please check the entered data and try again.", exception.getMessage());
    }


    @Test
    public void testChangeDescriptionCatalog_BedRequest_AlreadyExist(){

        // Arrange
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Long id = catalog.getId();

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.changeDescriptionName(id, catalog.getDescription());
        });

        assertEquals("This description is already exist", exception.getMessage());
    }

}
