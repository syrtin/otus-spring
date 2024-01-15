package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BookController {

    private final BookService bookService;

    private final CommentService commentService;

    @PutMapping("/api/books/{id}")
    public BookDto editBook(@PathVariable String id, @RequestBody BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        return bookService.update(id, title, authorId, genresIds);
    }

    @PostMapping("/api/books")
    public BookDto createBook(@RequestBody BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        return bookService.insert(title, authorId, genresIds);
    }

    @GetMapping("/api/books/{id}")
    public BookDto getBookById(@PathVariable String id) {
        return bookService.findById(id);
    }

    @GetMapping("/api/books")
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @DeleteMapping("/api/books/{id}")
    public void deleteBookById(@PathVariable String id) {
        bookService.deleteById(id);
    }

    @GetMapping("/api/books/{bookId}/comments")
    public List<CommentDto> getByBookId(@PathVariable String bookId) {
        return commentService.getByBookId(bookId);
    }
}