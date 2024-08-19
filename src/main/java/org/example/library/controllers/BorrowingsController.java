package org.example.library.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.library.dto.BookDto;
import org.example.library.entities.Book;
import org.example.library.services.BorrowingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class BorrowingsController {
    private final BorrowingsService borrowingsService;

    @Operation(summary = "Borrow a book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Borrowing added successfully", content = @Content(mediaType = "text")),
            @ApiResponse(responseCode = "404", description = "Member or book not found"),
            @ApiResponse(responseCode = "409", description = "Book already borrowed, limit reached or there is no such book"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/add/{memberId}/{bookId}")
    public ResponseEntity<String> borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        try {
            borrowingsService.addBorrowing(memberId, bookId);
            return ResponseEntity.ok("Borrowing added successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Return a borrowed book")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book returned successfully", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Member or book not found"),
            @ApiResponse(responseCode = "409", description = "Book wasn't borrowed"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @DeleteMapping("/return/{memberId}/{bookId}")
    public ResponseEntity<String> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        try {
            borrowingsService.returnBook(memberId, bookId);
            return ResponseEntity.ok("Book returned successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Get books borrowed by a member name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of borrowed books", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Member not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("books/member/{memberName}")
    public ResponseEntity<?> getBooksByMemberName(@PathVariable String memberName) {
        try {
            return ResponseEntity.ok(borrowingsService.findBorrowedBooksByMemberName(memberName));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Get all originally borrowed books")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of originally borrowed books", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("books/borrowed")
    public List<String> getOriginalBorrowedBooks() {
        return borrowingsService.findAllOriginalBorrowedBooks();
    }

    @Operation(summary = "Get all originally borrowed books with count")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of borrowed books with count", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("books/borrowed_count")
    public List<BookDto> getOriginalBorrowedBooksWithCount() {
        return borrowingsService.findAllOriginalBorrowedBooksWithCount();
    }
}