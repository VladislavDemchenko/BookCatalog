package org.example.bookcatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "field Book.name can`t be null")
    @NotEmpty
    @Column(nullable = false, unique = true)
    private String name;

    private String body;

    private BigDecimal pageCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>(); // user note for current book

    @Setter(value = AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "books", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Author> author = new ArrayList<>();

    @ManyToOne(optional = false) // book can`t be without catalog
    @JoinColumn(name = "catalog_fk")
    private Catalog catalog;

}
