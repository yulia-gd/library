package org.example.library.services;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.library.dto.BookDto;
import org.example.library.entities.Book;
import org.example.library.entities.Member;
import org.example.library.repositories.BookRepository;
import org.example.library.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BorrowingsService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Value("${BOOK_LIMIT:10}")
    @Setter
    private int bookLimit;

    @Transactional
    public void addBorrowing(Long memberId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NoSuchElementException("No book with such id"));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("No member with such id"));
        if (member.getBooks().size() > bookLimit) {
            throw new IllegalStateException("Borrowing limit reached for this member");
        }
        if (member.getBooks().contains(book)) {
            throw new IllegalStateException("The book has already been borrowed by this user");
        }
        if (book.getAmount() <= 0) {
            throw new IllegalStateException("There are no such books");
        }
        member.addBook(book);
        book.decrementAmount();
    }

    @Transactional
    public void returnBook(Long memberId, Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new NoSuchElementException("No book with such id"));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("No member with such id"));
        if (!member.getBooks().contains(book)) {
            throw new IllegalStateException("Member didn't borrow this book");
        }
        member.removeBook(book);
        book.incrementAmount();
    }

    @Transactional(readOnly = true)
    public Set<Book> findBorrowedBooksByMemberName(String name) {
        Member member = memberRepository.findByName(name);
        if (member == null) {
            throw new NoSuchElementException("No member found with name: " + name);
        }
        return member.getBooks();
    }


    @Transactional(readOnly = true)
    public List<String> findAllOriginalBorrowedBooks() {
        return bookRepository.findAllDistinctBorrowedBooksNames();
    }

    @Transactional(readOnly = true)
    public List<BookDto> findAllOriginalBorrowedBooksWithCount() {
        return bookRepository.findAllDistinctBorrowedBooksNamesWithCount();
    }

}
