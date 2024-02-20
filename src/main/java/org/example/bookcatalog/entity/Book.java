package org.example.bookcatalog.entity;

import jakarta.persistence.*;
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

    @Column(nullable = false, unique = true)
    private String name;

    private String body;

    private BigDecimal pageCount;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>(); // user note for current book

    @Setter(value = AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "books")
    private List<Author> author = new ArrayList<>();

    @ManyToOne(optional = false) // book can`t be without catalog
    @JoinColumn(name = "catalog_fk")
    private Catalog catalog;

}
