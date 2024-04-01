package org.example.bookcatalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "field Note.body can`t be null")
    @NotEmpty(message = "field Note.body can`t be empty")
    private String body;

    private LocalDateTime creationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "book_fk"))
    private Book book;

}
