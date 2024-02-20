package org.example.bookcatalog.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;

    private String lastName;

    @Setter(value = AccessLevel.PRIVATE)
    @ManyToMany
    @JoinTable(
            name = "authors_books",
            joinColumns = @JoinColumn(name = "authors", foreignKey = @ForeignKey(name = "authors_fk")),
            inverseJoinColumns = @JoinColumn(name = "books", foreignKey = @ForeignKey(name = "books_fk"))
    )
    private List<Book> books = new ArrayList<>();
}
