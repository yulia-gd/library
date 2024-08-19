package org.example.library.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.library.validation.FirstCapitalLetter;
import org.example.library.validation.TwoWordsWithCapitalLetter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@ToString(exclude = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    @FirstCapitalLetter
    private String title;

    @Column(name = "author")
    @NotNull(message = "Author cannot be null")
    @TwoWordsWithCapitalLetter
    private String author;

    @Column(name = "amount")
    private int amount;

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Member> members = new HashSet<>();

    public void incrementAmount() {
        this.amount++;
    }

    public void decrementAmount() {
        if (this.amount == 0) {
            return;
        }
        this.amount--;
    }

    public void update(Book book) {
        this.title = book.getTitle();
        this.author = book.getAuthor();
    }
}
