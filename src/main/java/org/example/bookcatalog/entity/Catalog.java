package org.example.bookcatalog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
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

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();


}
