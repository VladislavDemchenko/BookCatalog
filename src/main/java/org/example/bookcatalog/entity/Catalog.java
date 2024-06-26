package org.example.bookcatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogs")
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString(exclude = "books")
public class Catalog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalog")
    @SequenceGenerator(name = "catalog", sequenceName = "catalog_seq", allocationSize = 1)
    private Long id;

    private LocalDateTime creationDate;

    @NotNull(message = "field Catalog.name cannot be null")
    @NotEmpty(message = "field Catalog.name cannot be empty")
    @Column(name = "catalogName", unique = true)
    private String name;

    private String description;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "catalog", cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book){
        books.add(book);
    }
}
