package org.example.bookcatalog.service.entityService;

import jakarta.persistence.EntityManagerFactory;
import org.example.bookcatalog.entity.Book;
import org.example.bookcatalog.entity.Note;
import org.example.bookcatalog.service.DataService;
import org.springframework.stereotype.Service;

@Service
public class NoteService extends DataService {
    public NoteService(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public void addNoteToBook(Note note){
        Book book = this.executeInTransactionReturning(em ->  em.find(Book.class, note.getBook().getId()));
        book.addNote(note);
        this.validateAndUpdateCreationDate(book);
    }
}
