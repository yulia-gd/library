package org.example.library.repositories;

import org.example.library.dto.BookDto;
import org.example.library.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BookRepository extends JpaRepository<Book, Long> {
    Book findByTitleAndAuthor(String title, String author);

    @Query("select distinct b.title from Book b join b.members")
    List<String> findAllDistinctBorrowedBooksNames();

    @Query("select new org.example.library.dto.BookDto(b.title, count(m)) from Book b join b.members m group by b.title")
    List<BookDto> findAllDistinctBorrowedBooksNamesWithCount();
}
