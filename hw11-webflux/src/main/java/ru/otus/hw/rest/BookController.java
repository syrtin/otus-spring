package ru.otus.hw.rest;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BookController {

    private final AuthorRepository authorRepository;

    private final CommentRepository commentRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    @PostMapping("/api/books")
    public Mono<BookDto> createBook(@RequestBody BookDto book) {
        return save(null, book.getTitle(), book.getAuthor().getId(),
                book.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .collect(Collectors.toList()));
    }

    @PutMapping("/api/books/{id}")
    public Mono<BookDto> editBook(@PathVariable String id, @RequestBody BookDto book) {
        return save(id, book.getTitle(), book.getAuthor().getId(),
                book.getGenres()
                        .stream()
                        .map(Genre::getId)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/api/books/{id}")
    public Mono<BookDto> getBookById(@PathVariable String id) {
        return bookRepository.findById(id)
                .map(b -> modelMapper.map(b, BookDto.class))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s not found".formatted(id))));
    }

    @GetMapping("/api/books")
    public Flux<BookDto> getAllBooks() {
        return bookRepository.findAll()
                .map(b -> modelMapper.map(b, BookDto.class));
    }

    @DeleteMapping("/api/books/{id}")
    public Mono<Void> deleteBookById(@PathVariable String id) {
        return commentRepository.deleteAllByBookId(id)
                .then(bookRepository.deleteById(id));
    }

    @GetMapping("/api/books/{bookId}/comments")
    public Flux<CommentDto> getByBookId(@PathVariable String bookId) {
        return commentRepository.findAllByBookId(bookId)
                .map(c -> modelMapper.map(c, CommentDto.class));
    }

    private Mono<BookDto> save(String id, String title, String authorId, List<String> genresIds) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id " + authorId + " not found")))
                .flatMap(author -> genreRepository.findAllByIdIn(genresIds)
                        .collectList()
                        .filter(genres -> !genres.isEmpty())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Genres with ids " + genresIds + " not found")))
                        .map(genres -> {
                            var book = new Book(id, title, author, genres);
                            return bookRepository.save(book)
                                    .map(savedBook -> modelMapper.map(savedBook, BookDto.class));
                        }))
                .flatMap(Function.identity());
    }
}