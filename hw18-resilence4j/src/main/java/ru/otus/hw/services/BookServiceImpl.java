package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    @CircuitBreaker(name = "bookServiceFindById", fallbackMethod = "fallbackFindBookById")
    public BookDto findById(long id) {
        Optional<Book> bookOptional = bookRepository.findById(id);
        return bookOptional.map(b -> modelMapper.map(b, BookDto.class))
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
    }

    @CircuitBreaker(name = "bookServiceFindAll", fallbackMethod = "fallbackFindAllBooks")
    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(b -> modelMapper.map(b, BookDto.class))
                .collect(Collectors.toList());
    }

    @CircuitBreaker(name = "bookServiceInsert", fallbackMethod = "fallbackBookInsert")
    @Override
    @Transactional
    public BookDto insert(String title, long authorId, List<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @CircuitBreaker(name = "bookServiceUpdate", fallbackMethod = "fallbackBookUpdate")
    @Override
    @Transactional
    public BookDto update(long id, String title, long authorId, List<Long> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @CircuitBreaker(name = "bookServiceDeleteById", fallbackMethod = "fallbackDeleteBookById")
    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private BookDto fallbackFindBookById(long id, Exception e) {
        log.error("Failed to find book by id: {}", id);
        return new BookDto(id, "N/A", new AuthorDto(0, "N/A"), List.of(new Genre(0, "N/A")));
    }

    private List<BookDto> fallbackFindAllBooks(Exception e) {
        log.error("Failed to retrieve all books");
        return Collections.emptyList();
    }

    private BookDto fallbackBookInsert(String title, long authorId, List<Long> genresIds, Exception e) {
        log.error("Failed to insert new book: {}", title);
        return new BookDto(0, "N/A", new AuthorDto(0, "N/A"), List.of(new Genre(0, "N/A")));
    }

    private BookDto fallbackBookUpdate(long id, String title, long authorId, List<Long> genresIds, Exception e) {
        log.error("Failed to update book with id: {}", id);
        return new BookDto(0, "N/A", new AuthorDto(0, "N/A"), List.of(new Genre(0, "N/A")));
    }

    private void fallbackDeleteBookById(long id, Exception e) {
        log.error("Failed to delete book with id: {}", id);
    }

    private BookDto save(long id, String title, long authorId, List<Long> genresIds) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = genreRepository.findAllByIds(genresIds);
        if (isEmpty(genres)) {
            throw new EntityNotFoundException("Genres with ids %s not found".formatted(genresIds));
        }
        var book = new Book(id, title, author, genres);
        return modelMapper.map(bookRepository.save(book), BookDto.class);
    }
}
