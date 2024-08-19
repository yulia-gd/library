package org.example.library.services;

import lombok.RequiredArgsConstructor;
import org.example.library.entities.Book;
import org.example.library.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public Book saveBook(Book book) {
        Book resultBook = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (resultBook == null) {
            resultBook = bookRepository.save(book);
        }
        resultBook.incrementAmount();
        return resultBook;
    }

    @Transactional
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No book with such id"));
    }

    @Transactional
    public Book updateById(Long id, Book book) {
        Book existedBook = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No book with such id"));
        if (bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor()) != null) {
            throw new IllegalStateException("Book with this author and title already exists");
        }
        existedBook.update(book);
        return existedBook;
    }

    @Transactional
    public void deleteById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No book with such id"));
        if (!book.getMembers().isEmpty()) {
            throw new IllegalStateException("Book cannot be deleted as it is currently borrowed.");
        }
        bookRepository.deleteById(id);
    }
}
