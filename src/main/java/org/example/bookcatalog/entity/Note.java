package org.example.bookcatalog.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String body;

    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "book_fk"))
    private Book book;

}
