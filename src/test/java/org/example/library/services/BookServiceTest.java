package org.example.library.services;

import org.example.library.entities.Book;
import org.example.library.entities.Member;
import org.example.library.repositories.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BookServiceTest {


    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveNewBookTest() {

        Book newBook = new Book();
        newBook.setTitle("Test book");
        newBook.setAuthor("Test author");

        when(bookRepository.findByTitleAndAuthor(newBook.getTitle(), newBook.getAuthor())).thenReturn(null);
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        Book result = bookService.saveBook(newBook);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test book");
        assertThat(result.getAuthor()).isEqualTo("Test author");
        assertThat(result.getAmount()).isEqualTo(1); // Припускаючи, що початкове значення amount = 0 і має бути інкрементовано

    }

    @Test
    void saveExistedBook() {

        Book existingBook = new Book();
        existingBook.setTitle("Test book");
        existingBook.setAuthor("Test author");
        existingBook.setAmount(2);

        when(bookRepository.findByTitleAndAuthor(existingBook.getTitle(), existingBook.getAuthor())).thenReturn(existingBook);
        Book result = bookService.saveBook(existingBook);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test book");
        assertThat(result.getAuthor()).isEqualTo("Test author");
        assertThat(result.getAmount()).isEqualTo(3);

    }

    @Test
    void getExistedBookByIdTest() {
        Book existingBook = new Book();
        existingBook.setTitle("Test book");
        existingBook.setAuthor("Test author");
        existingBook.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        Book resultBook = bookService.getBookById(1L);
        assertEquals(resultBook, existingBook);
    }

    @Test
    void getNotExistedBookByIdTest() {

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> bookService.getBookById(1L));
    }

    @Test
    void updateExistedBookTest() {
        Book existedBook = new Book();
        existedBook.setId(1L);
        existedBook.setTitle("Book");
        existedBook.setAuthor("Author");
        existedBook.setAmount(2);

        Book dataBook = new Book();
        dataBook.setId(2L);
        dataBook.setTitle("Updated book");
        dataBook.setAuthor("Updated author");
        dataBook.setAmount(6);

        when(bookRepository.findById(existedBook.getId())).thenReturn(Optional.of(existedBook));
        when(bookRepository.findByTitleAndAuthor(dataBook.getTitle(), dataBook.getAuthor())).thenReturn(null);

        existedBook = bookService.updateById(1L, dataBook);

        assertNotEquals(existedBook.getId(), dataBook.getId());
        assertEquals(existedBook.getTitle(), dataBook.getTitle());
        assertEquals(existedBook.getAuthor(), dataBook.getAuthor());
        assertNotEquals(existedBook.getAmount(), dataBook.getAmount());
    }

    @Test
    void updateNotExistedBookTest() {
        Book dataBook = new Book();
        dataBook.setId(2L);
        dataBook.setTitle("Updated book");
        dataBook.setAuthor("Updated author");
        dataBook.setAmount(6);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        when(bookRepository.findByTitleAndAuthor(dataBook.getTitle(), dataBook.getAuthor())).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> bookService.updateById(1L, dataBook));
    }

    @Test
    void updateExistedBookWithAlreadyExistedDataTest() {
        Book existedBook = new Book();
        existedBook.setId(1L);
        existedBook.setTitle("Book");
        existedBook.setAuthor("Author");
        existedBook.setAmount(2);

        Book dataBook = new Book();
        dataBook.setId(2L);
        dataBook.setTitle("Updated book");
        dataBook.setAuthor("Updated author");
        dataBook.setAmount(6);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existedBook));
        when(bookRepository.findByTitleAndAuthor(dataBook.getTitle(), dataBook.getAuthor())).thenReturn(dataBook);


        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bookService.updateById(1L, dataBook));
        assertEquals("Book with this author and title already exists", exception.getMessage());
    }

    @Test
    void deleteExistedBookTest() {
        Book existedBook = new Book();
        existedBook.setId(1L);
        existedBook.setTitle("Book");
        existedBook.setAuthor("Author");
        existedBook.setAmount(2);

        when(bookRepository.findById(existedBook.getId())).thenReturn(Optional.of(existedBook));

        assertDoesNotThrow(() -> bookService.deleteById(existedBook.getId()));
    }

    @Test
    void deleteNotExistedBookTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> bookService.deleteById(1L));
        assertEquals("No book with such id", exception.getMessage());
    }

    @Test
    void deleteExistedBorrowedBookTest() {
        Book existedBook = new Book();
        existedBook.setId(1L);
        existedBook.setTitle("Book");
        existedBook.setAuthor("Author");
        existedBook.setAmount(2);
        existedBook.getMembers().add(new Member());

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existedBook));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> bookService.deleteById(1L));
        assertEquals("Book cannot be deleted as it is currently borrowed.", exception.getMessage());
    }

}
