package org.example.bookcatalog.controller;

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

        dataService = new DataService(persistenceUnit);
        dataAccessService = new DataAccessService(persistenceUnit);

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

        note = Note.builder()
                .body("testNote")
                .book(book)
                .build();

        MockitoAnnotations.initMocks(this);
    }
    @After
    public void cleaningDB(){
        try {
             dataAccessService.executeInTransaction(em -> em.remove(em.find(catalog.getClass(), catalog.getId())));
//            dataService.delete(catalog.getId(), Catalog.class);
        }catch (Exception e){}
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
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        dataService.create(book, new FieldDto<String>("name"), bindingResult);
        Catalog catalog2 = Catalog.builder().name(catalog.getName()).build();
        Book book2 = Book.builder().name(book.getName()).build();

        // Act & Assert
        InvalidRequestException catalogException = assertThrows(InvalidRequestException.class, () -> {
            catalogController.create(catalog2, bindingResult);
        });
        InvalidRequestException bookException = assertThrows(InvalidRequestException.class, () -> {
            bookController.addBook(book2, bindingResult);
        });

        assertEquals("This field name " + catalog.getName() + " already exists", catalogException.getMessage());
        assertEquals("This field name " + book.getName() + " already exists", bookException.getMessage());
    }

    @Test
    public void deleteEntity_Successful() {
        // Arrange
        catalogController.create(catalog, bindingResult);
        bookController.addBook(book, bindingResult);
        Long catalogId = catalog.getId();
        Long bookId = book.getId();

        // Act
        ResponseEntity<?> catalogResponse = catalogController.delete(catalogId); //cascade remove

        // Assert
        assertEquals(HttpStatus.OK, catalogResponse.getStatusCode());
        assertEquals("Validation successful", catalogResponse.getBody());
    }

    @Test
    public void testDeleteCatalog_Successful() {
        // Arrange
        catalog.setName("testCatalogName");
        dataService.create(catalog, new FieldDto<String>("name"), bindingResult);
        Long id = catalog.getId();

        // Act
        ResponseEntity<?> response = catalogController.delete(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Validation successful", response.getBody());
    }

    @Test
    public void testDeleteCatalog_HasErrors_BAD_REQUEST_InvalidId() {
        // Arrange
        Long id = -1L;

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.delete(id);
        });
        assertEquals("Not found value id. Please provide a valid identifier.", exception.getMessage());
    }

    @Test
    public void testDeleteCatalog_HasErrors_BAD_REQUEST_IdIsNull() {
        // Arrange
        Long id = null;

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            catalogController.delete(id);
        });
        assertEquals("Operation cannot be performed: identifier is missing. Please check the entered data and try again.", exception.getMessage());
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
