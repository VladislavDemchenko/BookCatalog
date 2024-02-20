package org.example.bookcatalog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogs")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Catalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "catalogName", nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();
}
