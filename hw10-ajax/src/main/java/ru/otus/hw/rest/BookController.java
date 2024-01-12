package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @PutMapping("/books/{id}")
    public BookDto editBook(@PathVariable String id, @RequestBody BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        return bookService.update(id, title, authorId, genresIds);
    }

    @PostMapping("/books")
    public BookDto createBook(@RequestBody BookDto book) {
        var title = book.getTitle();
        var authorId = book.getAuthor().getId();
        var genresIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());
        return bookService.insert(title, authorId, genresIds);
    }

    @GetMapping("/books/{id}")
    public BookDto getBookById(@PathVariable String id) {
        return bookService.findById(id);
    }

    @GetMapping("/books")
    public List<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @DeleteMapping("/books/{id}")
    public void deleteBookById(@PathVariable String id) {
        bookService.deleteById(id);
    }

    @GetMapping("/authors")
    public List<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return genreService.findAll();
    }

    @GetMapping("/books/{bookId}/comments")
    public List<CommentDto> getByBookId(@PathVariable String bookId) {
        return commentService.getByBookId(bookId);
    }
}