package org.example.bookcatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book")
    @SequenceGenerator(name = "book", sequenceName = "book_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "field Book.name can`t be null")
    @NotEmpty(message = "field Book.name can`t be empty")
    @Column(unique = true)
    private String name;

    private String body;

    private BigDecimal pageCount;

    private LocalDateTime creationDate;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "book", cascade = {CascadeType.REMOVE, CascadeType.MERGE,CascadeType.REFRESH})
    private List<Note> notes = new ArrayList<>(); // user note for current book

    @Setter(value = AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "books", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Author> authors = new ArrayList<>();

    @ManyToOne(optional = false) // book can`t be without catalog
    @JoinColumn(name = "catalog_fk")
    private Catalog catalog;

    public void addNote(Note note){
        notes.add(note);
    }
}
