package org.example.bookcatalog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "books")
public class Catalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "field Catalog.name can`t be null")
    @NotEmpty(message = "field Catalog.name can`t be empty")
    @Column(name = "catalogName", nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}
