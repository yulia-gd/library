package org.example.library.services;

import org.example.library.dto.BookDto;
import org.example.library.entities.Book;
import org.example.library.entities.Member;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


public class BorrowingsServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private BorrowingsService borrowingsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void addBorrowingWithNoSuchUserTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> borrowingsService.addBorrowing(1L, 1L));
        assertEquals(exception.getMessage(), "No book with such id");
    }

    @Test
    void addBorrowingWithNoSuchBookTest() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> borrowingsService.addBorrowing(1L, 1L));
        assertEquals(exception.getMessage(), "No member with such id");
    }

    @Test
    void borrowBookLimitReachedTest() {
        borrowingsService.setBookLimit(10);
        Book book = new Book();
        book.setId(1L);
        Member member = new Member();
        member.setId(1L);
        for (int i = 0; i < 11; i++) {
            member.getBooks().add(new Book());
        }
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> borrowingsService.addBorrowing(1L, 1L));
        assertEquals(exception.getMessage(), "Borrowing limit reached for this member");
    }

    @Test
    void borrowBookThatAlreadyBorrowedTest() {
        borrowingsService.setBookLimit(10);
        Book book = new Book();
        book.setId(1L);
        book.setTitle("My book");
        book.setAuthor("Me)");
        Member member = new Member();
        member.setId(1L);
        member.getBooks().add(book);


        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> borrowingsService.addBorrowing(1L, 1L));
        assertEquals(exception.getMessage(), "The book has already been borrowed by this user");
    }

    @Test
    void borrowBookWithNoSuchBookInStockTest() {
        borrowingsService.setBookLimit(10);
        Book book = new Book();
        book.setId(1L);
        book.setAmount(0);

        Member member = new Member();
        member.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> borrowingsService.addBorrowing(1L, 1L));
        assertEquals(exception.getMessage(), "There are no such books");
    }

    @Test
    void successfulBorrowBookTest() {
        borrowingsService.setBookLimit(10);
        Book book = new Book();
        book.setId(1L);
        book.setAmount(1);

        Member member = new Member();
        member.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        borrowingsService.addBorrowing(1L, 1L);
        assertEquals(book.getAmount(), 0);
        assertEquals(book.getMembers().size(), 1);
        assertEquals(member.getBooks().size(), 1);
    }

    @Test
    void returnBookWithNoSuchBookTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> borrowingsService.returnBook(1L, 1L));
        assertEquals("No book with such id", exception.getMessage());
    }

    @Test
    void returnBookWithNoSuchMemberTest() {
        Book book = new Book();
        book.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> borrowingsService.returnBook(1L, 1L));
        assertEquals("No member with such id", exception.getMessage());
    }

    @Test
    void returnBookNotBorrowedByMemberTest() {
        Book book = new Book();
        book.setId(1L);
        book.setAmount(1);
        Member member = new Member();
        member.setId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> borrowingsService.returnBook(1L, 1L));
        assertEquals("Member didn't borrow this book", exception.getMessage());
    }

    @Test
    void successfulReturnBookTest() {
        Book book = new Book();
        book.setId(1L);
        book.setAmount(1);

        Member member = new Member();
        member.setId(1L);
        member.addBook(book);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        borrowingsService.returnBook(1L, 1L);

        assertEquals(book.getAmount(), 2);
        assertEquals(book.getMembers().size(), 0);
        assertEquals(member.getBooks().size(), 0);
    }

    @Test
    void findBorrowedBooksByMemberNameWithNoSuchMemberTest() {
        when(memberRepository.findByName("Member")).thenReturn(null);
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> borrowingsService.findBorrowedBooksByMemberName("Member"));
        assertEquals("No member found with name: Member", exception.getMessage());
    }

    @Test
    void findBorrowedBooksByMemberNameTest() {
        Member member = new Member();
        member.setName("Member");

        Book book1 = new Book();
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setTitle("Book 2");

        Set<Book> borrowedBooks = new HashSet<>();
        borrowedBooks.add(book1);
        borrowedBooks.add(book2);

        member.addBook(book1);
        member.addBook(book2);

        when(memberRepository.findByName("Member")).thenReturn(member);

        Set<Book> resultBooks = borrowingsService.findBorrowedBooksByMemberName("Member");

        assertEquals(borrowedBooks, resultBooks);
    }

    @Test
    void findAllOriginalBorrowedBooksTest() {
        List<String> borrowedBooksNames = Arrays.asList("Book 1", "Book 2", "Book 3");

        when(bookRepository.findAllDistinctBorrowedBooksNames()).thenReturn(borrowedBooksNames);

        List<String> result = borrowingsService.findAllOriginalBorrowedBooks();

        assertEquals(borrowedBooksNames, result);
    }

    @Test
    void findAllOriginalBorrowedBooksWithCountTest() {
        BookDto bookDto1 = new BookDto("Book 1", 5L);
        BookDto bookDto2 = new BookDto("Book 2", 3L);

        List<BookDto> bookDtos = Arrays.asList(bookDto1, bookDto2);

        when(bookRepository.findAllDistinctBorrowedBooksNamesWithCount()).thenReturn(bookDtos);

        List<BookDto> result = borrowingsService.findAllOriginalBorrowedBooksWithCount();

        assertEquals(bookDtos, result);
    }


}
